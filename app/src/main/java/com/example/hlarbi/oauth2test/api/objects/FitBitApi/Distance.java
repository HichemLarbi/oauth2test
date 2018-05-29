package com.example.hlarbi.oauth2test.api.objects.FitBitApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Distance {

    @SerializedName("activity")
    @Expose
    private String activity = "";
    @SerializedName("distance")
    @Expose
    private Double distance = 0.0;

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

}
