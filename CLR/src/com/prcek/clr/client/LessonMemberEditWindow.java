package com.prcek.clr.client;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.TimeFieldBinding;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.LessonMember;
import com.prcek.clr.client.data.Member;

abstract public class LessonMemberEditWindow extends Window {
	private LessonMember current_lesson_member;
//	private FormBinding binds;
	private MemberComboBox member_combo;
	private Button btn_save;
	private Text member_info;  
	public LessonMemberEditWindow() {
		  setSize(350, 200);  
		  setPlain(true);  
		  setHeading("Zak lekce");  
		  setLayout(new FitLayout());
		  setClosable(false);
		  
//		  FormPanel form = new FormPanel();
//		  form.setHeaderVisible(false);
		  
		  VerticalPanel vp = new VerticalPanel();
		  vp.setWidth("100%");

		  HorizontalPanel hp = new HorizontalPanel();
		  
		  btn_save = new Button("ulozit");  
		  btn_save.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			       public void componentSelected(ButtonEvent ce) {
					 onCancel();
					 onSave(current_lesson_member);
					 hide();					 
				   }  
		  });  
		  hp.add(btn_save);
		  btn_save.disable();
		  Button btn2 = new Button("storno");  
		  btn2.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			       public void componentSelected(ButtonEvent ce) {
					 onCancel();
					 hide();
				   }  
		  });  
		  hp.add(btn2);
		  
		  member_combo = new MemberComboBox(){
			public void onMember(Member m) {
				if (m==null) {
					current_lesson_member.set("member_id", -1);
					hideMemberInfo();
					btn_save.disable();
				} else {
					current_lesson_member.set("member_id", m.getId());
					showMemberInfo();
					btn_save.enable();
				}
				super.onMember(m);
			}
		  };
		  vp.add(member_combo);
		  vp.add(hp);
		  member_info = new Text("");
		  vp.add(member_info);
		  add(vp,new FitData(4));

		  
		  
/*		  
		  TextField<String> name = new TextField<String>();
		  name.setMaxLength(20);
		  name.setName("name");
		  name.setFieldLabel("Jmeno");
		  form.add(name);  

		  TextField<String> surname = new TextField<String>();
		  surname.setMaxLength(30);
		  surname.setName("surname");
		  surname.setFieldLabel("Prijmeni");
		  form.add(surname);  
		  
		  
		  form.setButtonAlign(HorizontalAlignment.CENTER);  
		  form.addButton(new Button("Uložit", new SelectionListener<ComponentEvent>(){
			public void componentSelected(ComponentEvent ce) {
				binds.unbind();
				onSave(current_lesson_member);
				hide();
			}
		  }));  
		  form.addButton(new Button("Storno", new SelectionListener<ComponentEvent>(){
				public void componentSelected(ComponentEvent ce) {
					binds.unbind();
					onCancel();
					hide();
				}
		  }));  

		  
		  binds = new FormBinding(form);
		  binds.addFieldBinding(new FieldBinding(name,"name"));
		  binds.addFieldBinding(new FieldBinding(surname,"surname"));
		  add(form,new FitData(4));
*/		  
	}
/*	
	public void editLessonMember(LessonMember lm) {
		current_lesson_member = new LessonMember();
		current_lesson_member.setProperties(lm.getProperties());
		binds.bind(current_lesson_member);
		show();
	}
*/
	public void addLessonMember(int default_lesson_id) {
		current_lesson_member = new LessonMember();
		current_lesson_member.set("lesson_id", default_lesson_id);
		current_lesson_member.set("member_id", -1);
		current_lesson_member.set("id", -1);
		btn_save.disable();
		hideMemberInfo();
		member_combo.reset();
		//binds.bind(current_lesson_member);
		show();
	}
	
	public void hideMemberInfo() {
		member_info.setText("");
	}
	
	public void showMemberInfo() {
		int member_id=current_lesson_member.getMemberId();
		member_info.setText("");
		clr.dataService.getMemberInfo(member_id, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				member_info.setText(result);
			}
			
		});
	}
	
	public void onCancel() {
		
	}
	
	public void onSave(LessonMember lm) {
		if (lm.getId()== -1) {
			if (lm.getMemberId()==-1) {
				MessageBox.alert("chyba", "nebyl zvolen klient, pri pokusu prihlasit do lekce", null);
			} else {
				clr.dataService.insertLessonMember(lm, new AsyncCallback<Integer>(){
					public void onFailure(Throwable caught) {
					}
					public void onSuccess(Integer result) {
						afterLessonMemberChange();
					}
				});
			}
		} else {
			/*
			clr.dataService.updateLessonMember(lm, new AsyncCallback<Boolean>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(Boolean result) {
					clr.reload();
				}
			});
			*/
		}
	}
	public abstract void afterLessonMemberChange();
}
