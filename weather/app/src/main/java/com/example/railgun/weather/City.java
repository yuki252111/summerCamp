package com.example.railgun.weather;

import java.io.Serializable;

/**
 * Created by railgun on 16/7/19.
 */
public class City implements Serializable{
    private String id;
    private String name;
    private String englishName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public City(String id, String name, String englishName){
        this.id = id;
        this.name = name;
        this.englishName = englishName;
    }
}
