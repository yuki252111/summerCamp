package com.example.railgun.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends Activity {
    private UIHandler UIhandler;


    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            try {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                if(bundle == null ) throw new Exception("null pointer");
                JSONObject weather = new JSONObject( bundle.get("weather").toString() );
                if(weather == null ) throw new Exception("null pointer");

                TextView cityName = (TextView) findViewById(R.id.date);
                cityName.setText(weather.get("date").toString());

                TextView date = (TextView) findViewById(R.id.cityName);
                date.setText(bundle.get("city").toString());


                TextView tempRange = (TextView)findViewById(R.id.tempRange);
                String tempRangeStr = weather.get("low").toString() + "~"+weather.get("high").toString() + "℃";
                tempRange.setText(tempRangeStr);

                TextView main = (TextView)findViewById(R.id.main);
                main.setText(weather.get("text_day").toString());


                TextView wind = (TextView)findViewById(R.id.wind);
                String windStr = "风速: " + weather.get("wind_speed").toString()+"km/h";
                wind.setText(windStr);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader ;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "a1d8fec08eb3f436c793b37976e35eb2");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead ;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private class UIThread extends Thread {
        @Override
        public void run(){
            try {
                //JSONArray detailWeatherList = new JSONArray();
                Intent dateIt = getIntent();
                String dateString ;
                String cityName = null;
                JSONObject weather = null;
                if (dateIt != null) {
                    dateString = dateIt.getStringExtra("date");
                    cityName = dateIt.getStringExtra("cityName");
                    if (dateString == null || cityName == null) {
                        finish();
                    } else {
                        String httpUrl = "http://apis.baidu.com/thinkpage/weather_api/suggestion";
                        String position = "location="+cityName;
                        String httpArg = position+"&language=zh-Hans&unit=c&start=0&days=5";
                        String jsonResult = request(httpUrl, httpArg);

                        JSONObject days = new JSONObject(jsonResult);
                        JSONArray results = days.getJSONArray("results");
                        JSONArray dailys = results.getJSONObject(0).getJSONArray("daily");
                        cityName = results.getJSONObject(0).getJSONObject("location").get("name").toString();
                        int dailysLen = dailys.length();
                        for(int i = 0; i < dailysLen; i++){
                            JSONObject dayWeather = dailys.getJSONObject(i);
                            if(dayWeather.get("date").equals(dateString)){
                                weather = dayWeather;
                                break;
                            }
                        }
                    }
                } else {
                    finish();
                }
                if(weather == null) throw new Exception("weather is null");
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("weather", weather.toString());
                bundle.putSerializable("city", cityName);
                msg.setData(bundle);
                DetailActivity.this.UIhandler.sendMessage(msg);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        UIhandler = new UIHandler();
        UIThread ui= new UIThread();
        ui.start();
    }
}
