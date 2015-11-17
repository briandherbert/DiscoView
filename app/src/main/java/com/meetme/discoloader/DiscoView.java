package com.meetme.discoloader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class DiscoView extends View {
    public static final String TAG = DiscoView.class.getSimpleName();

    private int mExampleColor = Color.TRANSPARENT;
    private Drawable mExampleDrawable;

    float[] hsl = new float[3];
    static final int H_VARIANCE = 20;
    static final int S_VARIANCE = 4;
    static final int L_VARIANCE = 3;

    static final int START_BRIGHTNESS = 0;

    static float minSquaresPerSide = 30;
    static int changeOddsPct = 1;
    static int updateFreqMs = 100;

    double squareSize = 0;
    double squaresHigh;
    double squaresWide;

    int width = 0;
    int height = 0;

    Random rand = new Random();
    Paint paint = new Paint();

    static Paint iconPaint = new Paint();

    static {
        iconPaint.setColor(Color.RED);
    }

    int[][] colors;

    public DiscoView(Context context) {
        super(context);
        init(null, 0);
    }

    public DiscoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DiscoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DiscoView, defStyle, 0);

        mExampleColor = a.getColor(
                R.styleable.DiscoView_discoBaseColor,
                mExampleColor);

        if (a.hasValue(R.styleable.DiscoView_discoIcon)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.DiscoView_discoIcon);
            mExampleDrawable.setCallback(this);
        }

        ColorUtils.colorToHSL(mExampleColor, hsl);
        Log.v(TAG, "orig h: " + hsl[0] + " s: " + hsl[1] + " l: " + hsl[2]);

        a.recycle();
    }

    float randomHSL[] = new float[3];

    int getRandomColor() {
        if (mExampleColor == Color.TRANSPARENT)
            return Color.rgb(randColorInt(), randColorInt(), randColorInt());

        randomHSL[0] = Math.min(360, Math.max(0, hsl[0] + (rand.nextBoolean() ? -1 : 1 * rand.nextInt(H_VARIANCE))));

        randomHSL[1] = hsl[1] + (rand.nextBoolean() ? -rand.nextInt(S_VARIANCE) / 10f : rand.nextFloat() / 10f);
        if (randomHSL[1] < 0.0 || randomHSL[1] > 1) randomHSL[1] = hsl[1];

        randomHSL[2] = hsl[2] + (rand.nextBoolean() ? -rand.nextInt(L_VARIANCE) / 10f : rand.nextFloat() / 10f);
        if (randomHSL[2] < 0.0 || randomHSL[1] > 1) randomHSL[2] = hsl[2];

        return ColorUtils.HSLToColor(randomHSL);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        width = xNew;
        height = yNew;

        Log.v(TAG, "Size changed " + width + ", " + height);
        calcSquares(xNew != xOld || yNew != yOld);
    }

    Bitmap iconBmp;
    int leftMargin;
    int topMargin;

    public void calcSquares(boolean updateSize) {
        if (width <= 0 || height <= 0) return;

        if (updateSize) {
            Log.v(TAG, "width " + width + " height " + height);
            squareSize = width < height ? width / minSquaresPerSide : height / minSquaresPerSide;

            if (width < height) {
                squaresWide = minSquaresPerSide;
                squaresHigh = height / squareSize;
            } else {
                squaresHigh = minSquaresPerSide;
                squaresWide = width / squareSize;
            }

            colors = new int[(int) squaresWide][(int) squaresHigh];
        }

        if (mExampleDrawable != null) {
            Bitmap bmp = ((BitmapDrawable) mExampleDrawable).getBitmap();

            float w = bmp.getWidth();
            float h = bmp.getHeight();

            Log.v(TAG, "bitmap orig size " + w + ", " + h);

            double smallestRatio = Math.min(squaresWide / w, squaresHigh / h);
            iconBmp = Bitmap.createScaledBitmap(bmp, (int) (w * smallestRatio), (int) (h * smallestRatio), false);

            Log.v(TAG, "bitmap scaled size " + iconBmp.getWidth() + ", " + iconBmp.getHeight() + " square size " + squareSize);

            leftMargin = (int) ((width - iconBmp.getWidth() * squareSize) / 2);
            topMargin = (int) ((height - iconBmp.getHeight() * squareSize) / 2);
            Log.v(TAG, "margin left " + leftMargin + " top " + topMargin);
        } else {
            iconBmp = null;
            leftMargin = topMargin = 0;
        }

        generateColors(100);
    }

    public void generateColors(int oddsChange) {
        for (int i = 0; i < (int) squaresWide; i++) {
            for (int j = 0; j < (int) squaresHigh; j++) {
                if (rand.nextInt(100) >= oddsChange) continue;

                if (iconBmp == null) {
                    colors[i][j] = getRandomColor();
                } else {
                    colors[i][j] = (i < iconBmp.getWidth() && j < iconBmp.getHeight() && iconBmp.getPixel(i, j) != Color.TRANSPARENT) ?
                            getRandomColor() : Color.TRANSPARENT;
                }
            }
        }
    }

    public int randColorInt() {
        return START_BRIGHTNESS + rand.nextInt(256 - START_BRIGHTNESS);
    }

    public void setMinSquares(int min) {
        minSquaresPerSide = min;
        calcSquares(true);
    }

    public void setOdds(int odds) {
        changeOddsPct = odds;
    }

    public void setFreq(int freq) {
        updateFreqMs = freq * 100;
    }

    public int getMinSquares() {
        return (int) minSquaresPerSide;
    }

    public int getOdds() {
        return changeOddsPct;
    }

    public int getFreq() {
        return updateFreqMs / 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        generateColors(changeOddsPct);

        for (int i = 0; i < (int) squaresWide; i++) {
            for (int j = 0; j < (int) squaresHigh; j++) {
                paint.setColor(colors[i][j]);
                canvas.drawRect((int) (leftMargin + i * squareSize),
                        (int) (topMargin + j * squareSize),
                        (int) (leftMargin + i * squareSize + squareSize),
                        (int) (topMargin + j * squareSize + squareSize),
                        paint);
            }
        }

        if (!isInEditMode()) {
            removeCallbacks(invalidateRunnable);
            postDelayed(invalidateRunnable, updateFreqMs);
        }
    }

    Runnable invalidateRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;

        ColorUtils.colorToHSL(mExampleColor, hsl);
        Log.v(TAG, "orig h: " + hsl[0] + " s: " + hsl[1] + " l: " + hsl[2]);

        generateColors(100);

        invalidate();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
        mExampleColor = Color.TRANSPARENT;
        calcSquares(false);
    }
}
