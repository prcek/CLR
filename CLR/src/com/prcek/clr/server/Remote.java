package com.prcek.clr.server;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;



import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.apache.naming.java.javaURLContextFactory;


//import sun.security.action.GetLongAction;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.prcek.clr.client.DataService;
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
import com.prcek.clr.client.data.MassageSlot;
import com.prcek.clr.client.data.MassageType;
import com.prcek.clr.client.data.Member;
import com.prcek.clr.client.data.PermaItem;
import com.prcek.clr.client.data.PermaItemSum;
import com.prcek.clr.client.data.PermaType;
import com.prcek.clr.client.data.Lesson.LessonType;

@Retention( value=java.lang.annotation.RetentionPolicy.RUNTIME )
@interface RemoteGetMethod
{
    String ContentType() default "text/plain";
    String FileName() default "";
    String[] Args() default {};
}


public class Remote extends RemoteServiceServlet implements DataService{
	
	private static boolean debug_mode = false;
	private Connection db_connection;
	private static String db_url;// = "jdbc:mysql://127.0.0.1/pacosi?useUnicode=yes&characterEncoding=UTF-8&user=pacosi&password=telepath";
	private static String[] non_auth_methods = {"getConfig","doLogin","doLogout"};
	private static RemoteResources resources;
	private static PrintService printService;
	
	
	public void init(ServletConfig cfg) throws ServletException  {
         super.init(cfg);
         
         db_url = cfg.getInitParameter("db_url");
         if (db_url==null) { 
        	 throw new ServletException("db_url paramater missing in web.xml");
         }
         resources = new RemoteResources();
         printService = new PrintService(resources.FONT);
         
         //XX
         String debug = cfg.getInitParameter("debug");
         if (debug==null) {
        	 debug_mode = true; //kdyz neni, pak je to v eclipse, na serveru se dava debug=false; ;-(
         } else {
        	 debug_mode = Boolean.parseBoolean(debug);
         }
         try {
        	 Class.forName("com.mysql.jdbc.Driver").newInstance();
         } catch (Exception ex) {
        	 throw new ServletException("db driver registration problem");
         }
    
         require_db();
         
	}
	
	@RemoteGetMethod(ContentType="text/plain; charset=UTF-8", FileName="xx.txt", Args={"x","y"})
	public Boolean RGM_test(OutputStream out, String[] params) {
		PrintWriter pw = new PrintWriter(out);
		pw.println("test text");
		if (params[0]!=null) {
			pw.println("x="+params[0]);
		}
		if (params[1]!=null) {
			pw.println("y="+params[1]);
		}
		pw.close();
		return Boolean.TRUE;
	}
	
	@RemoteGetMethod(ContentType="application/pdf", FileName="lekce.pdf", Args={"lesson_id"})
	public Boolean RGM_print_lesson(OutputStream out, String[] params) {
		String lesson_id = params[0];
		if (lesson_id==null) return Boolean.FALSE;
		ArrayList<LessonMember> members = new ArrayList<LessonMember>();
		String lesson = "";
		
		try {
			
			  PreparedStatement s = get_db().prepareStatement("SELECT * FROM lekce WHERE id=?");
			  s.setString(1, lesson_id);
			  ResultSet rs = s.executeQuery();
			  int order = 1;
			  if(rs.next()) {
				  lesson = "Lekce " + rs.getString("date");
			  }
		  } catch (SQLException ex) {
			  System.err.println("print_lesson:" + ex.getMessage());
		  	  ex.printStackTrace();
		  	  return Boolean.FALSE;
		  }
		try {
			  PreparedStatement s = get_db().prepareStatement("SELECT * FROM zapis LEFT JOIN klienti ON zapis.klient_id=klienti.id WHERE lekce_id=? ORDER BY `date` ASC");
			  s.setString(1, lesson_id);
			  ResultSet rs = s.executeQuery();
			  int order = 1;
			  while(rs.next()) {
				  LessonMember lm = new LessonMember(rs.getInt("id"),0,order,rs.getInt("klient_id"),rs.getTimestamp("date").getTime(),rs.getString("number"),rs.getString("name"),rs.getString("surname"),rs.getString("phone"),(rs.getInt("attend")!=0));
				  members.add(lm);
				  order++;
			  }
		  } catch (SQLException ex) {
			  System.err.println("print_lesson:" + ex.getMessage());
		  	  ex.printStackTrace();
		  	  return Boolean.FALSE;
		  }
		  
		  
		return printService.print_lecture_members(out,lesson, members);
		//return Boolean.TRUE;
	}
	@RemoteGetMethod(ContentType="application/pdf", FileName="masaze.pdf", Args={"massage_date"})
	public Boolean RGM_print_massage_day(OutputStream out, String[] params) {
		String massage_date = params[0];
		if (massage_date==null) return Boolean.FALSE;
		long l = Long.decode(massage_date);
		Date date = new Date(l);
		String desc = String.format("Plán masáží pro den:  %2d. %2d. %4d", date.getDate(), date.getMonth()+1, date.getYear()+1900);
		List<MassageItem> items  = getMassageItems(l);
		InitDataBundle data = getInitDataBundle();
		return printService.print_massage_day(out, desc, items, data);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		
		String action = request.getParameter("action");

		if (action==null) {
			response.setContentType("text/plain; charset=UTF-8");
	        PrintWriter pw = response.getWriter();
	        pw.println("invalid action");
	        pw.close();
		} else {
			try {
				String method_name = "RGM_"+action;
				
				System.out.println("rpc: "+ getTimeString()+" "+getSessionKey(request)+" "+method_name);
				
				if (!authCheck(method_name,request)) {
					response.setContentType("text/plain; charset=UTF-8");
			        PrintWriter pw = response.getWriter();
			        pw.println("no auth");
			        pw.close();
			        return;
				}
				Class[] argTypes = new Class[] { OutputStream.class, String[].class };

				Method m = this.getClass().getMethod(method_name, argTypes);
				RemoteGetMethod an = null;
				if ((m==null) || (m.getGenericReturnType() != Boolean.class) || ((an = m.getAnnotation(RemoteGetMethod.class))==null)) {
					response.setContentType("text/plain; charset=UTF-8");
			        PrintWriter pw = response.getWriter();
			        pw.println("invalid action");
			        pw.close();
			        return;
				}
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				String[] args_names = an.Args();
				String[] args = new String[args_names.length];
				for(int a=0; a<args_names.length; a++) {
					args[a] = request.getParameter(args_names[a]);
				}
				Boolean r = (Boolean)m.invoke(this,os,args);
				if (r) {
					response.setContentType(an.ContentType());
					if (an.FileName().compareTo("")!=0) { response.setHeader("Content-Disposition", "attachment; filename="+an.FileName()); }
			        response.setHeader("Expires", "0");
			        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			        response.setHeader("Pragma", "public");
			        response.getOutputStream().write(os.toByteArray());
				} else {
					response.setContentType("text/plain; charset=UTF-8");
			        PrintWriter pw = response.getWriter();
			        pw.println("action failed");
			        pw.close();
			        return;
				}
			} catch (Exception ex) {
				response.setContentType("text/plain; charset=UTF-8");
		        PrintWriter pw = response.getWriter();
		        pw.println("action exception");
		        pw.close();
			}
		}
/*		
		response.setContentType("text/plain; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=test.txt");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        PrintWriter pw = response.getWriter();
        pw.println("ahoj");
        pw.close();
    */
	}
	
	public Map<String, String> getStatusInfo(String selector) {
		Map<String,String> status = new HashMap<String, String>();
		HttpSession s =  getThreadLocalRequest().getSession(false);
		if (s!=null) {
			
		}
		return status;
	}
	public Map<String, String> getConfig(String selector) {
		Map<String,String> cfg = new HashMap<String, String>();
		ServletConfig sc = getServletConfig();
		if (sc == null) {
			System.err.println("sc is null!");
			return cfg;
		}
		for (Enumeration<String> e = sc.getInitParameterNames() ; e.hasMoreElements() ;) {
	         String n = e.nextElement();
	         String v = sc.getInitParameter(n);
	         System.out.println("cfg["+n+"]='"+v+"'");
	         cfg.put(n, v);
        }
		cfg.put("server_version",getServletContext().getServerInfo());
		return cfg;
	}

	private String getSessionKey(HttpServletRequest req) {
		  if (req==null) return "no req, no session";
		  HttpSession s =  req.getSession(false);
		  if (s==null) return "no session";
		  return s.getId();
		  //return "";
	}
	private String getTimeString() {
		Date d = new Date();
		return d.toLocaleString();
	}
	@Override
	public String processCall(String payload) throws SerializationException {
		try {
			RPCRequest rpcRequest = RPC.decodeRequest(payload, this.getClass(), this);
			//getServletContext().log("test");
			System.out.println("rpc: "+ getTimeString()+" "+getSessionKey(getThreadLocalRequest())+" "+rpcRequest.getMethod().getName());
			if (!authCheck(rpcRequest.getMethod().getName(),getThreadLocalRequest())) {
				getServletContext().log("method "+rpcRequest.getMethod().getName()+" blocked, no auth!");
				AuthException aex = new AuthException();
 		        return RPC.encodeResponseForFailure(null, aex);
			}
		    return RPC.invokeAndEncodeResponse(this, rpcRequest.getMethod(), rpcRequest.getParameters(), rpcRequest.getSerializationPolicy());
		 } catch (IncompatibleRemoteServiceException ex) {
		      getServletContext().log("An IncompatibleRemoteServiceException was thrown while processing this call.",ex);
		      return RPC.encodeResponseForFailure(null, ex);
		 }
	}

  private boolean authCheck(String method_name, HttpServletRequest req) {
	  HttpSession s =  req.getSession(false);
	  if (s==null) {
		  System.out.println("no session, during authCheck");
	  } else {
		  //System.out.println("session ready, during authCheck");
		  Boolean a = (Boolean)s.getAttribute("auth");
		  if ((a!=null) && (a)) {
			  return true;
		  }
	  }
	  for(String m: non_auth_methods) {
		  if (m.compareTo(method_name)==0) return true;
	  }
	  return false;
  }
	
  private Connection get_db() throws SQLException {
	  boolean ok = false;
	  int max = 5;
	  while((!ok) && ((max--)>0)) {
		  if (db_connection == null) {
			  db_connection = DriverManager.getConnection(db_url);
		  }
	  
		  try {
			  PreparedStatement s = db_connection.prepareStatement("SELECT 1 as ok");
			  ResultSet rs = s.executeQuery();
			  if(rs.next()) {
				  int i = rs.getInt("ok");
				  if (i==1) {
					  ok = true;
					  //System.out.println("db_connection test query ok");
				  }
			  }
			  s.close();
			  rs.close();
		  } catch (SQLException ex) { 
			  System.err.println("db_connection test queury problem" + ex.getMessage());
		  	  ex.printStackTrace();
		  }
		  if (!ok) {
			  try {
				  db_connection.close();
		  		  db_connection = null;
			  } catch (SQLException ex) {
				  System.err.println("db_connection_close problem" + ex.getMessage());
			  	  ex.printStackTrace();
			  }
		  }
	  }
	  if (!ok) {
		  throw new SQLException("can't reconnect db");
	  }
	  return db_connection;
  }
  private void require_db() throws ServletException{
	  	try {
	  		if (db_connection != null) {
	  			db_connection.close();
	  			db_connection = null;
	  		} 
	  		db_connection = DriverManager.getConnection(db_url);
	  	} catch (SQLException ex){
	         System.err.println("require_db:" + ex.getMessage());
	  		 ex.printStackTrace();
	         throw new ServletException("can't connect db");
	  	}
  }
	
  public String test(String in) throws AuthException{
		// TODO Auto-generated method stub
	    HttpSession s =  getThreadLocalRequest().getSession(true);
	    if (s.isNew()) {
	    	s.setAttribute("test", new Date());
	    }
	    if (true) {
	    	throw new AuthException();
	    }
	    long l=s.getLastAccessedTime();
	    Date d = new Date(l);
	    Date d2 = (Date)s.getAttribute("test");
		return s.getId()+"|"+d.toGMTString()+"|"+d2.toGMTString();
	}

  private void createAuthSession(String user, int role) {
	  HttpSession s = getThreadLocalRequest().getSession(true);
	  s.setAttribute("user", user);
	  s.setAttribute("role", role);
	  s.setAttribute("auth", Boolean.TRUE);
  }
  public Boolean doLogin(String user,String password,int role) {
	  if (debug_mode) {
		  createAuthSession(user,role);
		  return Boolean.TRUE;
	  }

	  try {
		  PreparedStatement s = get_db().prepareStatement("SELECT * FROM uzivatele WHERE login=? and password=?");
		  s.setString(1,user);
		  s.setString(2,password);
		  ResultSet rs = s.executeQuery();
		  if(rs.next()) {
			  createAuthSession(user,rs.getInt("role"));
			  s.close();
			  rs.close();
			  return Boolean.TRUE;
		  }
		  s.close();
		  rs.close();
	  } catch (SQLException ex) {
		  System.err.println("doLogin:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }

/*	  
	  
	  if ((user.compareTo("admin")==0) && (password.compareTo("nimda")==0)) {
		  createAuthSession(user,role);
		  return Boolean.TRUE;
	  }
*/	  
      //System.out.println("doLogin");
	  return Boolean.FALSE;
  }
  
  
  public Boolean doLogout() {
	HttpSession s =getThreadLocalRequest().getSession(false);
	if (s!=null) {
		s.invalidate();
	}
	return Boolean.TRUE;
  }
  private void dataChangeInsert(String key) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("INSERT INTO `data_change` (`key`,`last`) VALUES (?,NOW())");
		  s.setString(1,key);
		  s.executeUpdate();
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("insertLesson:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
  }
  
  private void dataChange(String key) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `data_change` SET `last`=NOW() WHERE `key`=?");
		  s.setString(1, key);
		  if (s.executeUpdate() != 1) {
			  dataChangeInsert(key);
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("updateLesson:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
  }
  
public List<Lesson> getLessons(LessonType lesson_type, Date start_date, int max_count) {
	  List<Lesson> lessons = new ArrayList<Lesson>();
	  try {
		  java.sql.Date date = new java.sql.Date(start_date.getTime());
		  PreparedStatement s = get_db().prepareStatement("SELECT lekce.*,count(zapis.id) as `registered` FROM lekce LEFT JOIN zapis ON lekce.id=zapis.lekce_id WHERE lekce.date>=? and lekce.typ=? GROUP BY lekce.id ORDER BY `date` LIMIT ?");
		  s.setDate(1, date);
		  s.setInt(2, lesson_type.ordinal());
		  s.setInt(3, max_count);
		  ResultSet rs = s.executeQuery();
		  while(rs.next()) {
			  Lesson l = new Lesson(rs.getInt("id"),rs.getTimestamp("date").getTime(),rs.getInt("typ"),rs.getInt("capacity"),rs.getInt("registered"));
			  lessons.add(l);
		  }
		  s.close();
		  rs.close();
	  } catch (SQLException ex) {
		  System.err.println("getLessons:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
	  return lessons;
  }
  
  
  public Boolean updateLesson(Lesson l) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `lekce` SET `date`=?, `capacity`=? WHERE id=?");
		  s.setTimestamp(1, new java.sql.Timestamp(l.getDateTime()));
		  s.setInt(3, l.getId());
		  s.setInt(2, l.getCapacity());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("updateLesson:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
	  
  }
  public Integer insertLesson(Lesson l) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("INSERT INTO `lekce` (`date`,`typ`,`capacity`) VALUES (?,?,?)");
		  s.setTimestamp(1, new java.sql.Timestamp(l.getDateTime()));
		  s.setInt(2, l.getType());
		  s.setInt(3, l.getCapacity());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return -1;
		  }
		  ResultSet rs = s.getGeneratedKeys();
		  if (rs.next()) {
			  int ret = rs.getInt(1);
			  rs.close();
			  s.close();
			  return ret;
		  }
		  rs.close();
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("insertLesson:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return -1;
	  }
	  return -1;
  }
  public Boolean deleteLesson(int lesson_id) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("DELETE FROM `lekce` WHERE id=?");
		  s.setInt(1, lesson_id);
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("deleteLesson:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
  }
  
  
  
  
  public List<LessonMember> getLessonMembers(int lesson_id) {
	  List<LessonMember> members = new ArrayList<LessonMember>();
	  try {
		  PreparedStatement s = get_db().prepareStatement("SELECT * FROM zapis LEFT JOIN klienti ON zapis.klient_id=klienti.id WHERE lekce_id=? ORDER BY `date` ASC");
		  s.setInt(1, lesson_id);
		  ResultSet rs = s.executeQuery();
		  int order = 1;
		  while(rs.next()) {
			  boolean attend = false;
			  if (rs.getInt("attend")!=0) {
				  attend = true;
			  }
			  LessonMember lm = new LessonMember(rs.getInt("id"),lesson_id,order,rs.getInt("klient_id"),rs.getTimestamp("date").getTime(),rs.getString("number"),rs.getString("name"),rs.getString("surname"),rs.getString("phone"),attend);
			  members.add(lm);
			  order++;
		  }
		  s.close();
		  rs.close();
	  } catch (SQLException ex) {
		  System.err.println("getLessonMembers:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
	  return members;
	}
  
  /*
  public Boolean updateLessonMember(LessonMember lm) {
	  try {
		  PreparedStatement s = db.prepareStatement("UPDATE `zapis` SET jmeno=?, prijmeni=? WHERE id=?");
		  s.setString(1, lm.getName());
		  s.setString(2, lm.getSurname());
		  s.setInt(3, lm.getId());
		  if (s.executeUpdate() != 1) {
			  return Boolean.FALSE;
		  }
	  } catch (SQLException ex) {
		  System.err.println("updateLessonMember:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
  }
  */
  
  
  
  public Integer insertLessonMember(LessonMember lm) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("INSERT INTO `zapis` (`lekce_id`,`klient_id`,`date`) VALUES (?,?,NOW())");
		  s.setInt(1, lm.getLessonId());
		  s.setInt(2, lm.getMemberId());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return -1;
		  }
		  ResultSet rs = s.getGeneratedKeys();
		  if (rs.next()) {
			  int ret = rs.getInt(1);
			  s.close();
			  rs.close();
			  return ret;
		  }
	  } catch (SQLException ex) {
		  System.err.println("insertLessonMember:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return -1;
	  }
	  return -1;
	  
  }
  public Boolean deleteLessonMember(int lesson_member_id) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("DELETE FROM `zapis` WHERE id=?");
		  s.setInt(1, lesson_member_id);
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("deleteLessonMember:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
  }
  public Boolean attendLessonMember(int lesson_member_id, boolean attend) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `zapis` SET `attend`=? WHERE id=?");
		  if (attend) {
			  s.setInt(1, 1);
		  } else {
			  s.setInt(1, 0);
		  }
		  s.setInt(2, lesson_member_id);
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("attendLessonMember:" + ex.getMessage());
		  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
   }
  
  
  public PagingLoadResult<Member> getMembers(PagingLoadConfig cfg) {
	  return getMembers_internal(null, cfg);
  }
  public PagingLoadResult<Member> queryMembers(PagingLoadConfig cfg) {
	  String q = cfg.get("query");
	  return getMembers_internal(q, cfg);
  }
  
  private PagingLoadResult<Member> getMembers_internal(String query, PagingLoadConfig cfg) {
	  List<Member> members = new ArrayList<Member>();
	  int offset = cfg.getOffset();
	  int limit = cfg.getLimit();
	  int total = 1;
	  try {
		  List<String> query_values = new ArrayList<String>();
		  String query_sql = "";
		  if (query!=null) {
			  String[] keys = query.trim().split("\\s+",4);
			  for(String k: keys) {
	//			  System.out.println("q:"+k);
				  if (query_sql.length()>0) {
					  query_sql = query_sql + " && ";
				  }
				  query_sql = query_sql + " (`number` LIKE ? ) || (`name` LIKE ?) || (`surname` LIKE ?) ";
				  query_values.add(k+"%");
				  query_values.add("%"+k+"%");
				  query_values.add("%"+k+"%");
			  }
			  query_sql = "( " + query_sql + " )";
		  } else {
			  query_sql = "(1)";
		  }
		   
		  
		  String sort_dir = cfg.getSortInfo().getSortDir().name();
		  String sort_field = cfg.getSortInfo().getSortField();
		  if ((sort_dir==null) || (sort_dir.compareTo("NONE")==0)) {
			  sort_dir="ASC";
		  }
		  if (sort_field==null) {
			  sort_field="number";
		  }
		//  System.out.println("sortdir:"+sort_dir);
		//  System.out.println("sortfield:"+sort_field);
		//  System.out.println("query_sql:"+query_sql);
		  PreparedStatement s = get_db().prepareStatement("SELECT * FROM klienti WHERE deleted=0 AND (" + query_sql +") ORDER BY `"+sort_field+"` "+sort_dir+" LIMIT ?,?");
		  int pidx = 1;
		  for(String v: query_values) {
			  s.setString(pidx++, v);
		  }
		  s.setInt(pidx++, offset);
		  s.setInt(pidx++, limit);
		  ResultSet rs = s.executeQuery();
		  while(rs.next()) {
			  
			  long reg = 0;
			  try {
				  reg = rs.getTimestamp("created").getTime();
			  } catch (Exception ex) {
				  
			  }
			  
			  Member m = new Member(
					  rs.getInt("id"),
					  rs.getString("number"),
					  rs.getString("name"),
					  rs.getString("surname"),
					  rs.getString("phone"),
					  rs.getString("email"),
					  rs.getString("street"),
					  rs.getString("street_no"),
					  rs.getString("city"),
					  reg
			  	);
			  members.add(m);
		  }
		  s.close();
		  rs.close();
		  PreparedStatement s2 = get_db().prepareStatement("SELECT count(*) as total FROM klienti WHERE deleted=0 and ("+query_sql+")");
		  int pidx2 = 1;
		  for(String v: query_values) {
			  s2.setString(pidx2++, v);
		  }
		  rs = s2.executeQuery();
		  if (rs.next()) {
			  total = rs.getInt("total");
		  }
		  s2.close();
		  rs.close();
	  } catch (SQLException ex) {
		  System.err.println("getMembers:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
	  PagingLoadResult<Member> res = new BasePagingLoadResult<Member>(members,offset,total);
	  //res.getData().
	  return res;
  }

  
  
  public Boolean deleteMember(int member_id) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `klienti` SET deleted=1 WHERE id=?");
		  s.setInt(1, member_id);
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("deleteMember:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
  }

  public Integer insertMember(Member m) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("INSERT INTO `klienti` (`number`,`name`,`surname`,`phone`,`email`,`street`,`street_no`,`city`,`created`) VALUES (?,?,?,?,?,?,?,?,NOW())");
		  s.setString(1, m.getAsNonNullString("number"));
		  s.setString(2, m.getAsNonNullString("name"));
		  s.setString(3, m.getAsNonNullString("surname"));
		  s.setString(4, m.getAsNonNullString("phone"));
		  s.setString(5, m.getAsNonNullString("email"));
		  s.setString(6, m.getAsNonNullString("street"));
		  s.setString(7, m.getAsNonNullString("street_no"));
		  s.setString(8, m.getAsNonNullString("city"));
//		  s.setDate(9, new java.sql.Date(m.getDateAsLong("created")));
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return -1;
		  }
		  ResultSet rs = s.getGeneratedKeys();
		  if (rs.next()) {
			  int ret = rs.getInt(1);
			  s.close();
			  rs.close();
			  return ret;
		  }
		  s.close();
		  rs.close();
	  } catch (SQLException ex) {
		  System.err.println("insertMember:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return -1;
	  }
	  return -1;
	  
  }

  public Boolean updateMember(Member m) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `klienti` SET `number`=?,`name`=?, `surname`=?, `phone`=?, `email`=?, `street`=?, `street_no`=?, `city`=?, `created`=? WHERE id=?");
		  s.setString(1, m.getAsNonNullString("number"));
		  s.setString(2, m.getAsNonNullString("name"));
		  s.setString(3, m.getAsNonNullString("surname"));
		  s.setString(4, m.getAsNonNullString("phone"));
		  s.setString(5, m.getAsNonNullString("email"));
		  s.setString(6, m.getAsNonNullString("street"));
		  s.setString(7, m.getAsNonNullString("street_no"));
		  s.setString(8, m.getAsNonNullString("city"));
		  s.setDate(9, new java.sql.Date(m.getDateAsLong("created")));
		  s.setInt(10, m.getId());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("updateMember:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
  }
  private String genNum() {
	  Random rnd = new Random();
	  int num[] = new int[5];
	  num[1] = rnd.nextInt(10);
	  num[2] = rnd.nextInt(10);
	  num[3] = rnd.nextInt(10);
	  num[4] = rnd.nextInt(9)+1;
	  
	  int c = 10-((num[1]*3 + num[2]*1 + num[3]*3 + num[4]*1)%10);
	  if (c==10) { c=0; }
	  num[0] = c;
	  String res = "";
	  for(int i=4; i>=0; i--) {
		  res=res+Integer.toString(num[i]);
	  }
	  return res;
  }
  public String generateNumber() {
	  String n="chyba";   
	  boolean found = false;  
	  while(!found)  {
		  n = genNum();
		  try {
			  PreparedStatement s = get_db().prepareStatement("SELECT `number` FROM klienti WHERE `number`=?");
			  s.setString(1, n);
			  ResultSet rs = s.executeQuery();
			  if (rs.next()) {
				  
			  } else {
				  found = true;
			  }
			  s.close();
			  rs.close();
		  } catch (SQLException ex) {
			  System.err.println("getMembers:" + ex.getMessage());
			  ex.printStackTrace();
		  }
	  }
 
	  return n;
  }

public String getMemberInfo(int member_id) {
	// TODO Auto-generated method stub
	//SELECT zapis.attend 	FROM zapis 	LEFT JOIN lekce ON lekce.id = lekce_id 	WHERE klient_id=16 AND lekce.data<NOW() ORDER BY lekce.date DESC LIMIT 0 , 10
	  int lesson_count=0;
	  int attend_count=0;
	  try {
		  PreparedStatement s = get_db().prepareStatement("SELECT zapis.attend 	FROM zapis 	LEFT JOIN lekce ON lekce.id = lekce_id 	WHERE klient_id=? AND lekce.date<NOW() ORDER BY lekce.date DESC LIMIT 0 , 10");
		  s.setInt(1, member_id);
		  ResultSet rs = s.executeQuery();
		  while(rs.next()) {
			  if (rs.getInt("attend")!=0) {
				  attend_count++;
			  }
			  lesson_count++;
		  }
		  s.close();
		  rs.close();
	  } catch (SQLException ex) {
		  System.err.println("getLessonMembers:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }

	
	return "účast na posledních lekcích "+String.valueOf(attend_count)+"/"+String.valueOf(lesson_count);
}

public List<Holyday> getHolydays(int year) {
	// TODO Auto-generated method stub
	  List<Holyday> holydays = new ArrayList<Holyday>();
	  try {
		  PreparedStatement s = get_db().prepareStatement("SELECT datum,popis	FROM svatky WHERE YEAR(datum)=? ORDER BY datum ASC");
		  s.setInt(1, year);
		  ResultSet rs = s.executeQuery();
		  while(rs.next()) {
			    String ds = "xx";
			    Date d = rs.getDate("datum");
			    ds = String.format("%02d-%02d-%02d", d.getDate(),d.getMonth()+1, d.getYear()+1900);
			  	Holyday h = new Holyday(rs.getDate("datum").getTime(),rs.getString("popis"),ds);
			    holydays.add(h);
		  }
		  s.close();
		  rs.close();
	  } catch (SQLException ex) {
		  System.err.println("getHolydays:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
	return holydays;
}

public Boolean deleteHolyday(long date) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("DELETE FROM `svatky` WHERE datum=?");
		  s.setDate(1, new java.sql.Date(date));
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("deleteHolyday:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
}

public Boolean insertHolyday(Holyday h) {
	// TODO Auto-generated method stub
	  try {
		  PreparedStatement s = get_db().prepareStatement("INSERT INTO `svatky` (`datum`,`popis`) VALUES (?,?)");
		  s.setDate(1, new java.sql.Date(h.getDateAsLong()));
		  s.setString(2, h.getDesc());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
		  return Boolean.TRUE;
	  } catch (SQLException ex) {
		  System.err.println("insertHolyday:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
    return Boolean.FALSE;
}

public Boolean updateHolyday(Holyday h) {
	// TODO Auto-generated method stub
	return null;
}


public Boolean isHolyday(long date) {
	// TODO Auto-generated method stub
	Boolean res = Boolean.FALSE;
	  try {
		  PreparedStatement s = get_db().prepareStatement("SELECT *	FROM svatky WHERE DATE(datum)=?");
		  s.setDate(1, new java.sql.Date(date));
		  ResultSet rs = s.executeQuery();
		  if (rs.next()) {
			  res = Boolean.TRUE;
		  }
		  s.close();
		  rs.close();
	  } catch (SQLException ex) {
		  System.err.println("getHolydays:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
	return res;
}

public MassageDay getMassageDay(long date) {
	MassageDay day = new MassageDay(date);
	 
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM masaze_plan WHERE DATE(zacatek)=? ORDER BY zacatek ASC LIMIT 200");
		s.setDate(1, new java.sql.Date(date));
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			long s_time = rs.getTimestamp("zacatek").getTime(); 
			long e_time = rs.getTimestamp("konec").getTime();
			day.addSlot(s_time, e_time);
		}
		s.close();
		rs.close();
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	return day;
}


public List<MassageDay> getMassageDays(long from_date) {
	List<MassageDay> days = new ArrayList<MassageDay>();
	 
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM masaze_plan WHERE DATE(zacatek)>=? ORDER BY zacatek ASC LIMIT 200");
		s.setDate(1, new java.sql.Date(from_date));
		ResultSet rs = s.executeQuery();
		
		MassageDay cd = null;
		while(rs.next()) {
			long s_time = rs.getTimestamp("zacatek").getTime(); 
			long e_time = rs.getTimestamp("konec").getTime();
			if ((cd==null) || (!cd.isDate(s_time))) {
				if (cd!=null) {
					days.add(cd);
				}
				Date sd = new Date(s_time);
				Date sd2 = new Date(sd.getYear(),sd.getMonth(),sd.getDate());
				cd = new MassageDay(sd2.getTime());
			}
			cd.addSlot(s_time, e_time);
		}
		s.close();
		rs.close();
		if (cd!=null) {
			days.add(cd);
		}
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	
	
	return days;
}

public Boolean deleteMassageDay(long date) {
 	  dataChange("massage");
	  try {
		  PreparedStatement s = get_db().prepareStatement("DELETE FROM `masaze_plan` WHERE DATE(zacatek)=?");
		  s.setDate(1, new java.sql.Date(date));
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("deleteMassageDays:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
}


public Boolean deleteMassageDays(long start_date, long end_date) {
   	  dataChange("massage");
	  try {
		  PreparedStatement s = get_db().prepareStatement("DELETE FROM `masaze_plan` WHERE DATE(zacatek)>=? and DATE(zacatek)<=?");
		  s.setDate(1, new java.sql.Date(start_date));
		  s.setDate(2, new java.sql.Date(end_date));
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("deleteMassageDays:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
}

public Boolean updateMassageDay(MassageDay md) {
	dataChange("massage");
	deleteMassageDay(md.getDate());
	if (!insertMassageDay(md)) return Boolean.FALSE;
	return Boolean.TRUE;
}
public Boolean insertMassageDay(MassageDay md) {
	dataChange("massage");
	for(MassageSlot ms : md.slots) {
		java.sql.Timestamp s_time = new java.sql.Timestamp(ms.getStart());
		java.sql.Timestamp e_time = new java.sql.Timestamp(ms.getEnd());
		if (!s_time.before(e_time)) {
			return Boolean.FALSE;
		}
		try {
			PreparedStatement s = get_db().prepareStatement("INSERT INTO `masaze_plan` (`zacatek`,`konec`) VALUES (?,?)");
			s.setTimestamp(1, s_time);
			s.setTimestamp(2, e_time);
			if (s.executeUpdate() != 1) {
				s.close();
			  return Boolean.FALSE;
			}
			s.close();
		} catch (SQLException ex) {
		    System.err.println("insertHolyday:" + ex.getMessage());
		    ex.printStackTrace();
		    return Boolean.FALSE;
		}
	}
	return Boolean.TRUE;
}
public Boolean insertMassageDays(List<MassageDay> days) {
	for (MassageDay d: days) {
		if (!insertMassageDay(d)) {
			return Boolean.FALSE;
		}
	}
	return Boolean.TRUE;
}
public Boolean updateMassageDays(List<MassageDay> days, boolean skip_holy) {
	for (MassageDay d: days) {
		
		if (skip_holy) {
			if (isHolyday(d.getDate())) {
				continue;
			}
		}
		
		if (!updateMassageDay(d)) {
			return Boolean.FALSE;
		}
	}
	return Boolean.TRUE;
}

public List<MassageItem> getMassageItems(long date) {
	List<MassageItem> items = new ArrayList<MassageItem>();
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM masaze_plan WHERE DATE(zacatek)=? ORDER BY zacatek");
		s.setDate(1, new java.sql.Date(date));
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			long s_time = rs.getTimestamp("zacatek").getTime(); 
			long e_time = rs.getTimestamp("konec").getTime();
			long min30 = 1000*60*30;
			while(s_time<e_time) {
				MassageItem mi = new MassageItem(s_time,s_time+min30);
				items.add(mi);
				s_time+=min30;
			}
		}
		rs.close();
		s.close();
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}

	
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM masaze WHERE DATE(zacatek)=? ORDER BY zacatek");
		s.setDate(1, new java.sql.Date(date));
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			int id = rs.getInt("id");
			long s_time = rs.getTimestamp("zacatek").getTime(); 
			long e_time = rs.getTimestamp("konec").getTime();
			int typ = rs.getInt("typ");
			String phone = rs.getString("telefon");
			String surname = rs.getString("prijmeni");
			String desc = rs.getString("popis");
			
			long slots = ((e_time - s_time) / (1000*60*30));
			if ((slots<=0)|| (slots>4)) { continue; };
			for(MassageItem mi: items) {
				if (!mi.isEmpty()) { continue; }
				long is = mi.getAsLong("start");
				long ie = mi.getAsLong("end");
				if ((is>=s_time) && (e_time>=ie)) {
					mi.setReservation(surname, phone,desc,  typ,(is==s_time),id);
					mi.setRealEnd(e_time);
					mi.setRealStart(s_time);
				}
			}
		}
		rs.close();
		s.close();
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	
	for(int i=0; i<items.size(); i++) {
		if (i==0) {
			items.get(i).setFirst(true);
		} else {
			if (items.get(i-1).getAsLong("end")==items.get(i).getAsLong("start")) {
				items.get(i).setFirst(false);
			} else {
				items.get(i).setFirst(true);
			}
		}

		items.get(i).setMaxSlots(1);
		for(int n=1; n<48; n++) {
			if ((i+n)<items.size()) {
				if ((items.get(i+n).isEmpty() || !items.get(i+n).isPrimary() ) && (items.get(i+n).getAsLong("start")==items.get(i+n-1).getAsLong("end"))) {
					items.get(i).setMaxSlots(n+1);
				} else {
					break;
				}
			} else {
				break;
			}
		}
	}
	
	return items;
}

public List<Date> getMassagePlannedDays(long from_date) {
	List<Date> dates = new ArrayList<Date>();
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT DISTINCT DATE(zacatek) as zac FROM masaze_plan WHERE DATE(zacatek)>=? ORDER BY zacatek ASC LIMIT 10");
		s.setDate(1, new java.sql.Date(from_date));
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			dates.add(new Date(rs.getDate("zac").getTime())); 
		}
		s.close();
		rs.close();
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	return dates;
}



public String insertMassageItem(MassageItem mi) {
	 dataChange("massage");
	// TODO Auto-generated method stub
	java.sql.Timestamp s_time = new java.sql.Timestamp(mi.getAsLong("start"));
	java.sql.Timestamp e_time = new java.sql.Timestamp(mi.getAsLong("end"));
	if (!s_time.before(e_time)) {
		return "Konec je pred pocatkem!";
	}


	if (mi.getMassageID()!=-1) {
		// remove old
		  try {
			  PreparedStatement s = get_db().prepareStatement("DELETE FROM `masaze` WHERE id=?");
			  s.setInt(1, mi.getMassageID());
			  if (s.executeUpdate() != 1) {
				  s.close();
				  return "Nelze odstranit puvodni rezervaci (executeUpdate error)";
			  }
			  s.close();
		  } catch (SQLException ex) {
			  System.err.println("deleteMassageDays:" + ex.getMessage());
		  	  ex.printStackTrace();
			  return "Nelze odstranit puvodni rezervaci (SQL Exception)";
		  }
		
	}
	//TODO check for overlap
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT count(*) as ov FROM masaze WHERE (?>=zacatek and ?<konec) or (?>zacatek and ?<=konec)");
		s.setTimestamp(1, s_time);
		s.setTimestamp(2, s_time);
		s.setTimestamp(3, e_time);
		s.setTimestamp(4, e_time);
		ResultSet rs = s.executeQuery();
		if (rs.next()) {
			int ov = rs.getInt("ov");
			if (ov!=0) {
				s.close();
				rs.close();
				return "Interni chyba, prekryv zaznamu";
			}
		}
		s.close();
		rs.close();
	} catch (SQLException ex) {
		System.err.println("insertHolyday:" + ex.getMessage());
		ex.printStackTrace();
		return "Interni chyba (SQL Exception)";
	}
	
	try {
		PreparedStatement s = get_db().prepareStatement("INSERT INTO `masaze` (`zacatek`,`konec`,`typ`, `prijmeni`,`telefon`, `popis`) VALUES (?,?,?,?,?,?)");
		s.setTimestamp(1, s_time);
		s.setTimestamp(2, e_time);
		s.setInt(3, mi.getType());
		s.setString(4, (String)mi.get("surname"));
		s.setString(5, (String)mi.get("phone"));
		s.setString(6, (String)mi.get("desc"));
		if (s.executeUpdate() != 1) {
			  s.close();
			  return "Interni chyba (excuteUpdate)";
		}
		s.close();
	} catch (SQLException ex) {
	    System.err.println("insertHolyday:" + ex.getMessage());
	    ex.printStackTrace();
	    return "Interni chyba (SQL Exception)";
	}
	
	return null;
}

public String deleteMassageItem(int id) {
	  dataChange("massage");
	  try {
		  PreparedStatement s = get_db().prepareStatement("DELETE FROM `masaze` WHERE id=?");
		  s.setInt(1, id);
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return "Nelze odstranit rezervaci (executeUpdate error)";
		  }
    	  s.close();
	  } catch (SQLException ex) {
		  System.err.println("deleteMassageDays:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return "Nelze odstranit rezervaci (SQL Exception)";
	  }
	  return null;
}

public Date getLastDataChange(String key) {
	// TODO Auto-generated method stub
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT `last` FROM data_change WHERE `key`=?");
		s.setString(1, key);
		ResultSet rs = s.executeQuery();
		if(rs.next()) {
			return new Date(rs.getTimestamp("last").getTime());
		}
		rs.close();
  	  	s.close();
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	return new Date(0);
}

public InitDataBundle getInitDataBundle() {
	List<Doctor> doctors = new ArrayList<Doctor>();
	List<PermaType> permaTypes = new ArrayList<PermaType>();
	List<MassageType> massageTypes = new ArrayList<MassageType>();
	List<LessonName> lessonNames = new ArrayList<LessonName>();
	
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM doktori ORDER BY id");
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			boolean is_active = false;
			if (rs.getInt("aktivni")==1) {
				is_active = true;
			}
			doctors.add(new Doctor(rs.getInt("id"),is_active, rs.getString("jmeno")));
		}
		rs.close();
  	  	s.close();
	} catch (SQLException ex) {
		System.err.println("getInitDataBundle:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM permanentky_co ORDER BY id");
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			boolean is_active = false;
			if (rs.getInt("aktivni")==1) {
				is_active = true;
			}
			
			permaTypes.add(new PermaType(rs.getInt("id"),is_active, rs.getString("nazev")));
		}
		rs.close();
  	  	s.close();
	} catch (SQLException ex) {
		System.err.println("getInitDataBundle:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM masaze_typy ORDER BY id");
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			boolean is_lava = false;
			if (rs.getInt("kameny")==1) {
				is_lava = true;
			}
			boolean is_active = false;
			if (rs.getInt("aktivni")==1) {
				is_active = true;
			}
			
			massageTypes.add(new MassageType(rs.getInt("id"),is_active, rs.getString("nazev"),rs.getInt("delka"),is_lava));
		}
		rs.close();
  	  	s.close();
	} catch (SQLException ex) {
		System.err.println("getInitDataBundle:" + ex.getMessage());
	  	ex.printStackTrace();
	}

	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM lekce_jmena ORDER BY id");
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			lessonNames.add(new LessonName(rs.getInt("id"),rs.getString("nazev")));
		}
		rs.close();
  	  	s.close();
	} catch (SQLException ex) {
		System.err.println("getInitDataBundle:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	Integer role = -1;
	HttpServletRequest sr = getThreadLocalRequest();
	if (sr!=null) {
		HttpSession s = sr.getSession(false);
		if (s!=null) {
			if (s.getAttribute("role")!=null) {
				role = (Integer)s.getAttribute("role");
			}
		}
	}
	return new InitDataBundle(role, doctors,permaTypes,massageTypes,lessonNames);
}

public Boolean deletePermaItem(int id) {
	 try {
		  PreparedStatement s = get_db().prepareStatement("DELETE FROM `permanentky` WHERE id=?");
		  s.setInt(1, id);
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("deletePerma:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
}

public PagingLoadResult<PermaItem> getPermaItems(PagingLoadConfig cfg) {
	  List<PermaItem> items = new ArrayList<PermaItem>();
	  int offset = cfg.getOffset();
	  int limit = cfg.getLimit();
	  String sort_dir = cfg.getSortInfo().getSortDir().name();
	  String sort_field = cfg.getSortInfo().getSortField();
	  if ((sort_dir==null) || (sort_dir.compareTo("NONE")==0)) {
		  sort_dir="ASC";
	  }
	  if (sort_field==null) {
		  sort_field="created";
	  }
	  
	  int total = 1;
	  try {
		  
		  PreparedStatement s = get_db().prepareStatement("SELECT * FROM permanentky WHERE 1 ORDER BY `"+sort_field+"` "+sort_dir+" LIMIT ?,?");
		  s.setInt(1, offset);
		  s.setInt(2, limit);
		  ResultSet rs = s.executeQuery();
		  while(rs.next()) {
			  PermaItem pi = new PermaItem(rs.getInt("id"),rs.getInt("doctor_id"),rs.getInt("type_id"), rs.getInt("count"), rs.getInt("cost"),rs.getString("type"),rs.getString("user"),rs.getTimestamp("created").getTime());
			  items.add(pi);
		  }
		  PreparedStatement s2 = get_db().prepareStatement("SELECT count(*) as total FROM permanentky WHERE 1");
		  rs = s2.executeQuery();
		  if (rs.next()) {
			  total = rs.getInt("total");
		  }
		  rs.close();
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("getMembers:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
	  PagingLoadResult<PermaItem> res = new BasePagingLoadResult<PermaItem>(items,offset,total);
	  return res;
}

public Boolean insertPermaItem(PermaItem pi) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("INSERT INTO `permanentky` (`doctor_id`,`type_id`,`count`,`cost`,`type`,`user`,`created`) VALUES (?,?,?,?,?,?,NOW())");
		  s.setInt(1, (Integer)pi.get("doctor_id"));
		  s.setInt(2, (Integer)pi.get("type_id"));
		  s.setInt(3, (Integer)pi.get("count"));
		  s.setInt(4, (Integer)pi.get("cost"));
		  s.setString(5, (String)pi.get("type"));
		  s.setString(6, (String)pi.get("user"));
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
		  return Boolean.TRUE;
	  } catch (SQLException ex) {
		  System.err.println("insertPerma:" + ex.getMessage());
	  	  ex.printStackTrace();
	  }
    return Boolean.FALSE;
}

public Boolean updatePermaItem(PermaItem pi) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `permanentky` SET `doctor_id`=? , `type_id`=?,`count`=?, `cost`=?, `type`=?, `user`=?, `created`=? WHERE id=?");
		  s.setInt(1, (Integer) pi.get("doctor_id"));
		  s.setInt(2, (Integer) pi.get("type_id"));
		  s.setInt(3, (Integer) pi.get("count"));
		  s.setInt(4, (Integer) pi.get("cost"));
		  s.setString(5, (String) pi.get("type"));
		  s.setString(6, (String) pi.get("user"));
		  s.setTimestamp(7, new java.sql.Timestamp((Long) pi.get("created")));
		  s.setInt(8, (Integer) pi.get("id"));
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("updateMember:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	  return Boolean.TRUE;
}




public List<PermaItemSum> getPermaItemSum(long start_date, long end_date,
		int doctor_id) {
	List<PermaItemSum> items = new ArrayList<PermaItemSum>();
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT * FROM permanentky WHERE DATE(created)>=? and DATE(created)<=? and doctor_id=? ORDER by created ASC");
		s.setDate(1, new java.sql.Date(start_date));
		s.setDate(2, new java.sql.Date(end_date));
		s.setInt(3, doctor_id);
		ResultSet rs = s.executeQuery();
		PermaItemSum current_item = null;
		while(rs.next()) {
			int type_id = rs.getInt("type_id");
			int count = rs.getInt("count");
			int cost =  rs.getInt("cost");
			String type = rs.getString("type");
			
			if ((current_item != null) && (!current_item.isSame(doctor_id,type_id,count,cost,type))) {
				current_item = null;
				for(PermaItemSum f: items) {
					if (f.isSame(doctor_id,type_id,count,cost,type)) {
						current_item = f;
						break;
					}
				}
			}
			if (current_item == null) {
				current_item = new PermaItemSum(doctor_id,type_id,count,cost,type,1);
				items.add(current_item);
			} else {
				current_item.incMulti();
			}
		}
		rs.close();
		s.close();
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	return items;
}



public List<MassageItemSum> getMassageItemSum(long start_date, long end_date) {
	List<MassageItemSum> items = new ArrayList<MassageItemSum>();
	
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT typ,count(typ) as pocet FROM masaze WHERE DATE(zacatek)>=? and DATE(zacatek)<=? GROUP BY typ");
		s.setDate(1, new java.sql.Date(start_date));
		s.setDate(2, new java.sql.Date(end_date));
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			int type_id = rs.getInt("typ");
			int count = rs.getInt("pocet");
			items.add(new MassageItemSum(type_id,count));
		}
		rs.close();
		s.close();
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}
	
	
	return items;
}

public int getMassageCalDayStatus(long date) {
	
	List<MassageItem> items =  getMassageItems(date);
	if (items==null) return 0;
	for(MassageItem i: items) {
		if (i.isEmpty()) return 2;
	}
	return 1;
}
public List<MassageCalDayInfo> getMassageCalDayInfo(long[] req_days) {
	List<MassageCalDayInfo> days = new ArrayList<MassageCalDayInfo>();
	List<Long> plans = new ArrayList<Long>();

	if (req_days.length>0) {		
		try {
			PreparedStatement s = get_db().prepareStatement("SELECT DISTINCT DATE(zacatek) as zac FROM masaze_plan WHERE DATE(zacatek)>=? and DATE(zacatek)<=? ORDER by zacatek ASC");
			s.setDate(1, new java.sql.Date(req_days[0]));
			s.setDate(2, new java.sql.Date(req_days[req_days.length-1]));
			ResultSet rs = s.executeQuery();
			while(rs.next()) {
				long zac = rs.getDate("zac").getTime();
				plans.add(new Long(zac));
			}
			rs.close();
			s.close();
		} catch (SQLException ex) {
			System.err.println("getMassageDays:" + ex.getMessage());
		  	ex.printStackTrace();
		}
	}
	
	
	for(int i=0; i<req_days.length; i++) {
		long zac = req_days[i];
		boolean has_plan = false;
		//has plan?
		for(Long l: plans) {
			if (l.longValue()==zac) {
				has_plan = true;
				break;
			}
		}
		if (has_plan) {
			int stat = getMassageCalDayStatus(req_days[i]);
			days.add(new MassageCalDayInfo(zac,stat,req_days[0]));
		} else {
			days.add(new MassageCalDayInfo(zac,0,req_days[0]));
		}
	}
/*	
	try {
		PreparedStatement s = get_db().prepareStatement("SELECT DISTINCT DATE(zacatek) as zac FROM masaze_plan WHERE DATE(zacatek)>=? and DATE(zacatek)<=? ORDER by zacatek ASC");
		s.setDate(1, new java.sql.Date(start_date));
		s.setDate(2, new java.sql.Date(end_date));
		ResultSet rs = s.executeQuery();
		while(rs.next()) {
			long zac = rs.getDate("zac").getTime();
			int stat = getMessageCalDayStatus(zac);
			days.add(new MassageCalDayInfo(zac,stat,start_date));
		}
		rs.close();
		s.close();
	} catch (SQLException ex) {
		System.err.println("getMassageDays:" + ex.getMessage());
	  	ex.printStackTrace();
	}
*/	
	return days;
	
}

public Boolean deleteHolydayPlan(int year) {
	// TODO Auto-generated method stub
	List<Holyday> hlist = getHolydays(year);
	for(Holyday h: hlist) {
		deleteMassageDay(h.getDateAsLong());
	}
	return Boolean.TRUE;
}

private Boolean updateLessonName(LessonName ln) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `lekce_jmena` SET `nazev`=? WHERE id=?");
		  s.setString(1, ln.getName());
		  s.setInt(2, ln.getId());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("updateLessonName:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	
	return Boolean.TRUE;
	
}

private Boolean updateMassageType(MassageType mt) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `masaze_typy` SET `nazev`=?, `delka`=?, `kameny`=?, `aktivni`=? WHERE id=?");
		  s.setString(1, mt.getName());
		  s.setInt(2, mt.getSlots());
		  s.setInt(3, mt.isLava()?1:0);
		  s.setInt(4, mt.isActive()?1:0);
		  s.setInt(5, mt.getId());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("updateMassageType:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	
	return Boolean.TRUE;
	
}

private Boolean updatePermaType(PermaType pt) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `permanentky_co` SET `nazev`=?, `aktivni`=? WHERE id=?");
		  s.setString(1, pt.getName());
		  s.setInt(2, pt.isActive()?1:0);
		  s.setInt(3, pt.getId());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("updatePermaType:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	
	return Boolean.TRUE;
	
}

private Boolean updateDoctor(Doctor d) {
	  try {
		  PreparedStatement s = get_db().prepareStatement("UPDATE `doktori` SET `jmeno`=?, `aktivni`=? WHERE id=?");
		  s.setString(1, d.getFullName());
		  s.setInt(2, d.isActive()?1:0);
		  s.setInt(3, d.getId());
		  if (s.executeUpdate() != 1) {
			  s.close();
			  return Boolean.FALSE;
		  }
		  s.close();
	  } catch (SQLException ex) {
		  System.err.println("updateDoctor:" + ex.getMessage());
	  	  ex.printStackTrace();
		  return Boolean.FALSE;
	  }
	
	return Boolean.TRUE;
	
}


public Boolean updateLessonNames(List<LessonName> lessonNames) {
	for(LessonName ln: lessonNames) {
		if (!updateLessonName(ln)) return Boolean.FALSE;
	}
	return Boolean.TRUE;
}

public Boolean updateMassageTypes(List<MassageType> massageTypes) {
	for(MassageType mt: massageTypes) {
		if (!updateMassageType(mt)) return Boolean.FALSE;
	}
	return Boolean.TRUE;
}
 
public Boolean updateDoctors(List<Doctor> doctors) {
	for(Doctor d: doctors) {
		if (!updateDoctor(d)) return Boolean.FALSE;
	}
	return Boolean.TRUE;
}

public Boolean updatePermaTypes(List<PermaType> permaTypes) {
	for(PermaType pt: permaTypes) {
		if (!updatePermaType(pt)) return Boolean.FALSE;
	}
	return Boolean.TRUE;
}


}




