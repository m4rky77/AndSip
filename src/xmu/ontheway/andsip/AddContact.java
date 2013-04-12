package xmu.ontheway.andsip;

import xmu.ontheway.andsip.db.DBHelper;
import xmu.ontheway.andsip.db.DataHelper;
import xmu.ontheway.andsip.util.UIUtils;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddContact extends Activity implements OnClickListener {
	private EditText etAccount;
	private EditText etName;
	private Button btnSave;
	private Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_add_contact);
		initViews();
	}

	private void initViews() {
		etName = (EditText) findViewById(R.id.addcontact_name);
		etAccount = (EditText) findViewById(R.id.addcontact_account);
		btnSave = (Button) findViewById(R.id.addcontact_save);
		btnCancel = (Button) findViewById(R.id.addcontact_cancel);

		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}

	private boolean saveContact() {
		String name = etName.getText().toString().trim();
		String account = etAccount.getText().toString().trim();
		if (name.equals("") || account.equals("")) {
			UIUtils.showToast(this, "输入值不能为空...");
			return false;
		}
		ContentValues values = new ContentValues();
		values.put(DBHelper.KEY_CONTACT_NAME, name);
		values.put(DBHelper.KEY_CONTACT_ACCOUNT, account);
		return DataHelper.addContact(this, values);
	}

	@Override
	public void onClick(View v) {
		if (v == btnSave) {
			if (saveContact()) {
				setResult(RESULT_OK);
				finish();
			}
		} else if (v == btnCancel) {
			finish();
		}
	}
}
