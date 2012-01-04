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
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
//import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.prcek.clr.client.MassageCalendar.CalendarDayEvent;
import com.prcek.clr.client.data.Holyday;
import com.prcek.clr.client.data.MassageCalDayInfo;
import com.prcek.clr.client.data.MassageItem;
import com.prcek.clr.client.data.MassageType;
import com.prcek.clr.client.data.Member;
import com.prcek.clr.client.data.Repository;
import com.prcek.clr.client.ui.MonthField;
import com.prcek.clr.client.ui.SimpleGrid;

public class MassagePanel extends LayoutContainer {
	//private MassageCalendar massage_cal;
	private class ItemEditWnd extends Window {
		private FormPanel form;
		private MemberCombo member_combo;
		private TextField<String> phone;
		private TextField<String> surname;
		private String last_phone;
		private String last_surname;
		private ComboBox<MassageType> mtype;
		private TextField<String> mtime;
		private TextField<String> mdesc;
		private Button save_bt;
		private Button cancel_bt;
		private Button delete_bt;
		private Button use_bt;
		private Button last_bt;
		private MassageItem input_mi;
		private Boolean create_mode;
		
		private FieldSet actionSet;
		private FieldSet userSet;
		private FieldSet evSet;
		private FormData formData;
		private FormLayout actionLayout;
		private FormLayout userLayout;
		private FormLayout evLayout;
		
		public ItemEditWnd() {
			  setSize(350, 400);  
			  setPlain(true);  
			  setHeading("Masáž");
			  setModal(true);
			  setLayout(new FitLayout());
			  setResizable(clr.allowResizableWnd);
			  setModal(clr.allowModalWnd);
		
			  formData = new FormData("-20");
			  actionLayout = new FormLayout();  
			  actionLayout.setLabelWidth(75);
			  userLayout = new FormLayout();  
			  userLayout.setLabelWidth(75);
			  evLayout = new FormLayout();  
			  evLayout.setHideLabels(true);

			  
			  form = new FormPanel();
			  form.setHeaderVisible(false);
			  form.setLayout(new FlowLayout());

			  actionSet = new FieldSet();  
			  actionSet.setHeading("Masáž");
			  actionSet.setLayout(actionLayout);
		
			  userSet = new FieldSet();  
			  userSet.setHeading("Klient");  
			  userSet.setLayout(userLayout);

			  evSet = new FieldSet();  
			  evSet.setHeading("Z evidence/předchozí klient");  
			  evSet.setLayout(evLayout);
			  
			  
			  member_combo = new MemberCombo();
			  member_combo.setName("member");
			  member_combo.setFieldLabel("klient");
			  
			  evSet.add(member_combo,formData);
			 
			  use_bt = new Button("Použít z evidence", new SelectionListener<ButtonEvent>(){
					public void componentSelected(ButtonEvent ce) {
						if (member_combo.getValue()!=null) {
							surname.setValue(member_combo.getValue().getSurname());
							phone.setValue(member_combo.getValue().getPhone());
						}
					}
				  });
			  evSet.add(use_bt);			  
			  
			  last_bt = new Button("Vyplnit předchozí", new SelectionListener<ButtonEvent>(){
					public void componentSelected(ButtonEvent ce) {
							surname.setValue(last_surname);
							phone.setValue(last_phone);
					}
				  });
			  evSet.add(last_bt);			  
	
			  surname = new TextField<String>();
			  surname.setName("surname");
			  surname.setFieldLabel("Přijmení");
			  surname.setMaxLength(clr.const_max_massage_username_field);
			  surname.addListener(Events.KeyPress, new Listener<FieldEvent>(){
					public void handleEvent(FieldEvent be) {
						Utils.upcaseField(be.getField());
					}
				  });
			  userSet.add(surname,formData);

			  phone = new TextField<String>();
			  phone.setName("phone");
			  phone.setFieldLabel("Telefon");
			  phone.setMaxLength(clr.const_max_massage_phone_field);
			  userSet.add(phone,formData);
			  

			  			  
			  
			  
			  mtime = new TextField<String>();
			  mtime.setName("time");
			  mtime.setFieldLabel("termín");
			  mtime.setReadOnly(true);
			  actionSet.add(mtime,formData);
			  
			  
			  mtype = new ComboBox<MassageType>();
			  mtype.setStore(Repository.getMassageTypesStore());
			  mtype.setId("id");
			  mtype.setEditable(false);
			  mtype.setDisplayField("name");
			  mtype.setFieldLabel("druh");
			  mtype.setValue(mtype.getStore().findModel("id", 0));
			  mtype.setTriggerAction(TriggerAction.ALL);
			  mtype.setName("type");

			  actionSet.add(mtype,formData);
			  
			  mdesc = new TextField<String>();
			  mdesc.setName("desc");
			  mdesc.setFieldLabel("Poznámka");
			  mdesc.setMaxLength(clr.const_max_massage_desc_field);
			  actionSet.add(mdesc,formData);
			  
			  form.add(actionSet);
			  form.add(userSet);
			  form.add(evSet);
			  
			  form.setButtonAlign(HorizontalAlignment.CENTER);
			  save_bt = new Button("Uložit", new SelectionListener<ButtonEvent>(){
					public void componentSelected(ButtonEvent ce) {
						Boolean r;
						if (create_mode) {
							r = onSaveNew();
						} else {
							r = onSaveUpdate();
						}
						if (r) hide();
					}
				  });
			  form.addButton(save_bt);

			  delete_bt = new Button("Smazat", new SelectionListener<ButtonEvent>(){
					public void componentSelected(ButtonEvent ce) {
						if (create_mode) {
						} else {
							onDelete();
						}
						hide();
					}
				  });
			  form.addButton(delete_bt);
			  
			  
			  cancel_bt = new Button("Storno", new SelectionListener<ButtonEvent>(){
					public void componentSelected(ButtonEvent ce) {
						hide();
						afterChange();
					}
			  });
			  form.addButton(cancel_bt);
			  
			  add(form);
		}
		public void showError(String x) {
			MessageBox.alert("chyba", x, null);
		}
		
		public void storeLast() {
			last_phone = phone.getValue();
			last_surname = surname.getValue();
		}
		
		public Boolean onDelete() {
			storeLast();
			clr.dataService.deleteMassageItem(input_mi.getMassageID(), new AsyncCallback<String>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(String result) {
					if (result!=null) {
						MessageBox.alert("Chyba mazání rezervace masáže", result,null);
					}
					afterChange();
				}
			});
			return Boolean.TRUE;
		}
		
		public Boolean onSaveUpdate() {
			storeLast();
			long stime = input_mi.getAsLong("start");
			MassageType mt = mtype.getValue();
			if ((mt==null) || (mt.getId()==0)) {
				showError("není zvolen typ masáže");
				return Boolean.FALSE;
			}
            long etime = stime + 60*1000*30*mt.getSlots();
            int old_type_id = input_mi.getType();
            MassageType old_mt = mtype.getStore().findModel("id", old_type_id);
            if (old_mt==null) {
				showError("Interní chyba, nelze zjistit původní typ masáže");
				return Boolean.FALSE;
            }
            if (old_mt.getSlots()<mt.getSlots()) {
            	if (!input_mi.isPossible(mt.getSlots())) {
            		int tlen = mt.getSlots()*30;
            		if (mt.getSlots()>2) { 
            			showError("Masáž trvá "+tlen+" minut a následující časovky nejsou volné");
            		} else {
            			showError("Masáž trvá "+tlen+" minut a následující časovka není volná");
            		}
    				return Boolean.FALSE;
            	}
            }
            
            if (mt.isLava()) {
            	if (input_mi.isFirst()) {
    				showError("Masáž kameny nemůže být v první časovce");
    				return Boolean.FALSE;
            	}
            }
            
			MassageItem new_mi = new MassageItem(stime,etime);
//			new_mi.setReservation(member_combo.getValue().getId(),member_combo.getValue().getSurname(), member_combo.getValue().getPhone(), mt.getId(),true,input_mi.getMassageID());
			new_mi.setReservation(surname.getValue(), phone.getValue(), mdesc.getValue(), mt.getId(),true,input_mi.getMassageID());
			clr.dataService.insertMassageItem(new_mi, new AsyncCallback<String>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(String result) {
					if (result!=null) {
						MessageBox.alert("Chyba ukládání rezervace masáže", result,null);
					}
					afterChange();
				}
			});
            
			return Boolean.TRUE;
		}
		public Boolean onSaveNew() {
			storeLast();
			long stime = input_mi.getAsLong("start");
			MassageType mt = mtype.getValue();
			if ((mt==null) || (mt.getId()==0)) {
				showError("není zvolen typ masáže");
				return Boolean.FALSE;
			}
            long etime = stime + 60*1000*30*mt.getSlots();
            if (mt.getSlots()>=2) {
            	if (!input_mi.isPossible(mt.getSlots())) {
            		int tlen = mt.getSlots()*30;
            		if (mt.getSlots()>2) { 
            			showError("Masáž trvá "+tlen+" minut a následující časovky nejsou volné");
            		} else {
            			showError("Masáž trvá "+tlen+" minut a následující časovka není volná");
            		}
    				return Boolean.FALSE;
            	}
            }
            if (mt.isLava()) {
            	if (input_mi.isFirst()) {
    				showError("Masáž kameny nemůže být v první časovce");
    				return Boolean.FALSE;
            	}
            }
            
			MassageItem new_mi = new MassageItem(stime,etime);
			new_mi.setReservation(surname.getValue(), phone.getValue(), mdesc.getValue(), mt.getId(),true,-1);
			clr.dataService.insertMassageItem(new_mi, new AsyncCallback<String>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(String result) {
					if (result!=null) {
						MessageBox.alert("Chyba ukládání rezervace masáže", result,null);
					}
					afterChange();
				}
			});
			return Boolean.TRUE;
		}
		public void editItem(MassageItem mi) {
			member_combo.clear();
			input_mi = mi;
			if (mi.getMassageID()==-1) {
				create_mode=Boolean.TRUE;
				delete_bt.hide();
			} else {
				create_mode=Boolean.FALSE;
				delete_bt.show();
			}
			
			if (create_mode) {
				Date sdate = new Date(mi.getAsLong("start"));
				String s = DateTimeFormat.getFormat("HH:mm , E d.M.y").format(sdate);
				setHeading("Rezervace nové masáze");
				phone.setValue("");
				surname.setValue("");
				mtime.setValue(s);
				mtype.setValue(mtype.getStore().findModel("id", 0));
				mdesc.setValue("");
			} else {
				Date sdate = new Date(mi.getAsLong("real_start"));
				Date edate = new Date(mi.getAsLong("real_end"));
				String s = DateTimeFormat.getFormat("HH:mm - ").format(sdate) + DateTimeFormat.getFormat("HH:mm , E d.M.y").format(edate);
				setHeading("Změna rezervace masáže");
				//TODO
				//member_combo.setValue(mi.getMassageID());
				phone.setValue((String)mi.get("phone"));
				surname.setValue((String)mi.get("surname"));
				mtime.setValue(s);
				mtype.setValue(mtype.getStore().findModel("id", mi.getType()));
				mdesc.setValue((String)mi.get("desc"));
			}
			show();
		}
		public void afterChange() {}
		
	}
	
	private class DayPlanPanel extends ContentPanel {
		private ListLoader loader;
		private ListView<MassageItem> item_list;
		private Date day;
		private ItemEditWnd editWnd;
		private Date last_load_time;
		private Date last_server_time;
		public DayPlanPanel(Boolean wide_mode) {
			setHeaderVisible(false);
			setBorders(false);
			//setHeading("plán dne");
			setLayout(new FitLayout());
		
			last_load_time = new Date(0);
			
			editWnd = new ItemEditWnd() {
				public void afterChange() {
					reloadDay();
				}
			};
			
			item_list = new ListView<MassageItem>(){
				protected MassageItem prepareData(MassageItem model) {
					Date stime = new Date(model.getAsLong("start"));
					Date etime = new Date(model.getAsLong("end"));
					
					String t = DateTimeFormat.getFormat("HH:mm").format(stime); 
					model.set("time_s", t);
					if (model.isEmpty()) {
						model.set("time_c", "#afa");
						model.set("action", "");
						model.set("desc_t", "");
					} else {
						model.set("time_c", "#faa");
						MassageType mt = Repository.getMassageTypesStore().findModel("id", model.getType());
						String mname = mt.get("name");
						model.set("action", mname);
						String desc_t = (String) model.get("desc");
						if (desc_t!=null) {
							desc_t = "&nbsp<span style='background-color:#b0c4de;'>" + desc_t + "</span>";   
						} else {
							desc_t = "";
						}
						model.set("desc_t", desc_t);
					}
					
					return model;
				}
			};
			item_list.setBorders(true);
			
			//item_list.setHeight(520);
		//	if (wide_mode) {
		//		item_list.setWidth(320);
		//	} else {
		//		item_list.setWidth(170);
		//	}
			RpcProxy proxy = new RpcProxy() {
				protected void load(Object loadConfig, AsyncCallback callback) {
					clr.dataService.getMassageItems(day.getTime(), callback);	
				}
			};  
			ModelReader reader = new ModelReader();
			loader = new BaseListLoader(proxy,reader);
			loader.addLoadListener(clr.auth_check_listener);
			ListStore<MassageItem> store = new ListStore<MassageItem>(loader);
			//item_list.setSimpleTemplate("<i>{text}</i>");
			
			if (wide_mode) {
				item_list.setTemplate(
						"<tpl for=\".\">" +
						"<div class=x-view-item>" + 
							"<div class='massage_item_date_w' style='background-color:{time_c};'>{time_s}{debug}</div>" +
							"<div class='massage_item_info_w'>" +
								"<tpl if=\"primary == 1\">" +
									"<b>&nbsp {surname} &nbsp {phone}</b><br />&nbsp {action}{desc_t}"+
								"</tpl>" +
								"<tpl if=\"primary == 0\">" +
									"<i>&nbsp pokračování</i><br />&nbsp {action}"+
								"</tpl>" +
							"</div>" +	
						"</div></tpl>"
				);
			} else {
				item_list.setTemplate(
						"<tpl for=\".\">" +
						"<div class=x-view-item>" + 
							"<div class='massage_item_date' style='background-color:{time_c};'>{time_s}{debug}</div>" +
							"<div class='massage_item_info'>" +
								"<tpl if=\"primary == 1\">" +
									"<b>&nbsp {surname}<br />&nbsp {phone}</b><br />&nbsp {action}{desc_t}"+
								"</tpl>" +
								"<tpl if=\"primary == 0\">" +
									"<i>&nbsp pokračování</i><br />&nbsp"+
								"</tpl>" +
							"</div>" +	
						"</div></tpl>"
				);
			}
			item_list.setStore(store);
			item_list.setItemSelector("div.x-view-item");
			item_list.addListener(Events.Select, new Listener<ListViewEvent>(){
				public void handleEvent(ListViewEvent be) {
		            MassageItem mi = (MassageItem) be.getListView().getStore().getAt(be.getIndex());
		            if (mi.isPrimary()) {
		            	onSelect(mi);
		            } else if (be.getIndex()>0) {
		            	mi = (MassageItem) be.getListView().getStore().getAt(be.getIndex()-1);
		            	if (mi.isPrimary()) {
		            		onSelect(mi);
		            	}
		            }
				}
				
			});
			item_list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			item_list.setLoadingText("nahrávám...");
			add(item_list);
		}

		public void onSelect(MassageItem mi) {
			editWnd.editItem(mi);
			item_list.getSelectionModel().deselectAll();
		}
		public Date getDate() {
			return day;
		}
		public void showDate(Date d) {
			day = d;
			last_load_time = new Date();
			loader.load();
		}
		public void reloadDay() {
			showDate(day);
		}
		public void serverDataTime(Date t) {
			if (last_server_time==null) {
				last_server_time = t;
				return;
			}
			if (last_server_time.before(t)) {
				Date now = new Date();
				long now_s = now.getTime();
				long lt = last_load_time.getTime();
				if (now_s>(lt+1000*5)) {
					reloadDay();
				}
			}
			last_server_time = t;
		}
	}
	//private List<DayPlanPanel> days;
	private ContentPanel plan_hp;
	private DayPlanPanel day_panel;
	private DateField current_day_dp;
	private DateField report_from_dp;
	//private MonthField current_month_dp;
	private ListLoader info_loader;
	private long start_cal_date;
	private static int display_rows = 12;
	private DataMonitor dataMonitor;
	private Window plan_edit_wnd;
	private Window holy_edit_wnd;
	private MassagePlanForm plan_edit_form;
	private HolydayPanel holy_panel_form;
	//private DateField from_date_df;
	public MassagePanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		//setLayout(new FitLayout());
		
		ToolBar toolbar = new ToolBar();

		toolbar.add(new LabelToolItem("Přehled od:"));
		
		
		Button prev_bt = new Button();
		prev_bt.setIconStyle("x-tbar-page-prev");
		prev_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				DateWrapper d = new DateWrapper(report_from_dp.getValue());
				report_from_dp.setValue(d.addDays(-(7*(display_rows-1))).asDate());
			}
		});
		toolbar.add(prev_bt);
		
		report_from_dp = new DateField() {
			@Override
			public void setValue(Date value) {
				// TODO Auto-generated method stub
				super.setValue(value);
				start_cal_date = value.getTime();
				info_loader.load();
			}
			
		};
		report_from_dp.setAllowBlank(false);
		report_from_dp.setEditable(false);

		toolbar.add(report_from_dp);
		
		Button next_bt = new Button();
		next_bt.setIconStyle("x-tbar-page-next");
		next_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				DateWrapper d = new DateWrapper(report_from_dp.getValue());
				report_from_dp.setValue(d.addDays(7*(display_rows-1)).asDate());
			}
		});
		toolbar.add(next_bt);
		
		Button reload_bt = new Button();
		//reload_bt.setIconStyle("x-tbar-loading");
		reload_bt.setIcon(AbstractImagePrototype.create(clr.IMAGES.refresh()));
		reload_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				info_loader.load();
			}
		});
		toolbar.add(reload_bt);
		
		toolbar.add(new FillToolItem());
		toolbar.add(new LabelToolItem("Zobrazený den:"));

		current_day_dp = new DateField(){
			@Override
			public void setValue(Date value) {
				super.setValue(value);
				day_panel.showDate(value);
			}
			
		};
		current_day_dp.setAllowBlank(false);
		current_day_dp.setEditable(false);
		toolbar.add(current_day_dp);
		toolbar.add(new Button("Tisk", new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				com.google.gwt.user.client.Window.open(clr.printUrl+"?action=print_massage_day&massage_date="+String.valueOf(current_day_dp.getValue().getTime()), "_blank", "");
			}
		}));

		plan_edit_form =new MassagePlanForm(){
			@Override
			public void afterCancel() {
				plan_edit_wnd.hide();
			}
			@Override
			public void afterSave() {
				plan_edit_wnd.hide();
				reload();
			}
		};
		plan_edit_wnd = new Window();
		plan_edit_wnd.setSize(500, 450);
		plan_edit_wnd.setPlain(true);
		plan_edit_wnd.setHeading("Plánování časovek masáží");
		plan_edit_wnd.setLayout(new FitLayout());
		plan_edit_wnd.setResizable(clr.allowResizableWnd);
		plan_edit_wnd.setModal(clr.allowModalWnd);
		
		plan_edit_wnd.add(plan_edit_form);

		holy_edit_wnd = new Window();
		holy_edit_wnd.setSize(500, 400);
		holy_edit_wnd.setPlain(true);
		holy_edit_wnd.setHeading("Editování svátků");
		holy_edit_wnd.setLayout(new FitLayout());
		holy_edit_wnd.setResizable(clr.allowResizableWnd);
		holy_edit_wnd.setModal(clr.allowModalWnd);
		
		holy_panel_form = new HolydayPanel(){
			@Override
			public void onClose() {
				holy_edit_wnd.hide();
			}
		};
		

		Button check_plan_vs_holyday = new Button("Zrušit plán v době svátků");
		//check_plan_vs_holyday.setIconStyle("x-tbar-loading");
		check_plan_vs_holyday.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				//loader.load();
				int y = holy_panel_form.getYear();
				clr.dataService.deleteHolydayPlan(y, new AsyncCallback<Boolean>(){

					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
						
					}

					public void onSuccess(Boolean arg0) {
						// TODO Auto-generated method stub
						MessageBox.info("Hotovo","plán v době svátků odebrán",null);
					}
					
				});
			}
		});
		
		holy_panel_form.addToToolbar(check_plan_vs_holyday);
		holy_edit_wnd.add(holy_panel_form);
		
		Button plan_menu_item = new Button("Plánování");
		
		Menu plan_menu = new Menu();  
		MenuItem menuItem_day_plan = new MenuItem("Provozní doba",new SelectionListener<MenuEvent>(){
			public void componentSelected(MenuEvent ce) {
				plan_edit_form.editDate(current_day_dp.getValue());
				plan_edit_wnd.show();
			}
		});  
		plan_menu.add(menuItem_day_plan);

		MenuItem menuItem_holy_plan = new MenuItem("Svátky",new SelectionListener<MenuEvent>(){
			public void componentSelected(MenuEvent ce) {
				holy_edit_wnd.show();
				//MessageBox.info("Není funkční","editace svátků není zatím dostupná",null);
			}
		});    
		plan_menu.add(menuItem_holy_plan);
		
		plan_menu_item.setMenu(plan_menu);
		//item1.setIconStyle("icon-menu-show");  
		
		toolbar.add(plan_menu_item);
/*		
		toolbar.add(new TextToolItem("Plánování", new SelectionListener<ToolBarEvent>(){
			public void componentSelected(ToolBarEvent ce) {
				plan_edit_form.editDate(current_day_dp.getValue());
				plan_edit_wnd.show();
			}
		}));
*/		
		
		add(toolbar,new RowData(1,-1,new Margins(0)));
		
		
		
		
		RpcProxy proxy = new RpcProxy() {
			protected void load(Object loadConfig, AsyncCallback callback) {
				List<DateWrapper> days = new ArrayList<DateWrapper>();
				DateWrapper d = new DateWrapper(start_cal_date);
				d=d.clearTime();
				while(d.getDay()!=1) {
					d=d.addDays(-1);
				}
				for(int i=0; i<(display_rows*7); i++) {
					if ((d.getDay()>0) && (d.getDay()<6)) {
						days.add(d);
					}
					d=d.addDays(1);
				}
				long[] dd = new long[days.size()];
				for(int i=0; i<days.size(); i++) {
					dd[i] = days.get(i).getTime();
				}
				clr.dataService.getMassageCalDayInfo(dd, callback);
			}
		};
		
		dataMonitor = new DataMonitor(){
			@Override
			public void doReload() {
				info_loader.load();
			}
		};
		
		
		ModelReader reader = new ModelReader();
		info_loader = new BaseListLoader(proxy,reader);
		info_loader.addLoadListener(clr.auth_check_listener);
		info_loader.addLoadListener(dataMonitor);
		ListStore<MassageCalDayInfo> info_store = new ListStore<MassageCalDayInfo>(info_loader);
		
		String[] days = {"PO","ÚT","ST","ČT","PÁ"};
		SimpleGrid<MassageCalDayInfo> test_grid = new SimpleGrid<MassageCalDayInfo>(display_rows,5,days){
			@Override
			protected MassageCalDayInfo prepareData(MassageCalDayInfo model) {
				// TODO Auto-generated method stub
				String s1 = DateTimeFormat.getFormat("d.").format(new Date(model.getDate()));
				String s2 = DateTimeFormat.getFormat("MMMM").format(new Date(model.getDate()));
				switch(model.getStatus()) {
					case 0: //empty
							model.set("bg_color", "#aaa");
							break;
					case 1: //full
							model.set("bg_color", "#faa");
							break;
					case 2: //free;
							model.set("bg_color", "#afa");
							break;
					default:
							model.set("bg_color", "#fff");
				}
				model.set("date_string", s1+"<br />"+s2);
				return super.prepareData(model);
			}

			@Override
			public void onDataSelect(MassageCalDayInfo model) {
				if (model!=null) {
					current_day_dp.setValue(new Date(model.getDate()));
				}
			}
			
			
		};
		test_grid.setTemplate("<div style='background-color:{bg_color};'>{date_string}</div>");
		test_grid.setStore(info_store);

		
		plan_hp = new ContentPanel();
		plan_hp.setHeaderVisible(false);
	//	plan_hp.setSize(500, 500);
	//	plan_hp.setHeight("100%");
		plan_hp.setBorders(false);
		plan_hp.setLayout(new RowLayout(Orientation.HORIZONTAL));
		//plan_hp.add(massage_cal);
		//plan_hp.add(test_grid);
		//DatePicker dptest = new DatePicker();
		RowData data = new RowData(.5,1);
		data.setMargins(new Margins(0));

		
		
		plan_hp.add(test_grid,data);
		day_panel = new DayPlanPanel(true);
		//day_panel.setWidth("100%");
		plan_hp.add(day_panel,data);
		
		add(plan_hp,new RowData(1,1,new Margins(0)));
		
		//add(test_grid);
		report_from_dp.setValue(new Date());
		current_day_dp.setValue(new Date());

		
		
	}
	
	public void reload() {
		day_panel.reloadDay();
		info_loader.load();
	}
	
	public void serverDataTime(Date t) {
		day_panel.serverDataTime(t);
		dataMonitor.onServerTime(t);
		//massage_cal.serverDataTime(t);
	}
}
