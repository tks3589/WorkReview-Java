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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String API_URL = "http://tks3589.ddns.net:1021/akbtp/api/event";
    ArrayList<EventModel> eventData = new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        getAPI_EventData_No3Part();

    }

    private void setUI(ArrayList<EventModel> data){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new EventAdapter(this,data));
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
                    commonProcessJson(finalContent);
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


    private void gsonProcess(){

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
}