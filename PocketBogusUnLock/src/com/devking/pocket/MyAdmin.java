package com.devking.pocket;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyAdmin extends DeviceAdminReceiver {

	@Override
	public void onDisabled(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onDisabled(context, intent);
		Toast.makeText(context, context.getString(R.string.app_name)+",已经成功禁用", Toast.LENGTH_LONG).show();
	}
	
	
}
