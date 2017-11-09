
/* CS 380 - Computer Networks
 * Project 4 : IPv6 Client
 * Ismail Abbas & Oscar Alcaraz
 */

import java.io.*;
import java.net.*;

/*
 * This class, similar to project 3, will implement the Ipv6 connection, 
 * with the main difference from Ipv4 being the header size
 */
public class Ipv6Client {

	// The main method to run the project
	public static void main(String[] args) {
		try {
			Socket s = new Socket("18.221.102.182", 38004);
			int packetSize = 2;
			while (packetSize <= 4096) {
				System.out.println("\ndata length: " + packetSize);
				byte[] packet = generatePacket(packetSize, s);
				System.out.println("Response: " + sendReceive(packet, s));
				packetSize *= 2;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method will send the packet to the server, as well as convert the
	 * message to a Hex String, and then receive the message from the server
	 */
	public static String sendReceive(byte[] packetToSend, Socket s) {
		try {
			InputStream isStr = s.getInputStream();
			InputStreamReader isStrRead = new InputStreamReader(isStr, "UTF-8");
			BufferedReader buffRead = new BufferedReader(isStrRead);
			OutputStream outStr = s.getOutputStream();
			outStr.write(packetToSend);
			StringBuilder stringBuild = new StringBuilder();
			for (int i = 0; i < 4; ++i) {
				stringBuild.append(Integer.toHexString(isStr.read()).toUpperCase());
			}
			return stringBuild.toString();
		} catch (Exception e) {
		}
		return "an error occurred";
	}

	/*
	 * This method, according to the Ipv6 standard format, will generate the
	 * packet in bytes to send to the server
	 */
	public static byte[] generatePacket(int size, Socket s) {
		byte[] packetToSend = new byte[size + 40];
		final int version = 6;
		final int trafficClass = 0;
		final int flowLabel = 0;
		final int payLoadLength = size, nextHeader = 17, hopLimit = 20;
		byte[] srcAdd = { (byte) 192, 30, (byte) 252, (byte) 153 };
		byte[] destAdd = s.getInetAddress().getAddress();
		// First Row ////////////
		packetToSend[0] = (version * 16);
		packetToSend[1] = trafficClass;
		packetToSend[2] = trafficClass;
		packetToSend[3] = flowLabel;
		// Second Row /////////
		packetToSend[4] = (byte) ((payLoadLength & 0xFF00) >> 8);
		packetToSend[5] = (byte) (payLoadLength & 0x00FF);
		packetToSend[6] = nextHeader;
		packetToSend[7] = hopLimit;
		// Third Row //////////////
		// Sets up Source Address//
		for (int i = 8; i < 18; ++i) {
			packetToSend[i] = 0;
		}
		packetToSend[18] = (byte) 0xFF;
		packetToSend[19] = (byte) 0xFF;
		for (int j = 20, i = 0; i < srcAdd.length; ++i, ++j) {
			packetToSend[j] = srcAdd[i];
		}
		// Fourth Row //////////////////
		// Sets up Destination Address//
		for (int i = 24; i < 34; ++i) {
			packetToSend[i] = 0;
		}
		packetToSend[34] = (byte) 0xFF;
		packetToSend[35] = (byte) 0xFF;
		for (int j = 36, i = 0; i < destAdd.length; ++i, ++j) {
			packetToSend[j] = destAdd[i];
		}
		// Data //////////////
		for (int i = 0; i < size; ++i) {
			packetToSend[i + 40] = 0;
		}
		return packetToSend;
	}
}
