package com.prcek.clr.client.ui;

import java.util.Date;

import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.prcek.clr.client.Utils;


public class MonthPropertyEditor implements PropertyEditor<Date> {

	public Date convertStringValue(String value) {
		if (value == null) return null;
		Date date = null;
		int year = -1;
		int month = -1;
		String[] vs = value.trim().split("\\s+");
		for(String v: vs) {
			if (v.matches("^[0-9]{1,4}$")) {
				Integer i = Integer.decode(v);
				if ((i>1900) && (i<2999)){
					year = i;
				} else if ((i>=0) && (i<=12)) {
					month = i;
				}
			} else {
				for(int i=0; i<Utils.month_names.length; i++) {
					if (v.compareToIgnoreCase(Utils.month_names[i])==0) {
						month = i+1;
						break;
					}
				}
			}
		}
		if ((year!=-1) && (month!=-1)) {
			date = new Date(year-1900,month-1,1);
		}
		return date;
	}

	public String getStringValue(Date value) {
		if (value==null) return null;
		int y = value.getYear()+1900;
		String m = Utils.month_names[value.getMonth()];
		return m+" "+String.valueOf(y);
	}
	
}
