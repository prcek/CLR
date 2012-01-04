package com.prcek.clr.client.data;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.i18n.client.DateTimeFormat;

public class Holyday extends BaseModel {
	private static String notnull(String s) {
		if (s==null) {
			return "";
		} 
		return s;
	}

	public Holyday() {
		
	}
	public Holyday(long date, String name, String ds) {
		Date dp = new Date(date);
		set("date",dp);
		set("name",name);
		set("date_s",ds);
	}
	public Integer getYear() {
		Date dp = get("date");
		return dp.getYear()+1900;
	}
	public long getDateAsLong() {
		Date dp = get("date");
		return dp.getTime();
	}
	public String getDesc() {
		return notnull((String)get("name"));
	}
}
