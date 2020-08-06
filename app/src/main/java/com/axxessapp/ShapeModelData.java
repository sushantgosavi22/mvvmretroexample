package com.axxessapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShapeModelData  implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

   @SerializedName("link")
    public String link;

}
