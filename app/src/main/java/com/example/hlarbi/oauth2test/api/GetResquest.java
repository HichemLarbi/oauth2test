package com.example.hlarbi.oauth2test.api;

import com.example.hlarbi.oauth2test.api.objects.FitBitApi.Activities;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;

public interface GetResquest {


/*6LBFCH*/








    @GET("/1/user/{user_id}/activities/date/2018-04-26.json")
    Call<Activities> getActivitiesData(
            @HeaderMap Map<String, String> headers,
            @Path("user_id") String value
            );}
