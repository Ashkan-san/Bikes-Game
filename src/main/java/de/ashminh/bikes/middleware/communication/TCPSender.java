package de.ashminh.bikes.middleware.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCPSender implements Sender {
	public String send(byte[] bytes, InetAddress host, int port) {

		// TCP socket oeffnen auf port
		try (Socket connection = new Socket(host, port)) {
			System.out.println("CONNECTING TO OTHER SOCKET");

			try (var response = connection.getInputStream();
				 var request = (connection.getOutputStream())
			) {
//				gson.toJson(methodCall, MethodCall.class, request);
//				request.writeChars("Hez bitches");
//				System.out.println(new String(bytes));
				request.write(bytes);
				request.write((byte) '\n');
//				request.flush();

				System.out.printf("> SENT %d BYTES%n", bytes.length);

				System.out.println("* WAITING FOR RESPONSE...");

//				result = gson.fromJson(response, ReturnValue.class);
				var resultBytes = response.readAllBytes();

                System.out.printf("< RECEIVED %d BYTES%n", resultBytes.length);
				return new String(resultBytes);
			} catch (IOException e2) {

				System.out.println("Streams konnten nicht gelesen werden.");
				e2.printStackTrace();
			}
		} catch (IOException e) {
			System.out.printf("Socket konnte nicht erstellt werden!%n");
			e.printStackTrace();
			System.out.println("Ende");
		}

		return null;
	}
}
