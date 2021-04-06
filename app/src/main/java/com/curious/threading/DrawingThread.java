package com.curious.threading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;

public class DrawingThread extends Thread {

    private Canvas canvas;
    GameView gameView;
    Context context;

    boolean threadFlag = false;
    boolean touchedFlag = false;
    boolean pauseFlag = false;

    Bitmap backgroundBitmap;
    int displayX, displayY;

    int maxScore = 0;

    ArrayList<Robot> allRobots;
    ArrayList<Bitmap> allPossibleRobots;

    AnimationThread animationThread;
    ScoreCounterThread scoreCounterThread;

    Paint scorePaint;

    Dock dock;

    public DrawingThread( GameView gameView, Context context) {
        this.gameView = gameView;
        this.context = context;

        initializeAll();
    }

    private void initializeAll(){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display difaultDisplay = windowManager.getDefaultDisplay();
        Point displayDimantion = new Point();
        difaultDisplay.getSize(displayDimantion);
        displayX = displayDimantion.x;
        displayY = displayDimantion.y;
        displayY=displayDimantion.y+(displayY/19);

        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, displayX, displayY, true);

        initializeAllPossibleRobots();
        scoreCounterThread = new ScoreCounterThread(this);
        dock = new Dock(this, R.drawable.dock);
        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextAlign(Paint.Align.CENTER);
        scorePaint.setAlpha(170);
        scorePaint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));
        scorePaint.setTextSize(displayX/10);
    }

    private void initializeAllPossibleRobots() {
        allRobots = new ArrayList<>();
        allPossibleRobots = new ArrayList<>();

        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.baseball));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.football));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.basketball));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.coloredball));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.claroball));
        //add more in drawable.........

    }

    private Bitmap giveResizedRobotBitmap(int resourseID) {
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), resourseID);
        tempBitmap = Bitmap.createScaledBitmap(tempBitmap, displayX/5,
                (tempBitmap.getHeight()/tempBitmap.getWidth())*(displayX/5), true);
        return tempBitmap;
    }
    @Override
    public void run() {
        threadFlag = true;
        animationThread = new AnimationThread(this);
        animationThread.start();
        scoreCounterThread.start();

        while (threadFlag){
            canvas = gameView.surfaceHolder.lockCanvas();
            try {
                synchronized (gameView.surfaceHolder){
                    updateDisplay();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (canvas!=null){
                    gameView.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        animationThread.stopThread();
        scoreCounterThread.stopThread();
    }

    public void stopThread(){
        threadFlag=false;
    }

    public void updateDisplay(){
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        drawDock();
        for (int i=0; i<allRobots.size(); i++){
            Robot tempRobot = allRobots.get(i);
            canvas.drawBitmap(tempRobot.robotBitmap, tempRobot.centerX-(tempRobot.width/2), tempRobot.centerY-(tempRobot.height/2), tempRobot.robotPaint);
        }
        //drawSensorData();
        if (pauseFlag){
            pauseStateDraw();
        }

        drawScore();
    }

    private void pauseStateDraw() {
        Paint paint = new Paint();
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        paint.setAlpha(200);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawARGB(170, 0,0,0);
        canvas.drawText("PAUSED", displayX/2, displayY/2, paint);
    }

    private void drawSensorData(){
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(displayX/10);

        canvas.drawText("X: "+GameActivity.getgX(), 0, displayY/3, paint);
        canvas.drawText("Y: "+GameActivity.getgY(), 0, displayY/3+(displayX/5), paint);
    }

    private void drawDock(){
        canvas.drawBitmap(dock.dockBitmap, dock.topLeftPoint.x, dock.topLeftPoint.y, null);
    }

    private void drawScore(){
        if (maxScore>10000){
            scorePaint.setColor(Color.CYAN);
            scorePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }
        if (maxScore>25000){
            scorePaint.setColor(Color.YELLOW);
        }
        if (maxScore>50000){
            scorePaint.setColor(Color.GREEN);
        }
        if (maxScore>100000){
            scorePaint.setColor(Color.RED);
        }
        canvas.drawText(""+maxScore, displayX/2, displayY/7, scorePaint);
    }
}























