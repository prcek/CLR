package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class LessonMember extends BaseModel {
	private static String notnull(String s) {
		if (s==null) {
			return "";
		} 
		return s;
	}
	public LessonMember(){
	
	}
	
	public LessonMember(int id, int lesson_id, int order, int member_id, long date, String number, String name, String surname, String phone, boolean attend) {
		set("id",id);
		set("lesson_id",lesson_id);
		set("number",number);
		set("date",date);
		set("order",order);
		set("member_id",member_id);
		set("name",name);
		set("surname",surname);
		set("phone",phone);
		set("attend",attend);
	}
	public boolean getAttend() {
		return (Boolean) get("attend");
	}
	
	public int getId() {
		Integer x = get("id");
		return x;
	}

	public int getLessonId() {
		Integer x = get("lesson_id");
		return x;
	}

	public int getMemberId() {
		Integer x = get("member_id");
		return x;
	}
	public long getDateTime() {
		Long l = get("date");
		return l;
	}
	public String getName() {
		return notnull((String)get("name"));
	}
	public String getSurname() {
		return notnull((String)get("surname"));
	}

	public String getPhone() {
		return notnull((String)get("phone"));
	}
	public String getOrderAsString() {
		Number o = get("order");
		return String.valueOf(o);
		//return notnull((String)get("order"));
	}
	
}
