package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.MassageDay;
import com.prcek.clr.client.data.MassageSlot;

public class MassagePlanForm extends FormPanel {
	private FieldSet fieldSet_repeat;
	private FieldSet fieldSet_hours;
	private DateField day_field;
	private DateField repeat_day_field;
	private RadioGroup radioGroup_day;
	private Radio radio_day_open;
	private Radio radio_day_close;
	
	private RadioGroup radioGroup_repeat;
	private Radio radio_repeat_y;	
	private Radio radio_repeat_n;	
	
	
	private CheckBox check_skip_holy;
	private List<CheckBox> hours; 
	private List<CheckBox> rep_days; 
	public MassagePlanForm() {
		setFrame(false);
		setHeaderVisible(false);
		setLayout(new FormLayout());
		setLabelWidth(120);

		
		day_field = new DateField();
		day_field.setFieldLabel("Den");
		day_field.setEditable(false);
		add(day_field);
		
		radio_day_open = new Radio();  
		radio_day_open.setName("radio_day_open");  
		radio_day_open.setBoxLabel("Ano");  
		radio_day_open.setValue(true);  
		   
		radio_day_close = new Radio();  
		radio_day_close.setName("radio_day_close");  
		radio_day_close.setBoxLabel("Ne");
		

		radioGroup_day = new RadioGroup("day_state");
		radioGroup_day.setFieldLabel("Otevřeno");
		radioGroup_day.add(radio_day_open);
		radioGroup_day.add(radio_day_close);
		add(radioGroup_day);  

		radioGroup_day.addListener(Events.Change, new Listener<FieldEvent>(){
			public void handleEvent(FieldEvent be) {
				if (radioGroup_day.getValue()==radio_day_open) {
					fieldSet_hours.enable();
				} else {
					fieldSet_hours.disable();
				}
			}
		});

		fieldSet_hours = new FieldSet();  
		fieldSet_hours.setHeading("Provozní doba");  
		fieldSet_hours.setLayout(new FlowLayout());

		CheckBoxGroup checkGroup_hours = null;  

		Date date = new Date();
		hours = new ArrayList<CheckBox>();
		for(int m=6*60; m<21*60; m+=30) {
			if (checkGroup_hours==null) {
				checkGroup_hours = new CheckBoxGroup();
				fieldSet_hours.add(checkGroup_hours);
			}
			CheckBox check_hs = new CheckBox();
			int h = (m/60);
			int mr = (m%60);
			date.setHours(h);
			date.setMinutes(mr);
			check_hs.setBoxLabel(DateTimeFormat.getFormat("HH:mm").format(date));
			check_hs.setData("time", new Integer(m));
			checkGroup_hours.add(check_hs);
			hours.add(check_hs);
			
			if (checkGroup_hours.getAll().size()>5) {
				checkGroup_hours=null;
			}
		}
		
		
		add(fieldSet_hours);

		radio_repeat_y = new Radio();  
		radio_repeat_y.setName("radio");  
		radio_repeat_y.setBoxLabel("Ano");  
		   
		radio_repeat_n = new Radio();  
		radio_repeat_n.setName("radio");  
		radio_repeat_n.setBoxLabel("Ne");
		radio_repeat_y.setValue(true);  

		radioGroup_repeat = new RadioGroup("repeat_state");
		radioGroup_repeat.setFieldLabel("Opakovat");
		radioGroup_repeat.add(radio_repeat_y);
		radioGroup_repeat.add(radio_repeat_n);
		add(radioGroup_repeat);
		
		radioGroup_repeat.addListener(Events.Change, new Listener<FieldEvent>(){
			public void handleEvent(FieldEvent be) {
					if (radioGroup_repeat.getValue()==radio_repeat_y) {
						fieldSet_repeat.enable();
					} else {
						fieldSet_repeat.disable();
					}
			}
		});
		
		fieldSet_repeat = new FieldSet();  
		fieldSet_repeat.setHeading("Opakování");  
		   
		FormLayout layout2 = new FormLayout();  
		layout2.setLabelWidth(120);
		//layout2.setPadding(4); //???? FIXME
		fieldSet_repeat.setLayout(layout2);

		repeat_day_field = new DateField();
		repeat_day_field.setFieldLabel("Do");
		repeat_day_field.setEditable(false);
		repeat_day_field.setValue(new Date());
		fieldSet_repeat.add(repeat_day_field);

		CheckBoxGroup checkGroup_repeat_days = new CheckBoxGroup();  
		checkGroup_repeat_days.setFieldLabel("Každé");
		rep_days = new ArrayList<CheckBox>();
		
		CheckBox check_repeat_d;
		
		check_repeat_d = new CheckBox();
		check_repeat_d.setBoxLabel("Po");
		checkGroup_repeat_days.add(check_repeat_d);
		rep_days.add(check_repeat_d);

		check_repeat_d = new CheckBox();
		check_repeat_d.setBoxLabel("Út");
		checkGroup_repeat_days.add(check_repeat_d);
		rep_days.add(check_repeat_d);
		
		check_repeat_d = new CheckBox();
		check_repeat_d.setBoxLabel("St");
		checkGroup_repeat_days.add(check_repeat_d);
		rep_days.add(check_repeat_d);
		
		check_repeat_d = new CheckBox();
		check_repeat_d.setBoxLabel("Čt");
		checkGroup_repeat_days.add(check_repeat_d);
		rep_days.add(check_repeat_d);

		check_repeat_d = new CheckBox();
		check_repeat_d.setBoxLabel("Pá");
		checkGroup_repeat_days.add(check_repeat_d);
		rep_days.add(check_repeat_d);
		
		fieldSet_repeat.add(checkGroup_repeat_days);

		
		
		check_skip_holy = new CheckBox();
		check_skip_holy.setFieldLabel("Vynechat svátky");
		check_skip_holy.setValue(true);
		fieldSet_repeat.add(check_skip_holy);
		
		add(fieldSet_repeat);
		setButtonAlign(HorizontalAlignment.CENTER);  
		addButton(new Button("Uložit",new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				//onButtonSave();
				saveForm();
			}
		}));  
		addButton(new Button("Storno", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				closeForm();
			}
		}));
	}
	public void fillForm(MassageDay day_plan) {
		enable();
		day_field.setValue(new Date(day_plan.getDate()));
		for(CheckBox c: hours) {
			c.setValue(false);
		}
		if (day_plan.slots.size()>0) {
			radio_day_open.setValue(true);
			radio_day_close.setValue(false);
			radioGroup_day.setValue(radio_day_open);
			fieldSet_hours.enable();
			Date start_time = new Date();
			Date end_time = new Date();
			for(MassageSlot slot: day_plan.slots) {
				start_time.setTime(slot.getStart());
				end_time.setTime(slot.getEnd());
				int s_m = start_time.getHours()*60 + start_time.getMinutes();
				int e_m = end_time.getHours()*60 + end_time.getMinutes();
				int ci = 0;
				for(; ci<hours.size(); ci++) {
					Integer st = hours.get(ci).getData("time");
					if (st.intValue()==s_m) {
						for(;ci<hours.size(); ci++) {
							st = hours.get(ci).getData("time");
							if (st.intValue()==e_m) { break; }
							hours.get(ci).setValue(true);
						}
						break;
					}
				}
			}
		} else {
			
			radio_day_open.setValue(false);
			radio_day_close.setValue(true);
			radioGroup_day.setValue(radio_day_close);
			fieldSet_hours.disable();
		}
		radio_repeat_y.setValue(false);
		radio_repeat_n.setValue(true);
		radioGroup_repeat.setValue(radio_repeat_n);
		fieldSet_repeat.disable();
	}
	
	public void saveForm() {
		MassageDay day_plan = new MassageDay(day_field.getValue().getTime());
		Date d = new Date(day_field.getValue().getTime());
		if (radioGroup_day.getValue()==radio_day_open) {
			// fill slots
			int start_m = -1;
			for(CheckBox c: hours) {
				if (c.getValue()) {
					if (start_m==-1) {
						start_m = (Integer) c.getData("time");
					} 
				} else {
					if (start_m!=-1) {
						int end_m = (Integer) c.getData("time");
						d.setHours(start_m/60);
						d.setMinutes(start_m%60);
						long start_date = d.getTime();
						d.setHours(end_m/60);
						d.setMinutes(end_m%60);
						long end_date = d.getTime();
						day_plan.addSlot(start_date,end_date);
						start_m=-1;
					}
				}
			}
		} 
		
		if (radioGroup_repeat.getValue()==radio_repeat_y) {
//			// do repeat plan
			DateWrapper start_day = new DateWrapper(day_plan.getDate());
			DateWrapper last_day = new DateWrapper(repeat_day_field.getValue().getTime());
			List<MassageDay> days_plan = new ArrayList<MassageDay>();
			start_day.clearTime();
			last_day.clearTime();
			while(!last_day.before(start_day)) {
				int day = start_day.getDay();
				if ((day>0) && (day<6)) {
					if (rep_days.get(day-1).getValue()) {
						MassageDay day_plan_i = new MassageDay(start_day.getTime());
						for(MassageSlot ms: day_plan.slots) {
							Date ssb = new Date(ms.getStart());
							Date sse = new Date(ms.getEnd());
							ssb.setYear(start_day.asDate().getYear());
							ssb.setMonth(start_day.asDate().getMonth());
							ssb.setDate(start_day.asDate().getDate());
							sse.setYear(start_day.asDate().getYear());
							sse.setMonth(start_day.asDate().getMonth());
							sse.setDate(start_day.asDate().getDate());
							day_plan_i.addSlot(ssb.getTime(), sse.getTime());
						}
						days_plan.add(day_plan_i);
					}
				}
				start_day = start_day.addDays(1);
				if (days_plan.size()>1000) {
					MessageBox.alert("Nelze provést", "Příliž velký rozsah plánu, více jak 1000 dní", null);
					return;
				}
			}
			
			
			
			clr.dataService.updateMassageDays(days_plan, check_skip_holy.getValue(), new AsyncCallback<Boolean>(){
				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub
				}
				public void onSuccess(Boolean arg0) {
					// TODO Auto-generated method stub
					afterSave();
				}
			});
			
		} else {
		
			clr.dataService.updateMassageDay(day_plan, new AsyncCallback<Boolean>(){
				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub
					
				}
				public void onSuccess(Boolean arg0) {
					// TODO Auto-generated method stub
					afterSave();
				}
			});
		}
	}
	
	public void closeForm() {
		afterCancel();
	}
	
	public void editDate(Date date) {
		disable();
		clr.dataService.getMassageDay(date.getTime(), new AsyncCallback<MassageDay>(){
			public void onFailure(Throwable arg0) {
			}

			public void onSuccess(MassageDay arg0) {
				// TODO Auto-generated method stub
				fillForm(arg0);
			}
		});
	}

	
	
	public void afterSave() {
		
	}
	public void afterCancel() {
		
	}
}
