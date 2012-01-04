package com.prcek.clr.client.ui;

import java.util.Date;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.google.gwt.user.client.Element;

public class MonthField extends TriggerField<Date> {
	public class MonthFieldMessages extends TextFieldMessages {
		private String minText;
		private String maxText;
		private String invalidText;

		public String getInvalidText() {
			return invalidText;
		}

		public String getMaxText() {
			return maxText;
		}

		public String getMinText() {
			return minText;
		}

		public void setInvalidText(String invalidText) {
			this.invalidText = invalidText;
		}

		public void setMaxText(String maxText) {
			this.maxText = maxText;
		}

		public void setMinText(String minText) {
			this.minText = minText;
		}
	}

	private MonthMenu menu;
	private Date minValue;
	private Date maxValue;
	private BaseEventPreview focusPreview;
	private boolean formatValue;
	private boolean editable = true;

	public MonthField() {
		autoValidate = false;
		propertyEditor = new MonthPropertyEditor();
		messages = new MonthFieldMessages();
		setTriggerStyle("x-form-date-trigger");
	}

	public MonthPicker getMonthPicker() {
		if (menu == null) {
			menu = new MonthMenu();
			
			menu.addListener(Events.Select, new Listener<ComponentEvent>() {
				public void handleEvent(ComponentEvent ce) {
					focusValue = getValue();
					setValue(menu.getDate());
					menu.hide();
					el().blur();
				}
			});
			menu.addListener(Events.Hide, new Listener<ComponentEvent>() {
				public void handleEvent(ComponentEvent be) {
					focus();
				}
			});
		}
		return menu.getMonthPicker();
	}

	public Date getMaxValue() {
		return maxValue;
	}

	@Override
	public MonthFieldMessages getMessages() {
		return (MonthFieldMessages) messages;
	}

	public Date getMinValue() {
		return minValue;
	}

	@Override
	public MonthPropertyEditor getPropertyEditor() {
		return (MonthPropertyEditor) propertyEditor;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isFormatValue() {
		return formatValue;
	}

	public void setEditable(boolean value) {
		if (value == this.editable) {
			return;
		}
		this.editable = value;
		if (rendered) {
			El fromEl = getInputEl();
			if (!value) {
				fromEl.dom.setPropertyBoolean("readOnly", true);
				fromEl.addStyleName("x-combo-noedit");
			} else {
				fromEl.dom.setPropertyBoolean("readOnly", false);
				fromEl.removeStyleName("x-combo-noedit");
			}
		}
	}

	public void setFormatValue(boolean formatValue) {
		this.formatValue = formatValue;
	}

	public void setMaxValue(Date maxValue) {
		if (maxValue != null) {
			maxValue = new DateWrapper(maxValue).clearTime().asDate();
		}
		this.maxValue = maxValue;
	}
	public void setMinValue(Date minValue) {
		if (minValue != null) {
			minValue = new DateWrapper(minValue).clearTime().asDate();
		}
		this.minValue = minValue;
	}

	@Override
	public void setRawValue(String value) {
		super.setRawValue(value);
	}

	protected void expand() {
		MonthPicker picker = getMonthPicker();

		Object v = getValue();
		Date d = null;
		if (v instanceof Date) {
			d = (Date) v;
		} else {
			d = new Date();
		}
		picker.setValue(d, true);
		//picker.setMinDate(minValue); //TODO
		//picker.setMaxDate(maxValue); //TODO
		
		menu.show(getElement(), "tl-bl?");
		menu.focus();
	}

	@Override
	protected void onBlur(final ComponentEvent ce) {
		Rectangle rec = trigger.getBounds();
		if (rec.contains(BaseEventPreview.getLastClientX(), BaseEventPreview
				.getLastClientY())) {
			ce.stopEvent();
			return;
		}
		if (menu != null && menu.isVisible()) {
			return;
		}
		hasFocus = false;
		doBlur(ce);
	}

	@Override
	protected void onClick(ComponentEvent ce) {
		if (!editable && ce.getTarget() == getInputEl().dom) {
			onTriggerClick(ce);
			return;
		}
		super.onClick(ce);
	}

	protected void onDown(FieldEvent fe) {
		fe.cancelBubble();
		if (menu == null || !menu.isAttached()) {
			expand();
		}
	}

	@Override
	protected void onFocus(ComponentEvent ce) {
		super.onFocus(ce);
		focusPreview.add();
	}

	@Override
	protected void onKeyPress(FieldEvent fe) {
		super.onKeyPress(fe);
		int code = fe.getEvent().getKeyCode();
		if (code == 8 || code == 9) {
			if (menu != null && menu.isAttached()) {
				menu.hide();
			}
		}
	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		focusPreview = new BaseEventPreview();

		if (!this.editable) {
			this.editable = true;
			this.setEditable(false);
		}
		new KeyNav<FieldEvent>(this) {
			public void onDown(FieldEvent fe) {
				MonthField.this.onDown(fe);
			}
		};
	}

	@Override
	protected void onTriggerClick(ComponentEvent ce) {
		super.onTriggerClick(ce);
		if (disabled || isReadOnly()) {
			return;
		}

		expand();

		getInputEl().focus();
	}

	private void doBlur(ComponentEvent ce) {
		if (menu != null && menu.isVisible()) {
			menu.hide();
		}
		super.onBlur(ce);
		focusPreview.remove();
	}

}
