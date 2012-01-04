package com.prcek.clr.client.ui;

import java.util.Date;

import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.menu.Menu;

public class MonthMenu extends Menu {
	  /**
	   * The internal date picker.
	   */
	  protected MonthPicker picker;

	  private MonthMenuItem item;

	  public MonthMenu() {
	    item = new MonthMenuItem();
	    picker = item.picker;
	    add(item);
	    baseStyle = "x-date-menu";
	    setAutoHeight(true);
	  }

	  /**
	   * Returns the selected date.
	   * 
	   * @return the date
	   */
	  public Date getDate() {
	    return item.picker.getValue();
	  }

	  /**
	   * Returns the date picker.
	   * 
	   * @return the date picker
	   */
	  public MonthPicker getMonthPicker() {
	    return picker;
	  }

	  @Override
	  protected void doAttachChildren() {
	    super.doAttachChildren();
	    ComponentHelper.doAttach(picker);
	  }

	  @Override
	  protected void doDetachChildren() {
	    super.doDetachChildren();
	    ComponentHelper.doDetach(picker);
	  }

}
