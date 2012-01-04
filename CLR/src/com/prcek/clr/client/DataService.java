package com.prcek.clr.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.prcek.clr.client.data.AuthException;
import com.prcek.clr.client.data.Doctor;
import com.prcek.clr.client.data.Holyday;
import com.prcek.clr.client.data.InitDataBundle;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.LessonMember;
import com.prcek.clr.client.data.LessonName;
import com.prcek.clr.client.data.MassageCalDayInfo;
import com.prcek.clr.client.data.MassageDay;
import com.prcek.clr.client.data.MassageItem;
import com.prcek.clr.client.data.MassageItemSum;
import com.prcek.clr.client.data.MassageType;
import com.prcek.clr.client.data.Member;
import com.prcek.clr.client.data.PermaItem;
import com.prcek.clr.client.data.PermaItemSum;
import com.prcek.clr.client.data.PermaType;
import com.prcek.clr.client.data.Lesson.LessonType;

@RemoteServiceRelativePath("remote")
public interface DataService extends RemoteService {
	public String test(String in) throws AuthException;
	public Boolean doLogin(String user, String password, int role);
	public Boolean doLogout();
	public Map<String,String> getStatusInfo(String selector);
	public Map<String,String> getConfig(String selector);
	public String getMemberInfo(int member_id);
	public List<Lesson> getLessons(LessonType lesson_type, Date start_date, int max_count);
	public List<LessonMember> getLessonMembers(int lesson_id);
	public Boolean updateLesson(Lesson l);
	public Integer insertLesson(Lesson l);
	public Boolean deleteLesson(int lesson_id);
//	public Boolean updateLessonMember(LessonMember lm);
	public Integer insertLessonMember(LessonMember lm);
	public Boolean deleteLessonMember(int lesson_member_id);
	public Boolean attendLessonMember(int lesson_member_id, boolean attend);
	
	public PagingLoadResult<Member> getMembers(PagingLoadConfig cfg);
	public PagingLoadResult<Member> queryMembers(PagingLoadConfig cfg);

	public Boolean updateMember(Member m);
	public Integer insertMember(Member m);
	public Boolean deleteMember(int member_id);
	
	public List<Holyday> getHolydays(int year);
	public Boolean deleteHolyday(long date);
	public Boolean insertHolyday(Holyday h);
	public Boolean updateHolyday(Holyday h);
	public Boolean isHolyday(long date);

	public List<MassageDay> getMassageDays(long from_date);
	public MassageDay getMassageDay(long date);
	public Boolean deleteMassageDay(long date);
	public Boolean deleteMassageDays(long start_date, long end_date);
	public Boolean updateMassageDay(MassageDay md);
	public Boolean insertMassageDay(MassageDay md);
	public Boolean insertMassageDays(List<MassageDay> days);
	public Boolean updateMassageDays(List<MassageDay> days, boolean skip_holy);

	public List<MassageItem> getMassageItems(long date);
	public List<Date> getMassagePlannedDays(long from_date);
	public String insertMassageItem(MassageItem mi);
	public String deleteMassageItem(int id);
	public String generateNumber();
	
	public InitDataBundle getInitDataBundle();
	public Date getLastDataChange(String key);
	
	public PagingLoadResult<PermaItem> getPermaItems(PagingLoadConfig cfg);
	public Boolean deletePermaItem(int id);
	public Boolean insertPermaItem(PermaItem pi);
	public Boolean updatePermaItem(PermaItem pi);
	
	public List<PermaItemSum> getPermaItemSum(long start_date, long end_date, int doctor_id);
	public List<MassageItemSum> getMassageItemSum(long start_date, long end_date);
	
	public List<MassageCalDayInfo> getMassageCalDayInfo(long[] req_days);
	
	public Boolean deleteHolydayPlan(int year);
	
	
	public Boolean updateLessonNames(List<LessonName> lesson_names);
	public Boolean updateMassageTypes(List<MassageType> massage_types);
	public Boolean updateDoctors(List<Doctor> doctors);
	public Boolean updatePermaTypes(List<PermaType> perma_types);
	
}
