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

package org.ramidore;

import org.ramidore.core.PacketAnalyzeService;

/**
 * 定数.
 *
 * @author atmark
 */
public final class Const {
	
	/**
	 * 
	 */
	public static final String CHAT_DATE_FORMAT = "MM/dd HH:mm:ss";

    /**
     * 0x00以外の16進文字列にマッチする正規表現パターン.
     */
    public static final String BASE_PATTERN = "((?:[1-9A-F]{2}|[1-9A-F]0|0[1-9A-F])+)";

    /**
     * . 日本語エンコードはWindows-31J
     * <p>
     * ※ SJISだと機種依存文字が化ける
     */
    public static final String ENCODING = "Windows-31J";
    
    /**
     * 接続し続ける
     */
    public static final int LOOP_INFINITE = -1;
    
    /**
     * クライアント側アドレス
     */
    public static final String DST_ADDR = "220.100.85.108";
    
    /**
     * ホスト側ポート
     */
    public static final String[] SRC_PORTS = {"54631", "54632", "54633", "56621"};
    
    /**
     * dumpcapのダンプ出力先
     */
    public static final String DUMPCAP_LOG_PATH = ".\\dumplog.pcap";

    /**
     * デフォルト実行モード
     */
    public static final int DEFAULT_EXEC_MODE = PacketAnalyzeService.MODE_ONLINE;
    
    /**
     * パケットフィルタ条件(BFP)
     */
//    public static final String PACKET_FILTEREXEPRESSION = "tcp src port " + Const.SRC_PORTS[0] + " or tcp src port " + Const.SRC_PORTS[1];
    public static final String PACKET_FILTEREXEPRESSION =
    		"tcp src port " + Const.SRC_PORTS[0]
    		+ " or tcp src port " + Const.SRC_PORTS[1]
    		+ " or tcp src port " + Const.SRC_PORTS[2]
			+ " or tcp src port " + Const.SRC_PORTS[3];
    
    /**
     * 不明パケット出力有無
     */
    public static final boolean DUMP_UNKNOWN_PACKET = true;
    /**
     * 不明パケット出力先
     */
    public static final String UNKNOWN_PACKET_LOG_PATH = ".\\unknown_packetlog.txt";


    
    /**
     * プライベートコンストラクタ.
     */
    private Const() {
    }
}
