package com.prcek.clr.client;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.LessonName;
import com.prcek.clr.client.data.Lesson.LessonType;

public class LessonNameForm extends FormPanel {
	
	private List<TextField<String>> lesson_names;
	
	public LessonNameForm() {
		setFrame(true);
		setHeaderVisible(true);
		setHeading("Cvičení");
		setLayout(new FormLayout());
		setLabelWidth(120);
		
		lesson_names = new ArrayList<TextField<String>>();
		
		for(LessonType lt: LessonType.values()) {
			TextField<String> tf = new TextField<String>();
			tf.setName(Integer.toString(lt.ordinal()));
			tf.setFieldLabel("Cvičení kód - "+lt.ordinal());
			tf.setValue(clr.localService.getLessonName(lt));
			tf.setMaxLength(clr.const_max_lesson_name_field);
			lesson_names.add(tf);
			add(tf);
		}
		
		
		setButtonAlign(HorizontalAlignment.CENTER);  
		addButton(new Button("Uložit",new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				saveForm();
			}
		}));  
		
	}
	private void saveForm() {
			List<LessonName> names = new ArrayList<LessonName>();
			for(TextField<String> tf: lesson_names) {
				LessonName ln =  new LessonName(Integer.parseInt(tf.getName()), tf.getValue());
				names.add(ln);
			}
			clr.dataService.updateLessonNames(names, new AsyncCallback<Boolean>() {
				public void onSuccess(Boolean result) {
					if (result) {
						MessageBox.info("Ukládání OK","Nová jména budou vidět po novém přihlášení do aplikace",null);
					} else {
						MessageBox.alert("Chyba ukládání", "Nelze uložit nová jména cvičení", null);
					}
				}
				
				public void onFailure(Throwable caught) {
					MessageBox.alert("Chyba ukládání", "Nelze uložit nová jména cvičení", null);
				}
			});
	}
}
