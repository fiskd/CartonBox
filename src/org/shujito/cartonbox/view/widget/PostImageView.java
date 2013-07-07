package org.shujito.cartonbox.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PostImageView extends ImageView
{
	/* constructor */
	public PostImageView(Context context)
	{
		super(context);
	}
	
	public PostImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public PostImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	/* constructor */
	
	/* meth */
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return super.onTouchEvent(event);
	}
	/* meth */
}
