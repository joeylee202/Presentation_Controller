package joeylee.com.presentationcontroller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    Sensor mProximity;
    Sensor mAccelerometer;
    SensorManager mSensorManager;
//    TextView acceleeration
    TextView response;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;

    //SocketService
    SocketService ss;
    boolean mBound = false;

    Calendar c1r = Calendar.getInstance();
    Calendar c1l = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //Add proximity sensor
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //Add accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //initialize all the components
        editTextAddress = (EditText) findViewById(R.id.ip_address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonClear = (Button) findViewById(R.id.clearButton);



        buttonConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent socketIntent = new Intent(MainActivity.this, SocketService.class);
                socketIntent.putExtra("socketAddr", editTextAddress.getText().toString());
                socketIntent.putExtra("socketPort", Integer.parseInt(editTextPort.getText().toString()));
                MainActivity.this.startService(socketIntent);
                MainActivity.this.bindService(socketIntent, mConnection, Context.BIND_AUTO_CREATE);

             if (mBound) {
                    Log.d("DEBUG", "Getting IP from service: " + ss.ip_address);
                    Log.d("DEBUG", Integer.toString(ss.ip_port));
                    ss.direction = 11;
             }


            }
        });

        buttonClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mBound){
                    Log.d("DEBUG", "Getting IP from service: ");
                }
            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
            ss = binder.getService();
            mBound = true;
            Log.d("DEBUG", "service connection connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("DEBUG", "service connection disconnected");
            ss = null;
            mBound = false;
        }
    };


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            if(event.values[0] == 0){
                Log.d("DEBUG", "Near sensor");
                if(mBound){
                    ss.direction = 1;
                }
            }else{
               // Log.d("DEBUG", "Far Sensor");
            }
        }

        if(event.values[0] < -8.00){
            Calendar c2r = Calendar.getInstance();
            int secs1 = c1r.get(Calendar.SECOND);
            int secs2 = c2r.get(Calendar.SECOND);
            if(Math.abs(secs2 - secs1) > 2){
                Log.d("DEBUG", "Right");
                if(mBound){
                    ss.direction = 8;
                }
                c1r = c2r;
            }
        }

        if(event.values[0] > 8.00){
            Calendar c2l = Calendar.getInstance();
            int secs1 = c1l.get(Calendar.SECOND);
            int secs2 = c2l.get(Calendar.SECOND);
            if(Math.abs(secs2 - secs1) > 2){
                Log.d("DEBUG", "Left");
                if(mBound){
                    ss.direction = -8;
                }
                c1l = c2l;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
