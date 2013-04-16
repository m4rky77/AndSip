package xmu.ontheway.andsip.app;

import xmu.ontheway.andsip.ActCall;
import xmu.ontheway.andsip.InComingCallReceiver;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressLint("NewApi")
public class SipApplication extends Application {
	private static final String TAG = "SipApplication";

	private SipManager mSipManager = null;
	private SipProfile mSipProfile = null;
	private InComingCallReceiver callReceiver;
	private boolean isRegistered = false;

	public boolean isSipAvailable() {
		if (!SipManager.isApiSupported(this)) {
			Log.e(TAG, "this API cannot be surpported");
			return false;
		}
		if (!SipManager.isVoipSupported(this)) {
			Log.e(TAG, "your system don't support SIP-based VOIP API");
			return false;
		}
		Log.e(TAG, "Great ,you can use SIP !!!");
		return true;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (mSipManager == null) {
			mSipManager = SipManager.newInstance(this);
		}
		registerCallReceiver();

		//
	}

	@Override
	public void onTerminate() {
		closeLocalProfile();
		if (callReceiver != null) {
			this.unregisterReceiver(callReceiver);
		}
		super.onTerminate();
	}

	/**
	 * 监测来电
	 */
	private void registerCallReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.andSip.INCOMING_CALL");
		callReceiver = new InComingCallReceiver();
		this.registerReceiver(callReceiver, filter);
		Log.e(TAG, "注册callReceiver");
	}

	/**
	 * 向Sip服务器发起注册请求
	 * 
	 * @param registrationListener
	 */
	public void sipRegister(SipRegistrationListener registrationListener) {
		if (mSipManager == null) {
			mSipManager = SipManager.newInstance(this);
			if (mSipManager == null) {
				Log.e(TAG, "mSipManager is NULL");
				return;
			}
		}
		closeLocalProfile();
		//
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String username = prefs.getString("namePref", "");
		String domain = prefs.getString("domainPref", "");
		String password = prefs.getString("passPref", "");

		try {
			SipProfile.Builder builder = new SipProfile.Builder(username, domain);
			builder.setPassword(password);
			mSipProfile = builder.build();
			Log.e(TAG, "profile " + mSipProfile.getUriString());

			Intent i = new Intent();
			i.setAction("android.andSip.INCOMING_CALL");
			PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
			mSipManager.open(mSipProfile, pi, null);
			//
			mSipManager.setRegistrationListener(mSipProfile.getUriString(), registrationListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过Sip服务器呼叫对方，建立通话
	 * 
	 * @param dest被叫方Sip账号
	 * @param listener呼叫监听器
	 * @return
	 * @throws SipException
	 */
	public SipAudioCall sipStartCalling(String dest, SipAudioCall.Listener listener) throws SipException {
		return mSipManager.makeAudioCall(mSipProfile.getUriString(), dest, listener, 30);
	}

	public void sipAnswerIncomingCall(Intent callIntent) {
		Intent answerIntent = new Intent(this, ActCall.class);
		answerIntent.putExtras(callIntent);
		answerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
		this.startActivity(answerIntent);
	}

	/**
	 * 结束通话
	 * 
	 * @param call
	 * @throws SipException
	 */
	public void sipEndCalling(SipAudioCall call) {

		if (call != null) {
			try {
				call.endCall();
				call.close();
			} catch (SipException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 关闭Sip会话
	 * 
	 * @throws SipException
	 */
	public void closeLocalProfile() {
		try {
			if (mSipProfile != null) {
				mSipManager.close(mSipProfile.getUriString());
			}
		} catch (SipException e) {
			e.printStackTrace();
		}
	}

	public SipManager getSipManager() {
		return mSipManager;
	}

	public SipProfile getSipProfile() {
		return mSipProfile;
	}

	public void setSipManager(SipManager mSipManager) {
		this.mSipManager = mSipManager;
	}

	public void setSipProfile(SipProfile mSipProfile) {
		this.mSipProfile = mSipProfile;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

}
