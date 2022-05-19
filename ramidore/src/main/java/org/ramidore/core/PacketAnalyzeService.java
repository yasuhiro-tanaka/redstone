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

/**
 *
 */
package org.ramidore.core;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.ramidore.Const;
import org.ramidore.logic.AbstractMainLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;

/**
 * パケット解析サービス.
 *
 * @author atmark
 */
public class PacketAnalyzeService extends Service<Void> implements IConfigurable {

    /**
     * リアルタイムキャプチャ用.
     */
    public static final int MODE_ONLINE = 0;

    /**
     * オフラインファイル読み込み用.
     */
    public static final int MODE_OFFLINE = 1;

    /**
     * ダンプ出力用.
     */
    public static final int MODE_DUMP = 2;

    public static final List<String> MODE = Collections.unmodifiableList(new ArrayList<String>() {{
        add("on-line cap");
        add("off-line cap");
        add("dump");
    }});

    /**
     * 動作モード.
     */
    @Getter
    @Setter
//    private int mode = MODE_ONLINE;
    private int mode = MODE_OFFLINE;

    /**
     * . Logger
     */
    @Getter
    private static Logger LOG = LoggerFactory.getLogger(PacketAnalyzeService.class);

    /**
     * . パケット解析ロジック
     */
    @Getter
    @Setter
    private AbstractMainLogic logic;

    /**
     * . ネットワークインターフェースのリスト
     */
    @Getter
    private List<PcapNetworkInterface> devices = new ArrayList<>();

    /**
     * . カレントインターフェース
     */
    @Getter
    private PcapNetworkInterface currentDevice;

    /**
     * . カレントデバイスの持つIPアドレス一覧
     */
    private List<PcapAddress> addresses;

    /**
     * . ListenするIPアドレス
     */
    @Getter
    private PcapAddress listenAddress;

    /**
     * カレントタスク.
     */
    @Getter
    private AbstractTask currentTask;

    /**
     * コンストラクタ.
     *
     * @param logic  ロジック
     * @param config 設定
     */
    public PacketAnalyzeService(AbstractMainLogic logic, Properties config) {

        this.logic = logic;

        initialize();

        loadConfig(config);
    }

    /**
     * コンストラクタ.
     *
     * @param logic  ロジック
     * @param config 設定
     * @param mode   動作モード
     */
    public PacketAnalyzeService(AbstractMainLogic logic, Properties config, int mode) {

        this.logic = logic;
        this.mode = mode;

        initialize();

        loadConfig(config);
    }

    @Override
    public void loadConfig(Properties config) {

        // 初期選択するデバイス
        int defaultDevice = Integer.parseInt(config.getProperty("defaultDevice", "0"));

        setDevice(defaultDevice);

        // 初期選択するIPアドレス
//        int defaultListenAddress = Integer.parseInt(config.getProperty("defaultListenAddress", "0"));
        int defaultListenAddress = 0;
        this.listenAddress = getAddresses().get(defaultListenAddress);
        setListenAddress(defaultListenAddress);
        
        // 動作モード
        mode = Integer.parseInt(config.getProperty("execMode", String.valueOf(Const.DEFAULT_EXEC_MODE)));
        
        // 保持するロジックの設定をロード
        logic.loadConfig(config);
    }
    
    @Override
    public void saveConfig(Properties config) {

        // 初期選択するデバイス
        int defaultDevice = devices.indexOf(currentDevice);
        config.setProperty("defaultDevice", String.valueOf(defaultDevice));
        // 初期選択するIPアドレス
        int defaultListenAddress = getAddresses().indexOf(listenAddress);
        config.setProperty("defaultListenAddress", String.valueOf(defaultListenAddress));
        
        // 動作モード
        config.setProperty("execMode", String.valueOf(mode));

        // 保持するロジックの設定をセーブ
        logic.saveConfig(config);
    }

    @Override
    public void loadConfig() {
        // nop
    }

    @Override
    public void saveConfig() {
        // nop
    }

    @Override
    protected Task<Void> createTask() {

        if (mode == MODE_ONLINE) {
            // 通常のパケットキャプチャ
            currentTask = new OnlineTask(currentDevice, listenAddress, logic);
        } else if (mode == MODE_OFFLINE) {
            // オフラインファイル読込用のタスクを生成
            currentTask = new OfflineTask(listenAddress, logic);
        } else if (mode == MODE_DUMP) {
            // オフラインファイルダンプ用のタスクを生成
            currentTask = new DumperTask(currentDevice, listenAddress);
        }

        LOG.trace("Task created");

        return currentTask;
    }

    @Override
    public void restart() {

        LOG.trace("call Service.restart");

        super.restart();
    }

    /**
     * キャプチャを停止する.
     */
    public void stop() {

        if (currentTask != null) {
            currentTask.stop();
        }
    }

    @Override
    public void succeeded() {

        // State = SUCCEEDED で呼び出される
        LOG.trace("call Service.succeeded");

        super.succeeded();
    }

    @Override
    public void cancelled() {

        // State = CANCELLED で呼び出される
        LOG.trace("call Service.cancelled");
    }

    @Override
    public void failed() {

        // State = FAILED で呼び出される
        LOG.trace("call Service.failed");
    }

    /**
     * . 初期化
     *
     * @return 成功/失敗
     */
    public boolean initialize() {
    	
        StringBuilder errbuf = new StringBuilder();
        try {
        	// デバイス取得
        	List<PcapNetworkInterface> alldevices = Pcaps.findAllDevs();
        	for(PcapNetworkInterface ni : alldevices) {
        		devices.add(ni);
//        		if(!ni.getAddresses().isEmpty()) {
//        			// アドレスが設定されていないデバイスは除外
//        			devices.add(ni);
//        		}
        	}
        } catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
        	return false;
		}

        if (devices.isEmpty()) {
            LOG.error("Can't read list of devices, error is " + errbuf.toString());
            return false;
        }

        LOG.trace("Network devices found");

        int i = 0;
        for (PcapNetworkInterface device : devices) {
            LOG.trace(i++ + ":" + device.getName() + "[" + device.getDescription() + "]:[" + device.getAddresses() + "]");
        }
        
        try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			for(NetworkInterface ni: Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if(!ni.isUp()) {
					continue;
				}
				for(InetAddress ia: Collections.list(ni.getInetAddresses())) {
					if(ia instanceof Inet4Address) {
						System.out.println(ni.getIndex() + ":" + "[" + ni.getDisplayName() + "]:[" + ia.getHostAddress() + "]" );
											}
				}
			}
		} catch (SocketException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
        
        return true;
    }

    /**
     * . デバイスを設定する
     *
     * @param i デバイスのインデックス
     */
    public void setDevice(final int i) {

        currentDevice = devices.get(i < devices.size() ? i : 0);

        LOG.trace("Choosing " + currentDevice.getDescription() + " on your behalf");

        addresses = currentDevice.getAddresses();

        for (PcapAddress addr : getAddresses()) {
            LOG.trace("ipaddress : " + addr.getAddress().getHostAddress());
        }
    }

    /**
     * 表示用デバイス名一覧を取得.
     *
     * @return list
     */
    public List<String> getDeviceNameList() {

        List<String> deviceNameList = new ArrayList<>();

        for (PcapNetworkInterface device : devices) {

            deviceNameList.add(device.getDescription());
        }

        return deviceNameList;
    }

    /**
     * 表示用IPアドレス一覧を取得.
     *
     * @return list
     */
    public List<String> getAddressList() {

        List<String> addressList = new ArrayList<>();

        for (PcapAddress pcapAddr : getAddresses()) {
        	addressList.add(pcapAddr.getAddress().getHostAddress());
        }

        return addressList;
    }

    public int getCurrentDeviceIndex() {

        return devices.indexOf(currentDevice);
    }

    public int getCurrentListenAddressIndex() {
    	return getAddresses().indexOf(listenAddress);
    }

    /**
     * . ListenするIPアドレスを設定する
     *
     * @param i IPアドレスのインデックス
     */
    public void setListenAddress(final int i) {
        listenAddress = getAddresses().get(i);
    }
    
    public List<PcapAddress> getAddresses() {
    	if(addresses.isEmpty()) {
    		ArrayList<PcapAddress> list = new ArrayList<PcapAddress>();
    		list.add(getDummyAddress());
    		return list;
    	} else {
    		return addresses;
    	}
    }
    
    private PcapAddress getDummyAddress() {
    	PcapAddress dummyAddress = null;
    	try {
    		dummyAddress = new PcapAddress() {
				InetAddress dummy = (Inet4Address) InetAddress.getByName("0.0.0.0");
				@Override
				public InetAddress getNetmask() {
					return dummy;
				}
				@Override
				public InetAddress getDestinationAddress() {
					return dummy;
				}
				@Override
				public InetAddress getBroadcastAddress() {
					return dummy;
				}
				@Override
				public InetAddress getAddress() {
					return dummy;
				}
			};
		} catch (UnknownHostException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return dummyAddress;
    }
}