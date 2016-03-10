# retrofitDemo

#Retrofit 
```java

```

# RxDemo 

# 缓存清理






 - 	[PriceActivityNew比价](#jump)
 - 	[CinemaActivityNew城市选择列表](#jump2)
 - 	[SearchActivity搜索页面](#jump3)
 - 	[SearchResultActivity搜索结果页面](#jump4)



## <span id="jump">PriceActivityNew比价</span>

 `PriceActivityNew.java` 和`PriceFragment_New.java`逻辑和代码一样，区别只是主页(`PriceFragment_New`)和二级页面(`PriceActivityNew`)的区别。

此页面的逻辑是：首先加载影片的数据，影片数据加载完成后定位当前位置最近的影院。选择第一个影院，当前影片在该影院七天的场次。

- `setLayoutView()` 设置`Activity`的布局。

- `initVariable()` 初始化数据，接受传过来的参数。

- `findViews()` 找出控件和初始化控件。

- `setListener()` 设置控件的监听事件。

- `initData()`所有数据开始加载的方法。下拉刷新也调用此方法。

- `init()`从接口加载`Gallery`数据方法。

- `initPosterFilm()` `Gallery` 数据加载好后刷新数据的方法，并且定位所选的影片，触发`Gallery`的`Selection`方法。

- `initLocal()` 获取定位当前位置的第一个影院。

- `initCinemaFristData()`从接口获取当前位置的一个影院.

- `onActivityResult()` 当换影院的时候，点击切换回传影院的参数在这处理。

- `MyHandler`当影片和影院都加载好就触发`Handler`加载当天的场次。

- `InitTitlePager()` 初始化七天日期显示的布局和`RecyclerView` 

- `initprceData()` 从接口加载七天的场次，`HashMap`做缓存。	[PriceActivityNew比价](#jump)
- 
























	[PriceActivityNew比价](#jump)
