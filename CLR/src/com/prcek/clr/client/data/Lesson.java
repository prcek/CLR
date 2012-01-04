package com.prcek.clr.client.data;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Lesson extends BaseModel {
	public enum LessonType { LT_0, LT_1, LT_2, LT_3 };

	public Lesson(){
		
	}
	public Lesson(int id,long datetime, int typ, int capacity, int registered){
		Date dp = new Date(datetime);
		Date tp = new Date(datetime);
		set("id",id);
		set("date",dp);
		set("time",tp);
		set("capacity",capacity);
		set("registered",registered);
		set("type",typ);
	}
	public int getId() {
		Integer x = get("id");
		return x;
	}
	public long getDateTime() {
		Date dp = get("date");
		Date tp = get("time");
		dp.setHours(tp.getHours());
		dp.setMinutes(tp.getMinutes());
		dp.setSeconds(tp.getSeconds());
		return dp.getTime();
	}
	
	public int getCapacity() {
		Number x = get("capacity");
		return x.intValue();
	}
	public int getRegistered() {
		Number x = get("registered");
		return x.intValue();
	}
	public int getType() {
		Number x = get("type");
		return x.intValue();
	}
	
	public boolean isFull() {
		Number c = get("capacity",1);
		Number r = get("registered",0);
		if (c.intValue() <= r.intValue()) {
			return true;
		}
		return false;
	}

}
