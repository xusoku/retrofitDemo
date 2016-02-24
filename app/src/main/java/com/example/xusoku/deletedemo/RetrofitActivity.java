package com.example.xusoku.deletedemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import model.ModelUtil;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.schedulers.Schedulers;


public class RetrofitActivity extends AppCompatActivity {
    ApiService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api-test.dymfilm.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//
//
//            }
//        });

        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });
//        RetrofitDemo();
//        obser(service);
        RxjavaDemo.demor();
    }


    private void RetrofitDemo() {
        //        Call<ModelUtil.RespCityList> call=service.Repos();
//        call.enqueue(new Callback<ModelUtil.RespCityList>() {
//
//            @Override
//            public void onResponse(Response<ModelUtil.RespCityList> response, Retrofit retrofit) {
//                Log.e("aaa", response.body().cities.get(0).districts.toString());
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.e("aaa", t.toString());
//            }
//        });
//
//
//        Call<ModelUtil.RespCinemaFilmPriceLists> call1=service.getcinema("50");
//        call1.enqueue(new Callback<ModelUtil.RespCinemaFilmPriceLists>() {
//            @Override
//            public void onResponse(Response<ModelUtil.RespCinemaFilmPriceLists> response, Retrofit retrofit) {
//                Log.e("getcinema", response.body().films.get(0).name.toString());
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                Log.e("getcinema", throwable.toString());
//            }
//        });

        Call<ModelUtil.RespCinemaList> call1=service.getcinemalist1("上海市", "长宁区", "", "");
        call1.enqueue(new Callback<ModelUtil.RespCinemaList>() {
            @Override
            public void onResponse(Response<ModelUtil.RespCinemaList> response, Retrofit retrofit) {
                Log.e("getcinema", response.body().cinemas.get(0).name.toString());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("getcinema", throwable.toString());
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ModelUtil.RespCinemaList> call=service.getcinemalist1("上海市", "长宁区", "", "");
                    Response <ModelUtil.RespCinemaList> c=call.execute();
                    Log.e("getcinema", c.body().cinemas.get(0).name.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void obser(ApiService service) {

        final Observable<ModelUtil.RespCinemaList> observable =service.getcinemalist("上海市", "长宁区", "", "");
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ModelUtil.RespCinemaList>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getApplicationContext(),
                                "Completed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onNext(ModelUtil.RespCinemaList dessertItemCollectionDao) {
                        Toast.makeText(getApplicationContext(),
                                dessertItemCollectionDao.cinemas.get(0).name,
                                Toast.LENGTH_SHORT)
                                .show();
                        observable .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ModelUtil.RespCinemaList>() {
                            @Override
                            public void call(ModelUtil.RespCinemaList respCinemaList) {
                                Toast.makeText(getApplicationContext(),
                                        respCinemaList.cinemas.get(0).name,
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                })
        ;
    }

}
