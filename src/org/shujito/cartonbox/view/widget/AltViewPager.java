package org.shujito.cartonbox.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class AltViewPager extends ViewPager
{
	public AltViewPager(Context context)
	{
		super(context);
	}
	
	public AltViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e)
	{
		try
		{
			return super.onInterceptTouchEvent(e);
		}
		catch(Exception ex)
		{
			// have a crash, just catch it
			//Logger.e(this.getClass().getSimpleName(), "horror");
			return false;
		}
	}
}
