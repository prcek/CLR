package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class LessonName extends BaseModel {
	public LessonName(){}
	public LessonName(int id, String name) {
		if (name==null) {
			name="";
		}
		set("id",id);
		set("name",name);
	}
	public int getId() {
		Integer i = get("id");
		return i.intValue();
	}
	
	public String getName() {
		return (String) get("name");
	}
}
