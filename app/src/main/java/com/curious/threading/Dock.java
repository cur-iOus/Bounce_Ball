package com.curious.threading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class Dock {

    Bitmap dockBitmap;
    int dockWidth, dockHeight;
    int leftMostPoint, rightMostPoint;

    Point topLeftPoint = new Point(0, 0), bottomCneterPoint;
    DrawingThread drawingThread;

    boolean movingLeftFlag = false;
    boolean movingRightFlag = false;

    public Dock(DrawingThread drawingThread, int bitmapID) {
        this.drawingThread = drawingThread;
        Bitmap tempBitmap = BitmapFactory.decodeResource(drawingThread.context.getResources(), bitmapID);
        tempBitmap = Bitmap.createScaledBitmap(tempBitmap, drawingThread.displayX, drawingThread.displayX*tempBitmap.getHeight() / tempBitmap.getWidth(), true);
        dockBitmap = tempBitmap;
        dockWidth = dockBitmap.getWidth();
        dockHeight = dockBitmap.getHeight();

        bottomCneterPoint = new Point((int) drawingThread.displayX / 2, (int) drawingThread.displayY);
        topLeftPoint.y = bottomCneterPoint.y - dockHeight;

        updateInfo();
    }

    private void updateInfo() {
        leftMostPoint = bottomCneterPoint.x - dockWidth / 2;
        rightMostPoint = bottomCneterPoint.x + dockWidth / 2;
        topLeftPoint.x = leftMostPoint;
    }

    public void moveDockToLeft() {
        bottomCneterPoint.x -= 4;
        updateInfo();
    }

    public void moveDockToRight() {
        bottomCneterPoint.x += 4;
        updateInfo();
    }

    public void startMovingLeft() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                movingLeftFlag = true;
                while (movingLeftFlag) {
                    moveDockToLeft();
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }
    public void stopMovingLeft(){
        movingLeftFlag = false;
    }

    public void startMovingRight() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                movingRightFlag = true;
                while (movingRightFlag) {
                    moveDockToRight();
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }
    public void stopMovingRight(){
        movingRightFlag = false;
    }
}


















