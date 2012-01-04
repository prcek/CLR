package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Doctor extends BaseModel {
	public Doctor(){}
	public Doctor(int id, boolean active, String fullname) {
		set("id",id);
		if (fullname==null) {
			set("fullname", "");
		} else {
			set("fullname", fullname);
		}
		set("active",active?1:0);
	}
	
	public int getId() {
		Integer i = get("id");
		return i.intValue();
	}
	
	public String getFullName() {
		return (String) get("fullname");
	}
	
	public Boolean isActive() {
		Integer i = get("active");
		if (i==1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
