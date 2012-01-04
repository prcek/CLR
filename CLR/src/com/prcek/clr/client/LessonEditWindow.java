package com.prcek.clr.client;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.TimeFieldBinding;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.Lesson.LessonType;

public abstract class LessonEditWindow extends Window {
	private Lesson current_lesson;
	private FormBinding binds;
	private LessonType default_lesson_type;
	public LessonEditWindow(LessonType ltype) {
	  default_lesson_type = ltype;
	  setSize(400, 250);  
	  setPlain(true);
	  
	  String lname = clr.localService.getLessonName(ltype);
	  
	  setHeading("Lekce "+lname);
/*	  
	  switch (default_lesson_type) {
		  case LT_0:
			  setHeading("Lekce PILATES");  
		  break;
		  case LT_1:
			  setHeading("Lekce JOGA");  
		  break;
		  case LT_2:
			  setHeading("Lekce BOSSU");  
		  break;
		  case LT_3:
			  setHeading("Lekce KOND");  
		  break;
		  default:
			  setHeading("Lekce ?");
		  break;
	  }
*/	  
	  setLayout(new FitLayout());
	  setClosable(false);
	  
	  FormPanel form = new FormPanel();
	  form.setHeaderVisible(false);
	  
	  DateField date = new DateField();
	  date.getPropertyEditor().setFormat(DateTimeFormat.getFormat("d. M. y"));
	  date.setName("date");  
	  date.setFieldLabel("Datum");
	  form.add(date);  
	  
	  TimeField time = new TimeField();
	  time.setFormat(DateTimeFormat.getFormat("HH:mm"));
	  time.setName("time");
	  time.setFieldLabel("Čas");
	  time.setTriggerAction(TriggerAction.ALL);
	  form.add(time);
	  
	  
	  NumberField capacity = new NumberField();
	  capacity.setFormat(NumberFormat.getFormat("#0"));
	  capacity.setMinValue(0);
	  capacity.setMaxValue(99);
	  capacity.setName("capacity");
	  capacity.setFieldLabel("Kapacita");
	  form.add(capacity);
	  
	  form.setButtonAlign(HorizontalAlignment.CENTER);  
	  form.addButton(new Button("Uložit", new SelectionListener<ButtonEvent>(){
		public void componentSelected(ButtonEvent ce) {
			binds.unbind();
			onSave(current_lesson);
			hide();
		}
	  }));  
	  form.addButton(new Button("Storno", new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				binds.unbind();
				onCancel();
				hide();
			}
	  }));  
	  
	  binds = new FormBinding(form);
	  binds.addFieldBinding(new FieldBinding(date,"date"));
	  binds.addFieldBinding(new TimeFieldBinding(time,"time"));
	  binds.addFieldBinding(new FieldBinding(capacity,"capacity"));
	  add(form,new FitData(4));
	  
	}
	public void editLesson(Lesson l) {
		current_lesson = new Lesson();
		current_lesson.setProperties(l.getProperties());
		binds.bind(current_lesson);
		show();
	}

	public void addLesson(Date default_date) {
		current_lesson = new Lesson();
		current_lesson.set("date", default_date);
		current_lesson.set("time", default_date);
		current_lesson.set("id", -1);
		current_lesson.set("capacity", 10);
		current_lesson.set("type", default_lesson_type.ordinal());
		binds.bind(current_lesson);
		show();
	}
	
//	public void setLesson(Lesson l) {
//		current_lesson = l;
//		binds.bind(current_lesson);
//	}
	
	public void onCancel() {
		
	}
	
	public void onSave(Lesson l) {
		if (l.getId()== -1) {
			clr.dataService.insertLesson(l, new AsyncCallback<Integer>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(Integer result) {
					afterLessonChange();
				}
			});
		} else {
			clr.dataService.updateLesson(l, new AsyncCallback<Boolean>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(Boolean result) {
					afterLessonChange();
				}
			});
		}
	}
	public abstract void afterLessonChange();
}
