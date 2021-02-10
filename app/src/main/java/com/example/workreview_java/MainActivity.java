package com.example.workreview_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.workreview_java.model.EventModel;
import com.example.workreview_java.model.MemberModel;
import com.example.workreview_java.model.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;

import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private String API_URL = "http://tks3589.ddns.net:1021/akbtp/api/event";
    ArrayList<EventModel> eventData = new ArrayList<>();
    ArrayList<MemberModel> membersData = new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        //getAPI_EventData_No3Part();
        //getAPI_EventData_3Part();
        getAPI_RxJava();
    }

    private void setUI(ArrayList<EventModel> data){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new EventAdapter(this,data));
    }

    private void getAPI_RxJava(){ //getMember_ename -> getProduct
        RetrofitHelper.getMembers()
                .subscribeOn(Schedulers.io())
                .flatMap((Function<String, SingleSource<String>>) s -> {
                    commonProcessJson2(s);
                    Log.d("rxjava_test",(membersData.get(0).cname));
                    return RetrofitHelper.getProduct(membersData.get(0).ename);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>(){
                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull String s) {
                        Log.d("rxjava_test",s);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.d("rxjava_test",e.toString());
                    }
                });
    }

    private void getAPI_EventData_3Part(){
        RetrofitHelper.getEventData(new Callback<ArrayList<EventModel>>() {
            @Override
            public void onResponse(Call<ArrayList<EventModel>> call, Response<ArrayList<EventModel>> response) {
                setUI(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<EventModel>> call, Throwable t) {
                Log.d("ee",t.getMessage());
            }
        });
    }

    private void getAPI_EventData_No3Part(){
        new Thread(() -> {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(API_URL).openStream()));
                String line,content="";
                while((line = bufferedReader.readLine())!=null){
                    content+=line;
                }
                String finalContent = content;

                //直接更新ＵＩ
                runOnUiThread(() -> {
                    //commonProcessJson(finalContent);
                    gsonProcess(finalContent);
                    setUI(eventData);
                });

                //Handler更新
                /*Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("data",finalContent);
                msg.setData(bundle);
                msg.what = 1;
                mHandler.sendMessage(msg);*/

            }catch (Exception ee){
                Log.d("ee",ee.toString());
            }
        }).start();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String finalContent = msg.getData().getString("data");
                    commonProcessJson(finalContent);
                    //textView.setText(eventData.get(0).title);
                    break;
            }
        }
    };

    private void gsonProcess(String content){
        try {
            Type listTypeToken = new TypeToken<ArrayList<EventModel>>(){}.getType();
            eventData = new Gson().fromJson(content, listTypeToken);
        }catch (Exception e){
            Log.d("ee",e.toString());
        }
    }

    private void commonProcessJson(String content){
        try {
            JSONArray jsonArray = new JSONArray(content);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                int id = object.getInt("id");
                String title = object.getString("title");
                String imageurl = object.getString("imageurl");
                EventModel model = new EventModel();
                model.id = id;
                model.title = title;
                model.imageurl = imageurl;
                eventData.add(model);
            }
        } catch (JSONException e) {
            Log.d("ee",e.toString());
        }
    }

    private void commonProcessJson2(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray jsonArray = jsonObject.getJSONArray("members");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String cname = obj.getString("cname");
                String ename = obj.getString("ename");
                int type = obj.getInt("type");
                int mgroup = obj.getInt("mgroup");
                String imgurl = obj.getString("imgurl");
                MemberModel model = new MemberModel();
                model.cname = cname;
                model.ename = ename;
                model.type = type;
                model.mgroup = mgroup;
                model.imgurl = imgurl;

                membersData.add(model);
            }
        }catch (JSONException e) {
            Log.d("ee",e.toString());
        }
    }
}