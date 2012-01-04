package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class WeekDay extends BaseModel {
	public WeekDay(){};
	public WeekDay(int id, String longName, String shortName) {
		set("id",id);
		set("long",longName);
		set("short",shortName);
	}	
	public Boolean isValid() {
		Integer i = get("id");
		if (i==-1) { return Boolean.FALSE;}
		return Boolean.TRUE;
	}
}
