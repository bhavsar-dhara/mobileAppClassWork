package edu.neu.madcourse.dharabhavsar.utils.gcmcomm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("GcmBroadcastReceiver", "alert text" + CommunicationConstants.alertText);
		if(CommunicationConstants.alertText.equals("Message Notification") || CommunicationConstants.alertText.equals("Scraggle Word Game")) {
			Log.e("GcmBroadcastReceiver", "matching alert text");
			ComponentName comp = new ComponentName(context.getPackageName(),
					GcmIntentService.class.getName());
			startWakefulService(context, (intent.setComponent(comp)));
		}
		setResultCode(Activity.RESULT_OK);
	}
}
