package com.example.railgun.weather;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.HourlyForecast;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class WeatherActivity extends Activity implements View.OnClickListener {
    private Intent weatherIt ;
    private City city ;
    private UIHandler UIhandler;
    private JSONObject weatherInfoMap;

    private class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            try {
                TextView cityName = (TextView) findViewById(R.id.cityName);
                cityName.setText(city.getName());
                TextView date = (TextView) findViewById(R.id.date);
                SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
                String dt = dateFm.format(new java.util.Date());
                date.setText(dt);

                TextView temp = (TextView) findViewById(R.id.temp);
                String tempStr = weatherInfoMap.get("温度:") + "℃";
                temp.setText(tempStr);
                TextView tempRange = (TextView)findViewById(R.id.tempRange);
                String tempRangeStr = weatherInfoMap.get("最低温度:").toString() + "~"+weatherInfoMap.get("最高温度:").toString() + "℃";
                tempRange.setText(tempRangeStr);

                TextView main = (TextView)findViewById(R.id.main);
                main.setText(weatherInfoMap.get("天气:").toString());
                TextView pressure = (TextView)findViewById(R.id.pressure);
                String pressureStr = "气压: " + weatherInfoMap.get("气压:").toString();
                pressure.setText(pressureStr);
                TextView depth = (TextView)findViewById(R.id.depth);
                String depthStr = "湿度: " + weatherInfoMap.get("湿度:").toString();
                depth.setText(depthStr);
                TextView cloud = (TextView)findViewById(R.id.cloud);
                String cloudStr = "云占: " + weatherInfoMap.get("云占百分比:").toString() + "%";
                cloud.setText(cloudStr);
                TextView wind = (TextView)findViewById(R.id.wind);
                String windStr = "风速: " + weatherInfoMap.get("风速:").toString();
                wind.setText(windStr);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class UIThread extends Thread{
        @Override
        public void run(){
            try {
                weatherInfoMap = new JSONObject();
                weatherIt = getIntent();
                if (weatherIt != null) {
                    city = (City) weatherIt.getSerializableExtra("cityName");
                    if (city == null) {
                        finish();
                    } else {
                        DecimalFormat df  =  new  DecimalFormat("#.#");
                        OpenWeatherMap owm = new OpenWeatherMap("50b95478c6c326f917e43f8573170c6f");

                        owm.setUnits(OpenWeatherMap.Units.METRIC);
                        String cityName = city.getEnglishName();
                        CurrentWeather cwd = owm.currentWeatherByCityName(cityName);
                        Log.e("weather","lll");
                        if(cwd.hasWeatherInstance()){
                            if(cwd.getWeatherInstance(0).hasWeatherName()) {
                                weatherInfoMap.put("天气:", cwd.getWeatherInstance(0).getWeatherName());
                            }
                            else{
                                weatherInfoMap.put("天气:", "");
                            }
                        }
                        if(cwd.hasMainInstance()){
                            if(cwd.getMainInstance().hasTemperature()) {
                                weatherInfoMap.put("温度:", Float.valueOf(df.format(cwd.getMainInstance().getTemperature())).toString());

                            }
                            else{
                                weatherInfoMap.put("温度:", "-1000");
                            }
                            if(cwd.getMainInstance().hasMinTemperature()) {
                                weatherInfoMap.put("最低温度:", Float.valueOf(df.format(cwd.getMainInstance().getMinTemperature())).toString());

                            }
                            else{
                                weatherInfoMap.put("最低温度:", "-1000");
                            }
                            if(cwd.getMainInstance().hasMaxTemperature()) {
                                weatherInfoMap.put("最高温度:", Float.valueOf(df.format(cwd.getMainInstance().getMaxTemperature())).toString());

                            }
                            else{
                                weatherInfoMap.put("最高温度:", "-1000");
                            }
                            if(cwd.getMainInstance().hasPressure()) {
                                weatherInfoMap.put("气压:", Float.valueOf(df.format(cwd.getMainInstance().getPressure())).toString());

                            }
                            else{
                                weatherInfoMap.put("气压:", "-1000");
                            }
                            if(cwd.getMainInstance().hasHumidity()) {
                                weatherInfoMap.put("湿度:", Float.valueOf(df.format(cwd.getMainInstance().getHumidity())).toString());

                            }
                            else{
                                weatherInfoMap.put("湿度:", "-1000");
                            }
                        }
                        if(cwd.hasCloudsInstance()){
                            if(cwd.getCloudsInstance().hasPercentageOfClouds()) {
                                weatherInfoMap.put("云占百分比:", Float.valueOf(df.format(cwd.getCloudsInstance().getPercentageOfClouds())).toString());

                            }
                            else{
                                weatherInfoMap.put("云占百分比:", "-1000");
                            }
                        }
                        if(cwd.hasWindInstance()){
                            if(cwd.getWindInstance().hasWindSpeed()) {
                                weatherInfoMap.put("风速:", Float.valueOf(df.format(cwd.getWindInstance().getWindSpeed())).toString());

                            }
                            else{
                                weatherInfoMap.put("风速:", "-1000");
                            }
                        }
                        HourlyForecast hw = owm.hourlyForecastByCityName(cityName);
                        int detailWeatherCount = hw.getForecastCount();
                        Log.e("1",""+detailWeatherCount);
                    }
                } else {
                    finish();
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update",true);
                msg.setData(bundle);
                WeatherActivity.this.UIhandler.sendMessage(msg);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        UIhandler = new UIHandler();
        UIThread ui= new UIThread();
        ui.start();

        fillInDays();
    }

    private void fillInDays(){
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);


        calendar.add(Calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
        date=calendar.getTime(); //这个时间就是日期往后推一天的结果
        dateString = formatter.format(date);
        TextView day2 = (TextView)findViewById(R.id.day2);
        day2.setText(dateString);

        calendar.add(Calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
        date=calendar.getTime(); //这个时间就是日期往后推一天的结果
        dateString = formatter.format(date);
        TextView day3 = (TextView)findViewById(R.id.day3);
        day3.setText(dateString);

        day2.setOnClickListener(this);
        day3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        try {
            String dateString = ((TextView)v).getText().toString();
            Intent detailIt = new Intent(this, DetailActivity.class);
            detailIt.putExtra("date",dateString);
            detailIt.putExtra("cityName",city.getEnglishName());
            startActivity(detailIt);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
