package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class DayTime extends BaseModel {
	public DayTime(){};
	public DayTime(int time, String name){ 
		set("time",time);
		set("name",name);
	};
	
	public Boolean isValid() {
		Integer i = get("time");
		if (i==-1) { return Boolean.FALSE;}
		return Boolean.TRUE;
	}

	public int getHour() {
		Integer i = get("time");
		return i/(60*60);
	}
	public int getMinute() {
		Integer i = get("time");
		return (i/60)%60;
	}
}
