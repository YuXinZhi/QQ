package com.example.qq.ui;

import com.example.qq.ui.DragLayout.Status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MainLinearLayout extends LinearLayout {
	DragLayout mDragLayout;

	public MainLinearLayout(Context context) {
		super(context);
	}

	public MainLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDragLayout(DragLayout mDragLayout) {
		this.mDragLayout = mDragLayout;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 如果当前是关闭状态，按之前的方法判断
		if (mDragLayout.getStatus() == Status.Close) {
			return super.onInterceptTouchEvent(ev);
		} else {
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 如果当前是关闭状态，按之前的方法处理
		if (mDragLayout.getStatus() == Status.Close) {
			return super.onTouchEvent(event);
		} else {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				mDragLayout.close();
			}
			return true;
		}
	}

}
