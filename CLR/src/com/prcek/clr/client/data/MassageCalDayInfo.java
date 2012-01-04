package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MassageCalDayInfo extends BaseModel {
	public MassageCalDayInfo(){}
	public MassageCalDayInfo(long date, int status, long req_s) {
		set("date",date);
		set("status",status);
		set("req_s",req_s);
	}
	public long getDate() {
		Long l = get("date");
		return l.longValue();
	}
	public int getStatus() {
		Integer i = get("status");
		return i.intValue();
	}
	public long getReqStart() {
		Long l = get("req_s");
		return l.longValue();
	}
}
