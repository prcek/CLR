package com.prcek.clr.client;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.LessonMember;
import com.prcek.clr.client.data.Member;

public class MemberEditWindow extends Window {
	private Member current_member;
	private FormBinding binds;
/*	
	private static void upcaseField(Field<String> f) {
		if (f!=null) {
			String text = f.getRawValue();
			if (text!=null) {
				if (text.length()==1) {
					String new_text = text.toUpperCase();
					if (new_text.compareTo(text)!=0) {
						f.setRawValue(new_text);
					}
				}
					
			}
		}
	}
*/	
	public MemberEditWindow() {
		  setSize(350, 350);  
		  setPlain(true);  
		  setHeading("Klient");  
		  setLayout(new FitLayout());
		  setClosable(false);
		  
		  setResizable(clr.allowResizableWnd);
		  setModal(clr.allowModalWnd);
		  
		  
		  FormPanel form = new FormPanel();
		  form.setHeaderVisible(false);

		  TextField<String> number = new TextField<String>();
		  number.setName("number");
		  number.setFieldLabel("Číslo");
		  number.setReadOnly(true);
		  form.add(number);
		  
		  
		  TextField<String> name = new TextField<String>();
		  name.setName("name");
		  name.setFieldLabel("Jméno");
		  //name.setAutoValidate(true);
		  name.addListener(Events.KeyPress, new Listener<FieldEvent>(){
			public void handleEvent(FieldEvent be) {
				Utils.upcaseField(be.getField());
			}
		  });
		  form.add(name);

		  TextField<String> surname = new TextField<String>();
		  surname.setName("surname");
		  surname.setFieldLabel("Přijmení");
		  surname.addListener(Events.KeyPress, new Listener<FieldEvent>(){
				public void handleEvent(FieldEvent be) {
					Utils.upcaseField(be.getField());
				}
			  });
		  form.add(surname);
		  
		  TextField<String> phone = new TextField<String>();
		  phone.setName("phone");
		  phone.setFieldLabel("Telefon");
		  form.add(phone);

		  TextField<String> email = new TextField<String>();
		  email.setName("email");
		  email.setFieldLabel("e-mail");
		  form.add(email);

		  TextField<String> street = new TextField<String>();
		  street.setName("street");
		  street.setFieldLabel("Ulice");
		  street.addListener(Events.KeyPress, new Listener<FieldEvent>(){
				public void handleEvent(FieldEvent be) {
					Utils.upcaseField(be.getField());
				}
			  });
		  form.add(street);

		  TextField<String> street_no = new TextField<String>();
		  street_no.setName("street_no");
		  street_no.setFieldLabel("Č.o.");
		  form.add(street_no);

		  TextField<String> city = new TextField<String>();
		  city.setName("city");
		  city.setFieldLabel("Město");
		  city.addListener(Events.KeyPress, new Listener<FieldEvent>(){
				public void handleEvent(FieldEvent be) {
					Utils.upcaseField(be.getField());
				}
			  });
		  form.add(city);
		  
		  form.setButtonAlign(HorizontalAlignment.CENTER);  
		  form.addButton(new Button("Uložit", new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				binds.unbind();
				onSave(current_member);
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
		  binds.addFieldBinding(new FieldBinding(number,"number"));
		  binds.addFieldBinding(new FieldBinding(name,"name"));
		  binds.addFieldBinding(new FieldBinding(surname,"surname"));
		  binds.addFieldBinding(new FieldBinding(phone,"phone"));
		  binds.addFieldBinding(new FieldBinding(email,"email"));
		  binds.addFieldBinding(new FieldBinding(street,"street"));
		  binds.addFieldBinding(new FieldBinding(street_no,"street_no"));
		  binds.addFieldBinding(new FieldBinding(city,"city"));
		  add(form,new FitData(4));
		  
	}
	
	
	public void editMember(Member m) {
		current_member = new Member();
		current_member.setProperties(m.getProperties());
		binds.bind(current_member);
		show();
	}

	public void addMember(String num) {
		current_member = new Member();
		current_member.set("id", -1);
		current_member.set("number", num);
		current_member.set("registration_date", (long)0);
		binds.bind(current_member);
		show();
	}
	public void onCancel() {
		
	}
	public void onModify() {
		
	}
	public void onSave(Member m) {
		if (m.getId()== -1) {
			clr.dataService.insertMember(m, new AsyncCallback<Integer>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(Integer result) {
					onModify();
					clr.reload_lessons();
				}
			});
		} else {
			clr.dataService.updateMember(m, new AsyncCallback<Boolean>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(Boolean result) {
					onModify();
					clr.reload_lessons();
				}
			});
		}
	}

}
