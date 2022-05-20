package org.ramidore.logic.hunt;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ramidore.core.IConfigurable;
import org.ramidore.core.PacketData;
import org.ramidore.logic.IPacketExecutable;
import org.ramidore.logic.item.ItemLogic;
import org.ramidore.util.RamidoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

public class HuntLogic implements IPacketExecutable, IConfigurable {
	/**
	 * . Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ItemLogic.class);

	/**
	 * MOB討伐
	 */
	private static final String PATTERN_KILLLMOB_2022 = "^.*002811CDCD0500.*$";

	/**
	 * . 正規表現オブジェクト
	 */
	private static Pattern patternKillMob = Pattern.compile(PATTERN_KILLLMOB_2022);

	/**
	 * MOB討伐数表示用ラベル
	 */
	@Getter
	@Setter
	private Label numOfKillMob;

	/**
	 * MOB討伐数
	 */
	private int killMob = 0;

	@Override
	public boolean execute(PacketData data) {

		Matcher killMobMatcher = patternKillMob.matcher(data.getStrData());

		if (killMobMatcher.matches()) {
			++killMob;
			Platform.runLater(() -> {
				numOfKillMob.setText(formatNumOfKillMob(killMob));
			});
			return true;
		}

		LOG.info("Mob討伐 : " + killMob);

		return true;
	}
	
	private String formatNumOfKillMob(int killMob) {
		return RamidoreUtil.thoudsndsSeparate(killMob);
	}


	@Override
	public void loadConfig(Properties config) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void saveConfig(Properties config) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void loadConfig() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void saveConfig() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
