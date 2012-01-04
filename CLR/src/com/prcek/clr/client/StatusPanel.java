package com.prcek.clr.client;

import java.util.Map;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StatusPanel extends LayoutContainer {
	public StatusPanel(String v, String help_url) {
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		Text versionText = new Text(v);
		Html htmlHelp =  new Html("<a href=\""+help_url+"\">nápověda</a>");
		Button logout_bt = new Button("odhlásit", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				clr.dataService.doLogout(new AsyncCallback<Boolean>(){
					public void onFailure(Throwable caught) {
						clr.checkForAuthException(caught);
					}

					public void onSuccess(Boolean result) {
						clr.logout_app();
					}
					
				});
			}
		});
		add(logout_bt, new RowData(-1,-1,new Margins(0)));
		add(versionText, new RowData(-1,-1,new Margins(0,10,0,10)));
		add(htmlHelp, new RowData(-1,-1,new Margins(0,10,0,10)));
	}
}
