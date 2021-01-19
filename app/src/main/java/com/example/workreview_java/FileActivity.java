package com.example.workreview_java;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// https://developer.android.com/training/data-storage
// https://mp.weixin.qq.com/s/aiDMyAfAZvaYIHuIMLAlcg

public class FileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        //App-specific files (internal , external storage) 不需要任何權限
        // /data/data/com.example.workreview_java
        // /sdcard/Android/data/com.example.workreview_java
        //createStoragePrivateFile();
        //deleteStoragePrivateFile();

        //公有目錄 MediaStore處理 要權限
        // (掛載目錄 真實目錄在storage/emulated裡面) /sdcard/DCIM , /sdcard/Music , /sdcard/Movies , /sdcard/Download
        mediaStoreCreateFile();
        //mediaStoreDeleteFile();

        //Storage Access Framework
        //safCreateFile();
        //safDeleteFile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==87 && resultCode== Activity.RESULT_OK){ //create
            Uri fileUri = null;
            if(data!=null) {
                fileUri = data.getData();
                Log.d("fileUri: " ,fileUri.toString());
            }
            OutputStream os = null;
            try {
                if (fileUri != null) {
                    os = getContentResolver().openOutputStream(fileUri);
                }
                if (os != null) {
                    String content = "測試喔87";
                    byte[] contentInBytes = content.getBytes();
                    os.write(contentInBytes);
                    os.close();
                }
            } catch (IOException e) {
                Log.d("fail: ", e.toString());
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    Log.d("fail in close: " , e.toString());
                }
            }
        }else if(requestCode==78 && resultCode== Activity.RESULT_OK){ //delete
            Uri fileUri = null;
            if(data!=null) {
                fileUri = data.getData();
                Log.d("fileUri: " ,fileUri.toString());
            }
            try {
                if (fileUri != null) {
                    DocumentsContract.deleteDocument(getContentResolver(),fileUri);
                }
            } catch (Exception e) {
                Log.d("fail: ", e.toString());
            }
        }

    }

    void safDeleteFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent,78);
    }

    void safCreateFile(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE,"DemoFile87.txt");
        startActivityForResult(intent,87);
    }


    void mediaStoreDeleteFile(){
        Uri external = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }
        ContentResolver resolver = getContentResolver();
        String selection = MediaStore.Downloads.TITLE + "=?";
        String[] args = new String[]{"DemoFile555"};
        String[] projection = new String[]{MediaStore.Downloads._ID};
        Cursor cursor = resolver.query(external,projection,selection,args,null);
        Uri txtUri = null;
        if(cursor!=null && cursor.moveToFirst()) {
            txtUri = ContentUris.withAppendedId(external, cursor.getLong(0));
            cursor.close();
        }
        if(txtUri!=null) {
            Log.d("txtUri: ", txtUri.toString());
            getContentResolver().delete(txtUri, null, null);
        }
    }

    void mediaStoreCreateFile(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, "DemoFile555.txt");
        values.put(MediaStore.Downloads.TITLE, "DemoFile555.txt");
        values.put(MediaStore.Downloads.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS);

        Uri external = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }

        ContentResolver resolver = getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        Log.d("insertUri: " ,insertUri.toString());

        OutputStream os = null;
        try {
            if (insertUri != null) {
                os = resolver.openOutputStream(insertUri);
            }
            if (os != null) {
                String content = "測試喔3000";
                byte[] contentInBytes = content.getBytes();
                os.write(contentInBytes);
                os.close();
            }
        } catch (IOException e) {
            Log.d("fail: ", e.toString());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                Log.d("fail in close: " , e.toString());
            }
        }
    }


    void deleteStoragePrivateFile() {
        //File file = new File(getExternalFilesDir(null), "DemoFile.txt");
        //File file = new File(getCacheDir(), "DemoFile.txt");
        File file = new File(Environment.getExternalStoragePublicDirectory("Download"), "DemoFile2.txt");
        file.delete();
    }

    void createStoragePrivateFile() {
        //File file = new File(getExternalFilesDir(null), "DemoFile.txt");
        //File file = new File(getCacheDir(), "DemoFile.txt");
        File file = new File(Environment.getExternalStoragePublicDirectory("Download"), "DemoFile2.txt"); //要權限  已棄用 >=Q

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);
            String content = "測試喔3000";
            byte[] contentInBytes = content.getBytes();
            os.write(contentInBytes);
            os.close();
        } catch (IOException e) {
            Log.w("Storage", "Error writing " + file, e);
        }
    }
}