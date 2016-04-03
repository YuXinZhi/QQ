package com.example.qq;

import java.security.spec.MGF1ParameterSpec;
import java.util.Random;

import com.example.qq.ui.DragLayout;
import com.example.qq.ui.DragLayout.OnDragStatusChangeListener;
import com.example.qq.ui.MainLinearLayout;
import com.example.qq.utils.Cheeses;
import com.example.qq.utils.ToastUtils;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		final ListView mLeftList = (ListView) findViewById(R.id.lv_left);
		final ListView mMainList = (ListView) findViewById(R.id.lv_main);
		final ImageView mHeaderImage = (ImageView) findViewById(R.id.iv_header);
		final MainLinearLayout mMainLinearLayout = (MainLinearLayout) findViewById(R.id.mll);
		// 查找DragLayout设置监听
		DragLayout mDragLayout = (DragLayout) findViewById(R.id.dl);
		mDragLayout.setOnDragStatusChangeListener(new OnDragStatusChangeListener() {

			@Override
			public void onOpen() {
				ToastUtils.showToastShort(MainActivity.this, "onOpen");
				// 左面板Listview随机设置一个条目
				Random random = new Random();
				int nextInt = random.nextInt(50);
				mLeftList.smoothScrollToPosition(nextInt);
			}

			@Override
			public void onDragging(float percent) {
				// 更新图标透明度
				ViewHelper.setAlpha(mHeaderImage, 1 - percent);
			}

			@Override
			public void onClose() {
				ToastUtils.showToastShort(MainActivity.this, "onClose");
				// 让图标晃动

				// mHeaderImage.setTranslationX(translationX);
				ObjectAnimator mAnimator = ObjectAnimator.ofFloat(mHeaderImage, "translationX", 15.0f);
				mAnimator.setInterpolator(new CycleInterpolator(4));
				mAnimator.setDuration(500);
				mAnimator.start();
			}
		});

		// 设置引用
		mMainLinearLayout.setDragLayout(mDragLayout);
		mLeftList.setAdapter(
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings) {
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						View view = super.getView(position, convertView, parent);
						TextView mTextView = (TextView) view;
						mTextView.setTextColor(Color.WHITE);
						return view;
					}
				});

		mMainList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES) {

		});

	}

}
