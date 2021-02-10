package com.example.workreview_java;

import com.example.workreview_java.model.EventModel;
import com.example.workreview_java.model.MemberModel;
import com.example.workreview_java.model.ProductModel;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {
    @GET("event")
    Call<ArrayList<EventModel>> getEvent();

    @GET("members/3")
    Single<String> getMembers();

    @GET("products/{name}")
    Single<String> getProduct(
            @Path("name")
            String name
    );

}
