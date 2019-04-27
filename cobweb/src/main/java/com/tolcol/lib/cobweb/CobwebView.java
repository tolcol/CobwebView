package com.tolcol.lib.cobweb;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * 蛛网
 */
@SuppressWarnings("unused")
public class CobwebView extends View {

    private int mCount;//顶点数
    private float mAngle;//顶点角度
    private int mCenterX, mCenterY;//中心点
    private int mRadius;//半径

    //背景线条
    private Paint mCobwebPaint;
    private float mCobwebScale;//半径系数,给文字留出空间
    private float mCobwebWidth;//背景线条宽度
    private int mCobwebColor;//背景线条颜色

    //分数
    private Paint mScorePaint;
    private Path mPath;//线条Path
    private float mScoreMax;//最大分数
    private boolean mScorePoint;//是否要绘制点
    private float mScoreSetp;//分数间隔
    private float mSetpLegth;//间隔的长度
    private float mScoreStrokeWidth;//分数线条宽度
    private float mScoreUnitLength;//每一分的长度
    private boolean isScoreFill;//分数是否填充

    //文字
    private Paint mTextPaint;//画笔
    private float mTitleSize;//标题大小
    private int mTitleColor;//标题颜色
    private List<DataItem> mDataList;
    private String[] mTitles;

    public CobwebView(Context context) {
        this(context, null);
    }

    public CobwebView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CobwebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CobwebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                      int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //背景线条
        mPath = new Path();
        mCobwebPaint = new Paint();
        mScorePaint = new Paint();
        mTextPaint = new Paint();
        mCobwebPaint.setAntiAlias(true);
        mScorePaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CobwebView);
        //背景线条
        mCobwebColor = typedArray.getColor(R.styleable.CobwebView_cbvColor, 0xFF000000);
        mCobwebScale = typedArray.getFloat(R.styleable.CobwebView_cbvScale, 0.7f);
        mCobwebWidth = typedArray.getDimension(R.styleable.CobwebView_cbvStrokeWidth, 2);
        //分数
        mScoreMax = typedArray.getFloat(R.styleable.CobwebView_cbvScoreMax, 10f);
        mScoreSetp = typedArray.getInteger(R.styleable.CobwebView_cbvScoreStep, 1);
        mScoreStrokeWidth = typedArray
                .getDimension(R.styleable.CobwebView_cbvScoreStrokeWidth, mCobwebWidth);
        mScorePoint = typedArray.getBoolean(R.styleable.CobwebView_cbvScorePoint, false);
        isScoreFill = typedArray.getBoolean(R.styleable.CobwebView_cbvScoreFill, false);
        //标题
        mTitleSize = typedArray.getDimension(R.styleable.CobwebView_cbvTitleSize, 30);
        mTitleColor = typedArray.getColor(R.styleable.CobwebView_cbvTitleColor, mCobwebColor);
        typedArray.recycle();
        initPaint();
    }

    private void initPaint() {
        //背景线条
        mCobwebPaint.setColor(mCobwebColor);
        mCobwebPaint.setStyle(Paint.Style.STROKE);
        mCobwebPaint.setStrokeWidth(mCobwebWidth);
        //分数
        mScorePaint.setStyle(isScoreFill ? Paint.Style.FILL : Paint.Style.STROKE);
        mScorePaint.setStrokeWidth(mScoreStrokeWidth);
        //标题
        mTextPaint.setTextSize(mTitleSize);
        mTextPaint.setColor(mTitleColor);
        mTextPaint.setStyle(Paint.Style.STROKE);
    }

    private void initBasicData() {
        String errmsg = "Inconsistent number of titles or data length";
        if (mTitles != null && mTitles.length > 0) {
            mCount = mTitles.length;
        }
        if (mDataList != null && mDataList.size() > 0) {
            int dataLength = mDataList.get(0).length();
            if (mCount > 0 && mCount != dataLength) {
                throw new RuntimeException(errmsg);
            }
            for (DataItem data : mDataList) {
                if (dataLength != data.length()) {
                    throw new RuntimeException(errmsg);
                }
            }
        }
        mAngle = 360f / mCount;
        mScoreUnitLength = mRadius / mScoreMax;
        mPath.reset();
        mPath.moveTo(mCenterX, mCenterY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;
        mCenterY = h / 2;
        mRadius = (int) (Math.min(w, h) / 2 * mCobwebScale);
        mSetpLegth = mRadius / mScoreSetp;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCobweb(canvas);
        drawText(canvas);
        if (mDataList != null && mDataList.size() > 0) {
            for (DataItem dataItem : mDataList) {
                drawScore(canvas, dataItem);
            }
        }
    }

    /**
     * 绘制网状图
     */
    private void drawCobweb(Canvas canvas) {
        initBasicData();
        for (int i = 1; i <= mScoreSetp; i++) {
            float curR = mSetpLegth * i;
            mPath.reset();
            for (int j = 0; j < mCount; j++) {
                float angle = (float) (Math.toRadians(mAngle) * j);
                if (j == 0) {
                    mPath.moveTo(mCenterX + curR, mCenterY);
                } else {
                    float x = (float) (mCenterX + curR * Math.cos(angle));
                    float y = (float) (mCenterY + curR * Math.sin(angle));
                    mPath.lineTo(x, y);
                }
            }
            mPath.close();
            canvas.drawPath(mPath, mCobwebPaint);
        }
        mPath.reset();
        for (int k = 0; k <= mCount; k++) {
            mPath.moveTo(mCenterX, mCenterY);
            float angle = (float) (Math.toRadians(mAngle) * k);
            float x =
                    (float) (mCenterX + mRadius * Math.cos(angle));
            float y =
                    (float) (mCenterY + mRadius * Math.sin(angle));
            mPath.lineTo(x, y);
            canvas.drawPath(mPath, mCobwebPaint);
        }
    }

    /**
     * 绘制分数
     */
    private void drawScore(Canvas canvas, DataItem dataItem) {
        if (dataItem == null || dataItem.mScores == null || dataItem.mScores.length != mCount) {
            return;
        }
        initBasicData();
        mScorePaint.setColor(dataItem.mColor);
        for (int index = 0; index < mCount; index++) {
            float curRadius = dataItem.mScores[index] * mScoreUnitLength;//该分数应绘制的长度
            float angle = (float) (Math.toRadians(mAngle) * index);
            Log.d("angleS=", angle + "");
            if (curRadius > mRadius) {
                curRadius = mRadius;
            }
            if (curRadius < 0) {
                curRadius = 0;
            }

            if (index == 0) {
                mPath.moveTo(mCenterX + curRadius, mCenterY);
                if (mScorePoint) {
                    canvas.drawPoint(mCenterX + curRadius, mCenterY, mScorePaint);
                }
            } else {
                float pX = (float) (mCenterX + curRadius * Math.cos(angle));
                float pY = (float) (mCenterY + curRadius * Math.sin(angle));
                mPath.lineTo(pX, pY);
                if (mScorePoint) {
                    canvas.drawPoint(pX, pY, mScorePaint);
                }
            }
        }
        mPath.close();
        canvas.drawPath(mPath, mScorePaint);
    }

    /**
     * 绘制文字
     */
    private void drawText(Canvas canvas) {
        initBasicData();
        if (mTitles == null || mTitles.length < 1) {
            return;
        }
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;//得到文字的高度
        for (int i = 0; i < mCount; i++) {
            float scoreAngle = (float) (Math.toRadians(mAngle) * i);
            float x = (float) (mCenterX + (mRadius + fontHeight / 2) * Math.cos(scoreAngle));
            float y = (float) (mCenterY + (mRadius + fontHeight / 2) * Math.sin(scoreAngle));
            if (scoreAngle >= 0 && scoreAngle <= Math.PI / 2) {
                //第四象限
                canvas.drawText(mTitles[i], x, y + fontHeight / 3, mTextPaint);
            } else if (scoreAngle > Math.PI / 2 && scoreAngle <= Math.PI) {
                //第三象限
                float drs = mTextPaint.measureText(mTitles[i]);
                canvas.drawText(mTitles[i], x - drs, y + fontHeight / 3, mTextPaint);
            } else if (scoreAngle > Math.PI && scoreAngle < 3 * Math.PI / 2) {
                //第二象限
                float drs = mTextPaint.measureText(mTitles[i]);
                canvas.drawText(mTitles[i], x - drs, y, mTextPaint);
            } else {
                //第一象限
                canvas.drawText(mTitles[i], x, y, mTextPaint);
            }
        }
    }

    public CobwebView setTitles(String[] titles) {
        mTitles = titles;
        postInvalidate();
        return this;
    }

    public CobwebView addDates(@ColorInt int color, float[] scores) {
        mDataList.add(new DataItem(color, scores));
        postInvalidate();
        return this;
    }

    public static class DataItem {
        @ColorInt
        private int mColor;
        private float[] mScores;

        public DataItem(@ColorInt int mColor, float[] mScores) {
            this.mColor = mColor;
            this.mScores = mScores;
        }

        private int length() {
            return mScores == null ? 0 : mScores.length;
        }
    }

    public void testCount(int count) {
        mCount = count;
        mDataList = new ArrayList<>();
        float[] scs1 = new float[count];
        float[] scs2 = new float[count];
        mTitles = new String[count];
        for (int i = 0; i < count; i++) {
            scs1[i] = i * 1.5f;
            scs2[i] = i * 1f;
            mTitles[i] = "标题-" + i;
        }
        mDataList.add(new DataItem(0xFF00FF00, scs1));
        mDataList.add(new DataItem(0xFF0000FF, scs2));
        postInvalidate();
    }


}
