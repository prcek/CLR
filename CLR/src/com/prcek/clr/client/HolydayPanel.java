package com.prcek.clr.client;

import java.util.Date;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
//import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
//import com.extjs.gxt.ui.client.widget.toolbar.ToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.prcek.clr.client.data.Holyday;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.MassageItem;

public class HolydayPanel extends LayoutContainer {
	
	private class HolyAddWindow extends Window {
		private DatePicker date_picker;
		public Date date;
		public String desc;
		public HolyAddWindow() {
			  //setSize(350, 350);  
			  setPlain(true);  
			  setHeading("Pridani svatku - zvolit den");  
			  setLayout(new RowLayout(Orientation.VERTICAL));
			  //setClosable(false);
			  Button add_bt = new Button("pridat");
				add_bt.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						date = date_picker.getValue();
						hide();
						afterClose();
					}
					
				});
			  date_picker = new DatePicker();
			  add(date_picker);
			  add(add_bt);
			  
		}
		public void afterClose() {
			
		}
	};
	//private HolyAddWindow addWnd;
	private SimpleComboBox<Integer> year_cb;
	private ListLoader loader;
	private ListView<Holyday> holyday_list;
	private Integer current_year = 2007;
	private DateField date_field;
	private TextField<String> holy_name;
	private ToolBar toolbar;
	public HolydayPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		toolbar = new ToolBar();
		
		toolbar.add(new LabelToolItem("Svátky roku:"));
		year_cb = new SimpleComboBox<Integer>();
		
		Date now = new Date();
		int now_year = now.getYear()+1900;
		for(int y=now_year-2;y<now_year+10; y++) {
			year_cb.add(y);
		}
		year_cb.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<Integer>>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<Integer>> se) {
				show_year(se.getSelectedItem().getValue());
			}
			
		});
		year_cb.setEditable(false);
		toolbar.add(year_cb);

		Button reload_bt = new Button();
		reload_bt.setIcon(AbstractImagePrototype.create(clr.IMAGES.refresh()));
		reload_bt.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				loader.load();
			}
		});
		toolbar.add(reload_bt);

		toolbar.add(new FillToolItem());
		
		
		add(toolbar,new RowData(1,-1,new Margins(0)));
	
/*		
		addWnd = new HolyAddWindow() { public void afterClose(){
			if (addWnd.date!=null) {
				final Holyday h = new Holyday(addWnd.date.getTime(),addWnd.desc,"");
				clr.dataService.insertHolyday(h, new AsyncCallback<Boolean>(){

					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					public void onSuccess(Boolean result) {
						// TODO Auto-generated method stub
						select_year(h.getYear());
					}
					
				});
			}
		}};
*/		
		
		LayoutContainer vp = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));

		LayoutContainer list_panel = new LayoutContainer(new RowLayout(Orientation.VERTICAL));
		vp.add(list_panel,new RowData(.5,1,new Margins(0)));
		
		
		holyday_list = new ListView<Holyday>();
		holyday_list.setLoadingText("Nahrávám...");
		list_panel.add(holyday_list,new RowData(1,1,new Margins(0)));
		
		RpcProxy proxy = new RpcProxy() {
			protected void load(Object loadConfig, AsyncCallback callback) {
				clr.dataService.getHolydays(current_year, callback);	
			}
		};  
		ModelReader reader = new ModelReader();
		loader = new BaseListLoader(proxy,reader);
		loader.addLoadListener(clr.auth_check_listener);
		loader.addLoadListener(new LoadListener(){
			@Override
			public void loaderLoad(LoadEvent le) {
				// TODO Auto-generated method stub
				super.loaderLoad(le);
				if (date_field.getValue()!=null) {
					try_select(date_field.getValue().getTime());
				} else {
					try_select(0);
				}
			}
		});
		ListStore<Holyday> store = new ListStore<Holyday>(loader);
		holyday_list.setSimpleTemplate("{date_s} <i>{name}</i>");
		holyday_list.setStore(store);
		holyday_list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		holyday_list.addListener(Events.Select, new Listener<ListViewEvent>(){
			public void handleEvent(ListViewEvent be) {
				// TODO Auto-generated method stub
	            Holyday h = (Holyday) be.getListView().getStore().getAt(be.getIndex());
	            if (h!=null) {
	            	date_field.setValue(new Date(h.getDateAsLong()));
	            	holy_name.setValue(h.getDesc());
	            	//h.getDateAsLong();
	            	//h.getDesc();
	            }
			}
		});
		
		LayoutContainer cmd_panel = new LayoutContainer(new FitLayout());
		vp.add(cmd_panel,new RowData(.5,1,new Margins(0)));

		
		
		FormPanel cmd_form = new FormPanel();
		cmd_form.setFrame(false);
		cmd_form.setHeaderVisible(false);
		FormLayout cmd_form_l = new FormLayout();
		cmd_form_l.setLabelAlign(LabelAlign.TOP);
		cmd_form.setLayout(cmd_form_l);
		//cmd_form.setLabelWidth(75);
		//cmd_form.setLabelAlign(LabelAlign.TOP);
		cmd_form.setButtonAlign(HorizontalAlignment.CENTER);  

		
		date_field = new DateField();
		date_field.setFieldLabel("Den");
		date_field.setEditable(false);
		DateWrapper dw = new DateWrapper(new Date());
		date_field.setValue(dw.clearTime().asDate());

		cmd_form.add(date_field);
		
		holy_name = new TextField<String>();
		holy_name.setFieldLabel("Název");
		
		cmd_form.add(holy_name);
		
		
		Button del_day = new Button("Odebrat");
		del_day.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				Date d = date_field.getValue();
				clr.dataService.deleteHolyday(d.getTime(), new AsyncCallback<Boolean>(){
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
						}

						public void onSuccess(Boolean result) {
							// TODO Auto-generated method stub
							loader.load();
						}
				});
					 
				
			}
			
		});
		
		Button add_day = new Button("Přidat");
		add_day.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				Date d = date_field.getValue();
				String n = holy_name.getValue();
				if (n==null) n="";
				Holyday h = new Holyday(d.getTime(),n,"");
				clr.dataService.insertHolyday(h, new AsyncCallback<Boolean>(){
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
					public void onSuccess(Boolean result) {
						// TODO Auto-generated method stub
						loader.load();
					}
				});
			}
			
		});

		Button close_panel = new Button("Zavřít");
		close_panel.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				onClose();
			}
			
		});
		

		cmd_form.addButton(del_day);
		cmd_form.addButton(add_day);
		cmd_form.addButton(close_panel);
		
		cmd_panel.add(cmd_form);
		
		add(vp, new RowData(1,1,new Margins(0)));
		select_current_year();
	}
	
	public void addToToolbar(Component item) {
		toolbar.add(item);
	}
	
	public int getYear() {
		return current_year;
	}
	
	private void try_select(long d) {
		
		return;
/*		
		if (d==0) {
			if (holyday_list.getStore().getCount()>0) {
				holyday_list.getSelectionModel().select(0);
			}
		} else {
			Holyday h= holyday_list.getStore().findModel("date", d);
			if (h!=null) {
				holyday_list.getSelectionModel().select(h);
			} else {
				if (holyday_list.getStore().getCount()>0) {
					holyday_list.getSelectionModel().select(0);
				}
			}
		}
*/		
		//TODO
		//holyday_list.getStore().findModel(property, value)
	}

	public void show_year(Integer year) {
	//	MessageBox.info("year", String.valueOf(year),null);
		if (year != null) {
			current_year = year; 
		} else {
			current_year = 0;
		}
		loader.load();
	}
	public void select_year(Integer year) {
		SimpleComboValue<Integer> val = year_cb.findModel(year);
		if (val != null) {
			year_cb.setValue(val);
		}
	}
	public void select_current_year() {
		Date d = new Date();
		select_year(d.getYear()+1900);
	}
	
	public void onClose() {
		
	}
}
