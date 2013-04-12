package xmu.ontheway.andsip;

import xmu.ontheway.andsip.app.SipApplication;
import xmu.ontheway.andsip.bean.Contact;
import xmu.ontheway.andsip.util.UIUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("NewApi")
public class ActCall extends Activity {
	private static final String TAG = "ActCall";
	private Button btnEnd;
	private TextView tvStatus;

	private Contact mContact;

	public SipAudioCall call = null;
	public InComingCallReceiver callReceiver;
	private SipApplication mApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.act_call);
		mApplication = (SipApplication) this.getApplication();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initViews();

		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		mContact = (Contact) intent.getSerializableExtra("contact");
		if (mContact != null) {// 呼叫对方
			startCalling(mContact);
		} else {// 接受来电
			Log.e(TAG, "接收来电");
			answerIncomingCall(intent);
		}
	}

	private SipAudioCall.Listener callListener = new SipAudioCall.Listener() {
		@Override
		public void onCallBusy(SipAudioCall call) {
			updateStatus("对方正忙...");
		}

		@Override
		public void onCallEnded(SipAudioCall call) {
			updateStatus("通话结束");
			endCalling();
			finish();
		}

		@Override
		public void onCallEstablished(SipAudioCall call) {
			updateStatus("通话建立成功");
			Log.e(TAG, "通话建立成功...");
			call.startAudio();
			call.setSpeakerMode(true);
			if (call.isMuted()) {
				call.toggleMute();
			}
			updateStatus(call);
		}

		@Override
		public void onReadyToCall(SipAudioCall call) {
			updateStatus("onReadyToCall");
		}

		@Override
		public void onRingingBack(SipAudioCall call) {
			updateStatus("onRingingBack，等待对方接听...");
		}

		@Override
		public void onCallHeld(SipAudioCall call) {
			updateStatus("onCallHeldthe call is on hold");
		}

		@Override
		public void onError(SipAudioCall call, int errorCode,
				String errorMessage) {
			Log.e("TEST", "呼叫出错 " + errorCode + " " + errorMessage);
			updateStatus("onError呼叫出错:" + errorCode + " " + errorMessage);
			endCalling();
			ActCall.this.finish();
		}

		@Override
		public void onCalling(SipAudioCall call) {
			Log.e(TAG, "正在呼叫对方");
			updateStatus("正在呼叫对方");
			try {
				call.answerCall(30);
			} catch (SipException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onRinging(SipAudioCall call, SipProfile caller) {
			updateStatus("有新的电话进入...");
			Log.e(TAG, "有新的电话进入...");
			try {
				call.answerCall(30);
			} catch (SipException e) {
				Log.e(TAG, "接收来电出错---" + e.getMessage());
			}
		}
	};

	@SuppressLint("NewApi")
	private void startCalling(Contact contact) {
		if (!mApplication.isRegistered()) {
			UIUtils.showToast(this, "登录失败，无法呼叫...");
			finish();
			return;
		}
		Log.e("TEST", "注册成功,开始呼叫对方");

		try {
			call = mApplication.sipStartCalling(contact.getAccount(),
					callListener);
		} catch (SipException e) {
			Log.e(TAG, "呼叫对方失败 ----" + e.getMessage());
			endCalling();
		}
	}

	private void endCalling() {
		mApplication.sipEndCalling(call);
	}

	/**
	 * 接听电话
	 * 
	 * @param callIntent
	 */
	private void answerIncomingCall(Intent callIntent) {
		if (!SipManager.isVoipSupported(this)) {
			Log.e(TAG, "本设备不支持SIP");
			updateStatus("本设备不支持SIP");
			return;
		}
		Log.e(TAG, "answerIncomingCall");
		try {
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				@Override
				public void onRinging(SipAudioCall call, SipProfile caller) {
					try {
						call.answerCall(30);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			call = mApplication.getSipManager().takeAudioCall(callIntent,
					listener);
			Log.e(TAG, "开始接听电话");
			call.answerCall(30);
			call.startAudio();
			call.setSpeakerMode(true);
			if (call.isMuted()) {
				call.toggleMute();
			}
			updateStatus(call);
		} catch (SipException e) {
			Log.e(TAG, "answerIncomingCall接听错误 " + e.getMessage());
			mApplication.sipEndCalling(call);
			finish();
		}
	}

	public void updateStatus(final String status) {
		this.runOnUiThread(new Runnable() {
			public void run() {
				tvStatus.setText(status);
			}
		});
	}

	public void updateStatus(SipAudioCall call) {
		String useName = call.getPeerProfile().getDisplayName();
		if (useName == null) {
			useName = call.getPeerProfile().getUserName();
		}
		updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
	}

	private void initViews() {
		btnEnd = (Button) findViewById(R.id.call_btn_end);
		tvStatus = (TextView) findViewById(R.id.call_tv_status);

		btnEnd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				endCalling();
				finish();
			}
		});
	}
}
