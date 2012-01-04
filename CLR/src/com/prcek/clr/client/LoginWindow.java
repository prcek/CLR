package com.prcek.clr.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoginWindow extends Window {
	private TextField<String> login;
	private TextField<String> password;
	public LoginWindow(String sysname) {
		  setSize(410, 150);  
		  setPlain(true);  
		  setHeading("Přihlášení do systému "+sysname);  
		  setLayout(new FitLayout());
		  setClosable(false);
		  
		  FormPanel form = new FormPanel();
		  form.setHeaderVisible(false);
		  form.setLabelWidth(120);
		  form.setLabelAlign(LabelAlign.RIGHT);
		  login = new TextField<String>();
		  login.setName("login");
		  login.setFieldLabel("Přihlašovací jméno");
		  form.add(login);
		  
		  
		  password = new TextField<String>();
		  password.setName("password");
		  password.setFieldLabel("Heslo");
		  password.setPassword(true);
		  form.add(password);

		  
		  form.setButtonAlign(HorizontalAlignment.CENTER);  
		  form.addButton(new Button("Přihlásit", new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				checkLogin(login.getValue(),password.getValue());
			}
		  }));
		  
		  add(form,new FitData(4));
	}
	
	public void doLogin(boolean debug) {
		if (debug) {
			checkLogin("debug","debug");
		} else {
			login.setValue("");
			password.setValue("");
			show();
		}
	}
	private void checkLogin(String l,String p) {
		if (l==null) l="";
		if (p==null) p="";
		clr.dataService.doLogin(l, p, -1, new AsyncCallback<Boolean>(){

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				MessageBox.alert("Chyba přihlašování", "nelze komunikovat se serverem", null);
			}

			public void onSuccess(Boolean result) {
				// TODO Auto-generated method stub
				if (result) {
					hide();
					login.setValue("");
					password.setValue("");
					onLoginOk();
				} else {
					password.setValue("");
					MessageBox.alert("Chyba přihlašování", "Neplatné přihlašovací jméno nebo heslo", null);
				}
			}
			
		});
	}
	
	public void onLoginOk() {
		
	}
}
