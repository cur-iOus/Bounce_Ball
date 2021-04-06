package com.curious.threading;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    GameView gameView;
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;
    Sensor accelerometerSensor;
    private static float gX, gY;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //gameView = new GameView(this);
        initializeSensors();
        setContentView(R.layout.activity_game);
        gameView = findViewById(R.id.gameViewID);

        initializeButtons();
    }

    private void initializeButtons() {
        Button moveLeftButton = findViewById(R.id.leftButton);
        Button moveRightButton = findViewById(R.id.rightButton);

        moveLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        gameView.drawingThread.dock.startMovingLeft();
                        moveLeftButton.getBackground().setAlpha(100);
                        break;
                    case MotionEvent.ACTION_UP:
                        gameView.drawingThread.dock.stopMovingLeft();
                        moveLeftButton.getBackground().setAlpha(255);
                        break;
                    default:

                        break;
                }
                return true;
            }
        });

        moveRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        gameView.drawingThread.dock.startMovingRight();
                        moveRightButton.getBackground().setAlpha(100);
                        break;
                    case MotionEvent.ACTION_UP:
                        gameView.drawingThread.dock.stopMovingRight();
                        moveRightButton.getBackground().setAlpha(255);
                        break;
                    default:

                        break;
                }
                return true;
            }
        });
    }

    public static float getgX() {
        return gX;
    }

    public static float getgY() {
        return gY;
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    gX = -event.values[0];
                    gY = event.values[1];
                    if (gY < -2){
                        stopUsingSensors();
                        gameView.drawingThread.animationThread.stopThread();
                        gameView.drawingThread.scoreCounterThread.stopThread();

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GameActivity.this);
                        alertBuilder.setTitle("No Cheating!");
                        alertBuilder.setIcon(R.drawable.ic_warning);
                        alertBuilder.setMessage("You are shaking or holding your phone upside down!");
                        alertBuilder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restartGame(null);
                            }
                        });
                        alertBuilder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopGame(null);
                            }
                        });
                        alertBuilder.show();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        startUsingSensors();
    }

    private void startUsingSensors() {
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void stopUsingSensors(){
        sensorManager.unregisterListener(sensorEventListener);
    }

   /* @Override
    protected void onStart() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
        super.onStart();
    } */

    @Override
    protected void onPause() {
        stopUsingSensors();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startUsingSensors();
        super.onResume();
    }

    @Override
    protected void onStop() {
        stopUsingSensors();
        super.onStop();
    }

    public void pauseGame(View view){
        if (gameView.drawingThread.pauseFlag == false){
            stopUsingSensors();
            gameView.drawingThread.animationThread.stopThread();
            gameView.drawingThread.pauseFlag = true;
            view.setBackgroundResource(R.drawable.ic_play);
        }else {
            startUsingSensors();
            gameView.drawingThread.animationThread = new AnimationThread(gameView.drawingThread);
            gameView.drawingThread.animationThread.start();
            gameView.drawingThread.pauseFlag = false;
            view.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    public void restartGame(View view){
        stopUsingSensors();
        startUsingSensors();

        gameView.drawingThread.stopThread();
        gameView.drawingThread = new DrawingThread(gameView, this);
        gameView.drawingThread.start();

        Toast.makeText(this, "Game Restared", Toast.LENGTH_SHORT).show();
    }

    public void stopGame(View view){
        this.finish();
    }
}
















