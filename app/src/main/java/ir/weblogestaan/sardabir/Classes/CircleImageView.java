package ir.weblogestaan.sardabir.Classes;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

	private Paint paint = null;
	private int backgroundColor = 0x00000000;
	private float centerX;
	private float centerY;
	private float radius;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private BitmapShader shader;
	private boolean redraw = true;
	
	private void prepare(AttributeSet attrs) {
		if (paint==null) {
			paint = new Paint();
			paint.setAntiAlias(true);
			if (attrs!=null) {
				int background = attrs.getAttributeIntValue(
						"http://schemas.android.com/apk/res/android",
						"background",
						0x00000000);
				setBackgroundColor(background);
			}
		}
	}
	
	public CircleImageView(Context context) {
		super(context);
		prepare(null);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		prepare(attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		prepare(attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (redraw) {
			mBitmap.eraseColor(backgroundColor);
			super.onDraw(mCanvas);
			shader = new BitmapShader (mBitmap,  TileMode.CLAMP, TileMode.CLAMP);
	        paint.setShader(shader);
			redraw = false;
		}
        canvas.drawCircle(centerX, centerY, radius, paint);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void setImageAlpha(int alpha) {
		super.setImageAlpha(alpha);
		redraw = true;
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		redraw = true;
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		redraw = true;
	}

	@Override
	public void setImageLevel(int level) {
		super.setImageLevel(level);
		redraw = true;
	}

	@Override
	public void setImageMatrix(Matrix matrix) {
		super.setImageMatrix(matrix);
		redraw = true;
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		redraw = true;
	}

	@Override
	public void setImageState(int[] state, boolean merge) {
		super.setImageState(state, merge);
		redraw = true;
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		redraw = true;
	}

	@Override
	public void setBackgroundColor(int color) {
		if (color!=0x00000000) super.setBackgroundColor(0x00000000);
		backgroundColor = color;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//rect = new Rect(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
		centerX = getPaddingLeft() + ((float)(w - getPaddingLeft() - getPaddingRight()))/2f;
		centerY = getPaddingTop() + ((float)(h - getPaddingTop() - getPaddingBottom()))/2f;
		if (w<=h) {
			int max = getPaddingLeft()>=getPaddingRight()?getPaddingLeft():getPaddingRight();
			radius = ((float)(w - 2*max))/2f;
		}
		else {
			int max = getPaddingTop()>=getPaddingBottom()?getPaddingTop():getPaddingBottom();
			radius = ((float)(h - 2*max))/2f;
		}
		mBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		redraw = true;
	}

}

