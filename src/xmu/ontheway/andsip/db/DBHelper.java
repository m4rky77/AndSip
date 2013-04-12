package xmu.ontheway.andsip.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper {
	private SQLiteOpenHelper openHelper;
	private SQLiteDatabase db;
	private Context context;

	public static final String TBL_CONTACT = "contact";
	public static final String KEY_CONTACT_NAME = "name";
	public static final String KEY_CONTACT_ACCOUNT = "account";

	public DBHelper(Context context) {
		this.context = context;
		openHelper = new DBOpenHelper(context);
	}

	public void open() {
		if (db == null || !db.isOpen()) {
			db = openHelper.getWritableDatabase();
		}
	}

	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public Cursor readContacts() {
		return db.query(TBL_CONTACT, null, null, null, null, null, null);
	}

	public long insert(String tblName, ContentValues values) {
		return db.insert(tblName, null, values);
	}

	public long delete(String tblName, int id) {
		String args[] = { id + "" };
		return db.delete(tblName, "_id=?", args);
	}

	private class DBOpenHelper extends SQLiteOpenHelper {
		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "andsip";

		public DBOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql_contact = "CREATE TABLE if not exists " + TBL_CONTACT
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ KEY_CONTACT_NAME + " TEXT NOT NULL, "
					+ KEY_CONTACT_ACCOUNT + " TEXT NOT NULL)";
			Log.e("TEST", sql_contact);

			db.execSQL(sql_contact);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

	}
}
