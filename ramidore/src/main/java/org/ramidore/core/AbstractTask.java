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

package org.ramidore.core;

import java.net.Inet4Address;

import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.ramidore.Const;
import org.ramidore.logic.AbstractMainLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;

/**
 * 基底タスク.
 *
 * @author atmark
 */
public abstract class AbstractTask extends Task<Void> {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTask.class);

    /**
     * Pcapsオブジェクト.
     */
    protected Pcaps pcap;
    
    /**
     * ネットワーク接続ハンドラ
     */
    protected PcapHandle handle;

    /**
     * Listenするアドレス.
     */
    protected PcapAddress listenAddress;
    
    /**
     * ロジック.
     */
    protected AbstractMainLogic logic;

    /**
     * コンストラクタ.
     */
    public AbstractTask() {

    }

    @Override
    protected Void call() {
    	
        LOG.trace("call Task.call");

        hookOnTaskStart();

        concreteCall();

        hookOnTaskEnd();

        return null;
    }

    protected void hookOnTaskStart() {
    }

    protected abstract void concreteCall();

    protected void hookOnTaskEnd() {
    }

    /**
     * フィルタをセットする.
     *
     * @return 正否
     */
    protected boolean setFilter() {

//        PcapBpfProgram program = new PcapBpfProgram();
//        String dstAddr = FormatUtils.ip(listenAddress.getAddress().getData());
//        String expression = "tcp && (dst net " + dstAddr + ") && ((src port 54631) || (src port 54632) || (src port 56621) || (src port 53302) || (src port 53304))";
//        String expression = "tcp and (dst net " + listenAddress.getAddress().getHostAddress() + ") && ((src port " + Const.SRC_PORTS[0] + ") || (src port " + Const.SRC_PORTS[1] + "))";
//        String expression = "len > 64";
//        String expression = "tcp && (dst net ";
        String expression = Const.PACKET_FILTEREXEPRESSION;
        int optimize = 1;
        Inet4Address netmask = PcapHandle.PCAP_NETMASK_UNKNOWN;
        try {
			handle.setFilter(expression, BpfCompileMode.OPTIMIZE);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}

        LOG.debug("set filter : " + expression);

        return true;
    }

    public abstract void stop();
    
    /**
     * カレントデバイスをクローズする.
     */
    protected void close() {

    	handle.close();

        LOG.trace("handle close");
    }

    /**
     * パケットハンドラを生成するファクトリ.
     *
     * @return パケットハンドラ
     */
    @SuppressWarnings("rawtypes")
    protected abstract PacketListener packetHandlerFactory();
}
