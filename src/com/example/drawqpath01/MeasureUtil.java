package com.example.drawqpath01;

import android.content.Context;
import android.util.TypedValue;

public class MeasureUtil {
	
	public static float sp2dx(Context context,int sp) {
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				context.getResources().getDisplayMetrics());
		return size;
	}
	public static float dp2dx(Context context,int sp) {
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sp,
				context.getResources().getDisplayMetrics());
		return size;
	}
}
