package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MassageType extends BaseModel {
	public MassageType(){}
	public MassageType(int id, boolean is_active,  String name, int slots, boolean is_lava) {
		set("id",id);
		if (name==null) {
			set("name",""); 
		} else {
			set("name",name);
		}
		set("slots",slots);
		if (is_lava) {
			set("lava",1);
		} else {
			set("lava",0);
		}
		if (is_active) {
			set("active",1);
		} else {
			set("active",0);	
		}
	}
	public int getSlots() {
		Integer i = get("slots");
		return i.intValue();
	}
	public int getId() {
		Integer i = get("id");
		return i.intValue();
	}
	public String getName() {
		return (String) get("name");
	}
	public Boolean isLava() {
		Integer i = get("lava");
		if (i==1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	public Boolean isActive() {
		Integer i = get("active");
		if (i==1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
