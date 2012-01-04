package com.prcek.clr.client.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.clr;
import com.prcek.clr.client.data.Lesson.LessonType;

public class Repository {
	
	
	public static Repository ME;

	private static InitDataBundle initDataBundle;
	private static ListStore<WeekDay> weekdaystore; 
	private static ListStore<DayTime> daytimestore; 
	private static ListStore<MassageType> massagetypesstore;
	
	private static ListStore<PermaType> permatypesstore;
	private static ListStore<PermaType> permatypesstorecb;
	private static ListStore<Doctor> doctorsstore;

	
	public static ListStore<PermaType> getPermaTypesStore() {
		if (permatypesstore==null) {
			permatypesstore = new ListStore<PermaType>();
			if (initDataBundle!=null) {
				for(PermaType t: initDataBundle.getPermaTypes()) {
					if (t.isActive()) {
						permatypesstore.add(t);
					}
				}
			}
		}
		return permatypesstore;
	}

	public static ListStore<PermaType> getPermaTypesStoreCB() {
		if (permatypesstorecb==null) {
			permatypesstorecb = new ListStore<PermaType>();
			if (initDataBundle!=null) {
				for(PermaType t: initDataBundle.getPermaTypes()) {
					if (t.isActive()) {
						permatypesstorecb.add(t);
					}
				}
			}
		}
		return permatypesstorecb;
	}
	
	public static ListStore<Doctor> getDoctorsStore() {
		if (doctorsstore==null) {
			doctorsstore = new ListStore<Doctor>();
			if (initDataBundle!=null) {
				for(Doctor d: initDataBundle.getDoctors()) {
					if (d.isActive()) {
						doctorsstore.add(d);
					}
				}
			}
		}
		return doctorsstore;
	}
	
	public static ListStore<MassageType> getMassageTypesStore() {
		if (massagetypesstore==null) {
			massagetypesstore = new ListStore<MassageType>();
			if (initDataBundle!=null) {
				for(MassageType mt: initDataBundle.getMassageTypes()) {
					if (mt.isActive()) {
						massagetypesstore.add(mt);
					}
				}
			}
		}
		return massagetypesstore;
	}
	
	public static ListStore<WeekDay> getWeekDaysStore() {
		if (weekdaystore==null) {
			weekdaystore = createWeekDaysStore();
		}
		return weekdaystore;
	}
	public static ListStore<DayTime> getDayTimesStore() {
		if (daytimestore==null) {
			daytimestore = createDayTimesStore();
		}
		return daytimestore;
	}
/*
	private static ListStore<MassageType> createMassageTypesStore() {
		ListStore<MassageType> types = new ListStore<MassageType>();
		types.add(Repository.getMassageTypes());
		return types;
	}
*/	
	private static ListStore<WeekDay> createWeekDaysStore() {
		ListStore<WeekDay> days = new ListStore<WeekDay>();
		days.add(Repository.getWeekDays());
		return days;
	}
	private static ListStore<DayTime> createDayTimesStore() {
		ListStore<DayTime> times = new ListStore<DayTime>();
		times.add(Repository.getDayTimes());
		return times;
	}
	

	private static List<WeekDay> getWeekDays() {
		List<WeekDay> days = new ArrayList<WeekDay>();
		days.add(new WeekDay(-1,"-------","--"));  
		days.add(new WeekDay(1,"Pondělí","PO"));  
		days.add(new WeekDay(2,"Úterý","ÚT"));  
		days.add(new WeekDay(3,"Středa","ST"));  
		days.add(new WeekDay(4,"Čtvrtek","ČT"));  
		days.add(new WeekDay(5,"Pátek","PÁ"));  
		days.add(new WeekDay(6,"Sobota","SO"));  
		days.add(new WeekDay(0,"Neděle","NE"));  
		return days;
	}
/*
	private static List<MassageType> getMassageTypes() {
		List<MassageType> types = new ArrayList<MassageType>();
		types.add(new MassageType(0,"--------",1,false));  
		types.add(new MassageType(1,"Klasicka",1,false));  
		types.add(new MassageType(2,"Kameny",2,true));  
		types.add(new MassageType(3,"Reflexni",2,false));  
		types.add(new MassageType(4,"Anticelulitidova",2,false));  
		return types;
	}
*/	
	private static List<DayTime> getDayTimes() {
		List<DayTime> times = new ArrayList<DayTime>();
		times.add(new DayTime(-1,"--:--"));  
		for(int h=7; h<21; h++) {
			for(int m=0; m<60; m+=30) {
				String hs = String.valueOf(h);
				if (hs.length()==1) { hs="0"+hs; }
				String ms = String.valueOf(m);
				if (ms.length()==1) { ms="0"+ms; }
				times.add(new DayTime((h*60+m)*60,hs+":"+ms));
			}
		}
		return times;
	}

	
	public Repository() {
		ME = this;
	}
	public void load() {
		clr.dataService.getInitDataBundle(new AsyncCallback<InitDataBundle>(){
			public void onFailure(Throwable arg0) {
				afterInit(Boolean.FALSE);
			}
			public void onSuccess(InitDataBundle arg0) {
				initDataBundle = arg0;
				afterInit(Boolean.TRUE);
			}
		});
	}
	
	public void afterInit(Boolean ok) {
		
	}

	public String getLessonName(LessonType ltype) {
		for (LessonName ln: initDataBundle.getLessonNames()) {
			if (ln.getId()==ltype.ordinal()) {
				return ln.getName();
			}
		}
		return "?";
	}
/*
	public MassageType getMassageType(int id) {
		for(MassageType mt: initDataBundle.getMassageTypes()) {
			if (mt.getId()==id) {
				return mt;
			}
		}
		return null;
	}
*/
	public List<MassageType> getMassageTypes() {
		return  initDataBundle.getMassageTypes();
	}

	public List<Doctor> getDoctors() {
		return  initDataBundle.getDoctors();
	}

	public List<PermaType> getPermaTypes() {
		return  initDataBundle.getPermaTypes();
	}

	
	public Boolean isAdmin() {
		if (Boolean.parseBoolean(clr.getCfg("debug", "true"))) {
			return Boolean.TRUE;
		}
		return initDataBundle.isAdmin();
	}
}
