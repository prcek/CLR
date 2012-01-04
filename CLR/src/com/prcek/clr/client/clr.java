package com.prcek.clr.client;



import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Document;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HTML;  
import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.Repository;
import com.prcek.clr.client.data.Lesson.LessonType;
import com.prcek.clr.client.resources.Images;


import java.util.ArrayList;
import java.util.Date;  
import java.util.List;
import java.util.Map;


public class clr implements EntryPoint {
	
	public static int const_max_massage_desc_field = 20;
	public static int const_max_massage_name_field = 32;
	public static int const_max_massage_username_field = 64;
	public static int const_max_massage_phone_field = 32;
	public static int const_max_lesson_name_field = 32;
	public static int const_max_perma_name_field = 64;
	public static int const_max_doctor_name_field = 60;
	
	
	public static DataServiceAsync dataService;
	public static Repository localService;
	
	public LayoutContainer main_app;
	
	public static List<LessonPanel> lesson_panels;
	public static MembersPanel members_panel;	
	public static MassagePanel massage_panel;
	public static PermaPanel perma_panel;
	public static ReportPanel report_panel;
	public static SettingPanel setting_panel;
	public static LoadListener auth_check_listener;
	public static boolean allowResizableWnd = false;
	public static boolean allowModalWnd = true;
	public static Images IMAGES = (Images) GWT.create(Images.class);
	private static Map<String,String> cfg;
	private static clr ME;
	private static Timer dataCheckTimer;
	public static String printUrl="clr/remote";
	
	public static void reload_lessons() {
		for(LessonPanel lp: lesson_panels) {
			lp.reloadAll();
		}
	}
	
	public static void logout_app() {
		ME.stopApp("odhlášeno");
	}
	public static String getCfg(String name, String def_val) {
		if (cfg==null) return def_val;
		String v = cfg.get(name);
		if (v==null) return def_val;
		return v;
	}
	public static String getCfg(String name) {
		return getCfg(name,null);
	}
	private void initDataService() {
	     dataService = (DataServiceAsync) GWT.create(DataService.class);
	     auth_check_listener =new LoadListener(){
			@Override
			public void loaderLoadException(LoadEvent le) {
				// TODO Auto-generated method stub
				checkForAuthException(le.exception);
				super.loaderLoadException(le);
			}
	     } ;
	}
	
	public static void checkForAuthException(Throwable caught) {
		if (caught!=null) {
			MessageBox.alert("DataService exception", caught.toString(), null);
		} else{
			MessageBox.alert("DataService exception", "NULL", null);
		}
	}
	
	public void onModuleLoad() {
		ME=this;
		initDataService();
		
		//Document.get().setTitle("TEST");
		
		dataService.getConfig("init", new AsyncCallback<Map<String,String>>(){
			public void onFailure(Throwable caught) {
				abortApp("nelze nacist konfiguraci ze serveru");
			}
			public void onSuccess(Map<String, String> result) {
				cfg = result;
				loginApp();
			}
		});
		
	}
	private void loginApp() {
		LoginWindow lw = new LoginWindow(getCfg("system_name","")){
			public void onLoginOk() {
				initRepository();
			}
		};
		//lw.doLogin(false);
		lw.doLogin(Boolean.parseBoolean(getCfg("debug", "true")));
	}
	
	private void initRepository() {
		localService = new Repository(){
			@Override
			public void afterInit(Boolean ok) {
				if (ok) {
					startApp();
				} else {
					abortApp("nelze inicializovat lokalni uloziste");
				}
			}
			
		};
		localService.load();
	}
	
	
	private void abortApp(String text) {
//		RootPanel.get().clear();
//		Viewport view = new Viewport();
//		view.setLayout(new FitLayout());
//  	    RootPanel.get().add(view);
		stopDataChangeTimers();
		if (main_app!=null) {
			main_app.disable();
			main_app.hide();
		}
		MessageBox.alert("Chyba aplikace, zkuste obnovit stránku v prohlížeči", text, null);
	}
	private void stopApp(String text) {
		stopDataChangeTimers();
		
		if (main_app!=null) {
			main_app.disable();
			main_app.hide();
		}
		MessageBox.info("Aplikace ukončena, zavřete stránku v prohlížeči", text, null);
	}
	
	private void setupDataChangeTimers() {
		dataCheckTimer = new Timer() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				clr.ME.chechForDataChange();
			}
		};
		dataCheckTimer.scheduleRepeating(10*1000);
	}
	
	private void stopDataChangeTimers() {
		if (dataCheckTimer!=null) {
			dataCheckTimer.cancel();
		}
	}
	
	
	
	public void chechForDataChange() {
		clr.dataService.getLastDataChange("massage", new AsyncCallback<Date>(){
			public void onFailure(Throwable arg0) {
				
				onConnectionProblem();
				
			}
			public void onSuccess(Date arg0) {
				onDataChange("massage", arg0);
			}
		});
	}
	
	private void onDataChange(String key, Date d) {
		if ((key.compareTo("massage")==0) && (massage_panel!=null)) {
			massage_panel.serverDataTime(d);
		}
	}
	
	private void onConnectionProblem() {
		
	}
	
	private void startApp() {

		if (Boolean.parseBoolean(getCfg("debug", "true"))) {
			allowResizableWnd = true;
			allowModalWnd = true;
		}
		
		printUrl = getCfg("print_url",printUrl);
		
		//LayoutContainer center = new LayoutContainer();
		//center.setLayout(new CenterLayout());
		
		main_app = new LayoutContainer(new RowLayout());
		main_app.setSize(710, 600);
		
		
		TabPanel main_tab_panel = new TabPanel();
		main_tab_panel.setPlain(true);  

		
		TabItem tab;
		lesson_panels = new ArrayList<LessonPanel>();
		for (LessonType lt: LessonType.values()) {
			tab = new TabItem(localService.getLessonName(lt));
			tab.setLayout(new FitLayout());
			LessonPanel lp = new LessonPanel(lt);
			lesson_panels.add(lp);
			tab.add(lp);
			main_tab_panel.add(tab);  
		}
		
		
		tab = new TabItem("Masáže");
		tab.setLayout(new FitLayout());
		massage_panel = new MassagePanel();
		tab.add(massage_panel);
		main_tab_panel.add(tab);

		tab = new TabItem("Evidence");
		tab.setLayout(new FitLayout());
		members_panel = new MembersPanel();
		tab.add(members_panel);
		main_tab_panel.add(tab);  

		tab = new TabItem("Prodeje");
		perma_panel = new PermaPanel();
		tab.setLayout(new FitLayout());
		tab.add(perma_panel);
		main_tab_panel.add(tab);

		tab = new TabItem("Přehledy");
		tab.setLayout(new FitLayout());
		report_panel = new ReportPanel();
		tab.add(report_panel);
		main_tab_panel.add(tab);

		
		if (localService.isAdmin()) {
		
			tab = new TabItem("Nastavení");
			tab.setLayout(new FitLayout());
			setting_panel = new SettingPanel();
			tab.add(setting_panel);
			main_tab_panel.add(tab);
		
		}
		
		if (Boolean.parseBoolean(getCfg("debug", "true"))) {
/*
			tab = new TabItem("Svatky");
			tab.setLayout(new FitLayout());
			tab.add(new HolydayPanel());
			main_panel.add(tab);
			
			tab = new TabItem("Masaze - planovani");
			tab.add(new MassagePlanPanel());
			main_panel.add(tab);
			
			tab = new TabItem("Svatky");
			tab.add(new HolydayPanel());
			main_panel.add(tab);
*/	
			
			TabItem debug_tab = new TabItem("!debug - nepoužívat!");
			debug_tab.add(new DebugPanel());
			main_tab_panel.add(debug_tab);
		}

		
		LayoutContainer top_panel = new LayoutContainer(new RowLayout());
		top_panel.setHeight(30);
		//top_panel.setBorders(true);
		top_panel.add(new TopInfoPanel(getCfg("system_name", "")),new RowData(1,1,new Margins(2)));
		main_app.add(top_panel, new RowData(1,-1,new Margins(2)));
		
		
		main_app.add(main_tab_panel, new RowData(1,1,new Margins(0)));
		
		LayoutContainer bottom_panel = new LayoutContainer(new RowLayout());
		bottom_panel.setHeight(30);
		//bottom_panel.setBorders(true);
		String ver = "gxt: "+GXT.getVersion().getRelease() + " gwt: " + GWT.getVersion()+ "/" +getCfg("server_version","?")+ " clr:" +getCfg("app_version","?");
		bottom_panel.add(new StatusPanel(ver,getCfg("help_url","?")),new RowData(1,1,new Margins(2)));
		main_app.add(bottom_panel, new RowData(1,-1,new Margins(2)));
		
		//center.add(main_app);
		Viewport view = new Viewport();
		view.setLayout(new CenterLayout());
		view.add(main_app);
		view.setScrollMode(Scroll.AUTO);
	    RootPanel.get().clear();
  	    RootPanel.get().add(view);
  	    
		setupDataChangeTimers();
	}
}

