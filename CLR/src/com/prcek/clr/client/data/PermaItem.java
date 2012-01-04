package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class PermaItem extends BaseModel {
	public PermaItem() {}
	public PermaItem(int id, int doctor_id, int typ_id, int count, int cost, String typ, String user, long date) {
		set("id",id);
		set("doctor_id",doctor_id);
		set("type_id",typ_id);
		set("count",count);
		set("cost",cost);
		set("type",typ);
		set("user",user);
		set("created",date);
	}
	
	public int getId() {
		Integer i = get("id");
		return i.intValue();
	}

	public int getDoctorId() {
		Integer i = get("doctor_id");
		return i.intValue();
	}
	
	public int getTypeId() {
		Integer i = get("type_id");
		return i.intValue();
	}
	
	public int getCount() {
		Integer i = get("count");
		return i.intValue();
	}

	public int getCost() {
		Integer i = get("cost");
		return i.intValue();
	}

	public String getType() {
		String s = get("type");
		return s;
	}

	public String getUser() {
		String s = get("user");
		return s;
	}
	
	public long getCreated() {
		Long l = get("created");
		return l.longValue();
	}
	
}
