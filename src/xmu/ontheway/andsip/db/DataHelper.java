package xmu.ontheway.andsip.db;

import java.util.ArrayList;

import xmu.ontheway.andsip.bean.Contact;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DataHelper {
	public static boolean addContact(Context context, ContentValues values) {
		DBHelper dbHelper = new DBHelper(context);
		dbHelper.open();
		long result = dbHelper.insert(DBHelper.TBL_CONTACT, values);
		dbHelper.close();
		if (result > 0) {
			return true;
		}
		return false;
	}

	public static ArrayList<Contact> readContact(Context context) {
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		DBHelper dbHelper = new DBHelper(context);
		dbHelper.open();
		Cursor cursor = dbHelper.readContacts();
		Contact contact;
		cursor.moveToFirst();
		while (cursor.moveToNext()) {
			contact = Contact.fromCursor(cursor);
			contacts.add(contact);
		}
		dbHelper.close();
		return contacts;
	}

	public static boolean deleteContact(Context context, int id) {
		DBHelper dbHelper = new DBHelper(context);
		dbHelper.open();
		long result = dbHelper.delete(DBHelper.TBL_CONTACT, id);
		dbHelper.close();
		if (result > 0) {
			return true;
		}
		return false;
	}
}
