package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xusoku on 2016/2/19.
 */
public class ModelUtil {
    /**
     * Http返回模型的基类
     */
    public static class BaseRespModel implements Serializable
    {
        /**
         * 返回码
         */
        public int code = 0;

        /**
         * 返回信息
         */
        public String message = "";
    }
    public static class RespCityList extends BaseRespModel
    {

        public ArrayList<CityRespModel> cities = null;
    }

    public static class CityRespModel implements Serializable
    {
        public String name = "";
        public ArrayList<String> districts = null;
    }





    public static class RespCinemaFilmPriceLists extends BaseRespModel
    {
        public ArrayList<CinemaFilmPriceListModel> films = new ArrayList<>();
    }
    public static class CinemaFilmPriceListModel
    {
        public String filmID = "";
        public String name = "";
        public String country = "";
        public String post = "";
        public String director = "";
        public String cast = "";
        public String dymIndex = "";
        public String releaseDate = "";
        public String stubIndex = "";
    }



    public static class RespCinemaList extends BaseRespModel
    {
        public ArrayList<CinemaRespModel> cinemas = null;
    }
    public static class CinemaRespModel
    {
        public String cinemaID = "";
        public String name = "";
        public String address = "";
        public String tel = "";
        public String longitude = "";
        public String latitude = "";
        public String minPrice = "";
        public String showingThisFilm = "";
    }
}
