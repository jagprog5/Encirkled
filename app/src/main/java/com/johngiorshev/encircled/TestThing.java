package com.johngiorshev.encircled;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class TestThing extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // full-screen based on https://stackoverflow.com/a/22839594

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // full screen from the docs
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(flags);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // create layout to stack views on the Z axis
        RelativeLayout relLayout = new RelativeLayout(this);
        relLayout.setBackgroundColor(Color.BLACK);

        // null is fine. Layout params set manually
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
        static final int FPS = 5;//60;
        long lastUpdateTime = -1;
        long newUpdateTime = -1;
        Dot d;
        boolean paintSet = false;
        Paint p = new Paint();

        public BackgroundView(Context context) {
            super(context);
            d = new Dot(100, 100,
                    255, 255, 0, 0,
                    0, 0, 255, 255,
                    30, 40);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            newUpdateTime = System.currentTimeMillis();
            if (lastUpdateTime == -1) {
                lastUpdateTime = newUpdateTime;
            }

            super.onDraw(canvas);
            if (!paintSet) {
                p.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLACK,
                        Color.GRAY, Shader.TileMode.MIRROR));
                paintSet = true;
            }
            canvas.drawPaint(p);

            float timeDelta = (newUpdateTime - lastUpdateTime) / (1000f / FPS);

            d.update(canvas, timeDelta);

            postInvalidateDelayed(1000 / FPS);

            lastUpdateTime = newUpdateTime;
        }

    }

}
