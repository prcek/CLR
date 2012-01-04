package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MassageItem extends BaseModel {
	public MassageItem() {}
	public MassageItem(long start, long end) {
		set("start",start);
		set("real_start",start);
		set("end",end);
		set("real_end",end);
		set("member_id",-1);
		set("phone","");
		set("surname","");
		set("type",0);
		set("primary",1);
		set("first",0);
		set("max_slots",0);
		set("desc","");
		set("massage_id",-1);
		set("debug","");
	}
	public void setReservation(String surname, String phone, String desc, int mt, boolean primary, int massage_id) {
		if (phone!=null) {
			set("phone",phone);
		} else {
			set("phone","");
		}
		if (surname!=null) {
			set("surname", surname);
		} else {
			set("surname", "");
		}
		if (desc!=null) {
			set("desc", desc);
		} else {
			set("desc", "");
		}
		
		set("type",mt);
		set("massage_id",massage_id);
		if (primary) {
			set("primary",1);
		} else {
			set("primary",0);
		}
		set("debug","");
	}
	public void setRealEnd(long real_end) {
		set("real_end",real_end);
	}
	public void setRealStart(long real_start) {
		set("real_start",real_start);
	}
	public void setFirst(boolean f) {
		if (f) {
			set("first",1);
		} else {
			set("first",0);
		}
	}
	public void setMaxSlots(int c) {
		set("max_slots",c);
		//set("debug"," "+Integer.toString(c));
	}
	public long getStart() {
		return getAsLong("start");
	}
	
	public long getAsLong(String s) {
		Long l = get(s);
		return l.longValue();
	}
	public int getType() {
		Integer i = get("type");
		return i.intValue();
	}
	public int getMassageID() {
		Integer i = get("massage_id");
		return i.intValue();
	}
	
	public String getSurname() {
		return (String) get("surname");
	}

	public String getPhone() {
		return (String) get("phone");
	}

	public String getDesc() {
		return (String) get("desc");
	}
	
	public Boolean isPrimary() {
		Integer i = get("primary");
		if (i==1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	public Boolean isFirst() {
		Integer i = get("first");
		if (i==1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	public Boolean isPossible(int count) {
		Integer i = get("max_slots");
		if (i>=count) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	public Boolean isEmpty() {
		Integer i = get("type");
		if (i == 0) { return Boolean.TRUE; }
		return Boolean.FALSE;
	}

}
