package com.ble.layoutborder;

import com.ble.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by junweiliu on 16/6/3.
 */
public class BorderRelativeLayout extends RelativeLayout {
    /**
     * ����
     */
    private Paint mPaint;
    /**
     * �߿���ɫ
     */
    private int mPaintColor;
    /**
     * �߿��ϸ
     */
    private float mBorderStrokeWidth;
    /**
     * �ױ߱�����߶Ͽ�����
     */
    private int mBorderBottomLeftBreakSize;
    /**
     * �ױ߱����ұ߶Ͽ�����
     */
    private int mBorderBottomRightBreakSize;
    /**
     * �Ƿ���Ҫ�ϱ߿�
     */
    private boolean isNeedTopBorder;
    /**
     * �Ƿ���Ҫ���ұ߿�
     */
    private boolean isNeedLeftAndRightBorder;
    /**
     * �Ƿ���Ҫ�±߿�
     */
    private boolean isNeedBottomBorder;


    public BorderRelativeLayout(Context context) {
        this(context, null);
    }

    public BorderRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // ��ȡ�Զ�������
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BorderRelativeLayout);
        mPaintColor = ta.getColor(R.styleable.BorderRelativeLayout_borderColor, Color.BLACK);
        mBorderStrokeWidth = ta.getFloat(R.styleable.BorderRelativeLayout_borderStrokeWidth, 4.0f);
        mBorderBottomLeftBreakSize = ta.getDimensionPixelSize(R.styleable.BorderRelativeLayout_borderBottomLeftBreakSize, 0);
        mBorderBottomRightBreakSize = ta.getDimensionPixelSize(R.styleable.BorderRelativeLayout_borderBottomRightBreakSize, 0);
        isNeedTopBorder = ta.getBoolean(R.styleable.BorderRelativeLayout_needTopBorder, true);
        isNeedLeftAndRightBorder = ta.getBoolean(R.styleable.BorderRelativeLayout_needLeftAndRigtBorder, true);
        isNeedBottomBorder = ta.getBoolean(R.styleable.BorderRelativeLayout_needBottomBorder, true);
        ta.recycle();
        init();

    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mPaintColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mBorderStrokeWidth);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //  ��4����
        if (isNeedTopBorder) {
            canvas.drawLine(0, 0, this.getWidth(), 0, mPaint);
        }
        if (isNeedBottomBorder) {
            canvas.drawLine(mBorderBottomLeftBreakSize, this.getHeight(), this.getWidth() - mBorderBottomRightBreakSize, this.getHeight(), mPaint);
        }
        if (isNeedLeftAndRightBorder) {
            canvas.drawLine(0, 0, 0, this.getHeight(), mPaint);
            canvas.drawLine(this.getWidth(), 0, this.getWidth(), this.getHeight(), mPaint);
        }
    }

    /**
     * ���ñ߿���ɫ
     *
     * @param color
     */
    public void setBorderColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    /**
     * ���ñ߿���
     *
     * @param size
     */
    public void setBorderStrokeWidth(float size) {
        mPaint.setStrokeWidth(size);
        invalidate();
    }


    /**
     * �����Ƿ���Ҫ�����߿�
     *
     * @param needtopborder
     */
    public void setNeedTopBorder(boolean needtopborder) {
        isNeedTopBorder = needtopborder;
        invalidate();
    }

    /**
     * �����Ƿ���Ҫ�ײ��߿�
     *
     * @param needbottomborder
     */
    public void setNeedBottomBorder(boolean needbottomborder) {
        isNeedBottomBorder = needbottomborder;
        invalidate();
    }
}
