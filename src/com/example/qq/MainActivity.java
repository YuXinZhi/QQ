package com.example.qq;

import com.example.qq.ui.DragLayout;
import com.example.qq.ui.DragLayout.OnDragStatusChangeListener;
import com.example.qq.utils.ToastUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		DragLayout mDragLayout = (DragLayout) findViewById(R.id.dl);
		mDragLayout.setOnDragStatusChangeListener(new OnDragStatusChangeListener() {

			@Override
			public void onOpen() {
				ToastUtils.showToastShort(MainActivity.this, "onOpen");
			}

			@Override
			public void onDragging(float percent) {
				ToastUtils.showToastShort(MainActivity.this, "onDragging");
			}

			@Override
			public void onClose() {
				ToastUtils.showToastShort(MainActivity.this, "onClose");
			}
		});

	}

}
