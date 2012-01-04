package com.prcek.clr.client;

import java.util.List;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.Member;

public class MemberComboBox extends LayoutContainer {
	private int last_member_id = -1;
	private ComboBox<Member> combo;
	private Member last_selected;
	public MemberComboBox() {
		
		setLayout(new FlowLayout(0));
		
		RpcProxy<PagingLoadResult<Member>> proxy = new RpcProxy<PagingLoadResult<Member>>() {
			protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Member>> callback) {
		         clr.dataService.queryMembers((BasePagingLoadConfig)loadConfig, callback);  
			}
		};
		PagingLoader loader = new BasePagingLoader(proxy);  
		   
		ListStore<Member> store = new ListStore<Member>(loader);  
		   
		combo = new ComboBox<Member>();  
		combo.setWidth(300);  
		combo.setDisplayField("desc");
		//combo.setItemSelector("div.search-item");  
		//combo.setTemplate(getTemplate());  
		combo.setStore(store);  
		//combo.setHideTrigger(true);  
		combo.setMinChars(2);
		combo.setPageSize(10); 
		combo.addSelectionChangedListener(new SelectionChangedListener<Member>(){
			public void selectionChanged(SelectionChangedEvent<Member> se) {
				Member m = se.getSelectedItem();
				if (m!=null) {
					if (last_member_id != m.getId()) {
						last_member_id = m.getId();
						onMember(m);
					}
					//MessageBox.info("selected", (String)m.get("desc"), null);
				} else {
					//MessageBox.info("selected", "nic", null);
				}
			}
		});
		
		combo.addListener(Events.Collapse, new Listener<FieldEvent>(){
			public void handleEvent(FieldEvent be) {
		    	   List<Member> s= combo.getSelection();
		    	   if (s!=null) {
		    		   int len = s.size();
		    		   if (len>0) {
		   					Member m = s.get(0);
		   					if (m!=null) {
		   						if (last_member_id != m.getId()) {
		   							last_member_id = m.getId();
		   							last_selected = m;
		   							onMember(m);
		   						}
		   					}
//		    			   MessageBox.info("selected",(String)s.get(0).get("desc"), null);
		    		   } else {
		    			   //MessageBox.info("selected", "nic", null);
		    		   }
		    	   } else {
		    		   
		    	   }
			}
		});
		
		add(combo);
	/*	
		Button btn = new Button("Q");  
		btn.addSelectionListener(new SelectionListener<ButtonEvent>() {  
		       public void componentSelected(ButtonEvent ce) {
		    	   List<Member> s= combo.getSelection();
		    	   if (s!=null) {
		    		   int len = s.size();
		    		   if (len>0) {
		    			   MessageBox.info("selected",(String)s.get(0).get("desc"), null);
		    		   } else {
		    			   MessageBox.info("selected", "nic", null);
		    		   }
		    	   } else {
		    		   
		    	   }
			   }  
		});
		  
		add(btn);
		*/
		
	}
	public void reset() {
		last_member_id = -1;
		last_selected = null;
		combo.setValue(null);
	}
	public void onMember(Member m) {
		
	}
	public Member getMember() {
		return last_selected;
	}
	public void setMember(int member_id) {
		
	}
}
