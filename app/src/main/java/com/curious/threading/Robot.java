package com.curious.threading;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.VelocityTracker;

public class Robot {
    float centerX, centerY;
    int height, width;
    float velocityX, velocityY;

    Bitmap robotBitmap;
    Paint robotPaint;

    boolean robotFellDown = false;

    public Robot(Bitmap robotBitmap) {
        this.robotBitmap = robotBitmap;
        centerX=centerY=0;
        height=robotBitmap.getHeight();
        width=robotBitmap.getWidth();
        robotPaint = new Paint();
        velocityX=velocityY=0;
    }

    public Robot(Bitmap robotBitmap, int cX, int cY) {
        this(robotBitmap);
        centerX=cX;
        centerY=cY;
    }

    public Robot(Bitmap robotBitmap, Point center) {
        this(robotBitmap, center.x, center.y);
    }

    public void setCenter(Point centerPoint){
        centerX = centerPoint.x;
        centerY = centerPoint.y;
    }

    public void setVelocity(VelocityTracker velocityTracker){
        velocityX = velocityTracker.getXVelocity();
        velocityY = velocityTracker.getYVelocity();
    }
}
















