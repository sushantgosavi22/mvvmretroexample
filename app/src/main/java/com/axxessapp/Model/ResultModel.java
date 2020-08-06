package com.axxessapp.Model;

import com.google.gson.annotations.SerializedName;
import com.axxessapp.ShapeModelData;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultModel implements Serializable {
    @SerializedName("data")
    public ArrayList<ShapeModelData> data = new ArrayList<>();
    @SerializedName("success")
    public Boolean success= false;
    @SerializedName("status")
    public int status  =0;
}
