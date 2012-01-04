package com.prcek.clr.client.ui;

import java.util.Date;

import com.extjs.gxt.ui.client.core.CompositeElement;
import com.extjs.gxt.ui.client.core.CompositeFunction;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DatePickerEvent;

import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.GXT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.constants.DateTimeConstants;
//import com.google.gwt.i18n.client.constants.DateTimeConstants;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;


public class MonthPicker extends BoxComponent {
	
	  private DateTimeConstants constants;// = (DateTimeConstants) GWT.create(DateTimeConstants.class);
	  private CompositeElement mpMonths, mpYears;
	  private DateWrapper activeDate, value;
	  private int mpSelMonth, mpSelYear;
	  private int mpyear;

	  public MonthPicker() {
		    constants = LocaleInfo.getCurrentLocale().getDateTimeConstants();
		  
		    baseStyle = "x-date-picker";
		    //messages = new DatePickerMessages();
	 }

	
	  @Override
	  protected void doAttachChildren() {
	    super.doAttachChildren();
	    //header.onAttach();
	    //footer.onAttach();
	    //ComponentHelper.doAttach(grid);
	  }

	  @Override
	  protected void doDetachChildren() {
	    super.doDetachChildren();
	    //header.onDetach();
	    //footer.onDetach();
	    //ComponentHelper.doDetach(grid);
	    //monthPicker.setVisible(false);
	  }

	  @Override
	  protected void onRender(Element target, int index) {
		    String ok = "OK";
		    String cancel = "Cancel";
		    
		    StringBuffer buf = new StringBuffer();
		    buf.append("<table border=0 cellspacing=0>");
		    String[] monthNames = constants.shortMonths();
		    for (int i = 0; i < 6; i++) {
		      buf.append("<tr><td class=x-date-mp-month><a href=#>" + monthNames[i] + "</a></td>");
		      buf.append("<td class='x-date-mp-month x-date-mp-sep'><a href=#>"
		          + monthNames[i + 6] + "</a></td>");
		      if (i == 0) {
		        buf.append("<td class=x-date-mp-ybtn align=center><a class=x-date-mp-prev href=#></a></td><td class='x-date-mp-ybtn' align=center><a class='x-date-mp-next'></a></td></tr>");
		      } else {
		        buf.append("<td class='x-date-mp-year'><a href='#'></a></td><td class='x-date-mp-year'><a href='#'></a></td></tr>");
		      }
		    }
		    buf.append("<tr class=x-date-mp-btns><td colspan='4'><button type='button' class='x-date-mp-ok'>");
		    buf.append(ok);
		    buf.append("</button><button type=button class=x-date-mp-cancel>");
		    buf.append(cancel);
		    buf.append("</button></td></tr></table>");

		    setElement(XDOM.create(buf.toString()));
		    el().insertInto(target, index);

		    el().setWidth(175, true);

		    
		   // monthPicker.update(buf.toString());
		    
		    
		    NodeList<Element> ell = el().select("td.x-date-mp-month");
		    for(int i=0; i<ell.getLength(); i++) {
		    	mpMonths.add(ell.getItem(i));
		    }

		    ell = el().select("td.x-date-mp-year");
		    for(int i=0; i<ell.getLength(); i++) {
		    	mpYears.add(ell.getItem(i));
		    }
		    
		    mpMonths = new CompositeElement(Util.toElementArray(el().select("td.x-date-mp-month")));
		    mpYears = new CompositeElement(Util.toElementArray(el().select("td.x-date-mp-year")));

		    mpMonths.each(new CompositeFunction() {

		      public void doFunction(Element elem, CompositeElement ce, int index) {
		        index += 1;
		        if (index % 2 == 0) {
		          elem.setPropertyInt("xmonth", (int) (5 + (Math.round(index * .5))));
		        } else {
		          elem.setPropertyInt("xmonth", (int) (Math.round((index - 1) * .5)));
		        }
		      }

		    });
			
		    
		    activeDate = value != null ? value : new DateWrapper();
		    update(activeDate);

		    
		    el().addEventsSunk(Event.ONCLICK | Event.MOUSEEVENTS);
		    el().makePositionable();

	  }
	  
	  @Override
	  public void onComponentEvent(ComponentEvent ce) {
	    super.onComponentEvent(ce);
	    switch (ce.getEventTypeInt()) {
	      case Event.ONCLICK:
	        onClick(ce);
	        return;
	      case Event.ONMOUSEOVER:
	        onMouseOver(ce);
	        break;
	      case Event.ONMOUSEOUT:
	        onMouseOut(ce);
	        break;

	    }
	  }
	  private void onMouseOut(ComponentEvent ce) {
		    El target = ce.getTarget("td.x-date-active", 3);
		    if (target == null) {
		      target = ce.getTarget("td.x-date-nextday", 3);
		    }
		    if (target == null) {
		      target = ce.getTarget("td.x-date-prevday", 3);
		    }
		    if (target == null) {
		      target = ce.getTarget("td.x-date-mp-month", 3);
		    }
		    if (target == null) {
		      target = ce.getTarget("td.x-date-mp-year", 3);
		    }
		    if (target != null) {
		      target.removeStyleName("x-date-active-hover");
		    }

		  }

		  private void onMouseOver(ComponentEvent ce) {
		    El target = ce.getTarget("td.x-date-active", 3);
		    if (target == null) {
		      target = ce.getTarget("td.x-date-nextday", 3);
		    }
		    if (target == null) {
		      target = ce.getTarget("td.x-date-prevday", 3);
		    }
		    if (target == null) {
		      target = ce.getTarget("td.x-date-mp-month", 3);
		    }
		    if (target == null) {
		      target = ce.getTarget("td.x-date-mp-year", 3);
		    }
		    if (target != null) {
		      target.addStyleName("x-date-active-hover");
		    }

		  }

	  
	  protected void onClick(ComponentEvent be) {
		    be.stopEvent();
		    El target = be.getTargetEl();
		    El pn = null;
		    /*
		    String cls = target.getStyleName();
		    if (cls.equals("x-date-left-a")) {
		      showPrevMonth();
		    } else if (cls.equals("x-date-right-a")) {
		      showNextMonth();
		    }
		    */
		    if ((pn = target.findParent("td.x-date-mp-month", 2)) != null) {
		      mpMonths.removeStyleName("x-date-mp-sel");
		      El elem = target.findParent("td.x-date-mp-month", 2);
		      elem.addStyleName("x-date-mp-sel");
		      mpSelMonth = pn.dom.getPropertyInt("xmonth");
		    } else if ((pn = target.findParent("td.x-date-mp-year", 2)) != null) {
		      mpYears.removeStyleName("x-date-mp-sel");
		      El elem = target.findParent("td.x-date-mp-year", 2);
		      elem.addStyleName("x-date-mp-sel");
		      mpSelYear = pn.dom.getPropertyInt("xyear");
		    } else if (target.is("button.x-date-mp-ok")) {
		      DateWrapper d = new DateWrapper(mpSelYear, mpSelMonth, activeDate.getDate());
		      setValue(d.asDate());
		      //update(d);
		      //hideMonthPicker();
		    } else if (target.is("button.x-date-mp-cancel")) {
		      //hideMonthPicker();
		    } else if (target.is("a.x-date-mp-prev")) {
		      updateMPYear(mpyear - 10);
		    } else if (target.is("a.x-date-mp-next")) {
		      updateMPYear(mpyear + 10);
		    }

		    if (GXT.isSafari) {
		      focus();
		    }
		  }

	  public void setValue(Date date) {
		    setValue(date, false);
	  }
	  public Date getValue() {
		    return value.asDate();
	  }

	  
	  public void setValue(Date date, boolean supressEvent) {
		    this.value = new DateWrapper(date).clearTime().getFirstDayOfMonth();
		    if (rendered) {
		      update(value);
		    }
		    if (!supressEvent) {
		      MonthPickerEvent de = new MonthPickerEvent(this);
		      de.date = date;
		      fireEvent(Events.Select, de);
		    }

	  }

	  
	  private void update(DateWrapper date) {
		    DateWrapper vd = activeDate;
		    activeDate = date;
		    if (vd != null && el() != null) {
			   	mpSelMonth = date.getMonth();
			    updateMPMonth(mpSelMonth);
			    mpSelYear = date.getFullYear();
			    updateMPYear(mpSelYear);
		    }
		    
	  }
	  private void updateMPMonth(int month) {
		    for (int i = 0; i < mpMonths.getCount(); i++) {
		      Element elem = mpMonths.item(i);
		      int xmonth = elem.getPropertyInt("xmonth");
		      fly(elem).setStyleName("x-date-mp-sel", xmonth == month);
		    }
		  }

	  private void updateMPYear(int year) {
		    mpyear = year;

		    for (int i = 1; i <= 10; i++) {
		      El td = new El(mpYears.item(i - 1));
		      int y2;
		      if (i % 2 == 0) {
		        y2 = (int) (year + (Math.round(i * .5)));
		        td.firstChild().update("" + y2);
		        td.dom.setPropertyInt("xyear", y2);
		      } else {
		        y2 = (int) (year - (5 - Math.round(i * .5)));
		        td.firstChild().update("" + y2);
		        td.dom.setPropertyInt("xyear", y2);
		      }
		      fly(mpYears.item(i - 1)).setStyleName("x-date-mp-sel", y2 == year);
		    }
		  }

	  
}
