package org.shujito.cartonbox.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

// code taken from here:
// http://android-developers.blogspot.mx/2010/06/making-sense-of-multitouch.html
public class PostImageView extends View
{
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			PostImageView.this.mScaleFactor *= detector.getScaleFactor();
			
			// don't let it be too small or too large
			PostImageView.this.mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
			
			// redraw
			PostImageView.this.invalidate();
			return true;
		}
	}
	
	private static final int INVALID_POINTER_ID = -1;
	
	// the 'active pointer' is the one currently moving the object
	private int mActivePointerID = INVALID_POINTER_ID;
	
	private Drawable image = null;
	private float mPosX;
	private float mPosY;
	private float mLastTouchX;
	private float mLastTouchY;
	
	private ScaleGestureDetector mScaleDetector = null;
	private float mScaleFactor = 1.f;
	
	public PostImageView(Context context)
	{
		this(context, null, 0);
	}
	public PostImageView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	public PostImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.image = context.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
		this.image.setBounds(0, 0, this.image.getIntrinsicWidth(), this.image.getIntrinsicHeight());
		this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		canvas.save();
		canvas.translate(this.mPosX, this.mPosY);
		canvas.scale(this.mScaleFactor, this.mScaleFactor);
		
		this.image.draw(canvas);
		canvas.restore();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		this.mScaleDetector.onTouchEvent(event);
		
		final int action = event.getAction();
		switch(action & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
			{
				final float x = event.getX();
				final float y = event.getY();
				// remember where we started
				this.mLastTouchX = x;
				this.mLastTouchY = y;
				// save the ID of this pointer
				this.mActivePointerID = event.getPointerId(0);
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				// find the index of the active pointer and get it's pos'
				final int pointerIndex = event.findPointerIndex(this.mActivePointerID);
				final float x = event.getX(pointerIndex);
				final float y = event.getY(pointerIndex);
				
				// calculate the distance moved
				final float dx = x - this.mLastTouchX;
				final float dy = y - this.mLastTouchY;
				
				// move the object
				this.mPosX += dx;
				this.mPosY += dy;
				
				// remember this touch position for the next move event
				this.mLastTouchX = x;
				this.mLastTouchY = y;
				
				// invalidate to request redraw
				this.invalidate();
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				this.mActivePointerID = INVALID_POINTER_ID;
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			{
				this.mActivePointerID = INVALID_POINTER_ID;
				break;
			}
			case MotionEvent.ACTION_POINTER_UP:
			{
				// extract the index of the pointer that left the touch sensor
				final int pointerIndex =
						(action & MotionEvent.ACTION_POINTER_INDEX_MASK)
							>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = event.getPointerId(pointerIndex);
				if(pointerId == this.mActivePointerID)
				{
					// the previous pointer got away, grab  an existing one then
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					this.mLastTouchX = event.getX(newPointerIndex);
					this.mLastTouchY = event.getY(newPointerIndex);
					this.mActivePointerID = event.getPointerId(newPointerIndex);
				}
				break;
			}
		}
		
		return true;
	}
}
