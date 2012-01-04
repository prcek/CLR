package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.LessonMember;
import com.prcek.clr.client.data.Member;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class MembersPanel extends LayoutContainer {
	private static int page_size=20;
	private BasePagingLoader loader;
	private PagingToolBar p_toolBar;
	private FilterField<String> filterField;
	private Grid<Member> grid;
	private MemberEditWindow member_edit_window;
	public MembersPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		member_edit_window = new MemberEditWindow(){
			@Override
			public void onModify() {
			p_toolBar.refresh();
			}
			
		};			

		
		RpcProxy<PagingLoadResult<Member>> proxy = new RpcProxy<PagingLoadResult<Member>>() {
			protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Member>> callback) {
				 String filter = filterField!=null?filterField.getValue():null;
				 if ((filter!=null) && (filter.length()>0)) {
					 ((PagingLoadConfig)loadConfig).set("query", filter);
					 clr.dataService.queryMembers((PagingLoadConfig)loadConfig, callback);
				 } else {
					 clr.dataService.getMembers((PagingLoadConfig)loadConfig, callback);
				 }
			}
		};
			  
		
	    loader = new BasePagingLoader(proxy);
	    loader.addLoadListener(clr.auth_check_listener);
		loader.setRemoteSort(true);
		loader.setSortField("surname");
		loader.load(0, page_size);  
		ListStore<Member> store = new ListStore<Member>(loader);  
		   
		p_toolBar = new PagingToolBar(page_size);  
		p_toolBar.bind(loader);

		p_toolBar.add(new SeparatorToolItem());
		
		filterField = new FilterField<String>();
		filterField.addListener(Events.TriggerClick, new Listener<FieldEvent>() {
			public void handleEvent(FieldEvent be) {
				p_toolBar.refresh();
			}
		});
		p_toolBar.add(filterField);
		
		
		Button filter_bt = new Button("Filtruj");
		filter_bt.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				p_toolBar.refresh();
			}
		});
		p_toolBar.add(filter_bt);
		
		
		p_toolBar.add(new SeparatorToolItem());
		
		
		

		Button add_bt = new Button("Zaevidovat");
		//add_bt.setIconStyle("icon-add");
		add_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				addMember();
			}
		});
		p_toolBar.add(add_bt);
		//toolBar.add(new SeparatorToolItem());  
		Button del_bt = new Button("Odstranit");
		//del_bt.setIconStyle("icon-delete");
		del_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				delMember();
			}
		});
		p_toolBar.add(del_bt);
		
		//toolBar.add(new SeparatorToolItem());
		Button edit_bt = new Button("Opravit");
		//edit_bt.setIconStyle("icon-plugin");
		edit_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				editMember();
			}
		});
		p_toolBar.add(edit_bt);
		
		add(p_toolBar, new RowData(1,-1,new Margins(0)));
		
		
		
		
		
		GridCellRenderer<Member> gridDate = new GridCellRenderer<Member>() {  
		       public String render(Member model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<Member> store,Grid<Member> grid) {  
		    	   //return numberRenderer.render(null, property, model.get(property));
		    	   Date d = new Date(model.getDateTime());
		    	   String color = "black";
//		    	   if (model.isFull()) {
//		    		   color = "red";
		    	   //}
		    	   return "<span style='color:"+color+"'>" + DateTimeFormat.getFormat("d. M. y HH:mm").format(d) + "</span>";
		    	   //return "x";
		       }  
	    };  

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(new ColumnConfig("number","Číslo",45));
		configs.add(new ColumnConfig("surname","Přijmení",120));
		configs.add(new ColumnConfig("name","Jméno",80));
		configs.add(new ColumnConfig("phone","Telefon",80));
		
		ColumnConfig column = new ColumnConfig();  
		column.setId("created");  
		column.setHeader("Zaevidován");  
		column.setWidth(120);
		column.setRenderer(gridDate);
		configs.add(column);
		//configs.add(new ColumnConfig("date","registrace",80));
		
		
		
		configs.add(new ColumnConfig("email","e-mail",150));
		configs.add(new ColumnConfig("street","Ulice",200));
		configs.add(new ColumnConfig("street_no","Č.o.",20));
		configs.add(new ColumnConfig("city","Město",80));
		
		for(ColumnConfig cc: configs) {
			cc.setMenuDisabled(true);
		}
		
		ColumnModel cm = new ColumnModel(configs);  
			   
		grid = new Grid<Member>(store, cm);  
		grid.setLoadMask(true);  
		grid.setBorders(true);
		//grid.setHeight(500);
		
		add(grid, new RowData(1,1,new Margins(0)));
/*		
		ToolBar toolBar = new ToolBar();  
		
		TextToolItem add_bt = new TextToolItem("Zaevidovat", "icon-add");
		add_bt.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				addMember();
			}
		});
		toolBar.add(add_bt);
		
		
		toolBar.add(new SeparatorToolItem());  
		TextToolItem del_bt = new TextToolItem("Odstranit", "icon-delete");
		del_bt.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				delMember();
			}
		});
		toolBar.add(del_bt);
		
		toolBar.add(new SeparatorToolItem());  
		TextToolItem edit_bt = new TextToolItem("Opravit", "icon-plugin");
		edit_bt.addSelectionListener(new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				editMember();
			}
		});
		toolBar.add(edit_bt);
*/		
/*		
		ContentPanel panel = new ContentPanel();  
		panel.setFrame(true);  
		//panel.setCollapsible(true);  
		//panel.setAnimCollapse(false);  
		panel.setButtonAlign(HorizontalAlignment.CENTER);  
		panel.setIconStyle("icon-table");  
		panel.setHeading("Evidence klientů");  
		panel.setLayout(new FitLayout());  
		panel.add(grid);  
		panel.setSize(700, 570);  
		panel.setBottomComponent(p_toolBar);  
		panel.setTopComponent(toolBar);  
		add(panel);  
*/		
	}
	private void addMember() {
		clr.dataService.generateNumber(new AsyncCallback<String>(){
			public void onFailure(Throwable caught) {
				
			}
			public void onSuccess(String result) {
				member_edit_window.addMember(result);
			}
		});
	}
	private void delMember() {
		final Member m = grid.getSelectionModel().getSelectedItem();
		if (m==null) {
			MessageBox.alert("Není vybraný klient", "Je třeba zvolit klienta, který má být odstraněn z evidence", null);
		} else {
			MessageBox.confirm("Opravdu odstranit klienta z evidence?", "Zvolený klient bude nenávratně odstraněn z evidence", new Listener<MessageBoxEvent>(){
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().compareTo("yes")==0) {
						clr.dataService.deleteMember(m.getId(), new AsyncCallback<Boolean>(){
							public void onFailure(Throwable caught) {
							}
							public void onSuccess(Boolean result) {
								p_toolBar.refresh();
							}
						});
					} 
				}
			});
		}
	}
	private void editMember() {
		Member m = grid.getSelectionModel().getSelectedItem();
		if (m==null) {
			MessageBox.alert("Není vybraný klient", "Je třeba zvolit klienta, který má být editován", null);
		} else {
			member_edit_window.editMember(m);
		}
	}

}
