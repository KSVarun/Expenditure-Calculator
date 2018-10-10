package com.example.idks.expenditurecalc;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by idks on 3/14/2018.
 */

public class Salaries implements Serializable{
   public String description;
   public String value;
   public long timestamp;
   public String fullDescription;

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public Salaries() {

    }

    public Salaries(String salary, String incomeName,Long timestamp) {
        this.description = salary;
        this.value = incomeName;
        this.timestamp = timestamp;
    }

    public String getSalary() {
        return value;
    }

    public void setSalary(String salary) {
        this.value = salary;
    }

    public String getIncomeName() {
        return description;
    }

    public void setIncomeName(String incomeName) {
        this.description = incomeName;
    }


    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
