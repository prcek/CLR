package com.prcek.clr.client;

import java.util.Map;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.DateMenu;
//import com.extjs.gxt.ui.client.widget.menu.DateMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
//import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
//import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.prcek.clr.client.data.WeekDay;
import com.prcek.clr.client.ui.MonthField;
import com.prcek.clr.client.ui.MonthMenu;
import com.prcek.clr.client.ui.MonthPicker;
import com.prcek.clr.client.ui.SimpleGrid;

public class DebugPanel extends LayoutContainer {
	public DebugPanel() {
		setLayout(new RowLayout());
		
		Text gxtText = new Text(GXT.getVersion().getRelease());
		add(gxtText);
		
		Button bt = new Button("test", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.info("test","ok",null);
				clr.dataService.getConfig("", new AsyncCallback<Map<String,String>>(){
					public void onFailure(Throwable caught) {
						clr.checkForAuthException(caught);
					}

					public void onSuccess(Map<String, String> result) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		add(bt);
		Button bt2 = new Button("test auth", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				clr.dataService.test("", new AsyncCallback<String>(){
					public void onFailure(Throwable caught) {
						clr.checkForAuthException(caught);
					}

					public void onSuccess(String result) {
						MessageBox.info("test","ok",null);
					}
				});
			}
		});
		add(bt2);
/*		
		ContentPanel cp = new ContentPanel();
		cp.setHeading("Utery");
		cp.setFrame(true);
		cp.setWidth(300);
		cp.setHeight(400);
		cp.setScrollMode(Scroll.AUTOY);
		cp.setLayout(new RowLayout(Orientation.VERTICAL));
		
		for(int ci=0; ci<20; ci++) {
			ContentPanel c = new ContentPanel();
			c.setHeading("cas od do");
			c.setFrame(true);
			//c.set(20);
			c.add(new Text("ahoj"));
			cp.add(c);
		}
		
		
		add(cp);
*/
		//MassageCalendar cal = new MassageCalendar();
		//add(cal);
/*		
		com.prcek.client.ui.MonthPicker mp = new MonthPicker();
		add(mp);
		ToolBar toolBar = new ToolBar();
		
		
		TextToolItem item1 = new TextToolItem("Button w/ Menu");  
		item1.setIconStyle("icon-menu-show");  
		
		
		Menu menu = new Menu();
		MenuItem date = new MenuItem("Choose a Date");  
		date.setIconStyle("icon-calendar");  
		menu.add(date);
		date.setSubMenu(new MonthMenu());  
		
		item1.setMenu(menu); 
		toolBar.add(item1);  

		toolBar.add(new AdapterToolItem(new MonthField()));
		
		add(toolBar);
*/
/*
		Grid grid = new Grid(3,5);
	    grid.setStyleName("x-date-inner");
	    grid.setCellSpacing(0);
	    grid.setCellPadding(0);
	    grid.addTableListener(new TableListener() {
	      public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
//	        Event event = DOM.eventGetCurrentEvent();
//	        ComponentEvent be = new ComponentEvent(DatePicker.this, event);
//	        onDayClick(be);
	      }
	    });

	    for (int row = 0; row < 3; row++) {
	      for (int col = 0; col < 5; col++) {
	        grid.setHTML(row, col, "<a href=#><span>a</span></a>");
	      }
	    }
	    add(new WidgetComponent(grid));
		
	}
*/
		
		ToolBar toolBar = new ToolBar();
		
		
		Button item1 = new Button("Button w/ Menu");  
		item1.setIconStyle("icon-menu-show");  
		
		
		Menu menu = new Menu();
		MenuItem date = new MenuItem("Choose a Date");  
		date.setIconStyle("icon-calendar");  
		menu.add(date);

		item1.setMenu(menu); 
		toolBar.add(item1);  
		
		add(toolBar);
		
		
		//createForm2();
		
		//add(new MassagePlanForm());
		LessonNameForm lnf = new LessonNameForm();
		add(lnf);
	}
	
	
	   private void createForm2() {
		 FormData formData = new FormData("-20");
	     FormPanel form2 = new FormPanel();  
	     form2.setFrame(true);  
	     form2.setHeading("Simple Form with FieldSets");  
	     form2.setWidth(350);  
	     form2.setLayout(new FlowLayout());  
	   
	     FieldSet fieldSet = new FieldSet();  
	     fieldSet.setHeading("User Information");  
	     fieldSet.setCheckboxToggle(true);  
	   
	     FormLayout layout = new FormLayout();  
	     layout.setLabelWidth(75);  
	     fieldSet.setLayout(layout);  
	   
	     TextField<String> firstName = new TextField<String>();  
	     firstName.setFieldLabel("First Name");  
	     firstName.setAllowBlank(false);  
	     fieldSet.add(firstName, formData);  
	   
	     TextField<String> lastName = new TextField<String>();  
	     lastName.setFieldLabel("Last Name");  
	     fieldSet.add(lastName, formData);  
	   
	     TextField<String> company = new TextField<String>();  
	     company.setFieldLabel("Company");  
	     fieldSet.add(company, formData);  
	   
	     TextField<String> email = new TextField<String>();  
	     email.setFieldLabel("Email");  
	     fieldSet.add(email, formData);  
	   
	     form2.add(fieldSet);  
	   
	     fieldSet = new FieldSet();  
	     fieldSet.setHeading("Phone Numbers");  
	     fieldSet.setCollapsible(true);  
	   
	     layout = new FormLayout();  
	     layout.setLabelWidth(75);  
	     fieldSet.setLayout(layout);  
	   
	     TextField<String> field = new TextField<String>();  
	     field.setFieldLabel("Home");  
	     fieldSet.add(field, formData);  
	   
	     field = new TextField<String>();  
	     field.setFieldLabel("Business");  
	     fieldSet.add(field, formData);  
	   
	     field = new TextField<String>();  
	     field.setFieldLabel("Mobile");  
	     fieldSet.add(field, formData);  
	   
	     field = new TextField<String>();  
	     field.setFieldLabel("Fax");  
	     fieldSet.add(field, formData);  
	   
	     
	     
	     form2.add(fieldSet);  
	     form2.setButtonAlign(HorizontalAlignment.CENTER);  
	     form2.addButton(new Button("Save"));  
	     form2.addButton(new Button("Cancel"));  
	   
	     add(form2);  
	   }  
	   
	
}