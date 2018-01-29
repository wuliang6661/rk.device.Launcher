package rk.device.launcher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import rk.device.launcher.ui.main.MainActivity;
import rk.device.launcher.ui.main.home.HomeActivity;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")){
			Toast.makeText(context,"升级了一个安装包",Toast.LENGTH_SHORT).show();
			
			Intent intent2 = new Intent(context, HomeActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
			
		}
	}
}
