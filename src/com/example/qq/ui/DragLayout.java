package com.example.qq.ui;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 侧滑面板
 * 
 * @author Ben
 *
 */
public class DragLayout extends FrameLayout {
	private static final String TAG = "TAG";
	private ViewDragHelper mDragHelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;
	private int mWidth;
	private int mHeight;
	private int mRange;

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// a. 初始化操作(通过静态方法)

		mDragHelper = ViewDragHelper.create(this, mCallBack);
	}

	Callback mCallBack = new Callback() {

		// c.重写事件

		// 1.根据返回结果决定当前child是否可以拖拽
		// child 当前被拖拽的View
		// pointerId 区分多点触摸的id
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// 当尝试捕获child时调用，暂时没有移动
			return child == mMainContent;
		}

		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			// 当capturedChild被捕获时调用
			super.onViewCaptured(capturedChild, activePointerId);
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			// 返回拖拽的范围，不对拖拽进行限制，仅仅决定动画的执行速度
			return mRange;
		};

		@Override
		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
		}

		/**
		 * 根据范围修正左边值
		 * 
		 * @param left
		 * @return
		 */
		private int fixLeft(int left) {
			if (left < 0) {
				return 0;
			} else if (left > mRange) {
				return mRange;
			}
			return left;
		}

		// 2.根据建议值修正将要移动到的（横向）位置
		// 此时没有发生真正的移动
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// child:当前拖拽的view
			// left:新的位置建议值，dx:位置变化量
			// left=oldLeft+dx
			Log.d(TAG, "clampViewPositionHorizontal" + "old:" + child.getLeft() + "dx" + "left" + left);
			if (child == mMainContent) {
				left = fixLeft(left);
			}
			return left;
		}

		// 3.当view位置改变时，处理要做的事情（更新状态，伴随动画，重绘界面）
		// 此时view已经发生位置改变	
		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			// 当做面板移动之后，再强制放回去
			if (changedView == mLeftContent) {
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
			}
			// 为了兼容低版本，每次修改值之后进行重绘
			invalidate();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
		}

	};

	// b.传递触摸事件

	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		// 传递给mDragHelper
		return mDragHelper.shouldInterceptTouchEvent(ev);
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 返回true持续接收事件
		return true;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		// 容错性检查（至少有两个子View，子View必须是ViewGroup子类）
		if (getChildCount() < 2) {
			throw new IllegalStateException("至少有两个子View");
		}

		if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
			throw new IllegalStateException("子View必须是ViewGroup子类");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();

		mRange = (int) (mWidth * 0.6f);
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// return super.dispatchTouchEvent(ev);
	// }
}
