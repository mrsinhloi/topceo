package com.devsmart.android.ui;

import android.view.ViewGroup.LayoutParams;

public class LayoutParamsHelper {

	public static LayoutParams createWrapWrap() {
		LayoutParams retval = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		return retval;
	}
	
	public static LayoutParams createFillWrap() {
		LayoutParams retval = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		return retval;
	}
	
}
