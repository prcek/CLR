package com.prcek.clr.client;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class SettingPanel extends LayoutContainer {
	public SettingPanel() {
		setLayout(new RowLayout());
		setScrollMode(Scroll.AUTOY);
		LessonNameForm lnf = new LessonNameForm();
		add(lnf, new RowData(1,-1,new Margins(0)));
		
		MassageNameForm mnf = new MassageNameForm();
		add(mnf, new RowData(1,-1,new Margins(0)));

		DoctorNameForm dnf = new DoctorNameForm();
		add(dnf, new RowData(1,-1,new Margins(0)));
		
		PermaNameForm pnf = new PermaNameForm();
		add(pnf, new RowData(1,-1,new Margins(0)));
		
		
	}
}
