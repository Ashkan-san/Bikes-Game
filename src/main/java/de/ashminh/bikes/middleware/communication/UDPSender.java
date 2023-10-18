package de.ashminh.bikes.middleware.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPSender implements Sender {
	public String send(byte[] bytes, InetAddress host, int port) {

		// UDP socket oeffnen auf port
		try (var connection = new DatagramSocket()) {
			System.out.println("CONNECTING TO OTHER SOCKET");

			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, host, port);

			connection.send(packet);
			System.out.printf("> SENT %d BYTES%n", bytes.length);

			System.out.println("* WAITING FOR RESPONSE...");

			byte[] receivingBuffer = new byte[65535];

			DatagramPacket receivePacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);

			connection.receive(receivePacket);
			String s = new String(receivingBuffer, 0, receivePacket.getLength(), StandardCharsets.UTF_8);
			System.out.println("< RECEIVED DATA: " + s);

			return s;
		} catch (IOException e) {
			System.out.printf("Socket konnte nicht erstellt werden!%n");
			e.printStackTrace();
			System.out.println("Ende");
		}

		return null;
	}
}
