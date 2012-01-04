package com.prcek.clr.client;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.prcek.clr.client.data.MassageCalDayInfo;

public class MassageCalendar extends LayoutContainer {
	
	public class CalendarDayEvent extends BoxComponentEvent {

		  public CalendarDay cal_day;

		  public CalendarDayEvent(CalendarDay iconButton) {
		    super(iconButton);
		    this.cal_day = iconButton;
		  }

		  public CalendarDayEvent(CalendarDay cal_day, Event event) {
		    this(cal_day);
		    this.event = event;
		  }

	}

	
	public class CalendarDay extends BoxComponent {
		protected String style;
		protected String html;
		protected Date date;
		protected boolean cancelBubble = true;

		public CalendarDay() {
			style="calendar_day";
		}
		public void setDate(Date d) {
			date = d;
			String text = Integer.toString(d.getDate()) + ".<br />"+DateTimeFormat.getFormat("MMMM").format(d);
			
			setHtml(text);
		}
		public void setStatus(int s) {
		    removeStyleName(this.style + "-full");
		    removeStyleName(this.style + "-free");
		    removeStyleName(this.style + "-invalid");
			switch(s) {
				case -1:
					addStyleName(this.style + "-invalid");
					break;
				case 0: //no plan
					break;
				case 1: //full
				    addStyleName(this.style + "-full");
					break;
				case 2: //free;
				    addStyleName(this.style + "-free");
					break;
			}
		}
		public Date getDate() {
			return date;
		}
		
		//// 
		public void addSelectionListener(SelectionListener listener) {
			    addListener(Events.Select, listener);
		}
		public void removeSelectionListener(SelectionListener listener) {
			    removeListener(Events.Select, listener);
		}

		public void onComponentEvent(ComponentEvent ce) {
			    switch (ce.getEventTypeInt()) {
			      case Event.ONMOUSEOVER:
			        addStyleName(style + "-over");
			        break;
			      case Event.ONMOUSEOUT:
			        removeStyleName(style + "-over");
			        break;
			      case Event.ONCLICK:
			        onClick(ce);
			        break;
			    }
		}

		@Override
		protected ComponentEvent createComponentEvent(Event event) {
		    return new CalendarDayEvent(this, event);
		}

		protected void onClick(ComponentEvent ce) {
		    if (cancelBubble) {
		      ce.cancelBubble();
		    }
		    //removeStyleName(style + "-over");
		    fireEvent(Events.Select, ce);
		}
		
		public void setHtml(String html) {
		    this.html = html;
		    if (rendered) {
		      getElement().setInnerHTML(html);
		    }
		}
		public void changeStyle(String style) {
			    removeStyleName(this.style);
			    removeStyleName(this.style + "-over");
			    removeStyleName(this.style + "-invalid");
			    removeStyleName(this.style + "-full");
			    removeStyleName(this.style + "-free");
			    addStyleName(style);
			    this.style = style;
		}

		protected void onRender(Element target, int index) {
				super.onRender(target, index);
				setElement(DOM.createElement("div"), target, index);
			    if (html != null) {
			      setHtml(html);
			    }
			    addStyleName(style);
			    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
		}


	}
	
	private class CalendarDayHeader extends Html {
		public CalendarDayHeader(String text) {
			super("<div class='calendar_day_header'>"+ text + "</div>");
		}
	}
	private CalendarDay[] cal_days;
	private static int cal_days_count = 5*6;
	//private Text current_month_text;
	private Date current_month;
//	private Button next_bt;
//	private Button now_bt;
//	private Button prev_bt;
//	private Button reload_bt;
	private Date last_load_time;
	private Date last_server_time;
	private Date current_start_date;
	public MassageCalendar() {
		setLayout(new RowLayout());
		last_load_time = new Date(0);
		LayoutContainer cal_contr_cont = new HorizontalPanel();
		//current_month_text = new Text();
		//cal_contr_cont.add()
/*		
		prev_bt = new Button("<<", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				setStartDate(Utils.getPrevMonth(current_month));
			}
		});
		
		now_bt = new Button("Aktualní měsíc", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				setStartDate(new Date());
			}
		});
		
		next_bt = new Button(">>", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				setStartDate(Utils.getNextMonth(current_month));
			}
		});
		reload_bt = new Button("obnovit", new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				reloadDay();
			}
		});
*/		
//		cal_contr_cont.add(prev_bt);
//		cal_contr_cont.add(now_bt);
//		cal_contr_cont.add(next_bt);
		//cal_contr_cont.add(current_month_text);
//		cal_contr_cont.add(reload_bt);
		
		
		
		
		TableLayout tl = new TableLayout(5);
		tl.setCellHorizontalAlign(HorizontalAlignment.CENTER);
		tl.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		LayoutContainer cal_cont = new LayoutContainer(tl);
		
		
		cal_cont.add(new CalendarDayHeader("Po"));
		cal_cont.add(new CalendarDayHeader("Út"));
		cal_cont.add(new CalendarDayHeader("St"));
		cal_cont.add(new CalendarDayHeader("Čt"));
		cal_cont.add(new CalendarDayHeader("Pá"));
//		cal_cont.add(new CalendarDayHeader("So"));
//		cal_cont.add(new CalendarDayHeader("Ne"));
		
		SelectionListener<CalendarDayEvent> cal_day_listener = new SelectionListener<CalendarDayEvent>(){

			@Override
			public void componentSelected(CalendarDayEvent ce) {
				/*
				// TODO Auto-generated method stub
				Date d = ce.cal_day.getDate();
				String dt = DateTimeFormat.getFormat("E d. M. y").format(d);
				MessageBox.info("click on cal day", dt, null);
				*/
				fireEvent(Events.Select, ce);
			}
			
		};
		
		
		cal_days = new CalendarDay[cal_days_count];
		for(int i=0; i<cal_days.length; i++) {
			CalendarDay cd = new CalendarDay();
			cd.addSelectionListener(cal_day_listener);
			cal_days[i]=cd;
			cal_cont.add(cd);
		}
		add(cal_contr_cont);
		add(cal_cont);
		
		//setStartDate(new Date());
	}
	
/*	
	public void nextMonth() {
		setStartDate(Utils.getNextMonth(current_month));
	}
	public void prevMonth() {
		setStartDate(Utils.getPrevMonth(current_month));
	}
	public void selectMonth(Date d) {
		setStartDate(d);
	}
*/	
	
	public void setStartDate(Date d) {
		current_start_date = d;
		current_month = Utils.getFirstMonthDayDate(d);
		Date date = Utils.getFirstWeekDayDate(current_month);
		String month_name = Utils.month_names[d.getMonth()] + " " + DateTimeFormat.getFormat("y").format(d);
		long start_date = date.getTime();
		for(int i=0; i<cal_days.length; i++) {
			cal_days[i].setDate(date);
			cal_days[i].setStatus(-1);
			date=Utils.getNextDay(date);
			if (date.getDay()==6) {
				date=Utils.getNextDay(date);
				date=Utils.getNextDay(date);
			}
		}
		//current_month_text.setText(month_name);
		
		long d_s = start_date;
		long d_e = start_date;
		long d_c = 6*7; //cal_days.length;
		d_e += (long) d_c*24*60*60*1000; 
		last_load_time = new Date();
/*		
		clr.dataService.getMassageCalDayInfo(d_s,d_e, new AsyncCallback<List<MassageCalDayInfo>>(){
			public void onFailure(Throwable arg0) {
			}
			public void onSuccess(List<MassageCalDayInfo> arg0) {
				if (arg0==null) { return; }
				int idx = 0;
				for(MassageCalDayInfo i: arg0) {
					while( (idx<cal_days.length) && (!Utils.isSameDay(cal_days[idx].getDate(), new Date(i.getDate())))) {
						cal_days[idx].setStatus(0);
						idx++;
					}
					if (idx>=cal_days.length) { break; }
					cal_days[idx].setStatus(i.getStatus());
					idx++;
				}
				while(idx<cal_days.length) {
					cal_days[idx].setStatus(0);
					idx++;
				}
			}
		});
*/		
	}
	
	public void reloadDay() {
		setStartDate(current_start_date);
	}

	
	public void serverDataTime(Date t) {
		if (last_server_time==null) {
			last_server_time = t;
			return;
		}
		if (last_server_time.before(t)) {
			Date now = new Date();
			long now_s = now.getTime();
			long lt = last_load_time.getTime();
			if (now_s>(lt+1000*5)) {
				reloadDay();
			}
		}
		last_server_time = t;
	}

/*	
	@Override
	public void onComponentEvent(ComponentEvent ce) {
		// TODO Auto-generated method stub
		super.onComponentEvent(ce);
		
		if (ce.type == Event.ONCLICK) {
			ce.cancelBubble();
			if (ce.component!=null) {
				
			}
		}
	}
*/	
}
