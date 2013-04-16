package xmu.ontheway.andsip.ring;

import xmu.ontheway.andsip.R;

import android.content.Context;
import android.media.MediaPlayer;

public class PlayRing {

	// 背景音乐
	private static MediaPlayer player;

	public static void play(Context context) {
		if (player != null)
			player.stop();

		player = MediaPlayer.create(context, R.raw.bg_lol);
		player.setLooping(true);// 设置循环播放
		player.start();// 开始播放
	}

	public static void stop() {
		if (player != null) {
			player.stop();
		}
	}
}
