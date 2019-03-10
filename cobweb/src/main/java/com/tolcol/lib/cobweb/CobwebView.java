package com.tolcol.lib.cobweb;

import android.annotation.TargetApi;
import android.content.Context;
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
public class CobwebView extends View {

    private int mCount = 5;//顶点数
    private float mAngle;//顶点角度
    private int mCenterX, mCenterY;//中心点
    private int mRadius;//半径

    //背景线条
    private Paint mCobwebPaint;
    private int mCobwebColor = 0xFF404040;//背景线条颜色
    private float mBackLinePer = 0.7f;//半径系数,给文字留出空间
    private int mCobwebStrokeWidth = 2;//线条宽度

    //分数
    private Paint mScorePaint;
    private Path mPath;//线条Path
    private float mScoreMax = 10;//最大分数
    private boolean mScorePoint = true;//是否要绘制点
    private boolean mScoreFill = false;//是否要填充
    private float mSetp = 1;//分数间隔
    private float mSetpLegth;//间隔的长度
    private int mScoreStrokeWidth = 6;//分数线条宽度
    private float mScoreUnitLength;//每一分的长度

    //文字
    private int mTextSize = 20;//大小
    private int mTextColor;//颜色
    private Paint mTextPaint;//画笔
    private List<DataItem> mDatas;
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
        mPath = new Path();
        mCobwebPaint = new Paint();
        mCobwebPaint.setColor(mCobwebColor);
        mCobwebPaint.setAntiAlias(true);
        mCobwebPaint.setStyle(Paint.Style.STROKE);
        mCobwebPaint.setStrokeWidth(mCobwebStrokeWidth);
        mScorePaint = new Paint();
        mScorePaint.setAntiAlias(true);
        mScorePaint.setStyle(Paint.Style.STROKE);
        mScorePaint.setStrokeWidth(mScoreStrokeWidth);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStyle(Paint.Style.STROKE);
    }

    private void initBasicData() {
        if (mTitles != null && mTitles.length > 0) {
            mCount = mTitles.length;
        }
        if (mCount > 0) {
            mAngle = 360f / mCount;
        }
        mScoreUnitLength = mRadius / mScoreMax;
        mPath.reset();
        mPath.moveTo(mCenterX, mCenterY);
    }

    public void setCount(int count) {
        mCount = count;
        mDatas = new ArrayList<>();
        float[] scs1 = new float[count];
        float[] scs2 = new float[count];
        mTitles = new String[count];
        for (int i = 0; i < count; i++) {
            scs1[i] = i * 1.5f;
            scs2[i] = i * 1f;
            mTitles[i] = "标题-" + i;
        }
        mDatas.add(new DataItem(0xFF00FF00, scs1));
        mDatas.add(new DataItem(0xFF0000FF, scs2));
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = w / 2;
        mCenterY = h / 2;
        mRadius = (int) (Math.min(w, h) / 2 * mBackLinePer);
        mSetpLegth = mRadius / mSetp;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCobweb(canvas);
        drawText(canvas);
        if (mDatas != null && mDatas.size() > 0) {
            for (DataItem dataItem : mDatas) {
                drawScore(canvas, dataItem);
            }
        }
    }

    /**
     * 绘制网状图
     */
    private void drawCobweb(Canvas canvas) {
        initBasicData();
        for (int i = 1; i <= mSetp; i++) {
            float curR = mSetpLegth * i;
            mPath.reset();
            for (int j = 0; j < mCount; j++) {
                float angle = (float) (Math.toRadians(mAngle) * j);
                Log.d("angleC=", angle + "");
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


    public static class DataItem {
        @ColorInt
        private int mColor;
        private float[] mScores;

        public DataItem(@ColorInt int mColor, float[] mScores) {
            this.mColor = mColor;
            this.mScores = mScores;
        }
    }

}
