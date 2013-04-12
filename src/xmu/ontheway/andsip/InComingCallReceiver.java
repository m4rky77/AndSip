package xmu.ontheway.andsip;

import xmu.ontheway.andsip.app.SipApplication;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipManager;
import android.util.Log;

@SuppressLint("NewApi")
public class InComingCallReceiver extends BroadcastReceiver {
	private static final String TAG = "InComingCallReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "新的电话进入！");
		if (SipManager.isIncomingCallIntent(intent)) {
			SipApplication mApplication = (SipApplication) context;
			mApplication.sipAnswerIncomingCall(intent);
		}
	}
}
