package com.example.spirit.test820;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;

public class MyView extends ViewGroup {

    private View view1;
    private ViewDragHelper dragHelper;

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        view1 = getChildAt(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量方式一：
        //int measureSpec = MeasureSpec.makeMeasureSpec(view1.getLayoutParams().width, MeasureSpec
        //       .EXACTLY);
        //view1.measure(measureSpec, measureSpec);

        //测量方式二：
        measureChild(view1, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft() + getMeasuredWidth() / 2 - view1.getMeasuredWidth() / 2;
        int top = getPaddingTop();
        view1.layout(left, top, left + view1.getMeasuredWidth(), top + view1.getMeasuredHeight());
        System.out.println(l + ", " + t + ", " + r + ", " + b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将触摸事件交给dragHelper来解析处理
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让dragHelper自己来判断是否应该拦截
        boolean interceptTouchEvent = dragHelper.shouldInterceptTouchEvent(ev);
        return interceptTouchEvent;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        //用于判断是否捕获当前child的触摸事件
        //child当前触摸的子view
        //return：true就捕获并解析  false：不处理
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return true;
        }

        /**
         * 当view开始被捕获和解析的回调
         * @param capturedChild 当前被捕获的子view
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 获取view在水平方向的拖拽范围，但目前不能限制边界，返回的值目前用在手指抬起的时候view缓慢移动的动画
         * 时间上面的计算上面
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        /**
         * 获取view在垂直方向的拖拽范围
         * @param child
         * @return
         */
        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        /**
         * 控制child在水平方向的移动
         * left:表示viewDragHelper认为你想让当前的child的left改变的值，left=child.getLeft()+dx
         * dx:本次child移动的距离
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (left < 0) {
                left = 0;
            } else if (left > getMeasuredWidth() - child.getMeasuredWidth()) {
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;
        }

        /**
         * 控制child在垂直方向的移动
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (top < 0) {
                top = 0;
            } else if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
        }

        /**
         * 当child的位置改变的时候执行
         * @param changedView 位置改变的child
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx,
                                          int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            //计算view移动的百分比
            float fraction = changedView.getLeft() * 1f / (getMeasuredWidth() - changedView
                    .getMeasuredWidth());
            System.out.println(fraction);
            executeAnimation(fraction);
        }

        //执行伴随动画
        private void executeAnimation(float fraction) {
            //fraction 0-1
            ViewHelper.setScaleX(view1, 1+0.5f * fraction);
            view1.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED,Color.GREEN));
        }

        /**
         * 手指抬起的时候执行的方法
         * @param releasedChild
         * @param xvel  x方向移动的速度
         * @param yvel  y方向移动的速度
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft = getMeasuredWidth() / 2 - releasedChild.getMeasuredWidth() / 2;
            if (releasedChild.getLeft() < centerLeft) {

                dragHelper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(MyView.this);
            } else {
                int finalLeft = getMeasuredWidth() - releasedChild.getMeasuredWidth();
                dragHelper.smoothSlideViewTo(releasedChild, finalLeft, releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(MyView.this);
            }
        }
    };

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(MyView.this);
        }
    }
}
