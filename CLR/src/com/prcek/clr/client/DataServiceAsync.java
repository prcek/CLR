package com.prcek.clr.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
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


public interface DataServiceAsync {
	public void test(String in, AsyncCallback<String> callback);
	public void doLogin(String user, String password, int role, AsyncCallback<Boolean> callback);
	public void doLogout(AsyncCallback<Boolean> callback);
	
	public void getConfig(String selector, AsyncCallback<Map<String,String>> callback);
	public void getStatusInfo(String selector, AsyncCallback<Map<String,String>> callback);
	
	public void getLessons(LessonType lesson_type, Date start_date,int max_count, AsyncCallback<List<Lesson>> callback);
	public void getLessonMembers(int lesson_id,AsyncCallback<List<LessonMember>> callback);
	
	public void getMemberInfo(int member_id, AsyncCallback<String> callback);
	
	public void updateLesson(Lesson l,AsyncCallback<Boolean> callback);
	public void insertLesson(Lesson l,AsyncCallback<Integer> callback);
	public void deleteLesson(int lesson_id, AsyncCallback<Boolean> callback);

//	public void updateLessonMember(LessonMember lm,AsyncCallback<Boolean> callback);
	public void insertLessonMember(LessonMember lm,AsyncCallback<Integer> callback);
	public void deleteLessonMember(int lesson_member_id, AsyncCallback<Boolean> callback);
	public void attendLessonMember(int lesson_member_id, boolean attend, AsyncCallback<Boolean> callback);
	
	public void getMembers(PagingLoadConfig cfg, AsyncCallback<PagingLoadResult<Member>> callback);
	public void queryMembers(PagingLoadConfig cfg, AsyncCallback<PagingLoadResult<Member>> callback);
	public void updateMember(Member lm,AsyncCallback<Boolean> callback);
	public void insertMember(Member lm,AsyncCallback<Integer> callback);
	public void deleteMember(int member_id, AsyncCallback<Boolean> callback);

	public void generateNumber(AsyncCallback<String> callback);
	
	public void getHolydays(int year, AsyncCallback<List<Holyday>> callback);
	public void updateHolyday(Holyday h,AsyncCallback<Boolean> callback);
	public void insertHolyday(Holyday h,AsyncCallback<Boolean> callback);
	public void deleteHolyday(long date, AsyncCallback<Boolean> callback);
	public void isHolyday(long date,AsyncCallback<Boolean> callback);

	
	
	public void getMassageDays(long from_date, AsyncCallback<List<MassageDay>> callback);
	public void getMassageDay(long date, AsyncCallback<MassageDay> callback);
	public void deleteMassageDays(long start_date, long end_date,AsyncCallback<Boolean> callback);
	public void deleteMassageDay(long date,AsyncCallback<Boolean> callback);
	public void updateMassageDay(MassageDay md,AsyncCallback<Boolean> callback);
	public void insertMassageDay(MassageDay md,AsyncCallback<Boolean> callback);
	public void insertMassageDays(List<MassageDay> days, AsyncCallback<Boolean> callback);
	public void updateMassageDays(List<MassageDay> days, boolean skip_holy, AsyncCallback<Boolean> callback);
	
	public void getMassageItems(long date, AsyncCallback<List<MassageItem>> callback);
	public void getMassagePlannedDays(long from_date,AsyncCallback<List<Date>> callback);
	public void insertMassageItem(MassageItem mi, AsyncCallback<String> callback);
	public void deleteMassageItem(int id, AsyncCallback<String> callback);

	public void getInitDataBundle(AsyncCallback<InitDataBundle > callback);
	public void getLastDataChange(String key, AsyncCallback<Date> callback);
	
	public void getPermaItems(PagingLoadConfig cfg, AsyncCallback<PagingLoadResult<PermaItem>> callback);
	public void deletePermaItem(int id, AsyncCallback<Boolean> callback);
	public void insertPermaItem(PermaItem pi, AsyncCallback<Boolean> callback);
	public void updatePermaItem(PermaItem pi, AsyncCallback<Boolean> callback);

	public void getPermaItemSum(long start_date, long end_date, int doctor_id, AsyncCallback<List<PermaItemSum>> callback);
	public void getMassageItemSum(long start_date, long end_date, AsyncCallback<List<MassageItemSum>> callback);
	
	public void getMassageCalDayInfo(long[] req_days, AsyncCallback<List<MassageCalDayInfo>> callback);
	
	public void deleteHolydayPlan(int year, AsyncCallback<Boolean> callback);
	void updateLessonNames(List<LessonName> lesson_names,
			AsyncCallback<Boolean> callback);
	void updateMassageTypes(List<MassageType> massage_types,
			AsyncCallback<Boolean> callback);
	void updateDoctors(List<Doctor> doctors, AsyncCallback<Boolean> callback);
	void updatePermaTypes(List<PermaType> perma_types,
			AsyncCallback<Boolean> callback);

	
}
