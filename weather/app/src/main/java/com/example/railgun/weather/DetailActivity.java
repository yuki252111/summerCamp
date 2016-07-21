package com.example.railgun.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends Activity {
    private UIHandler displayInfo;
    private UIThread getData;

    private static class UIHandler extends Handler {
        private TextView cityName;
        private TextView date;
        private TextView tempRange;
        private TextView main;
        private TextView wind;

        public TextView getCityName() {
            return cityName;
        }

        public void setCityName(TextView cityName) {
            this.cityName = cityName;
        }

        public TextView getTempRange() {
            return tempRange;
        }

        public void setTempRange(TextView tempRange) {
            this.tempRange = tempRange;
        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }

        public TextView getMain() {
            return main;
        }

        public void setMain(TextView main) {
            this.main = main;
        }

        public TextView getWind() {
            return wind;
        }

        public void setWind(TextView wind) {
            this.wind = wind;
        }

        @Override
        public void handleMessage(Message msg){
            try {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                if(bundle == null ) {
                    Log.e("Error:","message is null");
                    return;
                }
                JSONObject weather = new JSONObject( bundle.get("weather").toString() );
                if(weather == null ) {
                    Log.e("Error:","weather is null");
                    return;
                }
                date.setText(weather.get("date").toString());
                cityName.setText(bundle.get("city").toString());
                String tempRangeStr = weather.get("low").toString() + "~"+weather.get("high").toString() + "℃";
                tempRange.setText(tempRangeStr);
                main.setText(weather.get("text_day").toString());
                String windStr = "风速: " + weather.get("wind_speed").toString()+"km/h";
                wind.setText(windStr);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null ;
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
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    private static class UIThread extends Thread {
        Intent dateIt;
        UIHandler displayInfo;

        public UIHandler getDisplayInfo() {
            return displayInfo;
        }

        public void setDisplayInfo(UIHandler displayInfo) {
            this.displayInfo = displayInfo;
        }

        public Intent getDateIt() {
            return dateIt;
        }

        public void setDateIt(Intent dateIt) {
            this.dateIt = dateIt;
        }

        @Override
        public void run(){
            try {
                String dateString ;
                String cityName = null;
                JSONObject weather = null;
                if (dateIt != null) {
                    dateString = dateIt.getStringExtra("date");
                    cityName = dateIt.getStringExtra("cityName");
                    if (dateString == null || cityName == null) {
                        return;
                    } else {
                        String httpUrl = "http://apis.baidu.com/thinkpage/weather_api/suggestion";
                        String position = "location="+cityName;
                        String httpArg = position+"&language=zh-Hans&unit=c&start=0&days=5";
                        String jsonResult = request(httpUrl, httpArg);

                        JSONObject days = new JSONObject(jsonResult);
                        JSONArray results = days.getJSONArray("results");
                        JSONArray dailys = results.getJSONObject(0).getJSONArray("daily");
                        cityName = results.getJSONObject(0)
                                .getJSONObject("location")
                                .get("name")
                                .toString();
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
                    return;
                }
                if(weather == null) {
                    Log.e("Error:","weather is null");
                    return;
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("weather", weather.toString());
                bundle.putSerializable("city", cityName);
                msg.setData(bundle);
                displayInfo.sendMessage(msg);
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
        displayInfo = new UIHandler();
        getTextViews();

        getData = new UIThread();
        getData.setDateIt(getIntent());
        getData.setDisplayInfo(displayInfo);
        getData.start();
    }

    private void getTextViews(){
        displayInfo.setCityName((TextView)findViewById(R.id.cityName));
        displayInfo.setDate((TextView) findViewById(R.id.date));
        displayInfo.setTempRange((TextView)findViewById(R.id.tempRange));
        displayInfo.setMain((TextView)findViewById(R.id.main));
        displayInfo.setWind((TextView)findViewById(R.id.wind));
    }
}
