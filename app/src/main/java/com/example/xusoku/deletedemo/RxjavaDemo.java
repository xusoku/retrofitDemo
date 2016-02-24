package com.example.xusoku.deletedemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import model.ModelUtil;
import rx.Notification;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.FuncN;
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
        Observable.just("aaa", "ccc")
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
    public static void demoa(Context context,ApiService service) {

        // doOnSubscribe() 执行在 subscribe() 发生的线程；而如果在 doOnSubscribe() 之后有 subscribeOn() 的话，
        // 它将执行在离它最近的 subscribeOn() 所指定的线程。
      final ProgressDialog progressBar=new ProgressDialog(context);
        progressBar.setMessage("稍等...");
//        Observable.just("aaa")
        final Observable<ModelUtil.RespCinemaList> observable =service.getcinemalist("上海市", "长宁区", "", "");
        observable
                .subscribeOn(Schedulers.io())//其他线程
                .doOnSubscribe(new Action0() {
                    @Override

                    public void call() {
                        progressBar.show(); // 需要在主线程执行
                    }
                })
//                .subscribeOn(Schedulers.io())//其他线程
                .subscribeOn(AndroidSchedulers.mainThread()) // 指定主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ModelUtil.RespCinemaList>() {
                    @Override
                    public void call(ModelUtil.RespCinemaList s) {
                        progressBar.dismiss();
                        System.out.println("Demoa  " + s.cinemas.get(0).name);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        progressBar.dismiss();
                        throwable.printStackTrace();
                    }
                });

    }
    //Demo b
    public static void demob() {
        //lambda方式 整形转字符串
        Observable.just(2)
                .map(i -> i + "aa")
                .subscribe(s1 -> System.out.println(s1));

    }
    //Demo c
    static int i=10;
    public static void democ() {

        // defer操作符是直到有订阅者订阅时，才通过Observable的工厂方法创建Observable并执行，defer操作符能够保证Observable的状态是最新的
        Observable justObservable = Observable.just(i);
        i=12;
        Observable deferObservable = Observable.defer(()-> Observable.just(i));
        i=15;

        justObservable.subscribe((Action1) i -> System.out.println("just result:" + i));

        deferObservable.subscribe((Action1) o -> {
            System.out.println("defer result:" + o);
            System.out.println(6 / (2 * (1 + 2)) + "");
        });
    }
    //Demo d
    public static void demod() {
        //timer
        //隔两秒产生一个数字
        Observable
                .timer(2,TimeUnit.SECONDS)
                .subscribe((Action1) s->  System.out.println("demod  ooo"));

        Observable
                .interval(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Action1) s -> System.out.println("demod  ooo1"));
                //停止
//              .unsubscribe();


    }
    //Demo e
    public static void demoe() {
//        range操作符是创建一组在从n开始，个数为m的连续数字
        Observable.range(3, 3)
                .subscribe((Action1) s -> System.out.println("demoe=" + s));

//        repeat操作符是对某一个Observable，重复产生多次结果
//        repeatWhen操作符是对某一个Observable，有条件地重新订阅从而产生多次结果，
        Observable
                .range(3,3)
                .repeat(2)
                .subscribe((Action1) s -> System.out.println("demoe=" + s));

        //all判断Observable发射的所有的数据项是否都满足某个条件
        Observable.just(1)
                .all(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer.intValue() == 1;
                    }
                })
                .subscribe((Action1) s -> System.out.println("demoe=" + s));


        //  Amb操作符可以将至多9个Observable结合起来，让他们竞争。哪个Observable首先发射了数据（包括onError和onComplete)就会继续发射这个Observable的数据，其他的Observable所发射的数据都会被丢弃。
        Observable<Integer> delay3 = Observable.just(1, 2, 3).delay(3000, TimeUnit.MILLISECONDS);
        Observable<Integer> delay2 = Observable.just(4, 5, 6).delay(2000, TimeUnit.MILLISECONDS);
        Observable<Integer> delay1 = Observable.just(7, 8, 9).delay(1000, TimeUnit.MILLISECONDS);
        Observable.amb(delay1, delay2, delay3)
                .subscribe((Action1) s -> System.out.println("demoe=" + s));

//        Contains操作符用来判断源Observable所发射的数据是否包含某一个数据，如果包含会返回true，如果源Observable已经结束了却还没有发射这个数据则返回false。
//        IsEmpty操作符用来判断源Observable是否发射过数据，如果发射过就会返回false，如果源Observable已经结束了却还没有发射这个数据则返回true。
        Observable.just(1, 2, 3)
//                .isEmpty()
                .contains(1)
                .subscribe((Action1) s -> System.out.println("demoe=" + s));

//        defaultIfEmpty操作符会判断源Observable是否发射数据，如果源Observable发射了数据则正常发射这些数据，如果没有则发射一个默认的数据
        Observable.create(new Observable.OnSubscribe<Integer>() {
                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
//                            subscriber.onNext(3);  //是否发射数据
                            subscriber.onCompleted();
                        }
                    })
                .defaultIfEmpty(4)
                .subscribe((Action1) s -> System.out.println("demoe=" + s));

        // SequenceEqual操作符用来判断两个Observable发射的数据序列是否相同（发射的数据相同，数据的序列相同，结束的状态相同），如果相同返回true，否则返回false
        Observable.sequenceEqual(Observable.just(1, 2, 3), Observable.just(1, 2, 3))
                .subscribe(s->System.out.println("demoe=" + s));
    }
    //Demo f
    public static void demof() {

//        SkipUnitl是根据一个标志Observable来判断的，当这个标志Observable没有发射数据的时候，
//        所有源Observable发射的数据都会被跳过；当标志Observable发射了一个数据，则开始正常地发射数据
        Observable
                .interval(1, TimeUnit.SECONDS)
                .skipUntil(Observable.timer(3, TimeUnit.SECONDS))
                .subscribe(s -> System.out.println("demof1 =" + s));

//        SkipWhile则是根据一个函数来判断是否跳过数据，当函数返回值为true的时候则一直跳过源Observable发射的数据；当函数返回false的时候则开始正常发射数据。

        Observable.interval(1, TimeUnit.SECONDS).skipWhile(a -> a < 5)
                .subscribe(s -> System.out.println("demof2 =" + s));

//        TakeUntil和TakeWhile操作符可以说和SkipUnitl和SkipWhile操作符是完全相反的功能。
    }
    //Demo g
    public static void demog() {
//        DoOnTerminate会在Observable结束前触发回调，无论是正常还是异常终止；finallyDo会在Observable结束后触发回调，无论是正常还是异常终止。


//        Meterialize操作符将OnNext/OnError/OnComplete都转化为一个Notification对象并按照原来的顺序发射出来，而DeMeterialize则是执行相反的过程。
        Observable.just(1, 2, 3).materialize()
                .subscribe(new Subscriber<Notification<Integer>>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(Notification<Integer> integerNotification) {
                        System.out.println(integerNotification.getKind()+"  "+integerNotification.getValue());
                    }
                });

        Observable.just(1, 2, 3).materialize().dematerialize()
                .subscribe(i -> System.out.println("deMeterialize:" + i));

    }
    //Demo h
    public static void demoh() {


        Observable observable=Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i <= 3; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });

        // TimeInterval会拦截发射出来的数据，取代为前后两个发射两个数据的间隔时间。对于第一个发射的数据，其时间间隔为订阅后到首次发射的间隔。
        observable.timeInterval().subscribe((Action1) i -> System.out.println(i));
        // TimeStamp会将每个数据项给重新包装一下，加上了一个时间戳来标明每次发射的时间
        observable.timestamp() .subscribe((Action1) i -> System.out.println(i));

    }



    private static Observable<Integer> createObserver(int index) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 1; i < 3; i++) {
                    subscriber.onNext(i );
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    private static Observable<Integer> combineLatestObserver() {
        return Observable.combineLatest(createObserver(1), createObserver(2), (num1, num2) -> {
            System.out.println("left:" + num1 + " right:" + num2);
            return num1 + num2;
        });
    }

    static List<Observable<Integer>> list = new ArrayList<>();

    private  static Observable<Integer>  combineListObserver() {
        for (int i = 1; i < 3; i++) {
            list.add(createObserver(i));
        }
        return Observable.combineLatest(list, new FuncN<Integer>() {
            @Override
            public Integer call(Object... args) {
                int temp = 0;
                for (Object i : args) {
                    System.out.println("i=="+i);
                    temp += (Integer) i;
                    System.out.println("temp=="+temp);
                }
                return temp;
            }
        });
//        return Observable.combineLatest(list, args -> {
//            int temp = 0;
//            for (Object i : args) {
//                System.out.println("i=="+i);
//                temp += (Integer) i;
//                System.out.println("temp=="+temp);
//            }
//            return temp;
//        });
    }
    //Demo i
    public static void demoi() {

//        CombineLatest操作符可以将2~9个Observable发射的数据组装起来然后再发射出来。不过还有两个前提：
//        1.所有的Observable都发射过数据。
//        2.满足条件1的时候任何一个Observable发射一个数据，就将所有Observable最新发射的数据按照提供的函数组装起来发射出去。
        combineListObserver().subscribe(i -> System.out.println("combineList:" + i));

        combineLatestObserver().subscribe(i -> System.out.println("CombineLatest:" + i));
    }
    //Demo j
    public static void demoj() {
//        Merge操作符将多个Observable发射的数据整合起来发射，就如同是一个Observable发射的数据一样。
//          但是其发射的数据有可能是交错的，如果想要没有交错，可以使用concat操作符。
//        当某一个Observable发出onError的时候，merge的过程会被停止并将错误分发给Subscriber，
//          如果不想让错误终止merge的过程，可以使用MeregeDelayError操作符，会将错误在merge结束后再分发。
        Observable.merge(Observable.just(1, 2, 3), Observable.just(4, 5, 6))
                .subscribe(i -> System.out.println("merge:" + i));

        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6))
                .subscribe(i -> System.out.println("concat:" + i));



        Observable.mergeDelayError(Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    if (i == 3) {
                        subscriber.onError(new Throwable("error"));
                    }
                    subscriber.onNext(i);
                }
            }
        }), Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    subscriber.onNext(5 + i);
                }
                subscriber.onCompleted();
            }
        })).subscribe(i -> System.out.println("mergeDelayError:" + i));

    }


    private static Observable<String> createObserverzip(int index) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 1; i <= index; i++) {
                    System.out.println("zip:" + index + "-" + i);
                    subscriber.onNext(index + "-" + i);
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }
    //Demo k
    public static void demok() {
//        Zip操作符将多个Observable发射的数据按顺序组合起来，每个数据只能组合一次，而且都是有序的。最终组合的数据的数量由发射数据最少的Observable来决定。

//        createObserverzip(2).subscribe(i -> System.out.println("zip1:" + i));
//        createObserverzip(3).subscribe(i -> System.out.println("zip2:" + i));

        createObserverzip(2).zipWith(createObserver(3), (s, s2) -> s + "-" + s2)
                .subscribe(i -> System.out.println("zipWith:" + i));
    }
    //Demo l
    public static void demol() {
//        buffer将数据安装规定的大小做一下缓存，然后将缓存的数据作为一个集合发射出去
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).buffer(2, 3)
                .subscribe(i -> System.out.println("buffer:" + i));


        Observable.interval(1, TimeUnit.SECONDS).buffer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> System.out.println("buffer:" + i));

//        Window操作符类似于我们前面讲过的buffer，不同之处在于window发射的是一些小的Observable对象，
//          由这些小的Observable对象来发射内部包含的数据。同buffer一样，window不仅可以通过数目来分组还可以通过时间等规则来分组

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).window(3)
                .subscribe(i -> {
                    System.out.println(i);
                    i.subscribe((j -> System.out.println("window:" + j)));
                });

        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .window(3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> {
                    System.out.println(i);
                    i.subscribe((j -> System.out.println("window:" + j)));
                });

    }
    //Demo m
    public static void demom() {
//        Cast将Observable发射的数据强制转化为另外一种类型，属于Map的一种具体的实现
        class Animal {
            protected String name = "Animal";

            Animal() {
                System.out.println("create " + name);
            }

            String getName() {
                return name;
            }
        }

        class Dog extends Animal {
            Dog() {
                name = getClass().getSimpleName();
                System.out.println("create " + name);
            }
        }
        class Cat extends Animal  {
            Cat() {
               String name = getClass().getSimpleName();
                System.out.println("create " + name);
            }

            String getName() {
                return "cat";
            }
        }

        Observable.just(new Dog())
                .cast(Dog.class)
                .subscribe(i -> System.out.println("cast:" + i.getName()));

        // Scan操作符对一个序列的数据应用一个函数，并将这个函数的结果发射出去作为下个数据应用这个函数时候的第一个参数使用，有点类似于递归操作
        ArrayList<Integer> list=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(2);
        }
        Observable.from(list).scan((x, y) -> x * y).observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> System.out.println("cast:" + i));

        Observable.just(2, 3).scan((x, y) -> x * y).observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> System.out.println("cast:" + i));
    }
    //Demo n
    public static void demon() {
//        debounce操作符也可以使用时间来进行过滤，这时它跟throttleWithTimeOut使用起来是一样，
//          但是deounce操作符还可以根据一个函数来进行限流。这个函数的返回值是一个临时Observable，
//          如果源Observable在发射一个新的数据的时候，上一个数据根据函数所生成的临时Observable还没有结束，那么上一个数据就会被过滤掉。

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).debounce(integer -> {
            System.out.println(integer);
            return Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    if (integer % 2 == 0 && !subscriber.isUnsubscribed()) {
                        System.out.println("debounce:" + integer);
                        subscriber.onNext(integer);
                        subscriber.onCompleted();
                    }
                }
            });
        })
//         .observeOn(AndroidSchedulers.mainThread())
        .subscribe(i -> System.out.println("debounce:=" + i));

//        Distinct操作符的用处就是用来去重，非常好理解

        Observable.just(1, 2, 3, 4, 5, 4, 3, 2, 1).distinct()
                .subscribe(i -> System.out.println("distinct:=" + i));

//        distinctUntilChanged,是用来过滤掉连续的重复数据
        Observable.just(1, 2, 3, 3, 3, 1, 2, 3, 3).distinctUntilChanged()
                .subscribe(i -> System.out.println("distinctUntilChanged:=" + i));

//        ElementAt只会返回指定位置的数据，而Filter只会返回满足过滤条件的数据
        Observable.just(0, 1, 2, 3, 4, 5).elementAt(2)
                .subscribe(i -> System.out.println("elementAt:=" + i));

        Observable.just(0, 1, 2, 3, 4, 5).filter(i -> i < 3)
                .subscribe(i -> System.out.println("filter:=" + i));


//        First操作符只会返回第一条数据，并且还可以返回满足条件的第一条数据
//        与First相反，Last操作符只返回最后一条满足条件的数据。

        Observable.just(0, 1, 2, 3, 4, 5).first(i -> i > 1)
                .subscribe(i -> System.out.println("first:=" + i));

        Observable.just(0, 1, 2, 3, 4, 5).last(i -> i > 1)
                .subscribe(i -> System.out.println("last:=" + i));

//        BlockingObservable方法,这个方法不会对Observable做任何处理，只会阻塞住，当满足条件的数据发射出来的时候才会返回一个BlockingObservable对象。
//        可以使用Observable.toBlocking或者BlockingObservable.from方法来将一个Observable对象转化为BlockingObservable对象。
//        BlockingObservable可以和first操作符进行配合使用

        Integer s = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 5; i++) {
                    if (!subscriber.isUnsubscribed()) {
                        System.out.println("toBlocking:" + i);
                        subscriber.onNext(i);
                    }
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        }).toBlocking()
                .first(i -> i > 1);
        System.out.println("toBlocking =" + s);

    }
    //Demo o
    public static void demoo() {
//        Skip操作符将源Observable发射的数据过滤掉前n项，而Take操作符则只取前n项，理解和使用起来都很容易，但是用处很大。
//          另外还有SkipLast和TakeLast操作符，分别是从后面进行过滤操作。
        Observable.just(0, 1, 2, 3, 4, 5).skip(2)
                .subscribe(i -> System.out.println("skip:=" + i));
        Observable.just(0, 1, 2, 3, 4, 5).skipLast(2)
                .subscribe(i -> System.out.println("skipLast:=" + i));
        Observable.just(0, 1, 2, 3, 4, 5).take(2)
                .subscribe(i -> System.out.println("take:=" + i));
        Observable.just(0, 1, 2, 3, 4, 5).takeLast(2)
                .subscribe(i -> System.out.println("takeLast:=" + i));
    }
    //Demo p
    public static void demop() {
//        Sample操作符会定时地发射源Observable最近发射的数据，其他的都会被过滤掉，等效于ThrottleLast操作符，
//              而ThrottleFirst操作符则会定期发射这个时间段里源Observable发射的第一个数据

        Observable<Integer> createObserver= Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    for (int i = 0; i < 20; i++) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        subscriber.onNext(i);
                    }
                    subscriber.onCompleted();
                }
            });

        createObserver.sample(1000, TimeUnit.MILLISECONDS)
                .subscribe(i -> System.out.println("sample:=" + i));

        createObserver.throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(i -> System.out.println("throttleFirst:=" + i));

        createObserver.throttleLast(1000, TimeUnit.MILLISECONDS)
                .subscribe(i -> System.out.println("throttleLast:=" + i));
    }
    //Demo q
    public static void demoq() {

//        Retry操作符在发生错误的时候会重新进行订阅,而且可以重复多次，所以发射的数据可能会产生重复。如果重复指定次数还有错误的话就会将错误返回给观察者
        Observable<Integer> createObserver=Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    for (int i = 0; i < 3; i++) {
                        if (i == 2) {
                            subscriber.onError(new Exception("Exception-"));
                        } else {
                            subscriber.onNext(i);
                        }
                    }
                }
            });
        createObserver.retry(1)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("retry-onCompleted\n");
                    }
                    @Override
                    public void onError(Throwable e) {
                        System.out.println("retry-onError:" + e.getMessage());
                    }
                    @Override
                    public void onNext(Integer o) {
                        System.out.println("retry-onNext:" + o);
                    }
                });
//        RetryWhen操作符。当错误发生时，retryWhen会接收onError的throwable作为参数，并根据定义好的函数返回一个Observable，如果这个Observable发射一个数据，就会重新订阅。
        createObserver.retryWhen(observable -> observable.zipWith(Observable.just(1, 2, 3),
                (throwable, integer) -> throwable.getMessage() + integer)
                .flatMap(throwable -> {
                    System.out.println(throwable);
                    return Observable.timer(1, TimeUnit.SECONDS);
                }))
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("retry-onCompleted\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("retry-onError:" + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer o) {
                        System.out.println("retry-onNext:" + o.toString());
                    }
                });
    }

    //Demo r
    public static void demor() {

//        Reduce操作符应用一个函数接收Observable发射的数据和函数的计算结果作为下次计算的参数，输出最后的结果。
//          跟前面我们了解过的scan操作符很类似，只是scan会输出每次计算的结果，而reduce只会输出最后的结果
        Observable.just(1, 2, 3).reduce((x, y) -> x * y)
                .subscribe(i -> System.out.println("reduce:" + i));
        Observable.just(1, 2, 3).collect(() -> new ArrayList<>(), (integers, integer) -> integers.add(integer))
                .subscribe(i -> System.out.println("collect:" + i));;
    }
    //Demo s
    public static void demos() {


    }
    //Demot
    public static void demot() {}
}
