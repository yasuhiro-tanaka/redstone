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

package org.ramidore.logic.system;

import lombok.Getter;
import org.ramidore.bean.RedStoneChartBean;
import org.ramidore.core.PacketData;
import org.ramidore.util.RamidoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Redstone返却先.
 *
 * @author atmark
 */
public class RedstoneLogic extends AbstractSystemMessageLogic {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RedstoneLogic.class);

    /**
     * フラグ　天上.
     */
    private static final String FLAG_TENJO = "00";

    /**
     * フラグ　地下.
     */
    private static final String FLAG_CHIKA = "01";

    /**
     * フラグ　悪魔.
     */
    private static final String FLAG_AKUMA = "02";

    /**
     * . REDSTONE返却時のシステムメッセージのパターン
     */
    public static final String PATTERN = "^2C002811CDCDCDCD0100000020005D120000BB00(00|01|02)000000(......)00(......)00(......)000100000000000000$";

    /**
     * 正規表現オブジェクト.
     */
    private static Pattern pattern = Pattern.compile(PATTERN);

    /**
     * JavaFXスレッドと同期するためのLinkedQueue.
     */
    @Getter
    private ConcurrentLinkedQueue<RedStoneChartBean> chartDataQ;

    /**
     * チャート用データ.
     */
    private RedStoneChartBean dataBean;

    /**
     * コンストラクタ.
     */
    public RedstoneLogic() {

        chartDataQ = new ConcurrentLinkedQueue<>();

        dataBean = new RedStoneChartBean();

        // 初期データを追加する
        chartDataQ.add(dataBean);
    }

    @Override
    public boolean execute(final PacketData data) {

        Matcher m = pattern.matcher(data.getStrData());

        if (m.matches()) {

            String flag = m.group(1);

            int tenjoCnt = RamidoreUtil.intValueFromDescHexString(m.group(2));
            int chikaCnt = RamidoreUtil.intValueFromDescHexString(m.group(3));
            int akumaCnt = RamidoreUtil.intValueFromDescHexString(m.group(4));

            check(tenjoCnt, chikaCnt, akumaCnt);

            dataBean.setCurrentTenjoCount(tenjoCnt);
            dataBean.setCurrentChikaCount(chikaCnt);
            dataBean.setCurrentAkumaCount(akumaCnt);

            dataBean.setCurrentTotalCount();

            if (FLAG_TENJO.equals(flag)) {
                dataBean.addTenjo();
            }
            if (FLAG_CHIKA.equals(flag)) {
                dataBean.addChika();
            }
            if (FLAG_AKUMA.equals(flag)) {
                dataBean.addAkuma();
            }
            dataBean.setLocalTotalCount();

            chartDataQ.add(dataBean);

            return true;
        }

        return false;
    }

    /**
     * 取得漏れチェック.
     *
     * @param tenjoCnt 天上個数
     * @param chikaCnt 地下個数
     * @param akumaCnt 悪魔個数
     */
    private void check(int tenjoCnt, int chikaCnt, int akumaCnt) {

        if (dataBean.getCurrentTenjoCount() == 0 || dataBean.getCurrentChikaCount() == 0 || dataBean.getCurrentAkumaCount() == 0) {

            return;
        } else {

            int diffTenjo = tenjoCnt - dataBean.getCurrentTenjoCount();
            int diffChika = chikaCnt - dataBean.getCurrentChikaCount();
            int diffAkuma = akumaCnt - dataBean.getCurrentAkumaCount();

            if (diffTenjo > 1 || diffChika > 1 || diffAkuma > 1) {

                LOG.warn("取得漏れ発生:" + diffTenjo + ":" + diffChika + ":" + diffAkuma);
            }
        }

    }

}
