package com.example.railgun.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    static ArrayList<City> citys = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fillCitys();

        ListView cityList = (ListView)findViewById(R.id.cityLv);
        MyAdapter adapter = new MyAdapter(this);
        cityList.setAdapter(adapter);
        cityList.setOnItemClickListener(this);
    }

    private void fillCitys(){
        try {
            InputStream fs = getAssets().open("city.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
            String cityStr = "";
            String temp ;
            while((temp = reader.readLine()) != null) {
                cityStr += temp;
            }
            JSONObject cityAll = new JSONObject(cityStr.trim());
            JSONArray cityArr = (JSONArray)cityAll.get("result");
            int cityArrLen = cityArr.length();
            for(int i = 0;i < cityArrLen; i++){
                JSONObject oneCity = (JSONObject)cityArr.get(i);
                if(oneCity.has("cities")){
                    JSONArray oneProvince = (JSONArray)oneCity.get("cities");
                    int oneProvinceLen = oneProvince.length();
                    for(int j = 0;j < oneProvinceLen;j++){
                        JSONObject oneProvinceCity = (JSONObject)oneProvince.get(j);
                        City cityObject = new City((String)oneProvinceCity.get("id"),(String)oneProvinceCity.get("name"),(String)oneProvinceCity.get("englishName"));
                        citys.add(cityObject);
                    }
                }
                else{
                    City cityObject = new City((String)oneCity.get("id"),(String)oneCity.get("name"),(String)oneCity.get("englishName"));
                    citys.add(cityObject);
                }
            }
            fs.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        //citys=new String[]{"beijing","shanghai"};
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        City clickedCity = citys.get(position);
        //Log.e("successful", clickedCity.getName());
        Intent weatherIt = new Intent(this,WeatherActivity.class);
        Bundle cityBundle = new Bundle();
        cityBundle.putSerializable("cityName",clickedCity);
        weatherIt.putExtras(cityBundle);
        startActivity(weatherIt);
    }
    /*@Override
    protected void onStart(){
        super.onStart();
        Log.e("hello","onStart\n");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.e("hello","onResume\n");
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.e("hello","onPause\n");
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.e("hello","onStop\n");
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        Log.e("hello","onRestart\n");
    }
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        Log.e("hello","onDestroy\n");
    }*/

    public final static class ViewHolder{
        TextView cityName;
    }

    public static class  MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount(){
            return citys.size();
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
            ViewHolder holder ;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.city_layout, parent, false);

                holder.cityName = (TextView)convertView.findViewById(R.id.cityName);

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.cityName.setText(citys.get(position).getName());
            return convertView;
        }
    }
}
