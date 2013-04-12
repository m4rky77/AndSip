package xmu.ontheway.andsip.util;

import xmu.ontheway.andsip.R;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class UIUtils {
	public static void showToast(Context context, String text) {
		Toast toast = Toast.makeText(context, text, 2000);
		toast.setGravity(Gravity.CENTER, 0, 0);
		Log.e("TEST", "@@@@@@@@@@@@@@@@@@@@@@@ ");
		toast.show();
	}

	public static void showErrorDialog(Context context, String title,
			String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title != null)
			builder.setTitle(title);
		builder.setMessage(text);
		builder.setNegativeButton(R.string.ok, null);
		builder.show();
	}
}