package com.example.xusoku.deletedemo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;

public class AppPackageManager {

	public static final String TAG = "=AppManager=";

	public Context context;


	public static AppPackageManager appManager;

	public static AppPackageManager getInstance(Context context) {
		if (appManager == null) {
			appManager = new AppPackageManager(context);
		}
		return appManager;
	}

	public AppPackageManager(Context context) {
		super();
		this.context = context;

	}


	/** * 系统总内存 */
	public long getTotalMemory() {
		try {
			FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String totalInfo = br.readLine();
			StringBuffer sb = new StringBuffer();
			for (char c : totalInfo.toCharArray()) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			long bytesize = Long.parseLong(sb.toString()) * 1024;
			return bytesize;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/** * 运行进程总个数 */
	public int getRunningPocessCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcessInfos = am
				.getRunningAppProcesses();
		int count = runningAppProcessInfos.size();
		return count;
	}

	/**
	 * 获取可用内存大小
	 */
	public long getAvailMemory(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem;
	}

	/**
	 * 清理进程 释放运行内存
	 */
	public void killRunningPocess(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
		List<ActivityManager.RunningServiceInfo> serviceInfos = am
				.getRunningServices(100);
		int count = 0;
		if (infoList != null) {
			for (int i = 0; i < infoList.size(); ++i) {
				RunningAppProcessInfo appProcessInfo = infoList.get(i);
				// importance 该进程的重要程度 分为几个级别，数值越低就越重要。
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
				// 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
				if (appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					String[] pkgList = appProcessInfo.pkgList;
					for (int j = 0; j < pkgList.length; ++j) {
						if(!pkgList[j].equals("com.kanke.control.phone")){
							am.killBackgroundProcesses(pkgList[j]);
							Log.i(TAG+"  kill————:" , pkgList[j]);
							count++;
						}
					}
				}
			}
		}
	}

	public void killMe(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		am.killBackgroundProcesses(context.getPackageName());

//		String str=context.getPackageName();
//		try {
//			Method forceStopPackage =PackageManager.class.getDeclaredMethod("forceStopPackage", String.class);
//			forceStopPackage.setAccessible(true);
//			forceStopPackage.invoke(am, context.getPackageName());
//		}
//		catch (Exception e) {
//
//			e.printStackTrace();
//		}

		try {
		Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
		method.invoke(am, context.getPackageName());  //packageName是需要强制停止的应用程序包名
		}catch (Exception e) {

			e.printStackTrace();
		}
	}

	/** * 获取应用缓存大小 */
	public void getAppCachSize() {
		countCachSize = 0;
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			getCacheSize(context.getPackageManager(), packs.get(i));
		}
	}

	private void getCacheSize(PackageManager pm, PackageInfo info) {
		try {
			Method method = PackageManager.class.getDeclaredMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			method.invoke(pm, info.packageName, new MyPackObserver(info, pm));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	long countCachSize = 0;

	private class MyPackObserver extends
			android.content.pm.IPackageStatsObserver.Stub {
		private PackageInfo info;
		private PackageManager pm;

		public MyPackObserver(PackageInfo info, PackageManager pm) {
			this.info = info;
			this.pm = pm;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cachesize = pStats.cacheSize;
			Log.i(TAG+" pStats:",pStats.packageName+"  "+cachesize);
				if (cachesize > 0) {
				countCachSize += cachesize;
				CacheInfo cacheInfo = new CacheInfo();
				cacheInfo.cachesize = cachesize;
				cacheInfo.packname = info.packageName;
				cacheInfo.appname = info.applicationInfo.loadLabel(pm)
						.toString();
				cacheInfo.icon = info.applicationInfo.loadIcon(pm);
				Log.i(TAG+" clearCach:",cacheInfo.packname+"--"+succeeded);
			}
		}

	}

	/*** 清除全部缓存 */
	public void cleanAllCach() {
		PackageManager pm = context.getPackageManager();
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Integer.MAX_VALUE,new ClearCacheObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

	class ClearCacheObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(final String packageName,
				final boolean succeeded) {
			Log.i(TAG+" cleanAllCach————:",packageName+"——"+succeeded);
		}
	}

	/*** 判断应用是否运行 */
	public boolean isAppRunable(String packName) {
		boolean isRunable = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(packName)
					&& info.baseActivity.getPackageName().equals(packName)) {
				isRunable = true;
				break;
			}
		}
		return isRunable;
	}


	class CacheInfo {

		public String packname;

		public String appname;

		public long cachesize;

		public Drawable icon;

	}
}
