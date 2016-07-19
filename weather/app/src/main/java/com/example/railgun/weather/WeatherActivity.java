package com.example.railgun.weather;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WeatherActivity extends Activity {
    Intent weatherIt = null;
    City city = null;
    static Map<String,String> weatherInfoMap=new HashMap<>();

    void fillinWeatherInfoMap(){
        try {
            weatherIt = getIntent();
            if (weatherIt != null) {
                city = (City) weatherIt.getSerializableExtra("cityName");
                if (city == null) {
                    finish();
                } else {
                    DecimalFormat df  =  new  DecimalFormat("#.#");
                    OpenWeatherMap owm = new OpenWeatherMap("50b95478c6c326f917e43f8573170c6f");
                    // getting current weather data for the "London" city
                    owm.setUnits(OpenWeatherMap.Units.METRIC);
                    CurrentWeather cwd = owm.currentWeatherByCityName(city.getEnglishName());
                    if(cwd.hasWeatherInstance()){
                        if(cwd.getWeatherInstance(0).hasWeatherName())
                            weatherInfoMap.put("天气:", cwd.getWeatherInstance(0).getWeatherName());
                        if(cwd.getWeatherInstance(0).hasWeatherDescription())
                            weatherInfoMap.put("描述:", cwd.getWeatherInstance(0).getWeatherDescription());
                        //if(cwd.getWeatherInstance(0).hasWeatherIconName())
                        //weatherInfoMap.put("icon", cwd.getWeatherInstance(0).getWeatherIconName());
                    }
                    if(cwd.hasMainInstance()){
                        if(cwd.getMainInstance().hasTemperature())
                            weatherInfoMap.put("温度:",Float.valueOf(df.format(cwd.getMainInstance().getTemperature())).toString());
                        if(cwd.getMainInstance().hasMinTemperature())
                            weatherInfoMap.put("最低温度:", Float.valueOf(df.format(cwd.getMainInstance().getMinTemperature())).toString());
                        if(cwd.getMainInstance().hasMaxTemperature())
                            weatherInfoMap.put("最高温度", Float.valueOf(df.format(cwd.getMainInstance().getMaxTemperature())).toString());
                        if(cwd.getMainInstance().hasPressure())
                            weatherInfoMap.put("气压:", Float.valueOf(df.format(cwd.getMainInstance().getPressure())).toString());
                        if(cwd.getMainInstance().hasHumidity())
                            weatherInfoMap.put("湿度:", Float.valueOf(df.format(cwd.getMainInstance().getHumidity())).toString());
                    }
                    if(cwd.hasCloudsInstance()){
                        if(cwd.getCloudsInstance().hasPercentageOfClouds())
                            weatherInfoMap.put("云占百分比:", Float.valueOf(df.format(cwd.getCloudsInstance().getPercentageOfClouds())).toString());
                    }
                    if(cwd.hasWindInstance()){
                        if(cwd.getWindInstance().hasWindSpeed())
                            weatherInfoMap.put("风速:", Float.valueOf(df.format(cwd.getWindInstance().getWindSpeed())).toString());
                    }
                }
            } else {
                finish();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        fillinWeatherInfoMap();

        ListView weatherInfoList = (ListView)findViewById(R.id.weatherInfoLv);
        WeatherAdapter weatherInfoAdapter = new WeatherAdapter(this);
        weatherInfoList.setAdapter(weatherInfoAdapter);
        //weatherInfoList.setOnItemClickListener(this);
    }

    public final static class WeatherViewHolder{
        TextView weatherInfoKey;
        TextView weatherInfoValue;
    }

    public static class  WeatherAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private String[] weatherInfoKeys;

        public WeatherAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
            Set<String> keys=weatherInfoMap.keySet();
            this.weatherInfoKeys=new String[keys.size()];
            keys.toArray(this.weatherInfoKeys);
        }

        @Override
        public int getCount(){
            return weatherInfoMap.size();
        }

        @Override
        public Object getItem(int position){
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            WeatherViewHolder holder ;

            if(convertView == null){
                holder = new WeatherViewHolder();
                convertView = mInflater.inflate(R.layout.weather_layout, parent, false);

                holder.weatherInfoKey = (TextView)convertView.findViewById(R.id.weatherInfoKey);
                holder.weatherInfoValue = (TextView)convertView.findViewById(R.id.weatherInfoValue);

                convertView.setTag(holder);
            }
            else{
                holder = (WeatherViewHolder)convertView.getTag();
            }
            holder.weatherInfoKey.setText(weatherInfoKeys[position]);
            holder.weatherInfoValue.setText(weatherInfoMap.get(weatherInfoKeys[position]));
            return convertView;
        }
    }
}
