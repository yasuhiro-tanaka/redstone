package org.ramidore.logic.party;

import org.junit.jupiter.api.Test;
import org.ramidore.util.SoundUtil;

class PartyListLogicTest extends PartyListLogic {
	private PartyListLogic target;

	@Test
	void test() {
		target = new PartyListLogic();
		SoundUtil.notifyDefaultSound();
		try {
			// 音が鳴る前にmainスレッドが終了しないようsleep
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
