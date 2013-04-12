package xmu.ontheway.andsip.bean;

import java.io.Serializable;

import android.database.Cursor;

public class Contact implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String account;

	public static Contact fromCursor(Cursor cursor) {
		Contact contact = new Contact();
		contact.id = cursor.getInt(0);
		contact.name = cursor.getString(1);
		contact.account = cursor.getString(2);
		return contact;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
