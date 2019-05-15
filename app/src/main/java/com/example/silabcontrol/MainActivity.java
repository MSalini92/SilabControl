package com.example.silabcontrol;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    Button buttonThrottle;
    Button buttonBrake;

    double inputGas = 0;
    double inputBrake = 0;
    double steering = 0;

    ImageView pointerSpeed;
    ImageView pointerRevs;

    Socket s;
    DataInputStream dis;
    InputStream is;
    DataOutputStream dos;
    OutputStream os;

    String server = "192.168.2.105";
    int port = 25143;




    private SensorManager sensorManager;
    private Sensor rotationSensor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


        pointerSpeed = (ImageView) findViewById(R.id.imageView2);
        pointerRevs = (ImageView) findViewById(R.id.imageView3);
        buttonThrottle = (Button) findViewById(R.id.button2);
        buttonBrake = (Button) findViewById(R.id.button);



        RotateAnimation rotate = new RotateAnimation(0, -45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(100);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        pointerSpeed.startAnimation(rotate);


        RotateAnimation rotate2 = new RotateAnimation(0, -45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate2.setDuration(100);
        rotate2.setInterpolator(new LinearInterpolator());
        rotate2.setFillAfter(true);
        pointerRevs.startAnimation(rotate);


        buttonThrottle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    inputGas = 0.7;
                    v.setPressed(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    inputGas = 0.0;
                    v.setPressed(false);
                }
                return true;
            }
        });



        buttonBrake.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    inputBrake = 2.0;
                    v.setPressed(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    inputBrake = 0.0;
                    v.setPressed(false);
                }
                return true;
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);




    }



    public void settings(View view){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.settings_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText ipEdittext = (EditText) dialogView.findViewById(R.id.ip);
        final EditText portEdittext = (EditText) dialogView.findViewById(R.id.port);

        dialogBuilder.setTitle("TCP/IP Konfiguration");

        dialogBuilder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                server = ipEdittext.getText().toString();
                port = Integer.parseInt(portEdittext.getText().toString());

                Client client = new Client();
                client.execute();

                View decorView = getWindow().getDecorView();
                // Hide both the navigation bar and the status bar.
                // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
                // a general rule, you should design your app to hide the status bar whenever you
                // hide the navigation bar.
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass

                View decorView = getWindow().getDecorView();
                // Hide both the navigation bar and the status bar.
                // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
                // a general rule, you should design your app to hide the status bar whenever you
                // hide the navigation bar.
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptions);

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();


    }








    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }


    public void onSensorChanged(SensorEvent event){
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];


        Log.i("z", Float.toString(z));


        steering = z*10 ;

    }






    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);


    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }










   public class Client extends AsyncTask<Void, Float, Float> {


        double speed;
        double rpm;
        float angle;
        float angle2;




        @Override
        protected void onProgressUpdate(Float ... angle){
            super.onProgressUpdate(angle);
            RotateAnimation rotate = new RotateAnimation(angle[0], angle[0], Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setInterpolator(new LinearInterpolator());
            rotate.setFillAfter(true);
            pointerSpeed.startAnimation(rotate);

            RotateAnimation rotate2 = new RotateAnimation(angle2, angle2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate2.setDuration(3000);
            rotate2.setInterpolator(new LinearInterpolator());
            rotate2.setFillAfter(true);
            pointerRevs.startAnimation(rotate2);



            // textView.setText(Double.toString(speed[0]));


        }


        @Override
        protected Float doInBackground(Void... voids) {
            try {
                s = new Socket(server, port);
                is = s.getInputStream();
                dis = new DataInputStream(is);
                os = s.getOutputStream();
                dos = new DataOutputStream(os);




                while (s.isConnected()){
                    speed = dis.readDouble();
                    rpm = dis.readDouble();
                    dos.writeDouble(inputGas);
                    dos.writeDouble(inputBrake);
                    dos.writeDouble(steering);




                    speed = (speed * 1.038) - 45;
                    rpm = Math.round(rpm);
                    rpm = (rpm * 0.0337) -45;



                    angle = (float)speed;
                    angle2 = (float)rpm;
                    Log.i("Speed", Double.toString(speed));
                    publishProgress(angle);

                }

            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }




}




