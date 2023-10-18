package de.ashminh.bikes.middleware.stubs;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import de.ashminh.bikes.middleware.common.MethodCall;
import de.ashminh.bikes.middleware.communication.Sender;
import de.ashminh.bikes.middleware.communication.TCPSender;
import de.ashminh.bikes.middleware.nameservice.INameService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class ClientStub {
	private ParsingUtil marshaller;
	private String nameServerLookupEntry;
	private Sender sender;

	public ClientStub(String nameServerLookupEntry, Sender sender, Map<Class<?>, JsonSerializer<?>> serializerMap, Map<Class<?>, JsonDeserializer<?>> deserializerMap) {
		this.nameServerLookupEntry = nameServerLookupEntry;

		marshaller = new ParsingUtil(serializerMap, deserializerMap);

		this.sender = sender;
	}

	public ClientStub(String nameServerLookupEntry, Sender sender) {
		this(nameServerLookupEntry, sender, Map.of(), Map.of());
	}

	public Object invoke(MethodCall call) {
		String ipAndPort = lookup(call.interfaceName);
//		System.out.printf("Lookup yields: Entry: %s%n", ipAndPort);

		if (ipAndPort == null) {
			throw new RuntimeException("Konnte das Interface nicht finden...");
		}

		var splitResult = ipAndPort.split(":");

		if (splitResult.length != 2) {
			System.out.printf("Got '%s'%n", ipAndPort);
		}

		String ipPart = splitResult[0];
		String portPart = splitResult[1];

		InetAddress ip = InetAddress.getLoopbackAddress();

		try {
			ip = InetAddress.getByName(ipPart);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		int port = 8000;

		try {
			port = Integer.parseInt(portPart);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		System.out.printf("Try to invoke(%s)%n%n", call.method);
		byte[] json = marshaller.marshall(call);
		var returnMessage = sender.send(json, ip, port);
		return marshaller.parse(returnMessage);
	}

	private String lookup(String interfaceName) {
		var splitResult = nameServerLookupEntry.split(":");

		String ipPart = splitResult[0];
		String portPart = splitResult[1];

		InetAddress ip = InetAddress.getLoopbackAddress();
		try {
			ip = InetAddress.getByName(ipPart);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		int port = 8000;

		try {
			port = Integer.parseInt(portPart);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		// Namensaufloesung muss hiermit TCP sein!!! Weil Name Service bisher kein Udp serverstub hat
		byte[] json = marshaller.marshall(new MethodCall(INameService.class.getName(), "lookup", List.of(interfaceName)));
		var returnMessage = new TCPSender().send(json, ip, port);
		return (String) marshaller.parse(returnMessage);
	}
}
