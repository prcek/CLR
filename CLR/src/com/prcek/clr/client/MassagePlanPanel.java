package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.DayTime;
import com.prcek.clr.client.data.Holyday;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.MassageDay;
import com.prcek.clr.client.data.MassageSlot;
import com.prcek.clr.client.data.Repository;
import com.prcek.clr.client.data.WeekDay;

public class MassagePlanPanel extends LayoutContainer {
	
	private class DayPlanPanel extends HorizontalPanel {
		
		private ComboBox<WeekDay> c;
		private ComboBox<DayTime> dt_f;
		private ComboBox<DayTime> dt_t;
		private ComboBox<WeekDay> createWeekDayComboBox() {
			ComboBox<WeekDay> c = new ComboBox<WeekDay>();
			c.setStore(Repository.getWeekDaysStore());
			c.setId("id");
			c.setDisplayField("long");
			c.setEditable(false);
			return c;
		}
		
		private ComboBox<DayTime> createDayTimeComboBox() {
			ComboBox<DayTime> c = new ComboBox<DayTime>();
			c.setStore(Repository.getDayTimesStore());
			c.setId("time");
			c.setDisplayField("name");
			c.setEditable(false);
			return c;
		}
		
		public DayPlanPanel() {
			c = createWeekDayComboBox();
			add(c);
			
			dt_f = createDayTimeComboBox();
			add(dt_f);

			dt_t = createDayTimeComboBox();
			add(dt_t);
			
			Button remove_bt = new Button("odebrat");
			remove_bt.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					want_close();
				}
				
			});
			add(remove_bt);
		}
		public Boolean isValid() {
			WeekDay wd = c.getValue();
			
			if (wd == null) { return Boolean.FALSE; }
			if (!wd.isValid()) { return Boolean.FALSE;}
			
			DayTime t = dt_f.getValue();
			if (t == null) { return Boolean.FALSE; }
			if (!t.isValid()) { return Boolean.FALSE;}
			
			t = dt_t.getValue();
			if (t == null) { return Boolean.FALSE; }
			if (!t.isValid()) { return Boolean.FALSE;}
			
			return Boolean.TRUE;
		}
		public Boolean isDay(int d) {
			WeekDay wd = c.getValue();
			if (wd == null) { return Boolean.FALSE; }
			Integer i = wd.get("id");
			if (i==d) {
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		public DayTime getFrom() {
			return dt_f.getValue();
		}

		public DayTime getTo() {
			return dt_t.getValue();
		}
		
		public void want_close() {
			
		}
	};
	
	private DatePicker from_picker;
	private List<DayPlanPanel> dayPlanPanels;
	private ContentPanel setup_panel;
	private DateField from_df;
	private DateField to_df;
	private long from_date;
	private ListLoader loader;
	private Grid<MassageDay> grid;

	private void removeDayPlanPanel(DayPlanPanel p) {
		setup_panel.remove(p);
		dayPlanPanels.remove(p);
	}
	
	private void addNewDayPlanPanel() {
		DayPlanPanel p = new DayPlanPanel() {
			@Override
			public void want_close() {
				removeDayPlanPanel(this);
			}
		}; 
		dayPlanPanels.add(p);
		setup_panel.add(p);
		setup_panel.layout();
	}
	
	public MassagePlanPanel() {
		setLayout(new RowLayout(Orientation.VERTICAL));
		
		ContentPanel view_panel = new ContentPanel();
		view_panel.setHeading("prehled provoznich dnu masazi od zvoleneho datumu");
		view_panel.setLayout(new RowLayout(Orientation.VERTICAL));
		
		HorizontalPanel view_hp = new HorizontalPanel(); 
		view_panel.add(view_hp);
		
		from_picker = new DatePicker();
		view_hp.add(from_picker);
		from_picker.addListener(Events.Select, new Listener<ComponentEvent>() {  
		       public void handleEvent(ComponentEvent be) {  
		    	   showPlanFrom(from_picker.getValue());
		       }  
	    }); 
		
// slot gird
		GridCellRenderer<MassageDay> gridDate = new GridCellRenderer<MassageDay>() {  
		       public String render(MassageDay model, String property, ColumnData config, int rowIndex,   
		           int colIndex, ListStore<MassageDay> store, Grid<MassageDay> grid) {  
		    	   Date d = new Date(model.getDate());
		    	   return "<span>" + DateTimeFormat.getFormat("d. M. y").format(d) + "</span>";
		       }  
		};  

		GridCellRenderer<MassageDay> gridSlots = new GridCellRenderer<MassageDay>() {  
	       public String render(MassageDay model, String property, ColumnData config, int rowIndex,  
	           int colIndex, ListStore<MassageDay> store, Grid<MassageDay> grid) {
	    	   String x=null;
	    	   for(MassageSlot ms : model.slots) {
	    		   if (x==null) {
	    			   x="";
	    		   } else {
	    			   x=x+", ";
	    		   }
	    		   Date s_d = new Date(ms.getStart());
	    		   Date e_d = new Date(ms.getEnd());
	    		   x=x+DateTimeFormat.getFormat("HH:mm").format(s_d)+" - "+DateTimeFormat.getFormat("HH:mm").format(e_d);
	    	   }
	    	   return "<span>" + x + "</span>";
	    	   //return "x";
	       }  
		};  
		
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();  
		column.setId("date");  
		column.setHeader("Datum");  
		column.setWidth(120);
		column.setRenderer(gridDate);
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setId("slots");  
		column.setHeader("Casovky");  
		column.setWidth(50);  
		column.setRenderer(gridSlots);
		configs.add(column);
	
		for(ColumnConfig cc: configs) {
			cc.setMenuDisabled(true);
			cc.setSortable(false);
		}

		Date now_date = new Date();
		from_date = now_date.getTime();
		RpcProxy proxy = new RpcProxy() {
			protected void load(Object loadConfig, AsyncCallback callback) {
				clr.dataService.getMassageDays(from_date, callback);	
			}
		};  
		ModelReader reader = new ModelReader();
		loader = new BaseListLoader(proxy,reader);
		loader.addLoadListener(clr.auth_check_listener);
		ListStore<MassageDay> store = new ListStore<MassageDay>(loader);  
		ColumnModel cm = new ColumnModel(configs);  
		
		grid = new Grid<MassageDay>(store, cm);  
		grid.setStyleAttribute("borderTop", "none"); 
		grid.setAutoExpandColumn("slots");  
		grid.setBorders(true);
		grid.setHeight(186);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		view_hp.add(grid);
		
// edit panel		
		setup_panel = new ContentPanel();
		setup_panel.setHeading("pridavani a editace provoznich dnu masazi");
		setup_panel.setLayout(new RowLayout(Orientation.VERTICAL));
		
		
		from_df = new DateField();
		//from_df.setFieldLabel("od");
		setup_panel.add(from_df);
		
		to_df = new DateField();
		//to_df.setFieldLabel("do");
		setup_panel.add(to_df);
		
		
		dayPlanPanels = new ArrayList<DayPlanPanel>();
		
		Button plan_bt = new Button("naplanovat");
		plan_bt.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				createPlan();
			}
			
		});

		Button plan_del_bt = new Button("promazat");
		plan_del_bt.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				deletePlan();
			}
			
		});
		
		Button add_bt = new Button("pridat");
		add_bt.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addNewDayPlanPanel();
			}
			
		});
		
		setup_panel.add(plan_bt);
		setup_panel.add(plan_del_bt);
		setup_panel.add(add_bt);
//		setup_panel.layout();
		add(view_panel);
		add(setup_panel);
	}
	
	private void showPlanFrom(Date from) {
		from_date = from.getTime();
		grid.getStore().removeAll();
		loader.load();
	}
	private void deletePlan() {
		Date from = from_df.getValue();
		Date to = to_df.getValue();
		if (from==null) {
			MessageBox.alert("planovani masazi", "neni zvolen pocatecni den", null);
			return;
		}
		if (to==null) {
			MessageBox.alert("planovani masazi", "neni zvolen koncovy den", null);
			return;
		}
		if (to.before(from)) {
			MessageBox.alert("planovani masazi", "koncovy den je pred pocatecnim", null);
			return;
		}

		clr.dataService.deleteMassageDays(from.getTime(), to.getTime(),new AsyncCallback<Boolean>(){
			public void onFailure(Throwable arg0) {
			}
			public void onSuccess(Boolean arg0) {
				MessageBox.alert("planovani masazi", "konec", null);
			}
		});
		
	}
	private void createPlan() {
		Date from = from_df.getValue();
		Date to = to_df.getValue();
		if (from==null) {
			MessageBox.alert("planovani masazi", "neni zvolen pocatecni den", null);
			return;
		}
		if (to==null) {
			MessageBox.alert("planovani masazi", "neni zvolen koncovy den", null);
			return;
		}
		if (to.before(from)) {
			MessageBox.alert("planovani masazi", "koncovy den je pred pocatecnim", null);
			return;
		}
		Date current_day = new Date(from.getTime());
		List<MassageDay> mdays = new ArrayList<MassageDay>();
		while(!current_day.after(to)) {
			int weekday = current_day.getDay();
			MassageDay md = new MassageDay(current_day.getTime());
			for(DayPlanPanel dp : dayPlanPanels) {
				if (dp.isValid() && dp.isDay(weekday)) {
					Date slot_f = new Date(current_day.getYear(),current_day.getMonth(),current_day.getDate(),dp.getFrom().getHour(),dp.getFrom().getMinute());
					Date slot_t = new Date(current_day.getYear(),current_day.getMonth(),current_day.getDate(),dp.getTo().getHour(),dp.getTo().getMinute());
					md.addSlot(slot_f.getTime(),slot_t.getTime());
				}
			}
			if (md.hasSlots()) {
				mdays.add(md);
			}
			current_day.setTime(current_day.getTime()+ 1000*60*60*24);
 		}
		clr.dataService.insertMassageDays(mdays, new AsyncCallback<Boolean>(){
			public void onFailure(Throwable arg0) {
			}
			public void onSuccess(Boolean arg0) {
				MessageBox.alert("planovani masazi", "konec", null);
			}
		});
	}
}
