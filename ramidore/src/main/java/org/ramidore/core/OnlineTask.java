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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.ramidore.Const;
import org.ramidore.logic.AbstractMainLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * パケットキャプチャの開始～終了までのタスク.
 *
 * @author atmark
 */
public class OnlineTask extends AbstractTask {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(OnlineTask.class);

	/**
	 * カレントネットワークデバイス.
	 */
	private PcapNetworkInterface device;

	/**
	 * コンストラクタ.
	 *
	 * @param currentDevice PcapIfオブジェクト
	 * @param listenAddress listenするアドレス
	 * @param logic         ロジック
	 */
	public OnlineTask(PcapNetworkInterface currentDevice, PcapAddress listenAddress, AbstractMainLogic logic) {

		this.device = currentDevice;
		this.listenAddress = listenAddress;
		this.logic = logic;
	}

	/**
	 * コンストラクタ.
	 *
	 * @param device        PcapIfオブジェクト
	 * @param listenAddress listenするアドレス
	 */
	public OnlineTask(PcapNetworkInterface device, PcapAddress listenAddress) {

		this.device = device;
		this.listenAddress = listenAddress;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected PacketListener packetHandlerFactory() {

		return new PacketListener() {

			@Override
			public void gotPacket(Packet p) {
				try {
//                	if( !p.contains(TcpPacket.class)) {
//                		return;
//                	}

//                	TcpPacket tcp = p.get(TcpPacket.class);
					// p.getByteArray(0, b); イーサネットフレームも含めたバイト列が返されてしまうので注意
					// TCPペイロード(アプリケーション層のデータ)を取得している
//                    byte[] b = tcp.getPayload().getRawData();

					Packet ipV4 = p.get(EthernetPacket.class).getPayload();
					if (ipV4 == null) {
						return;
					}
					Packet tcp = ipV4.get(IpV4Packet.class).getPayload();
					if (tcp == null) {
						return;
					}

					Timestamp timestamp = handle.getTimestamp();
					PacketData data = new PacketData(new Date(timestamp.getTime()), tcp.getRawData());

					// パケットの判別ロジックは以下に集約
					if (logic.execute(data)) {
						// JavaFXのスレッド以外から更新する場合必ずupdateMessage経由でやる
						OnlineTask.this.updateMessage(logic.getOshiraseMessage());
					} else {
						if (Const.DUMP_UNKNOWN_PACKET) {
							// 判別できないパケットをファイルに出力
							if (unknownTcpDump == null) {
								openDump();
							}
							unknownTcpDump.write(data.getStrData());
							unknownTcpDump.newLine();
//						unknownTcpDump.flush();
						}
					}

				} catch (Exception e) {

					LOG.error(ExceptionUtils.getStackTrace(e));
				}
			}

		};
	}

//    private PcapDumper unknownPacketDump = null;
	private BufferedWriter unknownTcpDump = null;

	private void openDump() {
		try {

			unknownTcpDump = Files.newBufferedWriter(Paths.get(Const.UNKNOWN_PACKET_LOG_PATH));
//    		unknownPacketDump = handle.dumpOpen(new File(Const.UNKNOWN_PACKET_LOG_PATH).getAbsolutePath());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	@Override
	protected void hookOnTaskStart() {
		logic.hookOnTaskStart();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void concreteCall() {
		if (open() && setFilter()) {

			try {
				handle.loop(Const.LOOP_INFINITE, packetHandlerFactory());
			} catch (InterruptedException e) {
				// キャプチャ停止時に発生するが処理継続可
				// ハンドリング要調査
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	/**
	 * キャプチャを停止する.
	 */
	public void stop() {

		// キャプチャ開始前に終了する場合nullチェック
		if (handle != null) {
			try {
				// dumpを閉じる
				if (unknownTcpDump != null) {
					unknownTcpDump.close();
				}
				if (handle.isOpen()) {
					// pcap.loopを抜けてclose()を呼ぶ
					handle.breakLoop();
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (NotOpenException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			close();
		}
	}

	@Override
	protected void updateMessage(String message) {

		super.updateMessage(message);
	}

	/**
	 * カレントデバイスをオープンする.
	 *
	 * @return 正否
	 */
	protected boolean open() {

		StringBuilder errbuf = new StringBuilder();

		int snaplen = 64 * 1024;
//        int flags = Pcap.MODE_PROMISCUOUS;
		int timeout = 1000;

//        pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
		try {
			handle = device.openLive(snaplen, PromiscuousMode.PROMISCUOUS, timeout);
		} catch (PcapNativeException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}

		if (handle == null) {
			LOG.error("Error while opening device for capture. " + device.toString());
			return false;
		} else {
			LOG.trace("pcap open on-line");
		}

		return true;
	}
}
