package com.mycompany.accelerometerapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private GraphicalView mChart;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries;
    private XYSeriesRenderer mCurrentRenderer;

    //private Vector<String> energyValues = new Vector<String>();
    private ArrayList<Float> energyValues = new ArrayList<>();
    private ArrayList<Float> energyValues1 = new ArrayList<>();

    private ArrayList<Float> xArrayList = new ArrayList<>();
    private ArrayList<Float> yArrayList = new ArrayList<>();
    private ArrayList<Float> zArrayList = new ArrayList<>();


    private void initChart() {
        mCurrentSeries = new XYSeries("Sample Data");
        mDataset.addSeries(mCurrentSeries); /* Makes Current Series (data to plot) part of the Dataset */
        mCurrentRenderer = new XYSeriesRenderer(); /* XY type of chart */
        mRenderer.addSeriesRenderer(mCurrentRenderer);
    }


    private Button startButton;
    private Button stopButton;
    private Button resetButton;

    String fileName = "accelerometerReadings.txt";
    String fileName1 = "energyCalculations.txt";

    boolean start = false;

    FileOutputStream outputStream;
    FileOutputStream outputStream1;

    OutputStreamWriter outputWrite;
    OutputStreamWriter outputWrite1;

    private TextView accelerometer;
    private TextView energy;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        accelerometer = (TextView) findViewById(R.id.accelerometerReading);
        energy = (TextView) findViewById(R.id.energyReading);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        initChart();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener (MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_GAME);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sensorManager.unregisterListener(MainActivity.this);

                try {
                    outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    outputWrite = new OutputStreamWriter(outputStream);

                    for(int i = 0; i < xArrayList.size() && i < yArrayList.size() && i < zArrayList.size(); ++i){
                        String valueOut = String.format("%f %f %f\n", xArrayList.get(i), yArrayList.get(i), zArrayList.get(i));
                        outputWrite.write(valueOut);
                    }

                    outputWrite.flush();
                    outputWrite.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Float energyTotal;

                try {
                    //Scanner scanner = new Scanner(this.getFilesDir() + fileName);
                    FileInputStream fis = new FileInputStream(getFilesDir() + File.separator + fileName);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

               /* while(scanner.hasNext()){
                    scanner.nextLine();
                    String xyz = scanner.nextLine();*/
                    String line = br.readLine();
                    while(line != null){

                        String[] xyzValues = line.split(" ");

                        float xA = Float.parseFloat(xyzValues[0]);
                        float yA = Float.parseFloat(xyzValues[1]);
                        float zA = Float.parseFloat(xyzValues[2]);

                        energyTotal = (float)Math.sqrt((xA * xA + yA * yA + zA * zA));

                        energyValues.add(energyTotal);

                        line = br.readLine();
                    }

                    br.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try{
                    outputStream1 = openFileOutput(fileName1, Context.MODE_PRIVATE);
                    outputWrite1 = new OutputStreamWriter(outputStream1);
                    for(int i = 0; i<energyValues.size(); ++i) {
                        String valueOut = String.format("%f \n", energyValues.get(i));
                        outputWrite1.write(valueOut);
                    }
                    outputWrite1.flush();
                    outputWrite1.close();
                } catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    //Scanner scanner = new Scanner(this.getFilesDir() + fileName);
                    FileInputStream fis = new FileInputStream(getFilesDir() + File.separator + fileName1);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

               /* while(scanner.hasNext()){
                    scanner.nextLine();
                    String xyz = scanner.nextLine();*/
                    String line = br.readLine();
                    while(line != null){

                        String[] xyzValues = line.split(" ");

                        float energyReading = Float.parseFloat(xyzValues[0]);

                        energyValues1.add(energyReading);

                        line = br.readLine();
                    }

                    br.close();
                    fis.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
                if (mChart == null) {
                    //initChart(); /* Inititalizes the dataset and rederer variables */
                    for(int i = 0; i < energyValues1.size(); ++i){
                        mCurrentSeries.add(i,energyValues1.get(i));
                    }
                    mChart = ChartFactory.getCubeLineChartView(MainActivity.this, mDataset, mRenderer, 0.3f);
            /*Uses the chart factory to combine dataset and rederer to create the chart */ /* In this case, a cubic line chart view */ /* ChartFactory function returns a variable of type GraphicalView */
                    layout.addView(mChart); }
                else { mChart.repaint();
                }


            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //File file = new File(getFilesDir(), fileName);
                //file.delete();
                //deleteFile(fileName);

               //File file1 = new File(getFilesDir(), fileName1);
               // file1.delete();
               // deleteFile(fileName1);
                accelerometer.setText("");
                energy.setText("");
                mChart = null;
                mCurrentSeries.clear();
              //  mRenderer.clearXTextLabels();
              //  mRenderer.clearYTextLabels();
                energyValues.clear();
                energyValues1.clear();
                xArrayList.clear();
                yArrayList.clear();
                zArrayList.clear();
                LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
                layout.removeAllViews();
            }
        });


        /*InputStream inputStream = null;
        try{
            inputStream = openFileInput(filename);
            startButton.setText(inputStream.read());
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
          //      SensorManager.SENSOR_DELAY_GAME);

}

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized(this) {
            float xA = event.values[0];
            float yA = event.values[1];
            float zA = event.values[2];

            xArrayList.add(xA);
            yArrayList.add(yA);
            zArrayList.add(zA);

            String accelerationOut = String.format("X: %.2f  Y: %.2f  Z: %.2f", xA, yA, zA);
            accelerometer.setText(accelerationOut);

            double energyValue = Math.sqrt(Math.pow(xA,2) + Math.pow(yA, 2) + Math.pow(zA, 2));
            String energyOut = String.format("%.2f", energyValue);
            energy.setText(energyOut);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
