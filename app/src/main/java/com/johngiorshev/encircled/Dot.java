package com.johngiorshev.encircled;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        targetX += GameMath.nextFloat(-50, 50);
        targetY += GameMath.nextFloat(-50, 50);
        float[] diff = GameMath.projectileSpeed(getX(), getY(), targetX, targetY, 20);
        setPosition(getX() + diff[0] * timeDelta,
                getY() + diff[1] * timeDelta);
        draw(c);
    }

    public void draw(Canvas c) {
        //draw lines in reverse order, so newest is on top
        for (int i = pastX.length - 1; i > 0; i--) {
            c.drawLine(pastX[i], pastY[i], pastX[i-1], pastY[i-1], getColorInTail(i - 1));
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
