/*
 * Copyright 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ramidore.logic.chat;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ramidore.Const;
import org.ramidore.bean.PartyChatTable;
import org.ramidore.core.PacketData;
import org.ramidore.util.RamidoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PTチャットのパケットを処理する.
 *
 * @author atmark
 */
public class PartyChatLogic extends AbstractChatLogic {
    /**
     * . Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(PartyChatLogic.class);

    /**
     * PTチャットのパターン.
     */
//    private static final String PATTERN = "^..002811CDCDCDCD..000000(?:.{2})+005811CCCCCCCCCC....C0" + Const.BASE_PATTERN + "00"
//            + Const.BASE_PATTERN + "000000(?:.{2})*$";
    private static final String PATTERN = "^.*5811CCCCCC....80" + Const.BASE_PATTERN + "00" + Const.BASE_PATTERN + "00....$";

    /**
     * 正規表現オブジェクト.
     */
    private static Pattern pattern = Pattern.compile(PATTERN);

    @Override
    public boolean execute(final PacketData data) {

        Matcher matcher = pattern.matcher(data.getStrData());

        if (matcher.matches()) {

            String name = RamidoreUtil.encode(matcher.group(1), Const.ENCODING);

            String content = RamidoreUtil.encode(matcher.group(2), Const.ENCODING);

            addData(new PartyChatTable(data.getDate(), name, content));

            LOG.info("【" + name + "】 " + content);

            return true;
        }

        return false;
    }

    @Override
    public void loadConfig(Properties config) {

        boolean enabled = Boolean.parseBoolean(config.getProperty("partyChat.enabled", "false"));

        this.setEnabled(enabled);
    }

    @Override
    public void saveConfig(Properties config) {

        config.setProperty("partyChat.enabled", String.valueOf(isNoticeable()));
    }

    @Override
    public final void loadConfig() {
        // nop
    }

    @Override
    public final void saveConfig() {
        // nop
    }
}
