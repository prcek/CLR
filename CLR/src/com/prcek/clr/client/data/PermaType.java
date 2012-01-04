package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class PermaType extends BaseModel {
	public PermaType(){}
	public PermaType(int id, boolean is_active, String name) {
		set("id",id);
		if (name==null) {
			set("name","");
		} else {
			set("name",name);
		}
		set("active", is_active?1:0);
	}
	public int getId() {
		Integer i = get("id");
		return i.intValue();
	}
	public String getName() {
		return (String) get("name");
	}
	
	public Boolean isActive() {
		Integer i = get("active");
		if (i==1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}


}
