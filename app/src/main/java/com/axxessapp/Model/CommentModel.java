package com.axxessapp.Model;

import com.aands.sim.simtoolkit.firebase.BaseFirebaseModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommentModel extends BaseFirebaseModel implements Serializable {
    @SerializedName("id")
    private String id ;
    @SerializedName("imageId")
    private String imageId ;
    @SerializedName("comment")
    private String comment ;
    @SerializedName("date")
    private Long date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
