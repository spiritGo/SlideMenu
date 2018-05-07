package com.example.spirit.test820;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

public class SlideMenu extends FrameLayout {

    private View layoutMain;
    private View leftMenu;
    private ViewDragHelper viewDragHelper;
    private int width;
    private float dragRange;
    private FloatEvaluator floatEvaluator;
    private IntEvaluator intEvaluator;

    public SlideMenu(@NonNull Context context) {
        super(context);
        init();
    }

    public SlideMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 2) {
            throw new IllegalArgumentException("slideMenu only have 2 children!");
        }

        leftMenu = getChildAt(0);
        layoutMain = getChildAt(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 该方法在onMeasure方法执行完之后执行，可以在该方法中初始化自己和子view的高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width * 0.6f;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == leftMenu || child == layoutMain;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return (int) dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (child == layoutMain) {
                if (left < 0) left = 0;
                if (left > dragRange) left = (int) dragRange;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx,
                                          int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == leftMenu) {
                leftMenu.layout(0, 0, leftMenu.getMeasuredWidth(), leftMenu.getMeasuredHeight());

                int newLeft = layoutMain.getLeft() + dx;
                if (newLeft < 0) newLeft = 0;
                if (newLeft > dragRange) newLeft = (int) dragRange;
                layoutMain.layout(newLeft, layoutMain.getTop() + dy, newLeft + layoutMain
                        .getMeasuredWidth(), layoutMain.getBottom() + dy);

            }

            //计算滑动的百分比
            float fraction = layoutMain.getLeft() / dragRange;
            //执行伴随的动画
            executeAnim(fraction);
            //更改状态，回调listener的方法
            if (fraction == 1 && currenState != DragState.open) {
                currenState = DragState.open;
                if (onDragStateChangeListener != null) {
                    onDragStateChangeListener.onOpen();
                }
            } else if (fraction == 0 && currenState != DragState.close) {
                currenState = DragState.close;
                if (onDragStateChangeListener != null) {
                    onDragStateChangeListener.onClose();
                }
            }

            if (onDragStateChangeListener != null) onDragStateChangeListener.onDragging(fraction);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (layoutMain.getLeft() < dragRange / 2) {
                close();
            } else {
                open();
            }

            if (xvel > 200 && currenState != DragState.open) {
                open();
            } else if (xvel < -200 && currenState != DragState.close) {
                close();
            }
        }
    };

    public void open() {
        viewDragHelper.smoothSlideViewTo(layoutMain, (int) dragRange, layoutMain.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    public void close() {
        viewDragHelper.smoothSlideViewTo(layoutMain, 0, layoutMain.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    private void executeAnim(float fraction) {
//        ViewHelper.setScaleX(layoutMain, 1 - 0.2f * fraction);
//        ViewHelper.setScaleY(layoutMain, 1 - 0.2f * fraction);

        ViewHelper.setScaleX(layoutMain, floatEvaluator.evaluate(fraction, 1f, 0.8f));
        ViewHelper.setScaleY(layoutMain, floatEvaluator.evaluate(fraction, 1f, 0.8f));

        ViewHelper.setTranslationX(leftMenu, intEvaluator.evaluate(fraction, -leftMenu
                .getMeasuredWidth() / 2, 0));
        ViewHelper.setScaleX(leftMenu, floatEvaluator.evaluate(fraction, 0.6f, 1f));
        ViewHelper.setScaleY(leftMenu, floatEvaluator.evaluate(fraction, 0.6f, 1f));

        ViewHelper.setAlpha(leftMenu, floatEvaluator.evaluate(fraction, 0.3f, 1f));
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,
                Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }


    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    private OnDragStateChangeListener onDragStateChangeListener;

    public void setOnDragStateChangeListener(OnDragStateChangeListener onDragStateChangeListener) {
        this.onDragStateChangeListener = onDragStateChangeListener;
    }

    enum DragState {
        open, close
    }

    private DragState currenState = DragState.close;

    public interface OnDragStateChangeListener {
        void onOpen();

        void onClose();

        void onDragging(float fraction);
    }

    public DragState getCurrenState() {
        return currenState;
    }
}
