package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MassageSlot extends BaseModel {
	public MassageSlot() {}
	public MassageSlot(long start, long end) {
		set("start",start);
		set("end",end);
	}
	
	public long getStart() {
		Long l = get("start");
		return l.longValue();
	}
	
	public long getEnd() {
		Long l = get("end");
		return l.longValue();
	}
}
