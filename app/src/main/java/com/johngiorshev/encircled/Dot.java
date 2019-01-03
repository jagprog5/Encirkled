package com.johngiorshev.encircled;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.Random;

public class Dot {
    public static final Random r = new Random();
    /*
     * pastX stores the previous x positions for trail drawing.
     * Increasing index means older positions.
     */
    private float pastX[];

    /*
     * pastY stores the previous y positions for trail drawing.
     * Increasing index means older positions.
     */
    private float pastY[];

    // size of drawn ellipses
    int size;

    // color properties of initial ellipse
    private int ai;
    private int ri;
    private int gi;
    private int bi;

    // color properties at end of tail
    // Generally alpha final value should be 0, for the trail to fade off.
    // This color will be approached, but not reached.
    private int af;
    private int rf;
    private int gf;
    private int bf;

    float targetX;
    float targetY;

    public Dot(int x, int y,
               int ai, int ri, int gi, int bi,
               int af, int rf, int gf, int bf,
               int tailSegments, int size) {
        this.ai = ai;
        this.ri = ri;
        this.gi = gi;
        this.bi = bi;
        this.af = af;
        this.rf = rf;
        this.gf = gf;
        this.bf = bf;
        this.size = size;
        pastX = new float[tailSegments];
        pastY = new float[tailSegments];
        setPosition(x, y);
    }

    /**
     * Current movement ai is to randomly move on screen.
     *
     * @param c The canvas that this object will draw itself on.
     * @param timeDelta The number of frames that have passed since the last update.
     */
    public void update(Canvas c, float timeDelta) {
        if (GameMath.distance(getX(), getY(), targetX, targetY) < 50) {
            targetX = r.nextInt(c.getWidth());
            targetY = r.nextInt(c.getHeight());
        }

        int jitter = 50;
        targetX += GameMath.nextFloat(-jitter, jitter);
        targetY += GameMath.nextFloat(-jitter, jitter);
        float[] diff = GameMath.projectileSpeed(getX(), getY(), targetX, targetY, 20);
        setPosition(getX() + diff[0] * timeDelta,
                getY() + diff[1] * timeDelta);
        draw(c);
    }

    public void draw(Canvas c) {
        // drawing is completed backwards, since the start of the trail needs to be on top

        int len = pastX.length;

        // End of trail. No curve.
        c.drawLine(pastX[len-2], pastY[len-2], pastX[len-1], pastY[len-1], getColorInTail(len - 1));
        // Iterate backwards, and ignore first segment (drawn in line above)
        float dampener = 5f; // Increase to make curve more rigid. Decrease to make more fluid
        float prevLeadControlX = (pastX[len-1] - pastX[len-2]) / dampener;
        float prevLeadControlY = (pastY[len-1] - pastY[len-2]) / dampener;
        for (int i = len - 2; i > 0; i--) {
            Path p = new Path();
            //[i] is the lagging point. [i-1] is the leading point
            p.moveTo(pastX[i], pastY[i]);
            float leadControlX = (pastX[i] - pastX[i-1]) / dampener;
            float leadControlY = (pastY[i] - pastY[i-1]) / dampener;
            p.cubicTo(pastX[i] - prevLeadControlX,
                    pastY[i] - prevLeadControlY,
                    pastX[i-1] + leadControlX,
                    pastY[i-1] + leadControlY,
                    pastX[i-1], pastY[i-1]);
            prevLeadControlX = leadControlX;
            prevLeadControlY = leadControlY;
            c.drawPath(p, getColorInTail(i - 1));
        }
    }

    public void setPosition(float x, float y) {
        shiftArray(pastX);
        shiftArray(pastY);
        pastX[0] = x;
        pastY[0] = y;
    }

    public float getX() {
        return pastX[0];
    }

    public float getY() {
        return pastY[0];
    }


    private Paint getColorInTail(int pastIndex) {
        Paint p = new Paint();
        p.setStrokeWidth(size);
        p.setStrokeCap(pastIndex == 0 ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);

        float prog = (float)pastIndex / pastX.length;
        p.setARGB((int)(ai + (af - ai) * prog),
                (int)(ri + (rf - ri) * prog),
                (int)(gi + (gf - gi) * prog),
                (int)(bi + (bf - bi) * prog));
        return p;
    }

    // Shifts all indexes by +1. Discards the end of the array, and frees [0] for a new value.
    private static void shiftArray(float[] arr) {
        System.arraycopy(arr, 0, arr, 1, arr.length-1);
    }
}
