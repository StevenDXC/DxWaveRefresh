package com.dx.waverefresh.lib;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Choreographer;


class ShapeBackgroundDrawable extends Drawable implements Animatable{


    public int color;
    /***
     *  angle of tilt
     */
    public int angle;

    /**
     *  amplitude of wave
     */
    public float waveAmplitude;
    /**
     * top margin of shape
     */
    public float topMargin;

    /**
     * the direction of tilt, 0:left, 1:right
     */
    public int gravity;

    /**
     * wave bitmap translate speed
     */
    public int speed;


    private Paint mPaint;
    private Path mPath;

    /**
     * tilting height
     */
    private float initOffset;

    /**
     *  dynamic tilting height，change the value for frame animation.
     */
    private float offset;

    /**
     *  dynamic top margin，change the value for frame animation.
     */
    private float topMarginOffset;

    /**
     * bounds of this drawable
     */
    private Rect rect;


    private boolean isLoading;

    /**
     * wave bitmap transition x and y
     */
    private float tx = 0,ty = 0;

    /**
     *  wave bitmap Matrix
     */
    private Matrix mMatrix;

    /**
     * draw 4 arc to this bitmap, simulate the shape of wave
     */
    private Bitmap mWaveBitmap;   //waveImage

    /**
     * the top of waveBitmap
     */
    private float mWaveTop;

    private Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long l) {
            invalidateSelf();
            if (isLoading) {
                Choreographer.getInstance().postFrameCallback(this);
            }
        }
    };


    public ShapeBackgroundDrawable(){

        mPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mMatrix = new Matrix();
        isLoading = false;
    }


    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        if(rect == null) {
            rect = new Rect(left, top, right, bottom);
            topMarginOffset = topMargin;
            mPaint.setColor(color);
            offset = initOffset = (float) Math.abs(Math.tan(angle / 180.0f * Math.PI) * (right - left));
            mWaveTop = topMargin - initOffset/2;
            ty = waveAmplitude/2; // set wave bitmap transition to half of waveAmplitude

            //draw filled arc to bitmap
            mWaveBitmap = Bitmap.createBitmap(right * 2, (int)waveAmplitude * 2, Bitmap.Config.ARGB_4444);
            Canvas c = new Canvas(mWaveBitmap);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);

            Path path = new Path();
            path.moveTo(0, waveAmplitude);
            path.quadTo(rect.right / 4, 0, rect.right / 2, waveAmplitude);
            path.quadTo(rect.right / 4 * 3, waveAmplitude * 2, rect.right, waveAmplitude);
            path.quadTo(rect.right / 4 * 5, 0, rect.right * 1.5f, waveAmplitude);
            path.quadTo(rect.right / 4 * 7, waveAmplitude * 2, rect.right * 2f, waveAmplitude);
            path.lineTo(rect.right * 2, waveAmplitude * 2);
            path.lineTo(0, waveAmplitude * 2);
            path.close();

            c.drawPath(path, p);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        int sc = canvas.saveLayer(0, 0, rect.width(), rect.height(), null, Canvas.ALL_SAVE_FLAG);

        if(isLoading){
            //while loading,draw wave bitmap,and translate the bitmap from left to right.
            canvas.drawRect(0,mWaveTop+waveAmplitude*2,rect.right,rect.bottom,mPaint);
            mMatrix.setTranslate(tx,mWaveTop+ty);
            canvas.drawBitmap(mWaveBitmap,mMatrix,mPaint);
            tx -= speed;
            if(tx <= -rect.right){
                tx += rect.right;
            }
        }else{
            mPath.reset();
            if(gravity == 0){
                mPath.moveTo(0,topMarginOffset);
                mPath.lineTo(rect.right,topMarginOffset-offset);
            }else{
                mPath.moveTo(0,topMarginOffset-offset);
                mPath.lineTo(rect.right,topMarginOffset);
            }
            mPath.lineTo(rect.right,rect.bottom);
            mPath.lineTo(0,rect.bottom);
            mPath.close();
            canvas.drawPath(mPath,mPaint);
        }

        canvas.restoreToCount(sc);

    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    @Override
    public void start() {
        isLoading = true;
        Choreographer.getInstance().postFrameCallback(mFrameCallback);
    }

    @Override
    public void stop() {
        stopLoadingAnimation();
    }

    @Override
    public boolean isRunning() {
        return isLoading;
    }

    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;
    }

    public float getOffsetY(){
        return initOffset;
    }

    /**
     * get the top of wave in the middle of drawable
     */
    public int getCurrentWaveTop(){
        float x = -tx;
        float f = x%(rect.right/4);
        int d = (int)(x/(rect.right/4));
        float v = 0;
        switch (d){
            case 0:
                v =  f/(rect.right/4) * -waveAmplitude;
                break;
            case 1:
                v = -waveAmplitude + waveAmplitude*f/(rect.right/4);
                break;
            case 2:
                v = waveAmplitude*f/(rect.right/4);
                break;
            case 3:
                v = waveAmplitude - waveAmplitude*f/(rect.right/4);
                break;
        }
        return (int)v;
    }

    public float getTopMargin(){
        return topMargin;
    }

    public void setTopMargin(float progress){
        topMarginOffset = topMargin + initOffset*progress;
        offset = initOffset * (1.0f - progress);
        invalidateSelf();
    }

    public float getGradientTop(float x){
       return topMargin - (float)Math.abs(Math.tan(angle/180.0f*Math.PI)) * x;
    }

    public Animator getPreLoadingAnimator(){
        ValueAnimator animator = ValueAnimator.ofFloat(waveAmplitude/2,0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ty = (Float) valueAnimator.getAnimatedValue();
                invalidateSelf();
            }
        });
        return animator;
    }

    private void stopLoadingAnimation(){
        ValueAnimator animator = ValueAnimator.ofFloat(0,waveAmplitude);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ty = (Float) valueAnimator.getAnimatedValue();
            }
        });

        ValueAnimator animator2 = ValueAnimator.ofFloat(0,initOffset);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                offset = (Float) valueAnimator.getAnimatedValue();
            }
        });

        ValueAnimator animator3 = ValueAnimator.ofFloat(topMargin, topMargin);
        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                topMargin = (Float) valueAnimator.getAnimatedValue();
                invalidateSelf();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.playTogether(animator,animator2,animator3);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isLoading = false;
                Choreographer.getInstance().removeFrameCallback(mFrameCallback);
                invalidateSelf();
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


}
