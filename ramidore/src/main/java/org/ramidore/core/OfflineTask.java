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

import java.io.File;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.pcap4j.core.PcapAddress;
import org.ramidore.Const;
import org.ramidore.logic.AbstractMainLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * オフラインキャプチャを行うクラス.
 *
 * @author atmark
 */
public class OfflineTask extends OnlineTask {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(OfflineTask.class);

	/**
	 * キャプチャ対象ファイル
	 */
	private File dumpFile;
	private DumpTailer dumpTailer;

	/**
	 * Tailerの待ち時間
	 */
	private static final int SLEEP = 500;

	/**
	 * コンストラクタ.
	 *
	 * @param listenAddress
	 * @param logic
	 */
	public OfflineTask(PcapAddress listenAddress, AbstractMainLogic logic) {
		super(null, listenAddress, logic);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void concreteCall() {
		if (open()) {
			dumpTailer = new DumpTailer(dumpFile);
			try {
				dumpTailer.start();
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	/**
	 * オフラインファイルをオープンする.
	 *
	 * @return 正否
	 */
	@Override
	protected boolean open() {

//        File f = selectFile("PCAP", "*.pcap");
		dumpFile = new File(Const.DUMPCAP_LOG_PATH);
		if (dumpFile == null) {
			LOG.error("Error while opening offline file for capture : " + Const.DUMPCAP_LOG_PATH);
			return false;
		}

		LOG.trace("pcap open offline-file : " + dumpFile.getAbsolutePath());
		return true;
	}

	private void switchStdIn(InputStream is) {
		System.setIn(is);
	}

	/**
	 * ファイルを選択する.
	 *
	 * @param kind         種別
	 * @param extensionPat 拡張子(*.foo)
	 * @return File
	 */
	private File selectFile(String kind, String extensionPat) {

		FileChooser fc = new FileChooser();
		fc.setTitle("読み込むファイルを選択してください。");
		fc.setInitialDirectory(new File(new File(".").getAbsoluteFile().getParent()));
		fc.getExtensionFilters().add(new ExtensionFilter(kind, extensionPat));

		return fc.showOpenDialog(null);
	}

	@SuppressWarnings("unused")
	private class DumpTailer {
		private static final int SLEEP = 500;
		private File dumpFile;
		private Tailer tailer;
		private ExecutorService executor = Executors.newSingleThreadExecutor();

		public DumpTailer(File dumpFile) {
			this.dumpFile = dumpFile;
		}

		public void start() throws InterruptedException {
			MyListener listener = new MyListener();
			tailer = new Tailer(this.dumpFile, listener, SLEEP, true);
			executor.submit(tailer);
		}

		public void stop() {
			tailer.stop();
			executor.shutdown();
		}

		public boolean isRun() {
			return !executor.isTerminated();
		}

		private class MyListener extends TailerListenerAdapter {
			@Override
			public void handle(String line) {
				byte[] b = null;
				b = line.getBytes();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				PacketData data = new PacketData(new Date(timestamp.getTime()), b);
				boolean isDebug = true;
				if (isDebug) {
					System.out.println(line);
					System.out.println(data.getStrData());
				}

				// パケットの判別ロジックは以下に集約
				if (logic.execute(data)) {
//					System.out.println(data.getStrData());
					// JavaFXのスレッド以外から更新する場合必ずupdateMessage経由でやる
					// updateMessage(logic.getOshiraseMessage());
				}
			}
		}
	}

	@Override
	public void stop() {
		dumpTailer.stop();
		dumpFile = null;
		super.stop();
	}

//    @Override
//    protected boolean setFilter() {
//
//        PcapBpfProgram program = new PcapBpfProgram();
//
//        String expression = "tcp && ((src port " + Const.SRC_PORTS[0] +") || (src port " + Const.SRC_PORTS[1] + "))";
//        int optimize = 1;
//        int netmask = 0xFFFFFF00;
//
//        if (pcap.compile(program, expression, optimize, netmask) != Pcap.OK) {
//
//            LOG.error(pcap.getErr());
//
//            return false;
//        }
//
//        if (pcap.setFilter(program) != Pcap.OK) {
//
//            LOG.error(pcap.getErr());
//
//            return false;
//        }
//
//        LOG.debug("set filter : " + expression);
//
//        return true;
//    }
}
