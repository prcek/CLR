package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.Repository;
import com.prcek.clr.client.data.Lesson.LessonType;

abstract public class LessonsGrid extends LayoutContainer {
	private ListLoader loader;
	private Date current_date;
	private Grid<Lesson> grid;
	private LessonType lesson_type;
	private LessonEditWindow lesson_edit_window;
	public LessonsGrid(LessonType ltype) {
		setLayout(new RowLayout(Orientation.VERTICAL));
		
		lesson_type = ltype;
		
		lesson_edit_window = new LessonEditWindow(lesson_type){
			@Override
			public void afterLessonChange() {
				// TODO Auto-generated method stub
				afterLessonGridChange();
			}
		};
		
		GridCellRenderer<Lesson> gridDate = new GridCellRenderer<Lesson>() {  
			       public String render(Lesson model, String property, ColumnData config, int rowIndex,  
			           int colIndex, ListStore<Lesson> store,Grid<Lesson> grid) {  
			    	   //return numberRenderer.render(null, property, model.get(property));
			    	   Date d = new Date(model.getDateTime());
			    	   String color = "black";
			    	   if (model.isFull()) {
			    		   color = "red";
			    	   }
			    	   return "<span style='color:"+color+"'>" + DateTimeFormat.getFormat("d. M. y HH:mm").format(d) + "</span>";
			    	   //return "x";
			       }  
		};  

		GridCellRenderer<Lesson> gridCap = new GridCellRenderer<Lesson>() {  
		       public String render(Lesson model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<Lesson> store,Grid<Lesson> grid) {  
		    	   //return numberRenderer.render(null, property, model.get(property));
		    	   int c = model.getCapacity();
		    	   int r = model.getRegistered();
		    	   String stav = Integer.toString(r) + "/" + Integer.toString(c);
		    	   Date d = new Date(model.getDateTime());
		    	   String color = "black";
		    	   if (model.isFull()) {
		    		   color = "red";
		    	   }
		    	   return "<span style='color:"+color+"'>" + stav + "</span>";
		    	   //return "x";
		       }  
	};  
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();  
		column.setId("date");  
		column.setHeader("Termín");  
		column.setWidth(100);
		column.setRenderer(gridDate);
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setHeader("Stav");  
		column.setWidth(50);  
		column.setRenderer(gridCap);
		configs.add(column);
		
		for(ColumnConfig cc: configs) {
			cc.setMenuDisabled(true);
			cc.setSortable(false);
		}
		
		RpcProxy proxy = new RpcProxy() {
			protected void load(Object loadConfig, AsyncCallback callback) {
				clr.dataService.getLessons(lesson_type, current_date, 12, callback);	
			}
		};  
		ModelReader reader = new ModelReader();
		loader = new BaseListLoader(proxy,reader);
		loader.addLoadListener(clr.auth_check_listener);
		ListStore<Lesson> store = new ListStore<Lesson>(loader);  
		
		ColumnModel cm = new ColumnModel(configs);  
		   
		//cp = new ContentPanel();  
		//cp.setBodyBorder(false);  
		//cp.setHeading("Seznam lekci");  
		//cp.setButtonAlign(HorizontalAlignment.CENTER);  
		//cp.setLayout(new FitLayout());  
		//cp.setSize(180, 190);  
		  
		
		ToolBar toolBar = new ToolBar();
		
		Button bt_menu = new Button("Nastavení");
		Menu menu = new Menu();
		
		menu.add(new MenuItem("Přidat lekci", new SelectionListener<MenuEvent>(){
			public void componentSelected(MenuEvent ce) {
				addLesson();
			}
		}));
		menu.add(new MenuItem("Zrušit lekci", new SelectionListener<MenuEvent>(){
			public void componentSelected(MenuEvent ce) {
				delLesson();
			}
		}));
		menu.add(new MenuItem("Změnit lekci", new SelectionListener<MenuEvent>(){
			public void componentSelected(MenuEvent ce) {
				editLesson();
			}
		}));
		

		bt_menu.setMenu(menu);
		toolBar.add(bt_menu);
		toolBar.add(new FillToolItem());
		
		Button reload_bt = new Button();
		reload_bt.setIcon(AbstractImagePrototype.create(clr.IMAGES.refresh()));
		//reload_bt.setIconStyle("x-tbar-loading");
		reload_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				reload();
			}
		});
		toolBar.add(reload_bt);
		
		
		add(toolBar, new RowData(1,-1,new Margins(0)));
		
//		cp.setTopComponent(toolBar);  
		
		
		
		grid = new Grid<Lesson>(store, cm);  
//		grid.setStyleAttribute("borderTop", "none"); 
//		grid.setAutoExpandColumn("date");  
		grid.setBorders(true);
		grid.setLoadMask(true);
		
		
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<Lesson>(){
			
			
			@Override
			public void selectionChanged(SelectionChangedEvent<Lesson> se) {
				// TODO Auto-generated method stub
				if (se.getSelection().size()>0) {
					onLessonSelect(se.getSelection().get(0));
				}
			}

		});
	
	//	cp.add(grid);  
		add(grid,new RowData(1,1,new Margins(0)));
		
		setStartDate(new Date());
	}
	private void addLesson() {
		lesson_edit_window.addLesson(current_date);
	}
	private void delLesson() {
		final Lesson l = grid.getSelectionModel().getSelectedItem();
		if (l==null) {
			MessageBox.alert("Není vybraná lekce", "Je třeba zvolit lekci, která má být smazána", null);
		} else {
			MessageBox.confirm("Opravdu smazat lekci?", "Zvolená lekce bude nenávratně smazána", new Listener<MessageBoxEvent>(){
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().compareTo("yes")==0) {
						clr.dataService.deleteLesson(l.getId(), new AsyncCallback<Boolean>(){
							public void onFailure(Throwable caught) {
							}
							public void onSuccess(Boolean result) {
								afterLessonGridChange();
							}
						});
					} 
				}
			});
		}
	}
	private void editLesson() {
		Lesson l = grid.getSelectionModel().getSelectedItem();
		if (l==null) {
			MessageBox.alert("Není vybraná lekce", "Je třeba zvolit lekci, která má být editována", null);
		} else {
			lesson_edit_window.editLesson(l);
		}
	}
	
	public void setStartDate(Date date) {
		current_date = date;
		loader.load();
	}
	public abstract void onLessonSelect(Lesson l);
	
	public void reload() {
		if (current_date != null) {
			loader.load();
		}
	}
	public abstract void afterLessonGridChange();
}

