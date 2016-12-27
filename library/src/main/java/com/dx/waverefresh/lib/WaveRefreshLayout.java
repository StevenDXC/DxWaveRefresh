package com.dx.waverefresh.lib;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;

public class WaveRefreshLayout extends LinearLayout{

    private ShapeBackgroundDrawable background;
    private boolean isLoading;
    private float topImageHeight;
    private int mPaddingTop;

    public WaveRefreshLayout(Context context) {
        super(context);
        init(null);
    }

    public WaveRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WaveRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs){

       background = new ShapeBackgroundDrawable();
       if(attrs != null){

           TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.WaveRefreshLayout);
           background.color = ta.getInt(R.styleable.WaveRefreshLayout_wr_bgColor, Color.WHITE);
           background.topMargin = topImageHeight = ta.getDimensionPixelSize(R.styleable.WaveRefreshLayout_wr_topImageHeight,getResources().getDimensionPixelOffset(R.dimen.defaultTopImageHeight));
           background.angle = ta.getInt(R.styleable.WaveRefreshLayout_wr_angle,10);
           background.gravity = ta.getInt(R.styleable.WaveRefreshLayout_wr_gravity,1);
           background.waveAmplitude = ta.getDimensionPixelSize(R.styleable.WaveRefreshLayout_wr_waveAmplitude,30);
           background.speed = ta.getInt(R.styleable.WaveRefreshLayout_wr_speed,6);
           ta.recycle();

       }else{
           background.color = Color.WHITE;
           background.topMargin = topImageHeight = getResources().getDimensionPixelOffset(R.dimen.defaultTopImageHeight);
           background.angle = 10;
           background.gravity = 1;
           background.waveAmplitude = 30;
           background.speed = 6;
       }
        mPaddingTop = (int)topImageHeight;
        setPadding(getPaddingLeft(),(int)topImageHeight,getPaddingRight(),getPaddingBottom());
        setBackground(background);
    }

    /**
     * set the shape background margin and shape. and change the top padding of this view when user dragging
     * @param progress pulling down progress(current touch move distance / start refresh move distance)
     */
    public void setBackgroundOffset(float progress){
        background.setTopMargin(progress);
        adjustPaddingTop((int)(background.getOffsetY()*progress));
    }


    public void setPaddingTop(int paddingTop){
        mPaddingTop = paddingTop;
        setPadding(getPaddingLeft(),mPaddingTop,getPaddingRight(),getPaddingBottom());
    }

    private void adjustPaddingTop(int padding){
        setPadding(getPaddingLeft(),mPaddingTop+padding,getPaddingRight(),getPaddingBottom());
    }

    /**
     * while touch up,restore the background to the initial state
     */
    public void restoreBackground(){
        background.setLoading(false);
        startAnimation(false);
    }

    /**
     * start play loading animation
     */
    public void startLoadingAnimation(){
        background.setLoading(true);
        startAnimation(true);
    }

    public Boolean isLoading(){
        return isLoading;
    }

    private void startLoading(){
        isLoading = true;
        background.start();
    }

    private void startAnimation(boolean startLoading){

        ValueAnimator animator = ValueAnimator.ofFloat(background.getTopMargin(),topImageHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float) valueAnimator.getAnimatedValue();
                background.setTopMargin(value - topImageHeight);
            }
        });

        ValueAnimator animatorPadding = ValueAnimator.ofInt(getPaddingTop(),mPaddingTop);
        animatorPadding.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                setPadding(0,value,0,0);
            }
        });

        AnimatorSet set = new AnimatorSet();
        if(startLoading){
            set.playTogether(animator,animatorPadding,background.getPreLoadingAnimator());
        }else{
            set.playTogether(animator,animatorPadding);
        }
        set.setDuration(300);
        set.setInterpolator(new BounceInterpolator());
        if(startLoading){
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) { }

                @Override
                public void onAnimationEnd(Animator animator) {
                    startLoading();
                }

                @Override
                public void onAnimationCancel(Animator animator) { }

                @Override
                public void onAnimationRepeat(Animator animator) { }
            });
        }
        set.start();
    }


    /**
     * stop play the loading animation
     */
    public void stopLoading(){
        if(!isLoading){
            return;
        }

        isLoading = false;
        background.stop();
    }

    /**
     * get the y value of point that in oblique line.
     * @param x  the distance of from lowest point to current point in the oblique line
     * @return  y in the oblique line
     */
    public float getGradientTop(float x){
        return background.getGradientTop(x);
    }

}
