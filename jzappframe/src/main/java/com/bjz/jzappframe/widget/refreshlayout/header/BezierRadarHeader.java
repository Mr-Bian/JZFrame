package com.bjz.jzappframe.widget.refreshlayout.header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bjz.jzappframe.R;
import com.bjz.jzappframe.widget.refreshlayout.api.RefreshHeader;
import com.bjz.jzappframe.widget.refreshlayout.api.RefreshKernel;
import com.bjz.jzappframe.widget.refreshlayout.api.RefreshLayout;
import com.bjz.jzappframe.widget.refreshlayout.constant.RefreshState;
import com.bjz.jzappframe.widget.refreshlayout.constant.SpinnerStyle;
import com.bjz.jzappframe.widget.refreshlayout.header.bezierradar.RippleView;
import com.bjz.jzappframe.widget.refreshlayout.header.bezierradar.RoundDotView;
import com.bjz.jzappframe.widget.refreshlayout.header.bezierradar.RoundProgressView;
import com.bjz.jzappframe.widget.refreshlayout.header.bezierradar.WaveView;
import com.bjz.jzappframe.widget.refreshlayout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 贝塞尔曲线类雷达风格刷新组件
 * Created by lcodecore on 2016/10/2.
 */

public class BezierRadarHeader extends FrameLayout implements RefreshHeader {

    private WaveView mWaveView;
    private RippleView mRippleView;
    private RoundDotView mDotView;
    private RoundProgressView mProgressView;

    //<editor-fold desc="FrameLayout">
    public BezierRadarHeader(Context context) {
        this(context,null);
    }

    public BezierRadarHeader(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BezierRadarHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        setMinimumHeight(DensityUtil.dp2px(100));
        /**
         * 初始化headView
         */
        mWaveView = new WaveView(getContext());
        mRippleView = new RippleView(getContext());
        mDotView = new RoundDotView(getContext());
        mProgressView = new RoundProgressView(getContext());
        if (isInEditMode()) {
            this.addView(mWaveView, MATCH_PARENT, MATCH_PARENT);
            this.addView(mProgressView, MATCH_PARENT, MATCH_PARENT);
            mWaveView.setHeadHeight(1000);
        } else {
            this.addView(mWaveView, MATCH_PARENT, MATCH_PARENT);
            this.addView(mDotView, MATCH_PARENT, MATCH_PARENT);
            this.addView(mProgressView, MATCH_PARENT, MATCH_PARENT);
            this.addView(mRippleView, MATCH_PARENT, MATCH_PARENT);
            mProgressView.setScaleX(0);
            mProgressView.setScaleY(0);
        }


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BezierRadarHeader);

        int primaryColor = ta.getColor(R.styleable.BezierRadarHeader_srlPrimaryColor, 0);
        int accentColor = ta.getColor(R.styleable.BezierRadarHeader_srlAccentColor, 0);
        if (primaryColor != 0) {
            setPrimaryColor(primaryColor);
        }
        if (accentColor != 0) {
            setAccentColor(primaryColor);
        }

        ta.recycle();
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public BezierRadarHeader setPrimaryColor(int color) {
        mWaveView.setWaveColor(color);
        mProgressView.setBackColor(color);
        return this;
    }

    public BezierRadarHeader setAccentColor(int color) {
        mDotView.setDotColor(color);
        mRippleView.setFrontColor(color);
        mProgressView.setFrontColor(color);
        return this;
    }

    public BezierRadarHeader setPrimaryColorId(int colorId) {
        setPrimaryColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public BezierRadarHeader setAccentColorId(int colorId) {
        setAccentColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            setPrimaryColor(colors[0]);
        }
        if (colors.length > 1) {
            setAccentColor(colors[1]);
        }
    }

    @NonNull
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }

    @Override
    public void onInitialized(RefreshKernel layout, int height, int extendHeight) {

    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {
        mWaveView.setHeadHeight(Math.min(headHeight, offset));
        mWaveView.setWaveHeight((int)(1.9f* Math.max(0, offset - headHeight)));
        mDotView.setFraction(percent);
    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {
        onPullingDown(percent, offset, headHeight, extendHeight);
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        mWaveView.setHeadHeight(headHeight);
        ValueAnimator animator = ValueAnimator.ofInt(
                mWaveView.getWaveHeight(), 0,
                -(int)(mWaveView.getWaveHeight()*0.8),0,
                -(int)(mWaveView.getWaveHeight()*0.4f),0);
        animator.addUpdateListener(animation -> {
            mWaveView.setWaveHeight((int) animation.getAnimatedValue()/2);
            mWaveView.invalidate();
        });
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(800);
        animator.start();
        /*处理圈圈进度条**/
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDotView.setVisibility(INVISIBLE);
                mProgressView.animate().scaleX((float) 1.0);
                mProgressView.animate().scaleY((float) 1.0);
                mProgressView.postDelayed(() -> {
                    mProgressView.startAnim();
                },200);
            }
        });

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(animation -> mDotView.setAlpha((Float) animation.getAnimatedValue()));
        valueAnimator.start();
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        mProgressView.stopAnim();
        mProgressView.animate().scaleX(0f);
        mProgressView.animate().scaleY(0f);
        mRippleView.setVisibility(VISIBLE);
        mRippleView.startReveal();
        return 400;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                mRippleView.setVisibility(GONE);
                mDotView.setAlpha(1);
                mDotView.setVisibility(VISIBLE);
                break;
            case PullDownToRefresh:
                mProgressView.setScaleX(0);
                mProgressView.setScaleY(0);
                break;
            case PullToUpLoad:
                break;
            case Refreshing:
                break;
            case Loading:
                break;
        }
    }
    //</editor-fold>
}
