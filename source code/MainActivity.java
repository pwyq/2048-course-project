package lab4_204_43.uwaterloo.ca.lab4_204_43;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    RelativeLayout rlayout;

    TextView tvAccelerometer;
    TextView tv1;
    AccelerometerEventListener accListener;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB) // Give 11 api access to this section
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlayout = (RelativeLayout)findViewById(R.id.activity_main);
        //Note: Only LinearLayout have setOrientation method

        tv1 = (TextView)findViewById(R.id.label1);
        tv1.setVisibility(View.GONE);

        //set up a square-shape layout
        rlayout.getLayoutParams().width = 1080;
        rlayout.getLayoutParams().height = 1080;
        //Need to check if the gameboard is top-left justified
        rlayout.setBackgroundResource(R.drawable.gameboard);

        tvAccelerometer = new TextView(getApplicationContext());
        tvAccelerometer.setText("State Readings");
        tvAccelerometer.setTextColor(Color.DKGRAY);
        tvAccelerometer.setTextSize(20.0f);
        rlayout.addView(tvAccelerometer);

        Timer myGameLoop = new Timer();
        GameLoopTask myGameLoopTask = new GameLoopTask(MainActivity.this, rlayout, getApplicationContext());
        myGameLoopTask.run();
        myGameLoop.schedule(myGameLoopTask, 50, 50);

        SensorManager sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accListener = new AccelerometerEventListener(tvAccelerometer, myGameLoopTask);
        sensorManager.registerListener(accListener, Accelerometer, SensorManager.SENSOR_DELAY_GAME);

    }
}
