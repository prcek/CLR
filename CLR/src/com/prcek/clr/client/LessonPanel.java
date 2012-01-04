package com.prcek.clr.client;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.prcek.clr.client.data.Lesson;
import com.prcek.clr.client.data.Lesson.LessonType;

public class LessonPanel extends LayoutContainer {

	private LessonMembersGrid members;
	private LessonsGrid lessons;
	private DatePicker date_picker;

	public LessonPanel(LessonType lt) {
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		members = new LessonMembersGrid(){

			@Override
			public void afterLessonChange() {
				// TODO Auto-generated method stub
				reloadAll();
			}
			
		};
		
		add(members,new RowData(1,1,new Margins(0)));
		
		LayoutContainer select_panel = new LayoutContainer(new RowLayout());
		select_panel.setWidth(180);
		
		
		lessons = new LessonsGrid(lt) {
			@Override
			public void afterLessonGridChange() {
				// TODO Auto-generated method stub
				reloadAll();
			}

			public void onLessonSelect(Lesson l) {
				members.setLesson(l);
			}
		};
		date_picker = new DatePicker();
		date_picker.addListener(Events.Select, new Listener<ComponentEvent>() {  
			       public void handleEvent(ComponentEvent be) {
			    	   lessons.setStartDate(date_picker.getValue());
			       }  
			   
		});
		
		select_panel.add(date_picker, new RowData(-1,-1,new Margins(0)));
		select_panel.add(lessons, new RowData(1,1,new Margins(0)));
		
		add(select_panel,new RowData(-1,1, new Margins(0)));
	}


	public void reloadAll() {
		lessons.reload();
		members.reload();
	}

}