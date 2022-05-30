package org.ramidore;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.junit.jupiter.api.Test;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapIpV4Address;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.PcapStat;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.NifSelector;
import org.pcap4j.util.Packets;
import org.ramidore.core.PacketData;

import com.sun.jna.Platform;

class MessageKeeperTest extends MessageKeeper {

	@Test
	void test_1() throws Exception {
//		Stream<ProcessHandle> handle = ProcessHandle.allProcesses();
//		handle.forEach(s -> {System.out.println(s.);});
//		Process process = new
//		Process.getInputStream();
		App app = new App();
		app.run();
	}

	private class App {
		private static final int SLEEP = 500;

		private void run() throws InterruptedException {
			MyListener listener = new MyListener();
			Tailer tailer = new Tailer(new File(Const.DUMPCAP_LOG_PATH), listener, SLEEP);
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(tailer);
		}
	}

	public class MyListener extends TailerListenerAdapter {
		@Override
		public void handle(String line) {
			System.out.println("String [" + line + "]");
			try {
				sysoutLine(line, StandardCharsets.US_ASCII);
				sysoutLine(line, StandardCharsets.UTF_8);
				sysoutLine(line, StandardCharsets.UTF_16);
				sysoutLine(line, Charset.forName("windows-31j"));
			} catch (UnsupportedEncodingException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		private void sysoutLine(String line, Charset charset) throws UnsupportedEncodingException {
			byte[] b = line.getBytes(charset);
			System.out.println(charset.displayName() + " [" + new String(b) + "]");
		}
	}

	@Test
	void test_2() throws PcapNativeException, NotOpenException {
		StringBuilder errbuf = new StringBuilder();
		String deviceName = "ブロードバンド接続";
		int snaplen = 64 * 1024;
		PromiscuousMode flags = PromiscuousMode.NONPROMISCUOUS;
//        int flags = Pcap.MODE_PROMISCUOUS;
		int timeout = 10 * 1000; // 10秒
//        Pcaps pcap = Pcap.openLive(deviceName, snaplen, flags, timeout, errbuf);
		try {
			Enumeration<NetworkInterface> niList_1 = NetworkInterface.getNetworkInterfaces();

			while (niList_1.hasMoreElements()) {
				NetworkInterface ni = niList_1.nextElement();
				if (!ni.isUp()) {
					continue;
				}
				StringBuilder sb = new StringBuilder();
				sb.append("Java : ");
				sb.append(ni.getIndex());
				sb.append(" [");
				sb.append(ni.getDisplayName());
				sb.append("]");

				Enumeration<InetAddress> addressList = ni.getInetAddresses();
				while (addressList.hasMoreElements()) {
					InetAddress address = addressList.nextElement();
					if (address instanceof Inet4Address) {
						sb.append("[");
						sb.append(address.toString());
						sb.append("]");
					}
				}
				System.out.println(sb.toString());
			}
			System.out.println();

			List<PcapNetworkInterface> niList_2 = Pcaps.findAllDevs();
			for (PcapNetworkInterface ni : niList_2) {
				StringBuilder sb = new StringBuilder();
				sb.append("Pcap4j : ");
				sb.append("description: [");
				sb.append(ni.getDescription());
				sb.append("]");
				List<PcapAddress> addressList = ni.getAddresses();
				if (addressList.isEmpty()) {
					continue;
				}
				for (PcapAddress address : addressList) {
					if (address instanceof PcapIpV4Address) {
						sb.append(" address: [");
						sb.append(address.getAddress().getHostAddress());
						sb.append("]");
					}
				}
				System.out.println(sb.toString());
			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		System.out.println();

		PcapNetworkInterface nif;
		try {
			nif = new NifSelector().selectNetworkInterface();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (nif == null) {
			return;
		}

//		    System.out.println(nif.getName() + "(" + nif.getDescription() + ")");
//		    final PcapHandle handle = nif.openLive(SNAPLEN, PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
		final PcapHandle handle = nif.openLive(-1, PromiscuousMode.PROMISCUOUS, 10);

//		String filter = "len > 64";
//		String filter = "tcp";
		String filter = Const.PACKET_FILTEREXEPRESSION;
//	    String filter = "((ip[2:2] - ((ip[0]&0xf)<<2)) - ((tcp[12]&0xf0)>>2)) != 0";
//	    String filter = "tcp port 80 and (((ip[2:2] - ((ip[0]&0xf)<<2)) - ((tcp[12]&0xf0)>>2)) != 0)";
//	    String filter = "tcp port 54632 or tcp port 56621";
//		Inet4Address subnetmask = null;
//		try {
//			subnetmask = (Inet4Address) Inet4Address.getByName("255.255.0.0");
//		} catch (UnknownHostException e1) {
//			// TODO 自動生成された catch ブロック
//			e1.printStackTrace();
//		}
		if (filter.length() != 0) {
		    	handle.setFilter(filter, BpfCompileMode.OPTIMIZE);
//			handle.setFilter(filter, BpfCompileMode.OPTIMIZE, subnetmask);
		}

		PacketListener listener = new PacketListener() {
			@Override
			public void gotPacket(Packet packet) {
				System.out.println("got packet. length : " + packet.length());
				sysoutPacketType(packet);
				// IpV4
				Packet ipV4 = packet.get(EthernetPacket.class).getPayload();
				if(ipV4 == null) {
					return;
				}
				
				// Tcp
				Packet tcp = ipV4.get(IpV4Packet.class).getPayload();
				if(tcp == null) {
					return;
				}
				String tcpHeader = tcp.getHeader().toString();
				System.out.println(tcpHeader);
				String hexData = ByteArrays.toHexString(tcp.getRawData(), " ");
				System.out.println("TCP rawData : " + hexData);
			}

			private Packet getChild(Packet packet) {
				if (packet == null) {
					return null;
				}
				sysoutPacketType(packet);
				
				Packet.Builder innerBuilder = packet.getBuilder();
				if (innerBuilder == null) {
					return packet;
				}
				Packet.Builder innerPayLoadBuilder = innerBuilder.getPayloadBuilder();
				if (innerPayLoadBuilder == null) {
					return packet;
				}
				Packet child = innerPayLoadBuilder.build();
				return child;
			}
			
			private <T extends Packet> Packet getChild_2(T packet) {
				if (packet == null) {
					return null;
				}
				
				T p = packet;
				Packet.Builder builder = p.getBuilder();
				if (builder == null) {
					return packet;
				}
				Packet buiildedPacket = builder.build();
				Packet.Builder payLoadBuilder = buiildedPacket.getBuilder().getPayloadBuilder();
				if (payLoadBuilder == null) {
					return packet;
				}
				Packet child = payLoadBuilder.build();
				return child;
			}

			private void sysoutPacketType(Packet packet) {
				
				if (Packets.containsEthernetPacket(packet)) {
					System.out.println("ethernet packet");
				}
				if (Packets.containsIpPacket(packet)) {
					System.out.println("ip packet");
				}
				if (Packets.containsIpV4Packet(packet)) {
					System.out.println("ipv4 packet");
				}
				if (Packets.containsIpV6Packet(packet)) {
					System.out.println("ipv6 packet");
				}
				if (Packets.containsTcpPacket(packet)) {
					System.out.println("tcp packet");
				}
				if (Packets.containsUdpPacket(packet)) {
					System.out.println("udp packet");
				}
			}
			
			
		};

		try {
			handle.loop(-1, listener);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		PcapStat ps = handle.getStats();
		System.out.println("ps_recv: " + ps.getNumPacketsReceived());
		System.out.println("ps_drop: " + ps.getNumPacketsDropped());
		System.out.println("ps_ifdrop: " + ps.getNumPacketsDroppedByIf());
		if (Platform.isWindows()) {
			System.out.println("bs_capt: " + ps.getNumPacketsCaptured());
		}

		handle.close();
	}

	@Test
	void test_3() {
		TailerListenerAdapter listener = new TailerListenerAdapter() {
			@Override
			public void handle(String line) {
				System.out.println(line);
			}
		};

		Executor executor = new Executor() {
			public void execute(Runnable command) {
				command.run();
			}
		};
		Tailer tailer = Tailer.create(new File(Const.DUMPCAP_LOG_PATH), listener, 1000, true);
		executor.execute(tailer);
	}

	@Test
	void test_4() {
		String rawData = "07606EFBFB3EFBE9AEFBF8BEFBE8DEFBFB10CEFBF9C64556CEFBF9568EFBF9E31EFBEAEEFBF9475EFBF9D23EFBFB0EFBE8AEFBEB550180200EFBFB5EFBFAA00007C002811EFBF8DEFBF8D010000007000581100003B0000006372726573003D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D3D0008007D307762EFBF8036";
//		String expres = "^.*002811CDCD01........0058110000..000000" + Const.BASE_PATTERN + "00" + Const.BASE_PATTERN	+ "00....$";
		String expres = "^.*002811EFBF8DEFBF8D0100......00581100003B000000" + Const.BASE_PATTERN + "00"
				+ Const.BASE_PATTERN + "00.*$";
		String expres1 = "^.*002811EFBF8DEFBF8D0100" + ".*$";
		Pattern pattern = Pattern.compile(expres);

		Matcher matcher = pattern.matcher(rawData);
		boolean isMatch = matcher.matches();
		System.out.println(isMatch + "name[" + matcher.group(1) + "], [" + matcher.group(2) + "]");
	}

	@Test
	void test_5() {
		DataInputStream dis;
		try {
			File f = new File(Const.DUMPCAP_LOG_PATH);
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));

			int i;
			StringBuilder sb = new StringBuilder();
			while ((i = dis.read()) != -1) {
				System.out.printf("%02X", i);
				System.out.println();
				sb.append((char) i);
			}

			System.out.println(sb.toString());
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	@Test
	void test_6() {
		String line = "01000100000010422000010008004500006B46FD40007606CED5CB8DF15BDC64556CDD2DE9A2A900768FB8060EAF501803E4BD74000043005811CCCCCCCC0C84736B79310020948482E881408273926D8EAF94E497A68B528E6D97708378838B83672B31308140343530967B81408EA882E682EB8160816000";
		byte[] b = line.getBytes();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		PacketData data = new PacketData(new Date(timestamp.getTime()), b);
		System.out.println(line);
		System.out.println(data.getStrData());
	}

	private class DumpTailer {
		private static final int SLEEP = 500;
		private File dumpFile;
		private Tailer tailer;

		public DumpTailer(File dumpFile) {
			this.dumpFile = dumpFile;
		}

		public void start() throws InterruptedException {
			MyListener listener = new MyListener();
			Executor executor = new Executor() {
				public void execute(Runnable command) {
					command.run();
				}
			};
			tailer = Tailer.create(this.dumpFile, listener, SLEEP, false);
			executor.execute(tailer);
		}

		public void stop() {
			tailer.stop();
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
					System.out.println();
				}
			}
		}
	}

	/*
	 * private void sysoutAllDevices() { String source = "rpcap://"; List<PcapIf>
	 * devices = new ArrayList<PcapIf>(); StringBuilder errbuf = new
	 * StringBuilder(); int r = WinPcap.findAllDevsEx(source, null,devices, errbuf);
	 * if (r != Pcap.OK) { return; } int i = 0; for(PcapIf device : devices) {
	 * StringBuilder sb = new StringBuilder(); sb.append(i++); sb.append(":");
	 * sb.append(device.getName()); sb.append("[");
	 * sb.append(device.getDescription()); sb.append("]:[");
	 * sb.append(device.getAddresses().toString()); sb.append("]");
	 * System.out.println(sb.toString()); } }
	 */

}
