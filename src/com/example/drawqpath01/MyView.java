package com.example.drawqpath01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Region.Op;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MyView extends SurfaceView implements Callback, Runnable {

	private SurfaceHolder mHolder;

	private Paint mPaint;

	private Paint mCirclePaint, mFlowTextPaint;

	private Canvas mCanvas;

	private Thread mThread;

	private boolean mFlag;

	private float mScreenX, mScreenY, mRealScreenX, mRadius, mX,mY;

	private float mStartX1, mStartY1, mControlX1, mControlY1, mEndX1, mEndY1;
	private float mStartX2, mStartY2, mControlX2, mControlY2, mEndX2, mEndY2;
	private float mStartX3, mStartY3, mControlX3, mControlY3, mEndX3, mEndY3;
	private float mStartX4, mStartY4, mControlX4, mControlY4, mEndX4, mEndY4;

	private Path mPath1, mPath2, mPath3, mPath4;

	private float mMaxY, mMinY;

	private static final int UP = 0;

	private static final int DOWN = 1;

	private int mState1 = DOWN;
	private int mState2 = UP;
	private int mState3 = DOWN;

	private static final int SWING = 7;

	private float SPEED = 7f;

	private Path mCirclePath;

	private static final float mOffsetX = 50f;

	private float mSizeOfFlowText;
	private float mSizeOfPrompText;
	private float mSizeOfDetailsText;
	
	private String mRemainFlowText;
	
	private FontMetrics fontMetrics;

	public MyView(Context context) {
		super(context);
		init(context);
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mSizeOfFlowText = MeasureUtil.sp2dx(context, 35);
		mSizeOfPrompText = MeasureUtil.sp2dx(context, 18);
		mSizeOfDetailsText = MeasureUtil.sp2dx(context,13);
		mHolder = getHolder();
		mHolder.addCallback(this);
		setZOrderOnTop(true);
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mPaint.setColor(getResources().getColor(R.color.bowen));
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(3);
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mCirclePaint.setColor(getResources().getColor(R.color.circle));
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setStrokeWidth(10);
		mCirclePath = new Path();
		mPath1 = new Path();
		mPath2 = new Path();
		mPath3 = new Path();
		mPath4 = new Path();

		mFlowTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

		mFlowTextPaint.setTextSize(mSizeOfFlowText);

		mFlowTextPaint.setColor(0xFFFFFFFF);

		mFlowTextPaint.setStyle(Style.FILL);
		
		fontMetrics = mFlowTextPaint.getFontMetrics();
		
		setFlowText("172.9");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mScreenX = (float) (getWidth() / 2.0);
		mRadius = mScreenX / 2;
		mRealScreenX = getWidth();
		mX = mRealScreenX / 2;
		mScreenY = getHeight();
		mY = (float) (mScreenY/2.0);
		mMaxY = mScreenY / 2 + SWING;
		mMinY = mScreenY / 2 - SWING;
		mStartX1 = mX - mRadius;
		mStartY1 = mScreenY / 2;
		mEndX1 = mX;
		mEndY1 = mScreenY / 2;
		mControlX1 = mX - mRadius / 2;
		mControlY1 = mScreenY / 2;
		mStartX2 = mEndX1;
		mStartY2 = mScreenY / 2;
		mEndX2 = mX + mRadius;
		mEndY2 = mScreenY / 2;
		mControlX2 = mX + mRadius / 2;
		mControlY2 = mScreenY / 2;
		mStartX3 = mEndX2;
		mStartY3 = mScreenY / 2;
		mEndX3 = mX + mRadius * 2;
		mEndY3 = mScreenY / 2;
		mControlX3 = mX + mRadius + mRadius / 2;
		mControlY3 = mScreenY / 2;
		mStartX4 = mEndX3;
		mStartY4 = mScreenY / 2;
		mEndX4 = mX + mRadius * 3;
		mEndY4 = mScreenY / 2;
		mControlX4 = mX + mRadius * 2 + mRadius / 2;
		mControlY4 = mScreenY / 2;
		mFlag = true;
		mThread = new Thread(this);
		mThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mFlag = false;
	}

	@Override
	public void run() {
		while (mFlag) {
			long start = System.nanoTime();
			draw();
			logic();
			long end = System.nanoTime();
			if (end - start < 50) {
				try {
					Thread.sleep(50 - (end - start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void draw() {
		try {
			mCanvas = mHolder.lockCanvas();
			if (mCanvas != null) {
				mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
				mCanvas.save();
				clipCircle(mCanvas);
				mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
				drawPath(mCanvas);
				drawCircle(mCanvas);
				mCanvas.restore();
				float flowW = mFlowTextPaint.measureText(mRemainFlowText);
				mCanvas.drawText(mRemainFlowText, mX-(flowW)/2, mY+(fontMetrics.descent-fontMetrics.ascent)/2-10, mFlowTextPaint);
			}
		} catch (Exception e) {

		} finally {
			if (mCanvas != null) {
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	private void drawCircle(Canvas canvas) {
		canvas.drawCircle(mRealScreenX / 2, mScreenY / 2, mScreenX / 2 - 2, mCirclePaint);
	}

	private void clipCircle(Canvas canvas) {
		mCirclePath.reset();
		canvas.clipPath(mCirclePath);
		mCirclePath.addCircle(mRealScreenX / 2, mScreenY / 2, mScreenX / 2, Direction.CCW);
		canvas.clipPath(mCirclePath, Op.REPLACE);
	}

	private void drawPath(Canvas canvas) {
		mPath1.reset();
		mPath1.moveTo(mStartX1 - 10, mStartY1);
		mPath1.quadTo(mControlX1, mMinY, mEndX1, mEndY1);
		mPath1.lineTo(mEndX1, mScreenY);
		mPath1.lineTo(mStartX1 - 10, mScreenY);
		mPath1.close();
		canvas.drawPath(mPath1, mPaint);
		mPath2.reset();
		mPath2.moveTo(mStartX2, mStartY2);
		mPath2.quadTo(mControlX2, mMaxY, mEndX2, mEndY2);
		mPath2.lineTo(mEndX2, mScreenY);
		mPath2.lineTo(mStartX2, mScreenY);
		mPath2.close();
		canvas.drawPath(mPath2, mPaint);
		mPath3.reset();
		mPath3.moveTo(mStartX3, mStartY3);
		mPath3.quadTo(mControlX3, mMinY, mEndX3, mEndY3);
		mPath3.lineTo(mEndX3, mScreenY);
		mPath3.lineTo(mStartX3, mScreenY);
		mPath3.close();
		canvas.drawPath(mPath3, mPaint);
		mPath4.reset();
		mPath4.moveTo(mStartX4, mStartY4);
		mPath4.quadTo(mControlX4, mMaxY, mEndX4, mEndY4);
		mPath4.lineTo(mEndX4, mScreenY);
		mPath4.lineTo(mStartX4, mScreenY);
		mPath4.close();
		canvas.drawPath(mPath4, mPaint);
	}

	private void logic() {
		if (mStartX1 <= mX - mRadius * 3) {
			mStartX1 = mX + mRadius;
			Log.d("", "===ZL : x1: " + mStartX1 + ",end4 : " + mEndX4);
			mControlX1 = mX + mRadius + mRadius / 2;
			mEndX1 = mX + mRadius * 2;
		} else {
			mStartX1 -= SPEED;
			mControlX1 -= SPEED;
			mEndX1 -= SPEED;
		}

		if (mStartX2 <= mX - mRadius * 3) {
			mStartX2 = mX + mRadius;
			Log.d("", "===ZL : x1: " + mStartX1 + ",end4 : " + mEndX4);
			mControlX2 = mX + mRadius + mRadius / 2;
			mEndX2 = mX + mRadius * 2;
		} else {
			mStartX2 -= SPEED;
			mControlX2 -= SPEED;
			mEndX2 -= SPEED;
		}

		if (mStartX3 <= mX - mRadius * 3) {
			mStartX3 = mX + mRadius;
			Log.d("", "===ZL : x1: " + mStartX1 + ",end4 : " + mEndX4);
			mControlX3 = mX + mRadius + mRadius / 2;
			mEndX3 = mX + mRadius * 2;
		} else {
			mStartX3 -= SPEED;
			mControlX3 -= SPEED;
			mEndX3 -= SPEED;
		}

		if (mStartX4 <= mX - mRadius * 3) {
			mStartX4 = mX + mRadius;
			Log.d("", "===ZL : x1: " + mStartX1 + ",end4 : " + mEndX4);
			mControlX4 = mX + mRadius + mRadius / 2;
			mEndX4 = mX + mRadius * 2;
		} else {
			mStartX4 -= SPEED;
			mControlX4 -= SPEED;
			mEndX4 -= SPEED;
		}

	}

	private FlaotPoint[] getPoints(Path path) {
		FlaotPoint[] pointArray = new FlaotPoint[20];
		PathMeasure pm = new PathMeasure(path, false);
		float length = pm.getLength();
		float distance = 0f;
		float speed = length / 20;
		int counter = 0;
		float[] aCoordinates = new float[2];
		while ((distance < length) && (counter < 20)) {
			pm.getPosTan(distance, aCoordinates, null);
			pointArray[counter] = new FlaotPoint(aCoordinates[0], aCoordinates[1]);
			counter++;
			distance = distance + speed;
		}
		return pointArray;
	}

	class FlaotPoint {
		float x, y;

		public FlaotPoint(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// // int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	// // int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	// // int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	// // int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	// // Log.i("", "===ZL widthsize : " + widthSize);
	// // Log.i("", "===ZL heightsize : " + heightSize);
	// super.measure(widthMeasureSpec, heightMeasureSpec);
	// }
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		Log.i("", "===ZL widthsize : " + widthSize);
		Log.i("", "===ZL heightsize : " + heightSize);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void setFlowText(String text){
		mRemainFlowText = text;
	}
}
