package com.prcek.clr.client;

import java.util.Date;

import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.LoadListener;

public abstract class DataMonitor extends LoadListener {
	private Date last_load_time;
	private Date last_server_time;
	
	public DataMonitor() {
		last_load_time = null;
		last_server_time = null;
	}
	
	public abstract void doReload();
	
	public void afterLoad(){
		last_load_time = new Date();
	}
	public void onServerTime(Date d) {
		if (last_server_time == null) {
			last_server_time = new Date(d.getTime());
			return;
		}
		long diff = d.getTime()  - last_server_time.getTime();
		if (diff>0) {
			last_server_time = new Date(d.getTime());
			if (last_load_time!=null) {
				Date now = new Date();
				diff = now.getTime() - last_load_time.getTime();
				if (diff>10) {
					last_load_time = now;
					doReload();
				}
			}
		}
	}

	@Override
	public void loaderLoad(LoadEvent le) {
		// TODO Auto-generated method stub
		afterLoad();
		super.loaderLoad(le);
	}
	
	
	
}
