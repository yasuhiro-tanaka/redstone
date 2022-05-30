package org.ramidore.util;

import java.awt.Taskbar;
import java.awt.Taskbar.Feature;

import org.junit.jupiter.api.Test;

class SoundUtilTest extends SoundUtil {
	private SoundUtil target;

	@Test
	void test() {
		System.out.println(Taskbar.isTaskbarSupported());
		Taskbar taskbar = Taskbar.getTaskbar();
		for(Feature f: Taskbar.Feature.values()) {
			System.out.println(f.name() + ":" + taskbar.isSupported(f));
		}
		taskbar.requestUserAttention(true, false);
	}

}
