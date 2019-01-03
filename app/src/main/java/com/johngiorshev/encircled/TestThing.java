package com.johngiorshev.encircled;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TestThing extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        // full screen from the docs
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // create layout to stack views on the Z axis
        RelativeLayout relLayout = new RelativeLayout(this);
        relLayout.setBackgroundColor(Color.BLACK);

        View xmlv = getLayoutInflater().inflate(R.layout.activity_test_thing,
                null);
        xmlv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        // draw background content behind functional UI
        relLayout.addView(new BackgroundView(this));

        relLayout.addView(xmlv);

        setContentView(relLayout);
    }

    class BackgroundView extends View {
        static final int FPS = 60;
        private float incre;
        long lastUpdateTime = -1;
        long newUpdateTime = -1;
        Paint paint;

        public BackgroundView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.parseColor("#CD5C5C"));
        }

        @Override
        protected void onDraw(Canvas canvas) {

            newUpdateTime = System.currentTimeMillis();
            if (lastUpdateTime == -1) {
                lastUpdateTime = newUpdateTime;
            }

//            Log.i("DRAW EVENT", "onDraw!");
            super.onDraw(canvas);
            Paint p = new Paint();
            // start at 0,0 and go to 0,max to use a vertical
            // gradient the full height of the screen.
            p.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLACK, Color.GRAY, Shader.TileMode.MIRROR));
            canvas.drawPaint(p);

            float timeDelta = (newUpdateTime - lastUpdateTime) / (1000f / FPS);

            incre += 5 * timeDelta;

            canvas.drawCircle(80 + incre, 120, 50, paint);
            if (incre > getWidth()) {
                incre = 0;
            }

            postInvalidateDelayed( 1000 / FPS);

            lastUpdateTime = newUpdateTime;
        }

    }

}
