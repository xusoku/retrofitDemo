package com.example.xusoku.deletedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteFileotherPackage();

        startActivity(new Intent(this, RetrofitActivity.class));
    }

    private void deleteFileotherPackage() {
        try {
            Process process = Runtime.getRuntime().exec("su");

            OutputStream os = process.getOutputStream();

            os.write("rm -rf /data/data/cn.kejin.android/a \n".getBytes());

            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void killMe(View view){
        AppPackageManager.getInstance(this).killMe(this);

    }
    public void killRunningPocess(View view){
        AppPackageManager.getInstance(this).killRunningPocess(this);

    }
    public void getAppCachSize(View view){
        AppPackageManager.getInstance(this).getAppCachSize();

    }
    public void getAvailMemory(View view){
      long a=  AppPackageManager.getInstance(this).getAvailMemory(this);
        Toast.makeText(this, "" + a, Toast.LENGTH_SHORT).show();


    }
    public void getRunningPocessCount(View view){
        int a=AppPackageManager.getInstance(this).getRunningPocessCount(this);
        Toast.makeText(this, "" + a, Toast.LENGTH_SHORT).show();

    }
    public void getTotalMemory(View view){
        long a =AppPackageManager.getInstance(this).getTotalMemory();

        Toast.makeText(this, "" + a, Toast.LENGTH_SHORT).show();

    }
    public void cleanAllCach(View view){
        AppPackageManager.getInstance(this).cleanAllCach();
    }
    public void toActivity(View view){
        startActivity(new Intent(this, RetrofitActivity.class));
    }
}
