package com.djx.pin.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ClearView extends View {
	Paint paint;
	RectF oval;

	private float startAngle = -90;
	private float sweepAngle;
	public static final int STATE_FORWARD = 1;
	public static final int STATE_BACKWARD = 0;
	public int[] speeds_b = { -4, -4, -4, -5, -6, -7, -8, -9, -10, -10, -10 };
	public int[] speeds_f = { 10, 10, 10, 9, 8, 7, 6, 5, 4, 4, 4 };
	public int index_f;
	public int index_b;
	int state;// ��ǰ״̬
	boolean isRunning;

	public ClearView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		setAngle();
	}

	public void setAngle() {
		sweepAngle = 360;
		invalidate();
	}

	public void setAngle(final float targetAngle) {

		if (isRunning) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// �Ƕȱ仯����
						// ��ǰ��״̬���ͺ���״̬��

						switch (state) {
						case STATE_BACKWARD:
							// ��360--�˻ص�0
							isRunning = true;
							sweepAngle += speeds_b[index_b++];
							if (index_b == speeds_b.length) {
								index_b--;
							}

							if (sweepAngle <= 0) {
								state = STATE_FORWARD;
								sweepAngle = 0;
							}
							break;

						case STATE_FORWARD:
							sweepAngle += speeds_f[index_f++];
							if (index_f == speeds_f.length) {
								index_f--;
							}

							if (sweepAngle >= targetAngle) {
								sweepAngle = targetAngle;
								timer.cancel();
								isRunning = false;
								state = 0;
							}
							break;
						}
						postInvalidate();
					}
				}, 200, 24);
			}
		}).start();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		oval = new RectF(0, 0, width, height);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
	}

}
