package com.prcek.clr.client;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.form.TriggerField;

public class FilterField<M> extends TriggerField<M> {
	public FilterField() {
		setTriggerStyle("x-form-clear-trigger");
	}

	@Override
	protected void onTriggerClick(ComponentEvent ce) {
		setValue(null);
		super.onTriggerClick(ce);
	}
}
