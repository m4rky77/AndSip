package xmu.ontheway.andsip;

import java.util.ArrayList;

import xmu.ontheway.andsip.bean.Contact;
import xmu.ontheway.andsip.db.DataHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ActContact extends Activity implements View.OnClickListener {
	private static final String TAG = "FragmentContact";
	private ImageButton btnAdd;
	private ListView lvContact;

	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private ContactListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.act_contact);
		initViews();
	}

	private void initViews() {
		btnAdd = (ImageButton) findViewById(R.id.btn_add_contact);
		lvContact = (ListView) findViewById(R.id.listview_contact);

		getContacts();
		btnAdd.setOnClickListener(this);
		lvContact
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.add(0, 0, 0, "删除联系人");
					}
				});
		lvContact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Contact contact = (Contact) adapter.getItem(position);
				Intent intent = new Intent(ActContact.this, ContactDetail.class);
				intent.putExtra("contact", contact);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == 0) {
			Contact contact = (Contact) adapter.getItem(menuInfo.position);
			if (DataHelper.deleteContact(this, contact.getId())) {
				contacts.remove(menuInfo.position);
				adapter.notifyDataSetChanged();
			}
		}
		return super.onContextItemSelected(item);
	}

	private void getContacts() {
		contacts = DataHelper.readContact(this);
		adapter = new ContactListAdapter();
		lvContact.setAdapter(adapter);
	}

	private void startCall(Contact contact) {
		Intent intent = new Intent(this, ActCall.class);
		intent.putExtra("contact", contact);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		if (v == btnAdd) {
			startActivityForResult(new Intent(this, AddContact.class), 0x01);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x01) {
			if (resultCode == RESULT_OK) {
				getContacts();
			}
		}
	}

	private class ContactListAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		private class ListViewItem {
			public TextView _name;
			public TextView _account;
			public Button _call;
		}

		public ContactListAdapter() {
			inflater = LayoutInflater.from(ActContact.this);
		}

		@Override
		public int getCount() {
			return contacts.size();
		}

		@Override
		public Object getItem(int position) {
			return contacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ListViewItem listViewItem = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listitem_contact, null);
				listViewItem = new ListViewItem();
				listViewItem._name = (TextView) convertView
						.findViewById(R.id.contact_item_name);
				listViewItem._account = (TextView) convertView
						.findViewById(R.id.contact_item_account);
				listViewItem._call = (Button) convertView
						.findViewById(R.id.contact_item_call);
				// 设置控件集
				convertView.setTag(listViewItem);
			} else {
				listViewItem = (ListViewItem) convertView.getTag();
			}

			final Contact contact = contacts.get(position);
			Log.e(TAG,
					"contact " + contact.getName() + " " + contact.getAccount()
							+ " " + contact.getId());
			listViewItem._name.setText(contact.getName());
			listViewItem._account.setText(contact.getAccount());
			listViewItem._call.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startCall(contact);
				}
			});
			return convertView;
		}
	}
}
