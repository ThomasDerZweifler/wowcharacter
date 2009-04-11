package de.stm.android.wowcharacter.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Fortschrittsbalken mit Text
 * 
 * @author
 * 
 */
public class CustomProgressBar extends ProgressBar {
	public final static int HORIZONTAL = 0;
	public final static int VERTICAL = 0;

	private Paint paint;
	private String processingText = "";
	private int textColor = 0xFFFFFFFF;
	private int orientation = HORIZONTAL;
	private int position = 0;
	private int min = 0;
	private int max = 100;
	private int xText;
	private int yText;

	/**
	 * 
	 * @param context
	 */
	public CustomProgressBar(Context context) {
		super(context);
		init();
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public CustomProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		paint = new Paint();
		paint.setTextSize(12);
		paint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.setMeasuredDimension(MeasureWidth(widthMeasureSpec),
				MeasureHeight(heightMeasureSpec));

	}

	/**
	 * 
	 * @param heightMeasureSpec
	 * @return
	 */
	private int MeasureHeight(int heightMeasureSpec) {
		int result = 0;
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		if (this.orientation == HORIZONTAL) {
			int heightText = (int) (paint.descent() - paint.ascent()) + 5;
			result = Math.min(specSize, heightText);
			yText = (int) ((result - paint.ascent()) / 2);
		} else {
			yText = 0;
			result = Math.min(specSize, max - min);
		}

		return result;
	}

	/**
	 * 
	 * @param widthMeasureSpec
	 * @return
	 */
	private int MeasureWidth(int widthMeasureSpec) {
		int result = 0;
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		int specMode = MeasureSpec.getMode(widthMeasureSpec);

		if (this.orientation == HORIZONTAL) {
			if (specMode == MeasureSpec.EXACTLY) {
				result = specSize;
			} else {
				int widthText = (int) paint.measureText(processingText) + 20;
				result = Math.min(widthText, specSize);
			}
			xText = (int) ((result - paint.measureText(processingText)) / 2);
		} else {
			xText = 0;
			result = Math.min(specSize, 20);
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (processingText != null && processingText.length() > 0) {
			if (this.orientation == HORIZONTAL) {
				paint.setColor(this.textColor);
				paint.setAntiAlias(true);
				canvas.drawText(processingText, xText, yText, paint);
			}
		}
	}

	public String getProcessingText() {
		return processingText;
	}

	public void setProcessingText(String processingText) {
		this.processingText = processingText;
		invalidate();
	}

	public final int getTextColor() {
		return textColor;
	}

	public final void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public final int getPosition() {
		return position;
	}

	public final void setPosition(int position) {
		this.position = position;
		invalidate();
	}

	public final int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public final int getMax() {
		return max;
	}

	public final void setMax(int max) {
		super.setMax(max);
		this.max = max;
	}
}
