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
import com.prcek.clr.client.data.PermaType;
import com.prcek.clr.client.data.Lesson.LessonType;

public class PermaNameForm extends FormPanel {


	public class PermaInfoPair {
		public int id;
		public CheckBox active;
		public TextField<String> name;
	}
	
	private  List<PermaInfoPair> permas;
	
	
	public PermaNameForm() {
		FormData formData = new FormData("100%");  
		setFrame(true);
		setHeaderVisible(true);
		setHeading("Prodeje");
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
	    
	    
		permas = new ArrayList<PermaInfoPair>();
		
		for(PermaType p: clr.localService.getPermaTypes()) {
			
			CheckBox pa = new CheckBox();
			pa.setName(Integer.toString(p.getId()));
			pa.setFieldLabel("Ev. typ kód - "+p.getId());
			pa.setValue(p.isActive());
			col0.add(pa,formData);
				
			TextField<String> pn = new TextField<String>();
			pn.setValue(p.getName());
			pn.setFieldLabel("název");
			pn.setMaxLength(clr.const_max_perma_name_field);
			col1.add(pn,formData);
			
			
			PermaInfoPair ip = new PermaInfoPair();
			
			ip.active = pa;
			ip.name = pn;
			ip.id = p.getId();
			
			permas.add(ip);
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
		
			List<PermaType> types = new ArrayList<PermaType>();
			for(PermaInfoPair ip: permas) {
				PermaType p = new PermaType(ip.id,ip.active.getValue(), ip.name.getValue());
				types.add(p);
			}
			
			clr.dataService.updatePermaTypes(types, new AsyncCallback<Boolean>() {
				public void onSuccess(Boolean result) {
					if (result) {
						MessageBox.info("Ukládání OK","Nové ev. typy budou vidět po novém přihlášení do aplikace",null);
					} else {
						MessageBox.alert("Chyba ukládání", "Nelze uložit nové ev. typy", null);
					}
				}
				
				public void onFailure(Throwable caught) {
					MessageBox.alert("Chyba ukládání", "Nelze uložit nové ev. typy", null);
				}
			});
			
	}
}
