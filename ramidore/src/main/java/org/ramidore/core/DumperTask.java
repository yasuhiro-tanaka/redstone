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

import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapDumper;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.Packet;
import org.pcap4j.sample.Dump;
import org.ramidore.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * ダンプファイル作成用.
 *
 * @author atmark
 */
public class DumperTask extends OnlineTask {

    /**
     * . Logger
     */
    private static Logger LOG = LoggerFactory.getLogger(DumperTask.class);

    /**
     * ダンパー.
     */
    private PcapDumper dumper = null;
    
    /**
     * ダンプファイル
     */
    private static String DUMP_FILE_NAME = Dump.class.getName() + ".pcapFile";

    /**
     * コンストラクタ.
     *
     * @param device        デバイス
     * @param listenAddress listenするアドレス.
     */
    public DumperTask(PcapNetworkInterface device, PcapAddress listenAddress) {

        super(device, listenAddress);

        try {
			dumper = handle.dumpOpen(DUMP_FILE_NAME);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected PacketListener packetHandlerFactory() {
        return new PacketListener() {
            @Override
            public void gotPacket(Packet p) {
//                dumper.dump(packet.getCaptureHeader(), packet);
            }
        };
    }

    @Override
    protected void hookOnTaskStart() {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void concreteCall() {

        if (open() && setFilter()) {

            try {
				handle.loop(Const.LOOP_INFINITE, packetHandlerFactory());
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
        }
    }

    @Override
    protected boolean open() {

        if (!super.open()) {

            return false;
        }

        File f = saveFile("PCAP", "*.pcap");

        if (f != null) {
            try {
				dumper = handle.dumpOpen(f.getAbsolutePath());
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				return false;
			}

            LOG.trace("pcap open dump-file : " + f.getAbsolutePath());

            return true;
        } else {

            LOG.trace("pcap open dump-file failed");

            return false;
        }
    }

    @Override
    protected void close() {

        dumper.close();
        super.close();
    }

    /**
     * ファイル保存ダイアログを開く.
     *
     * @param kind         ファイル種別
     * @param extensionPat 拡張子(*.foo)
     * @return File
     */
    private File saveFile(String kind, String extensionPat) {

        FileChooser fc = new FileChooser();
        fc.setTitle("保存するダンプファイル名を入力してください。");
        fc.setInitialDirectory(new File(new File(".").getAbsoluteFile().getParent()));
        fc.getExtensionFilters().add(new ExtensionFilter(kind, extensionPat));

        return fc.showSaveDialog(null);
    }

}
