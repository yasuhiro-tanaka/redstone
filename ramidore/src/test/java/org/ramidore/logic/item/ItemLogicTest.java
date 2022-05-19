package org.ramidore.logic.item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemLogicTest extends ItemLogic {
	private ItemLogic target;
	
	@BeforeEach
	public void setUp() {
		target = new ItemLogic();
	}
	
	@Test
	public void test() {
		Set<String> optionNames = new HashSet<String>();
		optionNames.add("[命中補正無視]");
		target.setNotifyOptionNames(optionNames);
		
		String dropOptionName = "[命中補正無視]";
		assertTrue(target.notifyOptionName(dropOptionName));

	}

}
