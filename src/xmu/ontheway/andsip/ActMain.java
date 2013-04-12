package xmu.ontheway.andsip;

import xmu.ontheway.andsip.app.SipApplication;
import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressLint("NewApi")
public class ActMain extends TabActivity {
	private static final String TAG = "ActMain";

	private TabHost mTabHost;
	private ProgressBar progressBar;
	private Button btnLogin;
	private TextView tvStatus;
	private View head;

	private SipApplication mApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (SipApplication) this.getApplication();
		setContentView(R.layout.act_main);
		initViews();

		startRegistration();
	}

	/**
	 * 开始向Sip服务器注册用户信息
	 */
	private void startRegistration() {
		if (!mApplication.isSipAvailable()) {
			tvStatus.setText("抱歉，系统不支持此 Sip通话");
			return;
		}
		SipRegistrationListener registrationListener = new SipRegistrationListener() {
			@Override
			public void onRegistrationFailed(String localProfileUri,
					int errorCode, String errorMessage) {
				updateRegisterStatus(-1);
			}

			@Override
			public void onRegistrationDone(String localProfileUri,
					long expiryTime) {
				updateRegisterStatus(1);
			}

			@Override
			public void onRegistering(String localProfileUri) {
				updateRegisterStatus(0);
			}
		};
		mApplication.sipRegister(registrationListener);
	}

	/**
	 * 更新注册状态
	 * 
	 * @param status
	 *            [0 正在注册,1注册成功,其他 注册失败]
	 */
	private void updateRegisterStatus(final int status) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (status == 0) {// 正在注册
					mApplication.setRegistered(false);
					mTabHost.setClickable(false);
					progressBar.setVisibility(View.VISIBLE);
					btnLogin.setVisibility(View.INVISIBLE);
					tvStatus.setText("正在登录...");
				} else if (status == 1) {// 注册成功
					mTabHost.setVisibility(View.VISIBLE);
					mApplication.setRegistered(true);
					mTabHost.setClickable(true);
					head.setVisibility(View.GONE);
					tvStatus.setText("登录成功");
				} else {// 注册失败
					mApplication.setRegistered(false);
					mTabHost.setClickable(false);
					progressBar.setVisibility(View.INVISIBLE);
					btnLogin.setVisibility(View.VISIBLE);
					tvStatus.setText("登录失败");
				}
			}
		});
	}

	private void initViews() {
		head = findViewById(R.id.main_head);
		progressBar = (ProgressBar) head.findViewById(R.id.main_progressbar);
		btnLogin = (Button) head.findViewById(R.id.main_btn_login);
		tvStatus = (TextView) head.findViewById(R.id.main_register_status);

		// mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		// mTabHost.setup(this.getLocalActivityManager());

		mTabHost = this.getTabHost();

		Resources res = getResources();
		mTabHost.addTab(mTabHost.newTabSpec("record")
				.setIndicator("通话记录", res.getDrawable(R.drawable.img_call))
				.setContent(new Intent(this, ActRecord.class)));

		mTabHost.addTab(mTabHost.newTabSpec("contact")
				.setIndicator("通讯录", res.getDrawable(R.drawable.img_contacts))
				.setContent(new Intent(this, ActContact.class)));

		// mTabHost.addTab(mTabHost.newTabSpec("message").setIndicator("Message")
		// .setContent(new Intent(this, ActMessage.class)));
		//
		// mTabHost.addTab(mTabHost.newTabSpec("settings")
		// .setIndicator("Settings")
		// .setContent(new Intent(this, ActSettings.class)));

		mTabHost.setCurrentTab(0);

		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startRegistration();
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Settings");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent settingsActivity = new Intent(getBaseContext(),
					ActSettings.class);
			startActivity(settingsActivity);
			break;
		}
		return true;
	}
}
