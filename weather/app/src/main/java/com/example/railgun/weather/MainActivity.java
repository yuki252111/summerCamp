package com.example.railgun.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<City> citys = new ArrayList<>();
        fillCitys(citys);

        RecyclerView cityList = (RecyclerView)findViewById(R.id.cityLv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        cityList.setLayoutManager(manager);
        final MyAdapter mAdapter = new MyAdapter();
        mAdapter.setCitys(citys);
        mAdapter.setOnItemClickListener(new MyAdapter.OnRecyclerViewItemClickListener(){
            public void onItemClick(View v, int position){
                City clickedCity = mAdapter.getCitys().get(position);
                Intent weatherIt = new Intent(MainActivity.this, WeatherActivity.class);
                Bundle cityBundle = new Bundle();
                cityBundle.putSerializable("cityName", clickedCity);
                weatherIt.putExtras(cityBundle);
                startActivity(weatherIt);
            }
        });
        cityList.setAdapter(mAdapter);
    }

    private void fillCitys(ArrayList<City> citys){
        InputStream fs = null;
        try {
            fs = getAssets().open("city.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
            String cityStr = "";
            String temp ;
            while((temp = reader.readLine()) != null) {
                cityStr += temp;
            }
            if(cityStr == ""){
                Log.e("Error:","no file content!");
                return;
            }
            JSONObject cityAll = new JSONObject(cityStr.trim());
            JSONArray cityArr = (JSONArray)cityAll.get("result");
            if(cityArr == null) {
                Log.e("Error:","no city");
                return;
            }
            int cityArrLen = cityArr.length();
            for(int i = 0;i < cityArrLen; i++){
                JSONObject oneCity = (JSONObject)cityArr.get(i);
                if(oneCity == null){
                    Log.e("Error:","no city");
                    return;
                }
                if(oneCity.has("cities")){
                    JSONArray oneProvince = (JSONArray)oneCity.get("cities");
                    if(oneProvince == null){
                        Log.e("Error:","no city");
                        return;
                    }
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
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if(fs != null) {
                try {
                    fs.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static class MyAdapter extends RecyclerView.Adapter {
        private ArrayList<City> citys;
        public LayoutInflater mInflater;
        private OnRecyclerViewItemClickListener mListener;

        public ArrayList<City> getCitys() {
            return citys;
        }

        public void setCitys(ArrayList<City> citys) {
            this.citys = citys;
        }

        public static interface  OnRecyclerViewItemClickListener{
            void onItemClick(View v, int position);
        }

        public void setOnItemClickListener(OnRecyclerViewItemClickListener mListener){
            this.mListener = mListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            mInflater = LayoutInflater.from(parent.getContext());
            View mView = mInflater.inflate(R.layout.city_layout, parent, false);
            MyViewHolder mViewHolder = new MyViewHolder(mView);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder mViewHolder, final int position){
            ((MyViewHolder)mViewHolder).getCityName().setText(citys.get(position).getName());
            ((MyViewHolder)mViewHolder).getCityName().setTag(String.valueOf(position));
            ((MyViewHolder)mViewHolder).getCityName().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        mListener.onItemClick(v,position);
                    }
                }
            });
        }

        @Override
        public int getItemCount(){
            if(citys == null) return 0;
            return citys.size();
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView cityName;

            public TextView getCityName() {
                return cityName;
            }

            public void setCityName(TextView cityName) {
                this.cityName = cityName;
            }

            public MyViewHolder(View v){
                super(v);
                if(v == null){
                    Log.e("Error:","view is null");
                }
                else{
                    cityName = (TextView)v.findViewById(R.id.cityName);
                }
            }
        }
    }
}
