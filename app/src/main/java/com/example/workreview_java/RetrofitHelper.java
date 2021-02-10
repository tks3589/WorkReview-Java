package com.example.workreview_java;

import com.example.workreview_java.model.EventModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHelper {
    private final static String API_URL = "http://tks3589.ddns.net:1021/akbtp/api/";

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS).build();

    private final static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build();

    private final static Api api = retrofit.create(Api.class);

    public static void getEventData(Callback<ArrayList<EventModel>> callback){
        api.getEvent().enqueue(new Callback<ArrayList<EventModel>>() {
            @Override
            public void onResponse(Call<ArrayList<EventModel>> call, Response<ArrayList<EventModel>> response) {
                if(response.isSuccessful()){
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EventModel>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public static Single<String> getMembers(){
        return api.getMembers();
    }

    public static Single<String> getProduct(String ename){
        return api.getProduct(ename);
    }
}
