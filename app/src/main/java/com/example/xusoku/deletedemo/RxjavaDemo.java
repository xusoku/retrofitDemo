package com.example.xusoku.deletedemo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xusoku on 2016/2/22.
 */
public class RxjavaDemo {
    //Demo 1
    public static void demo1() {

        //Observables（被观察者)
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext("Hello, world!");
                        sub.onCompleted();
                    }
                }
        );

        //Subscribers（观察者）
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        //事件绑定
        myObservable.subscribe(mySubscriber);

        /**
         * 简单写法
         */

        Observable.just("Hello, world!")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                    }
                });
    }
    //Demo 2 操作符（Operators）
    public static void demo2() {
//        map操作符，就是用来把把一个事件转换为另一个事件的
        Observable.just("Hello, world!")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s + " -Dan";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                    }
                });
        //把字符串转成整形
        Observable.just("Hello world!")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.hashCode();
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("demo2=="+Integer.toString(integer));
                    }
                });
        //进价--把字符串转成整形
        Observable.just("Hello world!")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return s.hashCode();
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer i) {
                        return Integer.toString(i);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String integer) {
                        System.out.println("demo2==" + (integer));
                    }
                });
    }
    //Demo 3
    public static void demo3() {
        Observable.just("just1", "just2", "just3")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("demo3==" + (s));
                    }
                });

        String [] sr={"just1","just2","just3","just4"};
        Observable.from(sr)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("demo3==" + (s));
                    }
                });

        ArrayList<String> strlist=new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            strlist.add("just"+i);
        }
        Observable.from(strlist)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("demo3==" + (s));
                    }
                });


    }
    //Demo 4
    public static void demo4() {
        //flatMap**把List事件变单个Observable处理
        Observable<ArrayList<String>> myObservable = Observable.create(
                new Observable.OnSubscribe<ArrayList<String>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<String>> sub) {
                        ArrayList<String> strlist=new ArrayList<>();
                        for (int i = 0; i < 4; i++) {
                            strlist.add("just"+i);
                        }
                        sub.onNext(strlist);
                        sub.onCompleted();
                    }
                }
        );
        myObservable
                .flatMap(new Func1<ArrayList<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(ArrayList<String> s) {
                        return Observable.from(s);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("demo4==" + (s));
                    }
                });
    }
    //Demo 5
    public static void demo5() {
        //filter()输出和输入相同的元素，并且会过滤掉那些不满足检查条件的。
        //take()输出最多指定数量的结果。
        Observable<ArrayList<String>> myObservables = getArrayListObservable();
        myObservables
                .flatMap(new Func1<ArrayList<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(ArrayList<String> s) {
                        return Observable.from(s);
                    }
                })
                .take(6)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        return getStringObservable(url);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("demo5==" + (s));
                    }
                });
    }
    @NonNull
    private static Observable<String> getStringObservable(final String url) {
        return Observable.create(
                    new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> sub) {
                            sub.onNext("转换后的"+url);
                            sub.onCompleted();
                        }
                    }
            );
    }
    @NonNull
    private static Observable<ArrayList<String>> getArrayListObservable() {
        return Observable.create(
                    new Observable.OnSubscribe<ArrayList<String>>() {
                        @Override
                        public void call(Subscriber<? super ArrayList<String>> sub) {
                             ArrayList<String>  strlist=new ArrayList<>();
                            strlist.add(null);
                            strlist.add("");
                            for (int i = 0; i < 13; i++) {
                                strlist.add("just"+i);
                            }
                            sub.onNext(strlist);
                            sub.onCompleted();
                        }
                    }
            );
    }

    //Demo 6
    public static void demo6() {
//        doOnNext()允许我们在每次输出一个元素之前做一些额外的事情
        Observable<ArrayList<String>> myObservables = getArrayListObservable();
        myObservables
                .flatMap(new Func1<ArrayList<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(ArrayList<String> s) {
                        return Observable.from(s);
                    }
                })
                .take(6)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        return getStringObservable(url);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        //做缓存处理
                        System.out.print(1 / 0);
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                });
    }
    //Demo 7
    public static void demo7() {

        //Demo6的变种
        Observable<ArrayList<String>> myObservables = getArrayListObservable();
        myObservables
                .flatMap(new Func1<ArrayList<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(ArrayList<String> s) {
                        return Observable.from(s);
                    }
                })
                .take(6)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        return getStringObservable(url);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        //做缓存处理
                    }
                })
                .subscribe(new Action1<String>() { //onNext
                    @Override
                    public void call(String s) {
                        System.out.println("Demo7  " + s);
                    }
                }, new Action1<Throwable>() { //onError
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }, new Action0() { //onCompleted
                    @Override
                    public void call() {
                        System.out.println("Demo7  " + "onCompleted");
                    }
                });
    }
    //Demo 8
    public static void demo8() {
        //自定义lift
        //lift 建议尽量使用已有的 lift() 包装方法（如 map() flatMap() 等）进行组合来实现需求，因为直接使用 lift() 非常容易发生一些难以发现的错误。
        Observable.just("aaa","ccc")
                .lift(new Observable.Operator<Integer, String>() {
                    @Override
                    public Subscriber<? super String> call(final Subscriber<? super Integer> subscriber) {
                        return new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onNext(String s) {
                                subscriber.onNext(s.hashCode());
                            }
                        };
                    }
                }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("Demo8  " + integer);
            }
        });
    }
    //Demo 9
    public static void demo9() {
        //compose() 方法
        Observable.just("aaa", "ccc")
                .compose(new Observable.Transformer<String, Integer>() {
                    @Override
                    public Observable<Integer> call(Observable<String> integerObservable) {
                        return integerObservable.lift(new Observable.Operator<Integer, String>() {
                            @Override
                            public Subscriber<? super String > call(final Subscriber<? super Integer> subscriber) {
                                return new Subscriber<String>() {
                                    @Override
                                    public void onCompleted() {
                                        subscriber.onCompleted();
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        subscriber.onError(e);
                                    }

                                    @Override
                                    public void onNext(String s) {
                                        subscriber.onNext(s.hashCode());
                                    }
                                };
                            }
                        });
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer string) {
                        System.out.println("Demo9  " + string);
                    }
                });
    }
    //Demo a
    public static void demoa() {


    }
    //Demo b
    public static void demob() {
    }
    //Demo c
    public static void democ() {
    }

}
