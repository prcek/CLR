package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Member extends BaseModel {
	public Member() {
		
	}
	
	public Member(int id, String number, String name, String surname, String phone, String email, String street, String street_no, String city, long created) {
		set("id",id);
		set("number",number);
		set("name",name);
		set("surname",surname);
		set("phone",phone);
		set("email",email);
		set("street",street);
		set("street_no",street_no);
		set("city",city);
		set("created",created);
		set("desc",number+" "+surname+" "+name);
	}
	public int getId() {
		Integer x = get("id");
		return x;
	}
	public String getAsNonNullString(String name) {
		String s = get(name,"");
		return s;
	}
	public long getDateAsLong(String name) {
		long l= get(name,(long)0);
		return l;
	}
	public long getDateTime() {
		Long l = get("created");
		return l;
	}

	public String getSurname() {
		return getAsNonNullString("surname");
	}

	public String getPhone() {
		return getAsNonNullString("phone");
	}

}
