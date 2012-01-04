package com.prcek.clr.client.data;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MassageItemSum extends BaseModel {
	public MassageItemSum(){}
	public MassageItemSum(int type, int count) {
		set("type",type);
		set("count",count);
	}
	
	public int getType() {
		Integer i = get("type");
		return i.intValue();
	}
	public int getCount() {
		Integer i = get("count");
		return i.intValue();
	}

}
