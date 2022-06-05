package org.ramidore.util;

import java.awt.Toolkit;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class SoundUtil {
	@Getter
	@Setter
	private static Stage stage;

	/**
	 * 通知音を鳴らす
	 */
	public static void notifyDefaultSound() {
		Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.default");
		if (runnable != null) {
			runnable.run();
		}
	}
	
	public static void notifySound(String path) {
		AudioClip c = null;
		try {
			c = new AudioClip(new URI(SoundUtil.class.getResource(path).toString()).toString());
		} catch (URISyntaxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		c.setVolume(0.2d);
		c.play();
	}
}
