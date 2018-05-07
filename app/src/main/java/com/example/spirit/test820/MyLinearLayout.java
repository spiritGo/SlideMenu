package com.example.spirit.test820;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 当slideMenu打开的时候拦截并消费掉触摸事件
 */
public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu slideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slideMenu != null && slideMenu.getCurrenState() == SlideMenu.DragState.open) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slideMenu != null && slideMenu.getCurrenState() == SlideMenu.DragState.open) {
            if (event.getAction()==MotionEvent.ACTION_UP){
                slideMenu.close();
            }

            return true;
        }
        return super.onTouchEvent(event);
    }
}
