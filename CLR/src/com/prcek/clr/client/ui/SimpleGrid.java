package com.prcek.clr.client.ui;

import java.util.List;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.core.El;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;

public class SimpleGrid<M extends ModelData> extends BoxComponent {
	private Grid days, grid;
	private Element[] cells;
	private Element[] textNodes;

	private int rows;
	private int cols;
	private String[] header_lables;
	static private String[] default_header_labels = {"PO","UT","ST","CT","PA","SO","NE"};
	protected ListStore<M> store;
	private StoreListener storeListener;
	private String loadingText;

	private Template template;
	private String defaultTemplate = "<div class=x-view-item>{short}</div>";

	
	public SimpleGrid(int rows, int cols, String[] names) {
		baseStyle = "myui-grid";
		this.cols = cols;
		this.rows = rows;
		this.header_lables = names;
		storeListener = new StoreListener() {
		      @Override
		      public void storeAdd(StoreEvent se) {
		        //onAdd(se.models, se.index);
		        refresh();
		      }

		      @Override
		      public void storeBeforeDataChanged(StoreEvent se) {
		        onBeforeLoad();
		      }

		      @Override
		      public void storeClear(StoreEvent se) {
		        refresh();
		      }

		      @Override
		      public void storeDataChanged(StoreEvent se) {
		        refresh();
		      }

		      @Override
		      public void storeFilter(StoreEvent se) {
		        refresh();
		      }

		      @Override
		      public void storeRemove(StoreEvent se) {
		        //onRemove(se.model, se.index);
		        refresh();
		      }

		      @Override
		      public void storeSort(StoreEvent se) {
		        refresh();
		      }

		      @Override
		      public void storeUpdate(StoreEvent se) {
		        //onUpdate((M) se.model, se.index);
		        refresh();
		      }
		};
		
	}
	
	public SimpleGrid() {
		this(5,7,default_header_labels);
	}
	
	public void setLoadingText(String loadingText) {
		    this.loadingText = loadingText;
	}

	protected M prepareData(M model) {
		    return model;
	}

	public void setTemplate(String t) {
		template = new Template(t);
	}
	
	 public void refresh() {
		    if (!rendered) {
		      return;
		    }
		    if (template == null) {
		        template = new Template(defaultTemplate);
		    }

		    List<M> models = store.getModels();
			int cell_count = grid.getColumnCount() * grid.getRowCount();
			for(int i=0; i<cell_count; i++) {
				if (models.size()>i) {
					M model = prepareData(models.get(i));
					String item_st = template.applyTemplate(new Params(model.getProperties()));
					cells[i].setClassName("myui-grid-active");
					El cellEl = new El(cells[i]);
					cellEl.firstChild().dom.setPropertyInt("storeIndex", i);
					fly(textNodes[i]).update("" + item_st);
				} else  {
					cells[i].setClassName("myui-grid-active");
					El cellEl = new El(cells[i]);
					cellEl.firstChild().dom.setPropertyInt("storeIndex", -1);
					fly(textNodes[i]).update("<b>?</b><br /><i>"+i+"</i>");
				}
			}
		    fireEvent(Events.Refresh);
		  }

	protected void onBeforeLoad() {
		if (!rendered) return;
		for(int i=0;i<cells.length; i++) {
			cells[i].setClassName("myui-grid-active");
			El cellEl = new El(cells[i]);
			cellEl.firstChild().dom.setPropertyInt("storeIndex", -1);
			fly(textNodes[i]).update("<div>&nbsp</div>");
		}
/*		
		if (loadingText != null) {
			//if (rendered) {
			//	el().setInnerHtml("<div class='loading-indicator'>" + loadingText + "</div>");
		    //}
		    //all.removeAll();
		}
*/		
	}

	@Override
	protected void doAttachChildren() {
		super.doAttachChildren();
		//header.onAttach();
		//footer.onAttach();
		ComponentHelper.doAttach(days);
		ComponentHelper.doAttach(grid);
	}

	@Override
	protected void doDetachChildren() {
		super.doDetachChildren();
		//header.onDetach();
		//footer.onDetach();
		ComponentHelper.doDetach(grid);
		ComponentHelper.doDetach(days);
		//monthPicker.setVisible(false);
	}
	
	
	public void setStore(ListStore store) {
	    if (this.store != null) {
	      this.store.removeStoreListener(storeListener);
	    }
	    if (store != null) {
	      store.addStoreListener(storeListener);
	    }
	    this.store = store;

	    if (store != null && isRendered()) {
	      refresh();
	    }
	  }


	protected void onRender(Element target, int index) {
	    setElement(DOM.createDiv(), target, index);
	    
	    int i;
	    
	    days = new Grid(1, cols);
	    days.setStyleName("myui-grid-header");
	    days.setCellPadding(0);
	    days.setCellSpacing(0);
	    days.setBorderWidth(0);

	    for(i=0; i<cols; i++) {
	    	days.setHTML(0,i,"<span>"+ header_lables[i] +"</span>");
	    }
	    
	    
	    
	    grid = new Grid(rows, cols);
	    grid.setStyleName("myui-grid-inner");
	    grid.setCellSpacing(0);
	    grid.setCellPadding(0);
	    grid.addTableListener(new TableListener() {
	       	
	      public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
	        Event event = DOM.eventGetCurrentEvent();
	        ComponentEvent be = new ComponentEvent(SimpleGrid.this, event);
	        onDayClick(be);
	      }
	    });
	    for (int row = 0; row < rows; row++) {
	        for (int col = 0; col < cols; col++) {
		          grid.setHTML(row, col, "<a href=#><span>a</span></a>");
	        }
	    }
	    
	    getElement().appendChild(days.getElement());
	    getElement().appendChild(grid.getElement());
	    
	    el().setWidth(60*cols);

	    
	    cells = Util.toElementArray(el().select("table.myui-grid-inner tbody td"));
	    textNodes = Util.toElementArray(el().select("table.myui-grid-inner tbody span"));

	    //updateTEST();
	    if (store != null && store.getCount() > 0) {
	        refresh();
	    }
	    
	    el().addEventsSunk(Event.ONCLICK | Event.MOUSEEVENTS);
	    el().makePositionable();
	}
/*	
	private void updateTEST() {
		int cell_count = grid.getColumnCount() * grid.getRowCount();
		for(int i=0; i<cell_count; i++) {
			cells[i].setClassName("myui-grid-active");
			fly(textNodes[i]).update("<b>T</b><br /><i>"+i+"</i>");
		}
	}
*/	
	protected void onDayClick(ComponentEvent ce) {
	    ce.stopEvent();
	    El target = ce.getTargetEl();
	    El e = target.findParent("a", 5);
	    if (e!=null) {
	    	 int i = e.dom.getPropertyInt("storeIndex");
	         if (i != -1) {
	        	 onDataSelect(store.getAt(i));
	        	 return;
	         }
	    }
/*	    
	    El target = ce.getTargetEl();
	    El e = target.findParent("a", 5);
	    if (e != null) {
	      String dt = e.dom.getPropertyString("dateValue");
	      if (dt != null) {
	        handleDateClick(e, dt);
	        return;
	      }
	    }
*/	    
	}
	public void onDataSelect(M model) {
		
	}

}
