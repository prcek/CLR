package com.prcek.clr.client;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.prcek.clr.client.data.PermaItem;

public class Utils {
	
	public static String[] month_names = {"Leden","Únor","Březen","Duben","Květen","Červen","Červenec","Srpen","Září","Říjen","Listopad","Prosinec"};
	
	public static void upcaseField(Field<?> field) {
		if (field!=null) {
			String text = field.getRawValue();
			if (text!=null) {
				if (text.length()==1) {
					String new_text = text.toUpperCase();
					if (new_text.compareTo(text)!=0) {
						field.setRawValue(new_text);
					}
				}
					
			}
		}
	}
	public static ColumnConfig createColumnConfigWithRenderer(String id, String name, int width, GridCellRenderer renderer) {
		ColumnConfig c = new ColumnConfig(id,name,width);
		c.setRenderer(renderer);
		//PermaItem
		return c;
	}
	
	public static Date getFirstWeekDayDate(Date d) {
		Date nd = new Date(d.getTime());
		while(nd.getDay()!=1) {
			nd.setTime(nd.getTime()-(long)(24*60*60*1000));
		}
		return nd;
	}
	public static Date getFirstMonthDayDate(Date d) {
		Date nd = new Date(d.getTime());
		nd.setDate(1);
		return nd;
	}
	
	public static Date getNextDay(Date d) {
		return new Date(d.getTime()+(long)(24*60*60*1000));
	}

	public static Date getPrevDay(Date d) {
		return new Date(d.getTime()-(long)(24*60*60*1000));
	}
	
	public static Date getPrevMonth(Date d) {
		Date nd = new Date(d.getTime());
		int m = nd.getMonth();
		if (m>0) { 
			nd.setMonth(m-1); 
		} else {
			nd.setYear(nd.getYear()-1);
			nd.setMonth(11);
		}
		return nd;
	}

	public static Date getNextMonth(Date d) {
		Date nd = new Date(d.getTime());
		int m = nd.getMonth();
		if (m<11) { 
			nd.setMonth(m+1); 
		} else {
			nd.setYear(nd.getYear()+1);
			nd.setMonth(0);
		}
		return nd;
	}
	
	
	public static Boolean isSameDay(Date d1,Date d2) {
		int d1_y = d1.getYear();
		int d1_m = d1.getMonth();
		int d1_d = d1.getDate();
		
		int d2_y = d2.getYear();
		int d2_m = d2.getMonth();
		int d2_d = d2.getDate();
		
		return (d1.getYear() == d2.getYear()) && (d1.getMonth()==d2.getMonth()) && (d1.getDate()==d2.getDate());
	}
}
