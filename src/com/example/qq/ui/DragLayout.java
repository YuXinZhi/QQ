package com.example.qq.ui;

import com.nineoldandroids.view.ViewHelper;

import android.R.id;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.provider.OpenableColumns;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
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
	private OnDragStatusChangeListener mListener;
	private Status mStatus = Status.Close;

	/**
	 * 状态枚举
	 */
	public static enum Status {
		Close, Open, Dragging
	}

	public interface OnDragStatusChangeListener {
		void onClose();

		void onOpen();

		void onDragging(float percent);
	}

	public void setOnDragStatusChangeListener(OnDragStatusChangeListener mListener) {
		this.mListener = mListener;
	}

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

			int newLeft = left;

			if (changedView == mLeftContent) {
				// 把当前变化量传递给mMainContent
				newLeft = mMainContent.getLeft() + dx;
				// 进行修正
				newLeft = fixLeft(newLeft);

				// 当做面板移动之后，再强制放回去
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}

			dispatchEvent(newLeft);

			// // 当做面板移动之后，再强制放回去
			// if (changedView == mLeftContent) {
			// mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
			// }
			// 为了兼容低版本，每次修改值之后进行重绘
			invalidate();
		}

		// 4.当view被释放时，处理事情（执行动画）
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			// releasedChild 被释放的子view
			// xvel 水平速度
			// yvel 垂直速度

			// 判断执行开启/关闭
			if (xvel == 0 && mMainContent.getLeft() > mRange / 2.0f) {
				open();
			} else if (xvel > 0) {
				open();
			} else {
				close();
			}
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

	@Override
	public void computeScroll() {
		super.computeScroll();
		// 2.持续平滑动画（高频率调用）
		if (mDragHelper.continueSettling(true)) {
			// 如果返回true,动画还需要继续执行
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	private void dispatchEvent(int newLeft) {
		float percent = newLeft * 1.0f / mRange;

		if (mListener!=null) {
			mListener.onDragging(percent);
		}
		
		// 更新状态，执行回调
		Status preStatus = mStatus;
		mStatus = updateStatus(percent);
		if (mStatus != preStatus) {
			// 状态发生变化
			if (mStatus == Status.Close) {
				// 当前为关闭状态
				if (mListener != null) {
					mListener.onClose();
				}
			} else if (mStatus == Status.Open) {
				if (mStatus == Status.Open) {
					mListener.onOpen();
				}
			}

		}
		// 伴随动画
		animViews(percent);
	}

	private Status updateStatus(float percent) {
		if (percent == 0.0f) {
			return Status.Close;
		} else if (percent == 1.0f) {
			return Status.Open;
		}
		return Status.Dragging;
	}

	private void animViews(float percent) {
		// 1.左面板：缩放动画，平移动画，透明度动画
		// Call requires API level 11
		// mLeftContent.setScaleX(0.5f+0.5f*percent);
		// mLeftContent.setScaleY(0.5f+0.5f*percent);
		ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f * percent);
		// 平移动画：-mWidth/2->0
		ViewHelper.setTranslationX(mLeftContent, evaluate(percent, -mWidth / 2.0f, 0.0f));
		// 透明度
		ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		// 2.主面板：缩放动画
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
		// 3.背景动画：亮度变化（颜色变化）
		getBackground().setColorFilter((int) evaluateColor(percent, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
	}

	/**
	 * 颜色变化过度
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public Object evaluateColor(float fraction, Object startValue, Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}

	/**
	 * 估值器
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	public void close(boolean isSmooth) {
		int finalLeft = 0;
		if (isSmooth) {
			// 1.触发一个平滑动画
			if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// 返回true代表还没有移动到指定位置，需要刷新界面
				// 参数传this（child所在的ViewGroup）
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	public void close() {
		close(true);
	}

	public void open(boolean isSmooth) {
		int finalLeft = mRange;
		if (isSmooth) {
			// 1.触发一个平滑动画
			if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// 返回true代表还没有移动到指定位置，需要刷新界面
				// 参数传this（child所在的ViewGroup）
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}

	}

	// 默认平滑移动
	public void open() {
		open(true);
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// return super.dispatchTouchEvent(ev);
	// }
}
