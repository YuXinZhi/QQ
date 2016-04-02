package com.example.qq.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	// 单例设计模式，可以不等上一个Toast消失直接显示下一条Toast信息
	public static Toast mToast;

	public static void showToastShort(Context context, String text) {
		if (mToast==null) {
			mToast=Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		mToast.setText(text);
		mToast.show();
	}

	public static void showToastLong(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
