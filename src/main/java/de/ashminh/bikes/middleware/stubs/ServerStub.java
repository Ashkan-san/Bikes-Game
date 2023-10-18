package de.ashminh.bikes.middleware.stubs;


import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import de.ashminh.bikes.middleware.common.ApplicationStubCallee;
import de.ashminh.bikes.middleware.common.MethodCall;
import de.ashminh.bikes.middleware.communication.TCPSender;
import de.ashminh.bikes.middleware.nameservice.INameService;
import de.ashminh.bikes.middleware.nameservice.NameService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerStub {
	public int port;
	public boolean isRunning;

	private ParsingUtil unmarshaller;
	private String nameServerLookupEntry;
	private boolean isHostingNameServer;

	private Map<String, ApplicationStubCallee> objectMap;


	public ServerStub(int port, String nameServerLookupEntry, Map<Class<?>, JsonSerializer<?>> serializerMap, Map<Class<?>, JsonDeserializer<?>> deserializerMap, boolean isTcp) {
		this(port, nameServerLookupEntry, serializerMap, deserializerMap, isTcp, false);
	}

	public ServerStub(int port, String nameServerLookupEntry, Map<Class<?>, JsonSerializer<?>> serializerMap, Map<Class<?>, JsonDeserializer<?>> deserializerMap, boolean isTcp, boolean isHostingNameServer) {
		objectMap = new HashMap<>();
		this.port = port;
		unmarshaller = new ParsingUtil(serializerMap, deserializerMap);

		this.nameServerLookupEntry = nameServerLookupEntry;
		this.isHostingNameServer = isHostingNameServer;

		if (isTcp) {
			start();
		} else {
			startUdp();
		}
	}

	public void register(String interfaceName, ApplicationStubCallee callee) {
		System.out.printf("Objekt unter dem Interface %s wurde registriert!%n", interfaceName);

		try {
			var thisLookupEntry = InetAddress.getLocalHost().getHostAddress() + ":" + port;
			if (isHostingNameServer) {
				NameService.getInstance().register(interfaceName, thisLookupEntry);
			} else {
				var clientStub = new ClientStub(nameServerLookupEntry, new TCPSender());
				clientStub.invoke(new MethodCall(INameService.class.getName(), "register", List.of(interfaceName, thisLookupEntry)));
			}

			objectMap.put(interfaceName, callee);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	// Soweit so gut
	public Object call(ApplicationStubCallee applicationStubCallee, MethodCall methodCall) {
		return applicationStubCallee.call(methodCall.interfaceName, methodCall.method, methodCall.args);
	}

	public void start() {
		// TCP socket oeffnen auf port
		new Thread(
				() -> {
					try (
							ServerSocket server = new ServerSocket(port)
					) {
						isRunning = true;

						port = server.getLocalPort();

						System.out.println(InetAddress.getLocalHost());
                        System.out.println("Server Stub läuft auf Addresse: " + server.getLocalSocketAddress());
//						System.out.println("Server Stub laeuft auf Addresse: " + server.getInetAddress());
//						System.out.println("Server Stub laeuft auf Addresse: " + server.getLocalPort());

						while (isRunning) {
							Socket client = server.accept();

							try (var request = new BufferedReader(new InputStreamReader((client.getInputStream())));
								 var response = client.getOutputStream()
							) {

								String json = request.readLine();

								var methodCall = unmarshaller.unmarshal(json);

								Object resultObj = null;

								if (objectMap.containsKey(methodCall.interfaceName)) {
									System.out.printf("invoke(%s) auf %s%n", methodCall.method, methodCall.interfaceName);
//									System.out.println("* CALLING METHOD...");

									var appStubCallee = objectMap.get(methodCall.interfaceName);

									var obj = call(appStubCallee, methodCall);
                                    System.out.println("Return: " + obj);
                                    resultObj = obj;
								} else {
//									System.out.println("Fuck Pfad...");
									System.out.println("Interface ist hier nicht registriert!!!");
								}

								var resultBytes = unmarshaller.serialize(resultObj);

								response.write(resultBytes);

//								System.out.println("> SENT RETURN VALUE");
							} catch (IOException e2) {
								// todo Error
								System.out.println("Streams konnten nicht gelesen werden.");
							}
							System.out.println();
						}
					} catch (IOException e) {
						System.out.printf("Socket konnte nicht erstellt werden!%n");
						e.printStackTrace();
						System.out.println("Ende");
						isRunning = false;
					}
				}
		).start();
	}

	public void startUdp() {
		// TCP socket oeffnen auf port
		new Thread(
				() -> {
                    System.out.println("UDP");
					try (DatagramSocket server = new DatagramSocket(port)) {
						isRunning = true;

                        System.out.println(InetAddress.getLocalHost());
                        System.out.println("Server Stub läuft auf Addresse: " + server.getLocalSocketAddress());

						port = server.getLocalPort();

						byte[] receiveBuffer = new byte[65535];
						DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

						while (isRunning) {
							server.receive(packet);

							System.out.println("GOT CLIENT CONNECTION");

							String json = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

							System.out.println("* Received #" + packet.getLength());

							var methodCall = unmarshaller.unmarshal(json);

							Object resultObj = null;

							if (objectMap.containsKey(methodCall.interfaceName)) {
								System.out.printf("Es will %s aufrufen..%n", methodCall.interfaceName);
								System.out.println("* CALLING METHOD...");

								var appStubCallee = objectMap.get(methodCall.interfaceName);
								var obj = call(appStubCallee, methodCall);
//                                System.out.println("Objekt erzeugt: " + obj);
                                resultObj = obj;
							} else {
								System.out.println("Fuck Pfad...");
							}

							var resultBytes = unmarshaller.serialize(resultObj);

							packet.setData(resultBytes);
							server.send(packet);

							System.out.println("> SENT DATA: " + new String(resultBytes));

							// Wir nutzen 2 Puffer, deswegen setzen wir ihn hier wieder auf den 1
							packet.setData(receiveBuffer);

							System.out.println("> SENT RETURN VALUE");

							System.out.println();
						}
					} catch (IOException e) {
						System.out.printf("Socket konnte nicht erstellt werden!%n");
						e.printStackTrace();
						System.out.println("Ende");
					}
				}
		).start();
	}

	public void stop() {
		isRunning = false;
	}
}
