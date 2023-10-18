package de.ashminh.bikes.middleware.communication;

import java.net.InetAddress;

public interface Sender {
	String send(byte[] bytes, InetAddress host, int port);
}
