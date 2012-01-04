package com.prcek.clr.client;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.Member;

public class MemberCombo extends ComboBox<Member> {
	private RpcProxy<PagingLoadResult<Member>> proxy;
	private PagingLoader<PagingLoadResult<Member>> loader;
	private ListStore<Member> store;
	public MemberCombo() {
		proxy = new RpcProxy<PagingLoadResult<Member>>() {
			protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<Member>> callback) {
		         clr.dataService.queryMembers((PagingLoadConfig)loadConfig, callback);  
			}
		};
		loader = new BasePagingLoader<PagingLoadResult<Member>>(proxy);
		store = new ListStore<Member>(loader);
		setStore(store);
		setMinChars(2);
		setPageSize(10);
		setDisplayField("desc");
	}
	

}
