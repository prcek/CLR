package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
//import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.Doctor;
import com.prcek.clr.client.data.MassageItemSum;
import com.prcek.clr.client.data.MassageType;
import com.prcek.clr.client.data.PermaItem;
import com.prcek.clr.client.data.PermaItemSum;
import com.prcek.clr.client.data.PermaType;

public class ReportPanel extends LayoutContainer {
	private DateField perma_start_dp;
	private DateField perma_end_dp;
	private ComboBox<Doctor>  perma_doctor;
	private ListLoader perma_loader;
	private Grid<PermaItemSum> perma_grid;

	private DateField massage_start_dp;
	private DateField massage_end_dp;
	private ListLoader massage_loader;
	private Grid<MassageItemSum> massage_grid;
	
	
	public ReportPanel() {
		setLayout(new RowLayout());
		
		ToolBar perma_tool = new ToolBar();
		perma_tool.add(new LabelToolItem("Přehled prodejů"));
		perma_tool.add(new SeparatorToolItem());
		
		perma_start_dp = new DateField();
		perma_start_dp.setAllowBlank(false);
		perma_start_dp.setEditable(false);
		perma_end_dp = new DateField();
		perma_end_dp.setAllowBlank(false);
		perma_end_dp.setEditable(false);
		perma_tool.add(new LabelToolItem("od"));
		perma_tool.add(perma_start_dp);
		perma_tool.add(new SeparatorToolItem());
		perma_tool.add(new LabelToolItem("do"));
		perma_tool.add(perma_end_dp);
		perma_tool.add(new SeparatorToolItem());
		perma_tool.add(new LabelToolItem("Doktor"));
		
		perma_doctor = new ComboBox<Doctor>();
		perma_doctor.setDisplayField("fullname");
		perma_doctor.setEditable(false);
		perma_doctor.setForceSelection(true);
		perma_doctor.setAllowBlank(false);
		perma_doctor.setStore(clr.localService.getDoctorsStore());
		perma_doctor.setTriggerAction(TriggerAction.ALL);
		perma_tool.add(perma_doctor);
		perma_tool.add(new FillToolItem());
		perma_tool.add(new Button("Zobrazit", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				updatePermaReport();
			}
		}));
		initPermaSelection();
		add(perma_tool, new RowData(1,-1,new Margins(0)));
		
		
		RpcProxy perma_proxy = new RpcProxy() {
			protected void load(Object loadConfig, AsyncCallback callback) {
				long start_d = perma_start_dp.getValue().getTime();
				long end_d = perma_end_dp.getValue().getTime();
				int doctor_id = perma_doctor.getValue().getId();
				clr.dataService.getPermaItemSum(start_d, end_d, doctor_id, callback);	
			}
		};  
		ModelReader perma_reader = new ModelReader();
		perma_loader = new BaseListLoader(perma_proxy,perma_reader);
		perma_loader.addLoadListener(clr.auth_check_listener);
		ListStore<PermaItemSum> perma_store = new ListStore<PermaItemSum>(perma_loader);  
		
		List<ColumnConfig> perma_configs = new ArrayList<ColumnConfig>();
		
		GridCellRenderer<PermaItemSum> perma_gridDoctor = new GridCellRenderer<PermaItemSum>() {  
		       public String render(PermaItemSum model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<PermaItemSum> store, Grid<PermaItemSum> grid) {
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

		GridCellRenderer<PermaItemSum> perma_gridType = new GridCellRenderer<PermaItemSum>() {  
		       public String render(PermaItemSum model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<PermaItemSum> store,Grid<PermaItemSum> grid) {
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
		
		perma_configs.add(Utils.createColumnConfigWithRenderer("doctor_id", "Doktor", 170, perma_gridDoctor));
		perma_configs.add(Utils.createColumnConfigWithRenderer("type_id", "Typ", 200, perma_gridType));
		perma_configs.add(new ColumnConfig("count","Kolik",80));
		perma_configs.add(new ColumnConfig("cost","Cena",80));
		perma_configs.add(new ColumnConfig("multi","Kolikrát",80));

		
		
		for(ColumnConfig cc: perma_configs) {
			cc.setMenuDisabled(true);
		}
		ColumnModel perma_cm = new ColumnModel(perma_configs);  
		perma_grid = new Grid<PermaItemSum>(perma_store, perma_cm);
		perma_grid.setLoadMask(true);
		perma_grid.setBorders(true);
//		perma_grid.setHeight(300);
		perma_grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		
		add(perma_grid, new RowData(1,.7,new Margins(0)));


		ToolBar massage_tool = new ToolBar();
		massage_tool.add(new LabelToolItem("Přehled masáží"));
		massage_tool.add(new SeparatorToolItem());
		
		massage_start_dp = new DateField();
		massage_start_dp.setAllowBlank(false);
		massage_start_dp.setEditable(false);
		massage_end_dp = new DateField();
		massage_end_dp.setAllowBlank(false);
		massage_end_dp.setEditable(false);
		massage_tool.add(new LabelToolItem("od"));
		massage_tool.add(massage_start_dp);
		massage_tool.add(new SeparatorToolItem());
		massage_tool.add(new LabelToolItem("do"));
		massage_tool.add(massage_end_dp);
		massage_tool.add(new SeparatorToolItem());
		massage_tool.add(new FillToolItem());
		
		massage_tool.add(new Button("Zobrazit", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				updateMassageReport();
			}
		}));
		initMassageSelection();
		add(massage_tool, new RowData(1,-1,new Margins(0)));

		
		RpcProxy massage_proxy = new RpcProxy() {
			protected void load(Object loadConfig, AsyncCallback callback) {
				long start_d = massage_start_dp.getValue().getTime();
				long end_d = massage_end_dp.getValue().getTime();
				clr.dataService.getMassageItemSum(start_d, end_d, callback);	
			}
		};  
		ModelReader massage_reader = new ModelReader();
		massage_loader = new BaseListLoader(massage_proxy,massage_reader);
		massage_loader.addLoadListener(clr.auth_check_listener);
		ListStore<MassageItemSum> massage_store = new ListStore<MassageItemSum>(massage_loader);  
		
		List<ColumnConfig> massage_configs = new ArrayList<ColumnConfig>();
		

		GridCellRenderer<MassageItemSum> massage_gridType = new GridCellRenderer<MassageItemSum>() {
			   public String render(MassageItemSum model, String property, ColumnData config, int rowIndex,  
		           int colIndex, ListStore<MassageItemSum> store,Grid<MassageItemSum> grid) {
		    	   int type_id = model.getType();
		    	   String ptname = "?";
		    	   if (type_id!=0) {
		    		   
		    		   MassageType pt = clr.localService.getMassageTypesStore().findModel("id", type_id);
		    		   if (pt!=null) { 
		    			   ptname = pt.getName(); 
		    		   } 
		    	   }
		    	   if (ptname==null) {
		    		  ptname = "?";
		    	   }
		    	   return "<span>" + ptname + "</span>";
		    	   //return "x";
		       }

		};  
		
		massage_configs.add(Utils.createColumnConfigWithRenderer("type_id", "Typ", 200, massage_gridType));
		massage_configs.add(new ColumnConfig("count","Kolik",80));
		
		
		for(ColumnConfig cc: massage_configs) {
			cc.setMenuDisabled(true);
		}
		ColumnModel massage_cm = new ColumnModel(massage_configs);  
		massage_grid = new Grid<MassageItemSum>(massage_store, massage_cm);
		massage_grid.setLoadMask(true);
		massage_grid.setBorders(true);
		//massage_grid.setHeight(200);
		massage_grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		add(massage_grid,new RowData(1,.3,new Margins(0)));
		
	}
	private void updatePermaReport() {
		perma_loader.load();
	}
	private void initPermaSelection() {
		Doctor doc = perma_doctor.getStore().getAt(0);
		perma_doctor.setValue(doc);
		Date d = new Date();
		Date d_s = Utils.getPrevMonth(Utils.getFirstMonthDayDate(d));
		Date d_e = Utils.getPrevDay(Utils.getNextMonth(d_s));
		perma_start_dp.setValue(d_s);
		perma_end_dp.setValue(d_e);
	}
	private void updateMassageReport() {
		massage_loader.load();
	}
	private void initMassageSelection() {
		Date d = new Date();
		Date d_s = Utils.getPrevMonth(Utils.getFirstMonthDayDate(d));
		Date d_e = Utils.getPrevDay(Utils.getNextMonth(d_s));
		massage_start_dp.setValue(d_s);
		massage_end_dp.setValue(d_e);
	}
	
}
