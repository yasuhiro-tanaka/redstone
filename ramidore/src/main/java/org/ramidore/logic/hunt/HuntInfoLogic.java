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

public class HuntInfoLogic implements IPacketExecutable, IConfigurable {
	/**
	 * . Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ItemLogic.class);

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


	/**
	 * MOB討伐時のパケット.同時に複数倒すとPATTERN_KILL_MOB_PARTが複数回出現する.
	 */
//	private static final String PATTERN_KILL_MOB_PART = "001200A5110000..0000000000"+ Const.BASE_PATTERN + "000000";
	private static final String PATTERN_KILL_MOB_PART = "00240046110000";
	private static final String PATTERN_002811CDCD06 = "^.*2811CDCD..00.*" + PATTERN_KILL_MOB_PART + ".*$";
	//"^.*2811CDCD..00.*" + "1200A51100" + ".*$";

	/**
	 * MOB討伐
	 */
	private static final String PATTERN_KILL_MOB_WHOLE = PATTERN_002811CDCD06;

	/**
	 * . 正規表現オブジェクト
	 */
	private static Pattern patternKillMobWhole = Pattern.compile(PATTERN_KILL_MOB_WHOLE);
	private static Pattern patternKillMobPart = Pattern.compile(PATTERN_KILL_MOB_PART);

	@Override
	public boolean execute(PacketData data) {

		int count = countKillMob(data.getStrData());
		if(count > 0) {
			killMob = killMob + count;
			LOG.info("Mob討伐 : " + killMob);
			Platform.runLater(() -> {
				numOfKillMob.setText(formatNumOfKillMob(killMob));
			});
			return false;
		}

		return false;
	}

	int countKillMob(String strData) {
		Matcher killMobWholeMatcher = patternKillMobWhole.matcher(strData);
		int count = 0;
		if (killMobWholeMatcher.matches()) {
			System.out.println("Kill MOB : matches");
			Matcher killMobPartMatcher = patternKillMobPart.matcher(strData);
			while(killMobPartMatcher.find()) {
				count++;
				System.out.println("Kill MOB : find [" + count + "]");
			}
		}
		return count;
	}
	
	public void clearNumOfKillMob() {
		killMob = 0;
		Platform.runLater(() -> {
			numOfKillMob.setText(formatNumOfKillMob(0));
		});
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
