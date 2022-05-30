package org.ramidore.logic.hunt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HuntInfoLogicTest extends HuntInfoLogic {
	private HuntInfoLogic target;
	
	@BeforeEach
	void before() {
		target = new HuntInfoLogic();
	}

	@Test
	void test_countKillMob() {
		String data = "D569C6254686766C7AA3FBA4501801FDE2AF000030002811CDCD020000001200A511000014000000000020FE680000001200A51100001200000000007C6E6A0000001200";
		int actual = target.countKillMob(data);
		assertEquals(2, actual);
	}

}
