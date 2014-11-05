package com.devking.pocket;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class MainActivity extends ActionBarActivity {
	Sensor sensor;

	private Switch serviceButton;
	private CheckBox mCheckBox;
	ComponentName componentName;
	DevicePolicyManager dpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		serviceButton = (Switch) findViewById(R.id.main_service_sth);
		mCheckBox = (CheckBox) findViewById(R.id.main_checkbtn);
		mCheckBox.setOnCheckedChangeListener(changeListener);
		dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		if (isWorked()) {
			serviceButton.setChecked(true);
		} else {
			serviceButton.setChecked(false);;
		}
		registerReceiver(myBroadcastReceiver, new IntentFilter("com.devking.proximity.run"));
		registerReceiver(myBroadcastReceiver, new IntentFilter("com.devking.proximity.stop"));
		serviceButton.setOnClickListener(serviceBtnClickListener);
		componentName = new ComponentName(this, MyAdmin.class); 
	}

	private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				PocketApp.saveStateRunning();
			}else {
				PocketApp.cancelStateRunning();
			}
		}
	};
	
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("com.devking.proximity.run")) {
				serviceButton.setChecked(true);
			}
			if (intent.getAction().equals("com.devking.proximity.stop")) {
				serviceButton.setChecked(false);
			}
		}
	};
	
	/*
	 * 防误触功能开关监听器
	 * 
	 */
	private OnClickListener serviceBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent("com.devking.service.proximityService");
			if (isWorked()) {
				stopService(i);
				serviceButton.setChecked(false);
			} else {
				startService(i);
				serviceButton.setChecked(true);
			}
		}

	};

	// 判断自己些的一个Service是否已经运行
	public boolean isWorked() {
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
		return isServiceRunning;
	}
	
	
	public void onActivate(View v){
		activeManage();
	}
	
	/**
	 * 激活设备管理权限
	 * 成功执行激活时，DeviceAdminReceiver中的 onEnabled 会响应
	 */
	private void activeManage() {
		// 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		
		//权限列表
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
        
        //描述(additional explanation)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "------ 其他描述 ------");
        
        startActivityForResult(intent, 0);
	}
	
	public void onUnActivate(View v){
		unActiveManage();
	}
	
	 /** 
     * 禁用设备管理权限 
     * 成功执行禁用时，DeviceAdminReceiver中的 onDisabled 会响应 
     */  
    private void unActiveManage() {  
        boolean active = dpm.isAdminActive(componentName);  
        if (active) {  
            dpm.removeActiveAdmin(componentName);  
        }
    }
	
}
