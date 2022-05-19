package org.ramidore.logic.item;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.ramidore.bean.ItemBean;
import org.ramidore.bean.ItemData;
import org.ramidore.bean.OptionBean;
import org.ramidore.util.RamidoreUtil;

class ItemDatHolderTest {

	@Test
	void test() {
		ItemDatHolder itemDat = new ItemDatHolder();
		Map<String, ItemBean> itemMap = itemDat.getItemMap();
		for(Map.Entry<String, ItemBean> item : itemMap.entrySet()) {
			ItemBean itemBean = item.getValue();
			ItemData itemData = new ItemData(itemBean);
			System.out.println("id : [" + itemBean.getId() + "], name : [" + itemBean.getName() + "]" );
		}
		Map<String, OptionBean> optionMap = itemDat.getOptionMap();
		System.out.println();
	}
	
	@Test
	void test_2() {
		String fileAll = loadData();
		String lines[] = fileAll.split("0D0A");
		System.out.println(fileAll.length());
	}
	
	private String fileName_1 = "item.dat";
	String loadData() {
		DataInputStream dis = null;
		String readAll = null;
		try {
			dis = new DataInputStream(new FileInputStream(fileName_1));
			byte[] buffer = new byte[4];
			int bytesRead = dis.read(buffer);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		try {
			readAll = RamidoreUtil.toHex(dis.readAllBytes());
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		return readAll;
	}

}
