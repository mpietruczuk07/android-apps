package pl.edu.pb.sensorappp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {
    static final String EXTRA_SENSOR_TYPE_PARAMETER = "EXTRA_SENSOR_TYPE";
    private SensorManager sensorManager;
    private Sensor sensorPressure;
    private Sensor sensorAccelerometer;
    private Sensor sensor;
    private TextView sensorNameTextView;
    private TextView sensorValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);
        Log.d("SENSOR_APP_TAG", "onCreate");

        sensorNameTextView = findViewById(R.id.sensor_name);
        sensorValueTextView = findViewById(R.id.sensor_value);

        int type = getIntent().getIntExtra(EXTRA_SENSOR_TYPE_PARAMETER, Sensor.TYPE_ACCELEROMETER);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(type);

        if(sensor == null){
            sensorNameTextView.setText(R.string.missing_sensor);
        }
        else{
            sensorNameTextView.setText(sensor.getName());
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("SENSOR_APP_TAG", "onStart");

        if(sensor != null){
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("SENSOR_APP_TAG", "onStop");

        sensorManager.unregisterListener(this);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("SENSOR_APP_TAG", "onSensorChanged");

        int sensorType = sensorEvent.sensor.getType();
        //float currentValue = sensorEvent.values[0];


        switch(sensorType){
            case Sensor.TYPE_PRESSURE:
                sensorValueTextView.setText(getResources().getString(R.string.sensor_label_value2, sensorEvent.values[0]));
                break;
            case Sensor.TYPE_LIGHT:
                sensorValueTextView.setText(getResources().getString(R.string.sensor_label_value2, sensorEvent.values[0]));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorValueTextView.setText(getResources().getString(R.string.sensor_label_value, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d("SENSOR_APP_TAG", "onAccuracyChanged");
    }
}
