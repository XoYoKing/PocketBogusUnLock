package com.devking.pocket;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PocketServices extends Service {

	Sensor sensor;
	private float x;

	private String TAG = "wangjing";
	private DevicePolicyManager dpm;
	private boolean isAdminActive;
	private ComponentName componentName;
	private SensorManager sensorManager;
	private NotificationManager nm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onStart Services!");
		super.onStart(intent, startId);
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mBatInfoReceiver, filter);
		dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		componentName = new ComponentName(this, MyAdmin.class);
		isAdminActive = dpm.isAdminActive(componentName);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sensorManager.registerListener(lsn, sensor,
				SensorManager.SENSOR_DELAY_GAME);
		sendBroadcast(new Intent("com.devking.proximity.run"));
	}
	
	private SensorEventListener lsn = new SensorEventListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onSensorChanged(SensorEvent event) {
			x = event.values[SensorManager.DATA_X];
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
//				Log.d(TAG, "-----------------screen is on...");
//				Log.d(TAG, "-----------------screen is on..., X = "+x);
				if (x < 5) {
					if (isAdminActive) {
						Log.v(TAG, "lockNow");
						dpm.lockNow();
					}
					else{
						Notification notification = new Notification(R.drawable.ic_launcher, "您的口袋防误触应用还未激活，请先激活", System.currentTimeMillis());
						notification.flags = Notification.FLAG_AUTO_CANCEL;
						Intent notificationIntent = new Intent(context,MainActivity.class);  
				        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,0 );  
				        notification.setLatestEventInfo(context, context.getString(R.string.app_name), "您的口袋防误触应用还未激活，请先激活", contentIntent);
				        nm.notify(0,notification); 
					}
				}
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
//				Log.d(TAG, "----------------- screen is off...");
//				Log.d(TAG, "-----------------screen is off..., X = "+x);
				isAdminActive = dpm.isAdminActive(componentName);
			}
		}
	};

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDestroy Services!");
		super.onDestroy();
		sensorManager.unregisterListener(lsn, sensor);
		unregisterReceiver(mBatInfoReceiver);
		stopSelf();
		sendBroadcast(new Intent("com.devking.proximity.stop"));
	}

}
