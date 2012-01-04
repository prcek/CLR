package com.prcek.clr.client.data;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class InitDataBundle extends BaseModel {
	
	private Integer role;
	private List<Doctor> doctors;
	private List<PermaType> permaTypes;
	private List<MassageType> massageTypes;
	private List<LessonName> lessonNames;
	
	public InitDataBundle(){}
	public InitDataBundle(Integer role, List<Doctor> doctors, List<PermaType> permaTypes, List<MassageType> massageTypes, List<LessonName> lessonNames){
		this.role = role;
		this.doctors = doctors;
		this.permaTypes = permaTypes;
		this.massageTypes = massageTypes;
		this.lessonNames = lessonNames;
	}
	public List<Doctor> getDoctors() {
		return doctors;
	}
	public List<PermaType> getPermaTypes() {
		return permaTypes;
	}
	public List<MassageType> getMassageTypes() {
		return massageTypes;
	}
	public List<LessonName> getLessonNames() {
		return lessonNames;
	}
	public int getRole() {
		if (role!=null) {
			return role.intValue();
		}
		return -1;
	}
	public Boolean isAdmin() { //role 0,1 
		int r = getRole();
		if (r<0) return Boolean.FALSE;
		if (r>1) return Boolean.FALSE;
		return Boolean.TRUE;
	}
}
