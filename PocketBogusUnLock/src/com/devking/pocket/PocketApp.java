package com.devking.pocket;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class PocketApp extends Application {
	static IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
	static IntentFilter filter2 = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
	static MyBroadcastReceiver receiver;
	public static PocketApp appInstance;
	Intent i = new Intent("com.devking.service.proximityService");

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.v("wangjing", "ProximityApp OnCreat!");
		super.onCreate();
		startService(i);
		receiver = new MyBroadcastReceiver();
		appInstance = this;
		registerReceiver(receiver, filter);
		registerReceiver(receiver, filter2);
	}

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
				boolean isServiceRunning = false;
				ActivityManager manager = (ActivityManager) getApplicationContext()
						.getSystemService(Context.ACTIVITY_SERVICE);
				for (RunningServiceInfo service : manager
						.getRunningServices(Integer.MAX_VALUE)) {
					if ("com.devking.pocket.PocketServices"
							.equals(service.service.getClassName())) {
						isServiceRunning = true;
					}
				}
				if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
					context.startService(i);
				}
				if (!isServiceRunning) {
					context.startService(i);
				}
		}

	}

	public static void saveStateRunning(){
		appInstance.registerReceiver(receiver, filter);
		appInstance.registerReceiver(receiver, filter2);
	}
	
	public static void cancelStateRunning() {
		appInstance.unregisterReceiver(receiver);
	}

}
