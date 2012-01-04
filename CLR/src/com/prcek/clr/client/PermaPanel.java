package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
//import com.extjs.gxt.ui.client.widget.PagingToolBar;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
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
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.Doctor;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.PermaItem;
import com.prcek.clr.client.data.PermaType;

public class PermaPanel extends LayoutContainer {
	
	private class PermaItemWnd extends Window {
		private FormPanel form;
		private ComboBox<PermaType> permaType;
		private ComboBox<Doctor> doctor;
		private NumberField count;
		private NumberField cost;
		private TextField<String> user;
		private int orig_id;
		private long orig_time;
		private Button save_bt;
		private Button del_bt;
		public PermaItemWnd() {
			  setHeading("Zaznam o prodeji");
			  setLayout(new FitLayout());
			  setSize(400,250);
			  setResizable(clr.allowResizableWnd);
			  setModal(clr.allowModalWnd);
			  
			  
			  form = new FormPanel();
			  form.setHeaderVisible(false);
			  form.setBorders(false);

			  doctor = new ComboBox<Doctor>();
			  doctor.setDisplayField("fullname");
			  doctor.setName("doctor_id");
			  doctor.setFieldLabel("kdo");
			  doctor.setEditable(false);
			  doctor.setForceSelection(true);
			  doctor.setAllowBlank(false);
			  doctor.setTriggerAction(TriggerAction.ALL);
			  doctor.setStore(clr.localService.getDoctorsStore());
			  form.add(doctor);
			  
			  permaType = new ComboBox<PermaType>();
			  permaType.setDisplayField("name");
			  permaType.setName("type_id");
			  permaType.setFieldLabel("typ");
			  permaType.setAllowBlank(false);
			  permaType.setTriggerAction(TriggerAction.ALL);
			  permaType.setStore(clr.localService.getPermaTypesStoreCB());
			  form.add(permaType);

			  user = new TextField<String>();
			  user.setName("user");
			  user.setFieldLabel("komu");
			  user.setAllowBlank(true);
			  user.setMaxLength(32);
			  form.add(user);
			  
			  count = new NumberField();
			  count.setName("count");
			  count.setFieldLabel("počet");
			  count.setAllowBlank(false);
			  count.setAllowDecimals(false);
			  count.setAllowNegative(false);
			  form.add(count);

			  cost = new NumberField();
			  cost.setName("cost");
			  cost.setFieldLabel("cena");
			  cost.setAllowBlank(false);
			  cost.setAllowDecimals(false);
			  cost.setAllowNegative(false);
			  form.add(cost);
			  
			  form.setButtonAlign(HorizontalAlignment.CENTER);
			  save_bt = new Button("Uložit", new SelectionListener<ButtonEvent>(){
				public void componentSelected(ButtonEvent ce) {
					if (form.isValid()) {
						Doctor dv = doctor.getValue();
						PermaType tv = permaType.getValue();
						int pt_id = 0;
						if (tv!=null) {
							pt_id = tv.getId();
						}
						String tsv = permaType.getRawValue();
						String ust = user.getValue();
						if (ust==null) {
								ust="";
						}
						Number countv =  count.getValue();
						Number costv =  cost.getValue();
						if (orig_id==-1) {
							PermaItem item = new PermaItem(-1,dv.getId(),pt_id,countv.intValue(),costv.intValue(),tsv,ust,0);
							clr.dataService.insertPermaItem(item, new AsyncCallback<Boolean>(){
								public void onFailure(Throwable arg0) {
								}
	
								public void onSuccess(Boolean arg0) {
									afterChange();
								}
							});
						} else {
							PermaItem item = new PermaItem(orig_id,dv.getId(),pt_id,countv.intValue(),costv.intValue(),tsv,ust,orig_time);
							clr.dataService.updatePermaItem(item, new AsyncCallback<Boolean>(){
								public void onFailure(Throwable arg0) {
								}
	
								public void onSuccess(Boolean arg0) {
									afterChange();
								}
							});
						}
						
						hide();
					} else {
						
					}
					//hide();
				}
			  });
			  form.addButton(save_bt);

			  del_bt = new Button("Smazat", new SelectionListener<ButtonEvent>(){
					public void componentSelected(ButtonEvent ce) {
							clr.dataService.deletePermaItem(orig_id, new AsyncCallback<Boolean>(){

								public void onFailure(Throwable arg0) {
									// TODO Auto-generated method stub
									
								}

								public void onSuccess(Boolean arg0) {
									afterChange();
								}
								
							});
							hide();
					}
				  });
			  form.addButton(del_bt);
			  
			  form.addButton(new Button("Storno", new SelectionListener<ButtonEvent>(){
					public void componentSelected(ButtonEvent ce) {
						hide();
					}
			  }));
			  add(form);
		}
		private void resetFields() {
			doctor.setValue(null);
			permaType.setValue(null);
			count.setValue(null);
			cost.setValue(null);
			user.setValue(null);
		}
		private void clearInvalid() {
			doctor.clearInvalid();
			permaType.clearInvalid();
			count.clearInvalid();
			cost.clearInvalid();
			user.clearInvalid();
		}
		private void setAllReadOnly(Boolean s) {
			doctor.setReadOnly(s);
			permaType.setReadOnly(s);
			count.setReadOnly(s);
			cost.setReadOnly(s);
			user.setReadOnly(s);
		}
		
		public void addItem() {
			setHeading("Přidání nového záznamu o prodeji");
			del_bt.hide();
			save_bt.show();
			orig_id = -1;
			setAllReadOnly(false);
			resetFields();
			clearInvalid();
			show();
		}
		public void editItem(PermaItem item) {
			setHeading("Oprava záznamu o prodeji");
			del_bt.hide();
			save_bt.show();
			setAllReadOnly(false);
			orig_id = item.getId();
			orig_time = item.getCreated();
			doctor.setValue(doctor.getStore().findModel("id", item.getDoctorId()));
			if (item.getTypeId() == 0) {
				permaType.setValue(new PermaType(0,true, item.getType()));
			} else {
				permaType.setValue(permaType.getStore().findModel("id", item.getTypeId()));
			}
			count.setValue(item.getCount());
			cost.setValue(item.getCost());
			user.setValue(item.getUser());
			show();
		}
		public void deleteItem(PermaItem item) {
			setHeading("Opravdu smazat záznam o prodeji?");
			del_bt.show();
			save_bt.hide();
			setAllReadOnly(true);
			orig_id = item.getId();
			orig_time = item.getCreated();
			doctor.setValue(doctor.getStore().findModel("id", item.getDoctorId()));
			if (item.getTypeId() == 0) {
				permaType.setValue(new PermaType(0,true, item.getType()));
			} else {
				permaType.setValue(permaType.getStore().findModel("id", item.getTypeId()));
			}
			count.setValue(item.getCount());
			cost.setValue(item.getCost());
			user.setValue(item.getUser());
			show();
		}
		
		public void afterChange(){};
	}
	
	
	private static int page_size=20;
	private BasePagingLoader loader;
	private PagingToolBar p_toolBar;
	private Grid<PermaItem> grid;
	private PermaItemWnd editWnd;
	public PermaPanel() {
		setLayout(new RowLayout());
		
		editWnd = new PermaItemWnd(){
			@Override
			public void afterChange() {
				p_toolBar.refresh();
			}
		};
		RpcProxy<PagingLoadResult<PermaItem>> proxy = new RpcProxy<PagingLoadResult<PermaItem>>() {
			protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<PermaItem>> callback) {
		         clr.dataService.getPermaItems((PagingLoadConfig)loadConfig, callback);  
			}
		};
	    loader = new BasePagingLoader(proxy);
	    loader.addLoadListener(clr.auth_check_listener);
		loader.setRemoteSort(true);
		loader.setSortField("created");
		loader.setSortDir(SortDir.DESC);
//		loader.load(0, page_size);  
		ListStore<PermaItem> store = new ListStore<PermaItem>(loader);  
		
		p_toolBar = new PagingToolBar(page_size);
		p_toolBar.bind(loader);
		p_toolBar.add(new SeparatorToolItem());
		p_toolBar.add(new Button("Přidat nový prodej",new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				editWnd.addItem();
			}
		}));
/*
		p_toolBar.add(new TextToolItem("XXX",new SelectionListener<ToolBarEvent>(){
			@Override
			public void componentSelected(ToolBarEvent ce) {
				int c = clr.localService.getPermaTypesStore().getCount();
				clr.localService.getPermaTypesStore().clearFilters();
				clr.localService.getPermaTypesStore().rejectChanges();
			}
		}));
*/
		
		GridCellRenderer<PermaItem> gridDate = new GridCellRenderer<PermaItem>() {  
		       public String render(PermaItem model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<PermaItem> store, Grid<PermaItem> grid) {  
		    	   Date d = new Date(model.getCreated());
		    	   return "<span>" + DateTimeFormat.getFormat("d. M. y HH:mm").format(d) + "</span>";
		    	   //return "x";
		       }  
		};  

		GridCellRenderer<PermaItem> gridDoctor = new GridCellRenderer<PermaItem>() {  
		       public String render(PermaItem model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<PermaItem> store,Grid<PermaItem> grid) {
		    	   Doctor d = clr.localService.getDoctorsStore().findModel("id", model.getDoctorId());
		    	   String dname = "?";
		    	   if (d!=null) {
		    		   dname = d.get("fullname");
		    	   }
		    	   if (dname==null) {
		    		   dname="?";
		    	   }
		    	   return "<span>" + dname + "</span>";
		    	   //return "x";
		       }  
		};  

		GridCellRenderer<PermaItem> gridType = new GridCellRenderer<PermaItem>() {  
		       public String render(PermaItem model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<PermaItem> store,Grid<PermaItem> grid) {
		    	   String ptname = model.getType();
		    	   if (model.getTypeId()!=0) {
		    		   
		    		   PermaType pt = clr.localService.getPermaTypesStore().findModel("id", model.getTypeId());
		    		   if (pt!=null) { 
		    			   ptname = pt.get("name"); 
		    		   } 
		    	   }
		    	   if (ptname==null) {
		    		  ptname = "?";
		    	   }
		    	   return "<span>" + ptname + "</span>";
		    	   //return "x";
		       }  
		};  
		
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
//		configs.add(new ColumnConfig("doctor_id","Doktor",200));
		configs.add(Utils.createColumnConfigWithRenderer("doctor_id", "Doktor", 170, gridDoctor));
		configs.add(Utils.createColumnConfigWithRenderer("type_id", "Typ", 120, gridType));
//		configs.add(new ColumnConfig("type_id","type_id",200));
		configs.add(new ColumnConfig("user","Klient",170));
		configs.add(new ColumnConfig("count","Kolik",50));
		configs.add(new ColumnConfig("cost","Cena",50));
		configs.add(Utils.createColumnConfigWithRenderer("created", "zaevidováno", 120, gridDate));
//		configs.add(new ColumnConfig("created","created",200));
		
		for(ColumnConfig cc: configs) {
			cc.setMenuDisabled(true);
			cc.setSortable(false);
		}
		ColumnModel cm = new ColumnModel(configs);  

		grid = new Grid<PermaItem>(store, cm);
		grid.setLoadMask(true);
		grid.setBorders(true);
		//grid.setHeight(500);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		add(p_toolBar, new RowData(1,-1,new Margins(0)));
		add(grid, new RowData(1,1,new Margins(0)));
	
		
		Menu contextMenu = new Menu();
		MenuItem cm_edit = new MenuItem();
		cm_edit.setText("opravit");
		cm_edit.addSelectionListener(new SelectionListener<MenuEvent>(){
			@Override
			public void componentSelected(MenuEvent ce) {
				PermaItem pi = grid.getSelectionModel().getSelectedItem();
				if (pi!=null) {
					editWnd.editItem(pi);
				}
			}
		});
		contextMenu.add(cm_edit);

		MenuItem cm_del = new MenuItem();
		cm_del.setText("smazat");
		cm_del.addSelectionListener(new SelectionListener<MenuEvent>(){
			@Override
			public void componentSelected(MenuEvent ce) {
				PermaItem pi = grid.getSelectionModel().getSelectedItem();
				if (pi!=null) {
					editWnd.deleteItem(pi);
				}
			}
		});
		contextMenu.add(cm_del);
		grid.setContextMenu(contextMenu);
		grid.addListener(Events.ContextMenu, new Listener<GridEvent>(){
			public void handleEvent(GridEvent be) {
				if (be.getRowIndex()==-1) {
					be.cancelBubble();
				}
			}
		});
		
		
		p_toolBar.refresh();
	}
	
}
