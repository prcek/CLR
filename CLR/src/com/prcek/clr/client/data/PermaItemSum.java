package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class PermaItemSum extends BaseModel {
	public PermaItemSum(){}
	public PermaItemSum(int doctor_id, int typ_id, int count, int cost, String typ, int multi) {
		set("doctor_id",doctor_id);
		set("type_id",typ_id);
		set("count",count);
		set("cost",cost);
		set("type",typ);
		set("multi",multi);
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

	public int getMulti() {
		Integer i = get("multi");
		return i.intValue();
	}
	
	public String getType() {
		String s = get("type");
		return s;
	}

	public Boolean isSame(int doctor_id, int typ_id, int count, int cost, String typ) {
		if (doctor_id != getDoctorId()) return Boolean.FALSE;
		if (typ_id != getTypeId()) return Boolean.FALSE;
		if (count != getCount()) return Boolean.FALSE;
		if (cost != getCost()) return Boolean.FALSE;
		if ((typ_id==0) && (typ.compareTo(getType())!=0)) return Boolean.FALSE; 
		return Boolean.TRUE;
	}
	public void incMulti() {
		Integer i = get("multi");
		i++;
		set("multi",i.intValue());
	}
}
