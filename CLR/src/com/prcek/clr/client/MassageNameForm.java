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
import com.prcek.clr.client.data.LessonName;
import com.prcek.clr.client.data.MassageType;
import com.prcek.clr.client.data.Lesson.LessonType;

public class MassageNameForm extends FormPanel {


	public class MassageInfoPair {
		public int id;
		public CheckBox active;
		public CheckBox lava;
		public TextField<String> name;
		public ComboBox<SlotsType> slots;
	}
	
	public class SlotsType extends BaseModelData {
		public SlotsType() {
			
		}
		public SlotsType(String name, Integer value) {
			set("name",name);
			set("value",value);
		}
		public Integer getValue() {
			return get("value");
		}
	}
	
	
	private  List<MassageInfoPair> massages;
	
	private ComboBox<SlotsType> createCb() {
		ComboBox<SlotsType> c = new ComboBox<SlotsType>();
		c.setDisplayField("name");
		c.setStore(new ListStore<SlotsType>());
		c.setAllowBlank(false);
		c.setEditable(false);
		c.setForceSelection(true);
		c.setTriggerAction(TriggerAction.ALL);
		SlotsType v;
		for(int i=1; i<5; i++) {
			v = new SlotsType(""+(i*30)+"min", i);
			c.getStore().add(v);
		}
		c.setValue(c.getStore().getAt(0));
		return c;
	}
	
	
	public MassageNameForm() {
		FormData formData = new FormData("100%");  
		setFrame(true);
		setHeaderVisible(true);
		setHeading("Masáže");
		setLabelAlign(LabelAlign.TOP);
		setLayout(new FormLayout());
		setLabelWidth(120);
		
		LayoutContainer main = new LayoutContainer();  
	    main.setLayout(new ColumnLayout());
		
	    LayoutContainer col0 = new LayoutContainer();  
	    col0.setStyleAttribute("paddingRight", "10px");  
	    FormLayout layout = new FormLayout();
//	    layout.setLabelWidth(50);
//	    layout.setLabelAlign(LabelAlign.TOP);  
	    col0.setLayout(layout);  

	    LayoutContainer col1 = new LayoutContainer();  
	    col1.setStyleAttribute("paddingRight", "10px");  
	    layout = new FormLayout();  
	    layout.setLabelWidth(50);
//	    layout.setLabelAlign(LabelAlign.TOP);
//	    layout.setLabelWidth(50);
	    col1.setLayout(layout);  
	    
	    LayoutContainer col2 = new LayoutContainer();  
	    col2.setStyleAttribute("paddingLeft", "10px");  
	    layout = new FormLayout();  
//	    layout.setLabelAlign(LabelAlign.TOP);  
	    layout.setLabelWidth(50);
	    col2.setLayout(layout);  

	    LayoutContainer col3 = new LayoutContainer();  
	    col3.setStyleAttribute("paddingLeft", "10px");  
	    layout = new FormLayout();  
//	    layout.setLabelAlign(LabelAlign.TOP);  
	    layout.setLabelWidth(50);
	    col3.setLayout(layout);  
	    

	    
	    
	    
		massages = new ArrayList<MassageInfoPair>();
		
		for(MassageType mt: clr.localService.getMassageTypes()) {
			
			CheckBox ma = new CheckBox();
			ma.setName(Integer.toString(mt.getId()));
			ma.setFieldLabel("Masáž kód - "+mt.getId());
			ma.setValue(mt.isActive());
			col0.add(ma,formData);
				
			TextField<String> mn = new TextField<String>();
			mn.setValue(mt.getName());
			mn.setFieldLabel("název");
			mn.setMaxLength(clr.const_max_massage_name_field);
			
			col1.add(mn,formData);
			
			ComboBox<SlotsType> ms = createCb();
			ms.setFieldLabel("délka");
			ms.setValue(ms.getStore().getAt(mt.getSlots()-1));
			col2.add(ms,formData);

			CheckBox ml = new CheckBox();
			ml.setFieldLabel("kameny");
			ml.setValue(mt.isLava());
			col3.add(ml,formData);
			
			MassageInfoPair ip = new MassageInfoPair();
			
			ip.active = ma;
			ip.slots = ms;
			ip.name = mn;
			ip.lava = ml;
			ip.id = mt.getId();
			
			massages.add(ip);
		}
		
		
		
		main.add(col0, new ColumnData(.2));  
		main.add(col1, new ColumnData(.45));  
		main.add(col2, new ColumnData(.2));  
		main.add(col3, new ColumnData(.15));  
		
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
		
			List<MassageType> types = new ArrayList<MassageType>();
			for(MassageInfoPair ip: massages) {
				MassageType mt = new MassageType(ip.id,ip.active.getValue(), ip.name.getValue(), ip.slots.getValue().getValue(), ip.lava.getValue());
				types.add(mt);
			}
			
			clr.dataService.updateMassageTypes(types, new AsyncCallback<Boolean>() {
				public void onSuccess(Boolean result) {
					if (result) {
						MessageBox.info("Ukládání OK","Nová jména budou vidět po novém přihlášení do aplikace",null);
					} else {
						MessageBox.alert("Chyba ukládání", "Nelze uložit nová jména masáží", null);
					}
				}
				
				public void onFailure(Throwable caught) {
					MessageBox.alert("Chyba ukládání", "Nelze uložit nová jména masáží", null);
				}
			});
			
	}
}
