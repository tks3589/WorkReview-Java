package com.example.workreview_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


public class DbActivity extends AppCompatActivity {
    DbHelper dbHelper;
    TextView db_show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        dbHelper = new DbHelper(this);
        db_show = findViewById(R.id.db_show);
        //insertDB();
        showDB();
    }

    private void insertDB(){
        ContentValues data = new ContentValues();
        data.put("date","2021-01-31");
        data.put("info","aaaaa");
        long id = dbHelper.getWritableDatabase().insert("testDB",null,data);
        if(id > -1)
            Toast.makeText(this,"succ",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"err",Toast.LENGTH_SHORT).show();
    }

    private void showDB(){
        Cursor cursor = dbHelper.getReadableDatabase().query("testDB",null,null,null,null,null,null);
        StringBuilder result = new StringBuilder();
        int rows_num = cursor.getCount();
        if(rows_num != 0) {
            cursor.moveToFirst();
            for(int i=0; i<rows_num; i++) {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String info = cursor.getString(cursor.getColumnIndex("info"));
                result.append(date+" "+info+"\n");
                cursor.moveToNext();
            }
        }
        cursor.close();

        db_show.setText(result);
    }
}