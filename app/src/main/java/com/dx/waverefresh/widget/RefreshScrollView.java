package com.dx.waverefresh.widget;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ScrollView;

import com.dx.waverefresh.lib.WaveRefreshLayout;

public class RefreshScrollView extends ScrollView{


    private float downY;

    private WaveRefreshLayout mContentLayout;

    private float touchSlop;

    private OnStartRefreshingListener mOnStartRefreshingListener;

    private View mAnimatableView;

    private int animationDuration;

    private boolean startRefresh = false;

    public RefreshScrollView(Context context) {
        super(context);
        init();
    }

    public RefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        animationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mContentLayout = (WaveRefreshLayout) getChildAt(0);
    }

    /**
     * process pull down to refresh touch event
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(mContentLayout.isLoading()){
            return super.onTouchEvent(ev);
        }

        if(ev.getAction() == MotionEvent.ACTION_DOWN && getScrollY() == 0){
            startRefresh = true;
            downY = ev.getY();
        }

        if(ev.getAction() == MotionEvent.ACTION_MOVE && startRefresh){
            float diff = ev.getY() - downY;
            if(diff > touchSlop && diff < getHeight()/5.0f){
                float p = diff/(getHeight()/5.0f);
                mContentLayout.setBackgroundOffset(p);
                scaleAnimationView(p);
            }
        }

        if(ev.getAction() == MotionEvent.ACTION_UP && startRefresh){
            float diff = ev.getY() - downY;

            if(diff >= getHeight()/5.0f){
                mContentLayout.startLoadingAnimation();
                if(mOnStartRefreshingListener != null){
                    mOnStartRefreshingListener.startRefreshing();
                    playScaleAnimation();
                }
            }else if(diff > touchSlop){
                mContentLayout.restoreBackground();
                playRestoreScaleAnimation();
            }

            startRefresh = false;
            downY = 0;
        }

        return super.onTouchEvent(ev);
    }


    public void setOnStartRefreshingListener(OnStartRefreshingListener listener){
        this.mOnStartRefreshingListener = listener;
    }


    public void stopLoading(){
        mContentLayout.stopLoading();
        playStopAnimation();
    }

    /**
     * set the view that animate with user's gesture.
     */
    public void setAnimatableView(View view){
        this.mAnimatableView = view;
    }

    private void scaleAnimationView(float p){
        if(mAnimatableView != null){
            float scale = p > 0.9 ? 0 : 1 - p - .1f;
            mAnimatableView.setScaleX(scale);
            mAnimatableView.setScaleY(scale);
        }
    }

    private void playRestoreScaleAnimation(){

        if(mAnimatableView == null){
            return;
        }

        ObjectAnimator animatorSX = ObjectAnimator.ofFloat(mAnimatableView,"scaleX",mAnimatableView.getScaleX(),1);
        ObjectAnimator animatorSY = ObjectAnimator.ofFloat(mAnimatableView,"scaleY",mAnimatableView.getScaleY(),1);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(animationDuration);
        set.playTogether(animatorSX,animatorSY);
        set.setInterpolator(new BounceInterpolator());
        set.start();
    }

    private void playScaleAnimation(){

        if(mAnimatableView == null){
            return;
        }

        ObjectAnimator animatorSX = ObjectAnimator.ofFloat(mAnimatableView,"scaleX",mAnimatableView.getScaleX(),0);
        ObjectAnimator animatorSY = ObjectAnimator.ofFloat(mAnimatableView,"scaleY",mAnimatableView.getScaleY(),0);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(animationDuration);
        set.playTogether(animatorSX,animatorSY);
        set.setInterpolator(new BounceInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mAnimatableView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.start();
    }

    private void playStopAnimation(){

        if(mAnimatableView == null){
            return;
        }

        if(mAnimatableView.getVisibility() != View.VISIBLE){
            mAnimatableView.setVisibility(View.VISIBLE);
        }

        ObjectAnimator animatorSX = ObjectAnimator.ofFloat(mAnimatableView,"scaleX",0,1);
        ObjectAnimator animatorSY = ObjectAnimator.ofFloat(mAnimatableView,"scaleY",0,1);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(animationDuration);
        set.setStartDelay(animationDuration);
        set.playTogether(animatorSX,animatorSY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    /**
     * start refreshing callback
     */
    public interface OnStartRefreshingListener{
        void startRefreshing();

    }

}
