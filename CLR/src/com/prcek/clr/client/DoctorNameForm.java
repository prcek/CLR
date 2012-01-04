package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.Doctor;
import com.prcek.clr.client.data.LessonName;
import com.prcek.clr.client.data.MassageType;
import com.prcek.clr.client.data.Lesson.LessonType;

public class DoctorNameForm extends FormPanel {


	public class DoctorInfoPair {
		public int id;
		public CheckBox active;
		public TextField<String> name;
	}
	
	private  List<DoctorInfoPair> doctors;
	
	
	public DoctorNameForm() {
		FormData formData = new FormData("100%");  
		setFrame(true);
		setHeaderVisible(true);
		setHeading("Doktoři");
		setLabelAlign(LabelAlign.TOP);
		setLayout(new FormLayout());
		setLabelWidth(120);
		
		LayoutContainer main = new LayoutContainer();  
	    main.setLayout(new ColumnLayout());
		
	    LayoutContainer col0 = new LayoutContainer();  
	    col0.setStyleAttribute("paddingRight", "10px");  
	    FormLayout layout = new FormLayout();
	    col0.setLayout(layout);  

	    LayoutContainer col1 = new LayoutContainer();  
	    col1.setStyleAttribute("paddingRight", "10px");  
	    layout = new FormLayout();  
	    layout.setLabelWidth(50);
	    col1.setLayout(layout);  
	    
	    
		doctors = new ArrayList<DoctorInfoPair>();
		
		for(Doctor d: clr.localService.getDoctors()) {
			
			CheckBox da = new CheckBox();
			da.setName(Integer.toString(d.getId()));
			da.setFieldLabel("Doktor kód - "+d.getId());
			da.setValue(d.isActive());
			col0.add(da,formData);
				
			TextField<String> dn = new TextField<String>();
			dn.setValue(d.getFullName());
			dn.setFieldLabel("jméno");
			dn.setMaxLength(clr.const_max_doctor_name_field);
			col1.add(dn,formData);
			
			
			DoctorInfoPair ip = new DoctorInfoPair();
			
			ip.active = da;
			ip.name = dn;
			ip.id = d.getId();
			
			doctors.add(ip);
		}
		
		
		
		main.add(col0, new ColumnData(.3));  
		main.add(col1, new ColumnData(.7));  
		
		add(main);
		setButtonAlign(HorizontalAlignment.CENTER);  
		addButton(new Button("Uložit",new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				saveForm();
			}
		}));  
		
	}
	private void saveForm() {
		
			List<Doctor> docs = new ArrayList<Doctor>();
			for(DoctorInfoPair ip: doctors) {
				Doctor d = new Doctor(ip.id,ip.active.getValue(), ip.name.getValue());
				docs.add(d);
			}
			
			clr.dataService.updateDoctors(docs, new AsyncCallback<Boolean>() {
				public void onSuccess(Boolean result) {
					if (result) {
						MessageBox.info("Ukládání OK","Nová jména budou vidět po novém přihlášení do aplikace",null);
					} else {
						MessageBox.alert("Chyba ukládání", "Nelze uložit nová jména doktorů", null);
					}
				}
				
				public void onFailure(Throwable caught) {
					MessageBox.alert("Chyba ukládání", "Nelze uložit nová jména doktorů", null);
				}
			});
			
	}
}
