package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.LessonMember;
import com.prcek.clr.client.data.Repository;
import com.prcek.clr.client.data.Lesson.LessonType;

abstract public class LessonMembersGrid extends LayoutContainer {
	private ListLoader loader;
	private Lesson current_lesson;
	private Grid<LessonMember> grid;
	//private ContentPanel cp;
	private LessonMemberEditWindow member_edit_window;
	private LabelToolItem lesson_info;
	public LessonMembersGrid() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		
		member_edit_window = new LessonMemberEditWindow(){

			@Override
			public void afterLessonMemberChange() {
				// TODO Auto-generated method stub
				afterLessonChange();
			}
			
		};
		
		
		GridCellRenderer<LessonMember> gridDate = new GridCellRenderer<LessonMember>() {  
		       public String render(LessonMember model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<LessonMember> store, Grid<LessonMember> grid) {  
		    	   Date d = new Date(model.getDateTime());
		    	   String color = "black";
		    	   return "<span style='color:"+color+"'>" + DateTimeFormat.getFormat("d. M. y HH:mm").format(d) + "</span>";
		       }  
		};  

		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();  
		column.setId("order");  
		column.setHeader("#");  
		column.setWidth(20);  
		configs.add(column);

		column = new ColumnConfig();  
		column.setId("number");  
		column.setHeader("Číslo");  
		column.setWidth(45);  
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setId("surname");  
		column.setHeader("Přijmení");  
		column.setWidth(120);  
		configs.add(column);

		column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader("Jméno");  
		column.setWidth(80);  
		configs.add(column);

		column = new ColumnConfig();  
		column.setId("phone");  
		column.setHeader("Telefon");  
		column.setWidth(80);  
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setId("date");  
		column.setHeader("Zapsán");  
		column.setWidth(100);
		column.setRenderer(gridDate);
		configs.add(column);
		
		
		CheckColumnConfig check_col = new CheckColumnConfig("attend","Účast",40);
		configs.add(check_col);

		for(ColumnConfig cc: configs) {
			cc.setMenuDisabled(true);
			cc.setSortable(false);
		}

		
		RpcProxy proxy = new RpcProxy() {
			protected void load(Object loadConfig, AsyncCallback callback) {
				clr.dataService.getLessonMembers(current_lesson.getId(), callback);	
			}
		};  
		ModelReader reader = new ModelReader();
		loader = new BaseListLoader(proxy,reader);
		loader.addLoadListener(clr.auth_check_listener);
		ListStore<LessonMember> store = new ListStore<LessonMember>(loader);  
		   
		ColumnModel cm = new ColumnModel(configs);  

		
		
		
		
		
		ToolBar toolBar = new ToolBar();  
		
		lesson_info = new LabelToolItem("Lekce: ");
		toolBar.add(lesson_info);
		
		
		Button reload_bt = new Button();
//		reload_bt.setIconStyle("x-tbar-loading");
		reload_bt.setIcon(AbstractImagePrototype.create(clr.IMAGES.refresh()));
		reload_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				reload();
			}
		});
		toolBar.add(reload_bt);
		
		toolBar.add(new FillToolItem());
		toolBar.add(new SeparatorToolItem());  
		
		
		Button add_bt = new Button("Přihlásit");
		add_bt.setIconStyle("icon-add");
		add_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				addMember();
			}
		});
		toolBar.add(add_bt);
		
		
//		toolBar.add(new SeparatorToolItem());  
		Button del_bt = new Button("Odhlásit");
		del_bt.setIconStyle("icon-delete");
		del_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				delMember();
			}
		});
		toolBar.add(del_bt);

//		toolBar.add(new SeparatorToolItem());  
		Button attend_bt = new Button("Účast");
		attend_bt.setIconStyle("icon-delete");
		attend_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				attendMember();
			}
		});
		toolBar.add(attend_bt);
		
//		toolBar.add(new SeparatorToolItem());  
		Button print_bt = new Button("Tisk");
		print_bt.setIconStyle("icon-plugin");
		print_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				printMembers();
			}
		});
		toolBar.add(print_bt);

		
		grid = new Grid<LessonMember>(store, cm);  
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		add(toolBar, new RowData(1,-1,new Margins(0)));
		add(grid, new RowData(1,1,new Margins(0)));
		
	}
	public void setLesson(Lesson l) {
		current_lesson = l;
		String x = DateTimeFormat.getFormat("d. M. y HH:mm").format(new Date(l.getDateTime()));
		String stav = "";
		String typ = "";
		if ((l.getType()>=0) && (l.getType()<LessonType.values().length)) {
			typ = clr.localService.getLessonName(LessonType.values()[l.getType()]);
		}
		if (l.isFull()) {
			stav = "[OBSAZENO]";
		}
		lesson_info.setLabel("Lekce "+typ+" "+x+" "+stav);
		loader.load();
	}
	
	private void addMember() {
		member_edit_window.addLessonMember(current_lesson.getId());
	}
	private void printMembers() {
		 Window.open(clr.printUrl+"?action=print_lesson&lesson_id="+String.valueOf(current_lesson.getId()), "_blank", "");
	}
	private void delMember() {
		final LessonMember lm = grid.getSelectionModel().getSelectedItem();
		if (lm==null) {
			MessageBox.alert("Není vybraný klient", "Je třeba zvolit klienta, který má být odhlášen", null);
		} else {
			MessageBox.confirm("Opravdu odhlásit klienta z lekce?", "Zvolený klient bude nenávratně odhlášen", new Listener<MessageBoxEvent>(){
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().compareTo("yes")==0) {
						clr.dataService.deleteLessonMember(lm.getId(), new AsyncCallback<Boolean>(){
							public void onFailure(Throwable caught) {
							}
							public void onSuccess(Boolean result) {
								afterLessonChange();
							}
						});
					} 
				}
			});
		}
	}
	private void attendMember() {
		final LessonMember lm = grid.getSelectionModel().getSelectedItem();
		if (lm==null) {
			MessageBox.alert("Není vybraný klient", "Je třeba zvolit klienta", null);
		} else {
			MessageBox.confirm("Účastnil se klient lekce?", "Vyberte zda se klient účastnil Ano nebo Ne", new Listener<MessageBoxEvent>(){
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().compareTo("yes")==0) {
						clr.dataService.attendLessonMember(lm.getId(),true, new AsyncCallback<Boolean>(){
							public void onFailure(Throwable caught) {
							}
							public void onSuccess(Boolean result) {
								afterLessonChange();
							}
						});
					}
					if (be.getButtonClicked().getItemId().compareTo("no")==0) {
						clr.dataService.attendLessonMember(lm.getId(),false, new AsyncCallback<Boolean>(){
							public void onFailure(Throwable caught) {
							}
							public void onSuccess(Boolean result) {
								afterLessonChange();
							}
						});
					} 

				}
			});
		}
	}

	public void reload() {
		if (current_lesson != null) {
			loader.load();
		}
	}
	public abstract void afterLessonChange();
}
