package org.ramidore.logic.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ramidore.Const;
import org.ramidore.core.IConfigurable;
import org.ramidore.core.PacketData;
import org.ramidore.logic.IPacketExecutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketAnalyzeLogic implements IPacketExecutable, IConfigurable {
	/**
	 * . Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PacketAnalyzeLogic.class);

	private static final String PATTERN_002811CDCD03 = "^.*002811CDCD..0000005A003A1200.*$";
	private static final String PATTERN_002811CDCD04 = "^.*2811CDCD..00.*0014005D1200.*$";// MOB討伐？カウントできてないときもある
	private static final String PATTERN_002811CDCD05 = "^.*002811CDCD..00.*001000471100.*$";// スキル使用？
	private static final String PATTERN_KILL_MOB_PART = "001200A5110000..0000000000"+ Const.BASE_PATTERN + "000000";
	private static final String PATTERN_002811CDCD06 = "^.*2811CDCD..00.*" + PATTERN_KILL_MOB_PART + ".*$";
	private static final String PATTERN_002811CDCD07 = "001200A5110000..00" + Const.BASE_PATTERN + "0000"+ Const.BASE_PATTERN + "000000";// MOBリポップ

	/**
	 * 除外
	 */
	String[] pattern_exclud = {
			 "XXXXXXXXXXXX"// ダミー
//			 "000A00E91100"
			,"000E00241100"
			,"001000471100"// 明らかに頻度が高い
//			,"001000511100"
//			,"0010005D1200"
//			,"001100651100"
			,"001200231100"
			,"001200291100"
//			,"001200971200"
//			,"0014005D1200"// スキル使用？
			,"0018005D1200"
//			,"001A00261100"
			,"001B004D1200"
			,"001E00131200"
			,"0020005D1200"
			,"002201151200"
//			,"002400461100"
			,"002400501100"
			,"002600251100"
			,"002600B01100"// HP回復？
			,"003E00E91200"
			,"004200151200"// 頻度が高い。移動に関連？
			,"004E00E91200"
			,"005A003A1200"
			,"00B200151200"
			,"455A1D000000"
			,"617973696B75"
			};

	/*
	 * Mob討伐らしきもの
	 */
	String[] pattern_killMob = {
			"XXXXXXXXXXXXX"// ダミー
//			,"0008004F1100"
//			,"000A00341100"
//			,"001200911200"// 範囲攻撃で討伐？ドロップ？
			,"001200A51100"// 討伐で+1、リポップで+1 ?
			,"0014005D1200"// スキル使用？
			,"002400461100"
//			,"002600711200"
//			,"0055002E1100"
//			,"005A003A1200"//ターゲット合わせ？
//			,"007A00151200"
//			,"008400551100"
			};
	/*
	 * private static final String PATTERN_002811CDCD08 = "^.*002811CDCD0800.*$";
	 * private static final String PATTERN_002811CDCD09 = "^.*002811CDCD0900.*$";
	 * private static final String PATTERN_002811CDCD0A = "^.*002811CDCD0A00.*$";
	 * private static final String PATTERN_002811CDCD0B = "^.*002811CDCD0B00.*$";
	 * private static final String PATTERN_002811CDCD0C = "^.*002811CDCD0C00.*$";
	 * private static final String PATTERN_002811CDCD0D = "^.*002811CDCD0D00.*$";
	 * private static final String PATTERN_002811CDCD0E = "^.*002811CDCD0E00.*$";
	 * private static final String PATTERN_002811CDCD0F = "^.*002811CDCD0F00.*$";
	 * private static final String PATTERN_002811CDCD16 = "^.*002811CDCD1600.*$";
	 * private static final String PATTERN_002811CDCD1B = "^.*002811CDCD1B00.*$";
	 * private static final String PATTERN_002811CDCD1F = "^.*002811CDCD1F00.*$";
	 */
	private String[] expres = { PATTERN_002811CDCD03, PATTERN_002811CDCD04, PATTERN_002811CDCD05, PATTERN_002811CDCD06,
			PATTERN_002811CDCD07
			/*
			 * ,PATTERN_002811CDCD08 ,PATTERN_002811CDCD09 ,PATTERN_002811CDCD0A
			 * ,PATTERN_002811CDCD0B ,PATTERN_002811CDCD0C ,PATTERN_002811CDCD0D
			 * ,PATTERN_002811CDCD0E ,PATTERN_002811CDCD0F ,PATTERN_002811CDCD16
			 * ,PATTERN_002811CDCD1B ,PATTERN_002811CDCD1F
			 */
	};

	private static Pattern CDCD03 = Pattern.compile(PATTERN_002811CDCD03);
	private static Pattern CDCD04 = Pattern.compile(PATTERN_002811CDCD04);
	private static Pattern CDCD05 = Pattern.compile(PATTERN_002811CDCD05);
	private static Pattern CDCD06 = Pattern.compile(PATTERN_002811CDCD06);
	private static Pattern CDCD07 = Pattern.compile(PATTERN_002811CDCD07);
	/*
	 * private static Pattern CDCD08 = Pattern.compile(PATTERN_002811CDCD08);
	 * private static Pattern CDCD09 = Pattern.compile(PATTERN_002811CDCD09);
	 * private static Pattern CDCD0A = Pattern.compile(PATTERN_002811CDCD0A);
	 * private static Pattern CDCD0B = Pattern.compile(PATTERN_002811CDCD0B);
	 * private static Pattern CDCD0C = Pattern.compile(PATTERN_002811CDCD0C);
	 * private static Pattern CDCD0D = Pattern.compile(PATTERN_002811CDCD0D);
	 * private static Pattern CDCD0E = Pattern.compile(PATTERN_002811CDCD0E);
	 * private static Pattern CDCD0F = Pattern.compile(PATTERN_002811CDCD0F);
	 * private static Pattern CDCD16 = Pattern.compile(PATTERN_002811CDCD16);
	 * private static Pattern CDCD1B = Pattern.compile(PATTERN_002811CDCD1B);
	 * private static Pattern CDCD1F = Pattern.compile(PATTERN_002811CDCD1F);
	 */
	private Pattern[] patterns = { CDCD03, CDCD04, CDCD05, CDCD06, CDCD07
			/*
			 * ,CDCD08 ,CDCD09 ,CDCD0A ,CDCD0B ,CDCD0C ,CDCD0D ,CDCD0E ,CDCD0F ,CDCD16
			 * ,CDCD1B ,CDCD1F
			 */
	};
	int[] counts = new int[patterns.length];

	Map<String, Integer> keyCountMap = new HashMap<String, Integer>();
	Map<String, List<String>> keyDataMap = new HashMap<String, List<String>>();

	private boolean keyCount(String data) {
		String key = getKey(data);
		if(key.isEmpty()) {
			return false;
		}
		
		if(exclude(data)) {
			return true;
		}
		
		if (keyCountMap.containsKey(key)) {
			Integer count = keyCountMap.get(key);
			count++;
			keyCountMap.put(key, count);
			List<String> keyData = keyDataMap.get(key);
			keyData.add(data);
		} else {
			keyCountMap.put(key, Integer.valueOf(1));
			List<String> keyData = new ArrayList<String>();
			keyData.add(data);
			keyDataMap.put(key, keyData);
		}

		logKeyCount(keyCountMap);

		return false;
	}

	private boolean keyCount_2(String data) {
		String key = getKey(data);
		if(key.isEmpty()) {
			return false;
		}

		if (keyCountMap.isEmpty()) {
			for (String e : pattern_killMob) {
				keyCountMap.put(e, Integer.valueOf(0));
				List<String> keyData = new ArrayList<String>();
				keyDataMap.put(e, keyData);
			}
		}

		if (keyCountMap.containsKey(key)) {
			Integer count = keyCountMap.get(key);
			count++;
			keyCountMap.put(key, count);
			List<String> keyData = keyDataMap.get(key);
			keyData.add(data);
		}

		logKeyCount(keyCountMap);

		return false;
	}

	private void logKeyCount(Map<String, Integer> keyCountMap) {
		System.out.println("=================================================");
		for (Entry<String, Integer> e : keyCountMap.entrySet()) {
			if (e.getValue().intValue() > -1) {
				System.out.println(e.getKey() + " : " + e.getValue());
			}
		}
		System.out.println("=================================================");
	}

	private boolean log(String data) {
		boolean match = false;
		System.out.println("=================================================");
		for (int i = 0; i < patterns.length; i++) {
			Matcher m = patterns[i].matcher(data);
			if (m.matches()) {
				counts[i]++;
				match = true;
			}
			System.out.println(expres[i] + " : " + counts[i]);
		}
		System.out.println("=================================================");

		return match;
	}
	
	private String getKey(String strData) {
		if (strData.length() < 70) {
			return "";
		}
		return strData.substring(58, 70);
	}
	
	/**
	 * 
	 * @param strData
	 * @return true : 除外
	 */
	private boolean exclude(String strData) {
		if(strData.length() <= 40) {
			return true;
		}
		String key = getKey(strData);
		return Arrays.asList(pattern_exclud).contains(key);
	}

	@Override
	public boolean execute(PacketData data) {
		if(exclude(data.getStrData())) {
			// 除外対象はパケット調査用のファイルに出力しない
			return true;
		}
		
//		if (keyCount(data.getStrData())) {
//			return false;
//		}

		if (keyCount_2(data.getStrData())) {
			return false;
		}

		return false;
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
