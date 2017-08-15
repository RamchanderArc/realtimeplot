package com.example.dcp.realtimeplot;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.WindowManager;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

public class realtime extends AppCompatActivity implements SensorEventListener {

    private LineChart mChart;

    private SensorManager sensorManager;

    TextView x;
    TextView y;
    TextView z;
    TextView xyz;

    String sx,sy,sz,sxyz;
    int i=0, framelen=31;
    double xyzfilt;
    double coeff[]= {-0.0411,-0.0264,-0.0127,-0.0001,0.0115,0.0221,0.0318,0.0403,0.0479,0.0545,0.0601,0.0646,0.0682,0.0707,0.0722,0.0727,0.0722,0.0707,0.0682,0.0646,0.0601,0.0545,0.0479,0.0403,0.0318,0.0221,0.0115,-0.0001,-0.0127,-0.0264,-0.0411};

    private double t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_realtime);

        mChart = (LineChart) findViewById(R.id.chart1);
        x = (TextView) findViewById(R.id.xtextView);
        y = (TextView) findViewById(R.id.ytextView);
        z = (TextView) findViewById(R.id.ztextView);
        xyz = (TextView) findViewById(R.id.xyztextView);
        //mChart.setOnChartValueSelectedListener(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

        mChart.getDescription().setEnabled(true);

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLUE);

        mChart.setData(data);

        Legend l = mChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(mTfLight);
        l.setTextColor(Color.RED);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis yl = mChart.getAxisLeft();
        yl.setTextColor(Color.BLACK);
        yl.setAxisMaximum(20f);
        yl.setAxisMinimum(0f);
        yl.setDrawGridLines(true);


        YAxis yr = mChart.getAxisRight();
        yr.setEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh, menu);
        inflater.inflate(R.menu.stop,menu);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1){

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            LineData data = mChart.getData();

            if (data != null) {

                ILineDataSet set = data.getDataSetByIndex(0);
                // set.addEntry(...); // can be called as well

                if (set == null) {
                    set = createSet();
                    data.addDataSet(set);
                }

                t = t + 0.1;
                float xVal = event.values[0];
                float yVal = event.values[1];
                float zVal = event.values[2];

                double xyzVal = Math.sqrt(xVal * xVal + yVal * yVal + zVal * zVal);

                if(i<framelen){
                    //xyzfilt = coeff[i]*xyzVal;
                    i++;
                }
                if(i>=framelen){
                    i=0;
                }

                sx = "X Value : <font color = '#80080'>" + xVal + "</font>";
                sy = "Y Value : <font color = '#80080'>" + yVal + "</font>";
                sz = "Z Value : <font color = '#80080'>" + zVal + "</font>";
                sxyz = "XYZ Value : <font color = '#80080'>" + xyzVal + "</font>";

                data.addEntry(new Entry(set.getEntryCount(), (float) xyzVal), 0);
                data.notifyDataChanged();

                mChart.notifyDataSetChanged();

                mChart.moveViewToX(data.getEntryCount());

                x.setText(Html.fromHtml(sx));
                y.setText(Html.fromHtml(sy));
                z.setText(Html.fromHtml(sz));
                xyz.setText(Html.fromHtml(sxyz));
            }
        }
    }

    private LineDataSet createSet() {

            LineDataSet set = new LineDataSet(null, "Dynamic Data");
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            //set.setColor(ColorTemplate.getHoloBlue());
            //set.setCircleColor(Color.BLACK);
            set.setLineWidth(2f);
            //set.setCircleRadius(0f);
            set.setFillAlpha(65);
            //set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setValueTextColor(Color.BLACK);
            set.setValueTextSize(9f);
            set.setDrawValues(false);
            return set;
    }
}
