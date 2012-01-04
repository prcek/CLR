package com.prcek.clr.client.ui;

import com.extjs.gxt.ui.client.widget.menu.Item;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.user.client.Element;

public class MonthMenuItem extends Item {
	protected MonthPicker picker;
	public MonthMenuItem() {
		    hideOnClick = true;
		    picker = new MonthPicker();
		    picker.addListener(Events.Select, new Listener<ComponentEvent>() {
		      public void handleEvent(ComponentEvent ce) {
		        parentMenu.fireEvent(Events.Select, ce);
		        parentMenu.hide(true);
		      }
		    });
	}

		  @Override
		  protected void onRender(Element target, int index) {
		    super.onRender(target, index);
		    picker.render(target, index);
		    setElement(picker.getElement());
		  }

		  @Override
		  protected void handleClick(ComponentEvent be) {
		    picker.onComponentEvent((ComponentEvent) be);
		  }

}
