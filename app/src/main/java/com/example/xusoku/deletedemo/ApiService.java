package com.example.xusoku.deletedemo;

import model.ModelUtil;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by xusoku on 2016/2/19.
 */
public interface ApiService {
    @GET("/ticket/lbs/info")
    Call<ModelUtil.RespCityList> Repos();

//    http://api-test.dymfilm.com/cinema/50/film/order/list
    @GET("/cinema/{cinema}/film/order/list")
    Call<ModelUtil.RespCinemaFilmPriceLists> getcinema(@Path("cinema") String cinema);

//    http://api-test.dymfilm.com/cinema/list?city=上海市&district=长宁区&lng=&lat=

    @GET("/cinema/list")
    Call<ModelUtil.RespCinemaList> getcinemalist1(@Query("city") String city,@Query("district") String district,@Query("lng") String lng,@Query("lat") String lat);

    @GET("/cinema/list")
    Observable<ModelUtil.RespCinemaList> getcinemalist(@Query("city") String city,@Query("district") String district,@Query("lng") String lng,@Query("lat") String lat);
}