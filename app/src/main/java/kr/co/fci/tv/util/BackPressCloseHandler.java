package kr.co.fci.tv.util;

import android.app.Activity;
import android.widget.Toast;

import kr.co.fci.tv.R;

/**
 * Created by elliot.oh on 2015-06-07.
 */
public class BackPressCloseHandler {
	private long backKeyPressedTime = 0;
	private Toast toast;

	private Activity activity;

	public BackPressCloseHandler(Activity context) {
		this.activity = context;
	}

	public void onBackPressed() {
		if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
			backKeyPressedTime = System.currentTimeMillis();
			showGuide();
			return;
		}
		if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
			activity.finish();
			toast.cancel();
		}
	}

	public void showGuide() {
		CustomToast toast = new CustomToast(activity);
		toast.showToast(activity, R.string.backKeyAgainQuit, Toast.LENGTH_SHORT);
		/*toast = Toast.makeText(activity, R.string.backKeyAgainQuit, Toast.LENGTH_SHORT);
		toast.show();*/
	}
}
