package org.ramidore.logic.party;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ramidore.Const;
import org.ramidore.core.IConfigurable;
import org.ramidore.core.PacketData;
import org.ramidore.logic.IPacketExecutable;
import org.ramidore.util.RamidoreUtil;
import org.ramidore.util.SoundUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartyListLogic implements IPacketExecutable, IConfigurable{
	/**
	 * . Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PartyListLogic.class);
	
	/**
	 * PT申込みを受けたパターン
	 */
//	private static final String PATTERN = "^.*2811CDCD..0000004A017D110000" + Const.BASE_PATTERN + "5F30303000CC+CC......00" + Const.BASE_PATTERN + "00CC+00.*$";
	private static final String PATTERN = "^.*2811CDCD..000000..007E110000CDCC" + Const.BASE_PATTERN + "00.*$";
	
	/**
     * 正規表現オブジェクト.	
     */
    private static Pattern pattern = Pattern.compile(PATTERN);
    
	@Override
	public boolean execute(PacketData data) {
		Matcher matcher = pattern.matcher(data.getStrData());

        if (matcher.matches()) {
            String requesterName = RamidoreUtil.encode(matcher.group(1), Const.ENCODING);
            notify(requesterName);

            return true;
        }

        return false;
	}
	
	private void notify(String requesterName) {
        SoundUtil.notifyDefaultSound();
        
        LOG.info("パーティ参加申し込み【" + requesterName + "】");		
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
