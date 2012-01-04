package com.prcek.clr.client.ui;

import java.util.Date;

import com.extjs.gxt.ui.client.event.ComponentEvent;

public class MonthPickerEvent extends ComponentEvent {
	 public MonthPicker monthPicker;

	 public Date date;

	 public MonthPickerEvent(MonthPicker monthPicker) {
	    super(monthPicker);
	    this.monthPicker = monthPicker;
	 }

}
