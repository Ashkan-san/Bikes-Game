package de.ashminh.bikes.stubs.caller;

import de.ashminh.bikes.application.BikesGame;
import de.ashminh.bikes.application.model.lobby.ILobby;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.common.RoomDto;
import de.ashminh.bikes.common.Util;
import de.ashminh.bikes.middleware.common.MethodCall;
import de.ashminh.bikes.middleware.communication.TCPSender;
import de.ashminh.bikes.middleware.stubs.ClientStub;

import java.util.Collection;
import java.util.List;

public class LobbyStub implements ILobby {
	private static ILobby instance = new LobbyStub();

	private final String interfaceName = ILobby.class.getName();

	String nameServiceAddress = Util.getNameServiceAddress();
	private ClientStub clientStub;

	private LobbyStub() {
		BikesGame.getCustomThings();
		clientStub = new ClientStub(nameServiceAddress, new TCPSender(), BikesGame.serializerMap, BikesGame.deserializerMap);
	}

	public static ILobby getInstance() {
		return instance;
	}

	@Override
	public String openRoom(String roomName) {
		return (String) clientStub.invoke(new MethodCall(interfaceName, "openRoom", List.of(roomName)));
	}

	@Override
	public void closeRoom(String roomName) {
		clientStub.invoke(new MethodCall(interfaceName, "closeRoom", List.of(roomName)));
	}

	@Override
	public String joinRoom(String roomName, String name, PlayerNumber number) {
		return (String) clientStub.invoke(new MethodCall(interfaceName, "joinRoom", List.of(roomName, name, number)));
	}

	@Override
	public List<String> getRoomNames() {
		return (List<String>) clientStub.invoke(new MethodCall(interfaceName, "getRoomNames", List.of()));
	}

	@Override
	public Collection<RoomDto> getRoomDtos() {
		return (Collection<RoomDto>) clientStub.invoke(new MethodCall(interfaceName, "getRoomDtos", List.of()));
	}

	@Override
	public boolean containsRoom(String roomName) {
		return (boolean) clientStub.invoke(new MethodCall(interfaceName, "containsRoom", List.of(roomName)));
	}
}
