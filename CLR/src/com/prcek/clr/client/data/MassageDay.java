package com.prcek.clr.client.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MassageDay extends BaseModel {
	public List<MassageSlot> slots;
	public MassageDay() {
		slots = new ArrayList<MassageSlot>();
	}
	public MassageDay(long date) {
		// TODO Auto-generated constructor stub
		Date dp = new Date(date);
		set("date",dp);
		slots = new ArrayList<MassageSlot>();
	}
	public void addSlot(long start,long end) {
		slots.add(new MassageSlot(start,end));
	}
	public void addSlot(MassageSlot slot) {
		slots.add(slot);
	}
	public long getDate() {
		Date dp = get("date");
		return dp.getTime();
	}
	
	public Boolean isDate(long l) {
		Date a = get("date");
		Date b = new Date(l);
		if ((a.getDate()==b.getDate()) && (a.getMonth()==b.getMonth()) && (a.getYear()==b.getYear())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public Boolean hasSlots() {
		if (slots.size()!=0) { return Boolean.TRUE; }
		return Boolean.FALSE;
	}
	
}
