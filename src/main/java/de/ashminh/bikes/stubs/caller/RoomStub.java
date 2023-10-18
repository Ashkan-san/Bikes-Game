package de.ashminh.bikes.stubs.caller;

import de.ashminh.bikes.application.BikesGame;
import de.ashminh.bikes.application.model.room.Direction;
import de.ashminh.bikes.application.model.room.IRoom;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.common.PlayerDto;
import de.ashminh.bikes.common.RoomDto;
import de.ashminh.bikes.common.Util;
import de.ashminh.bikes.middleware.common.MethodCall;
import de.ashminh.bikes.middleware.communication.UDPSender;
import de.ashminh.bikes.middleware.stubs.ClientStub;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.scene.paint.Color;

import java.util.List;

public class RoomStub implements IRoom {
	private final String interfaceName;

	String nameServiceAddress = Util.getNameServiceAddress();
	private ClientStub clientStub;

	public RoomStub(String roomName) {
		interfaceName = String.format("%s#%s", roomName, IRoom.class.getName());
		BikesGame.getCustomThings();
		clientStub = new ClientStub(nameServiceAddress, new UDPSender(), BikesGame.serializerMap, BikesGame.deserializerMap);
	}

	@Override
	public void updateStatus() {
		clientStub.invoke(new MethodCall(interfaceName, "updateStatus", List.of()));
	}

	@Override
	public void addPlayer(String name, PlayerNumber playerNumber) {
		clientStub.invoke(new MethodCall(interfaceName, "addPlayer", List.of(name, playerNumber)));
	}

	@Override
	public void restartAt() {
		clientStub.invoke(new MethodCall(interfaceName, "restartAt", List.of()));
	}

	@Override
	public void setDirection(PlayerNumber playerNumber, Direction direction) {
		clientStub.invoke(new MethodCall(interfaceName, "setDirection", List.of(playerNumber, direction)));

	}

	@Override
	public Direction getDirection(PlayerNumber playerNumber) {

		return (Direction) clientStub.invoke(new MethodCall(interfaceName, "getDirection", List.of(playerNumber)));

	}

	@Override
	public boolean droveIntoWall(PlayerNumber playerNumber, int rows, int cols) {
		return (boolean) clientStub.invoke(new MethodCall(interfaceName, "droveIntoWall", List.of(playerNumber, rows, cols)));

	}

	@Override
	public void moveBike(PlayerNumber playerNumber) {
		clientStub.invoke(new MethodCall(interfaceName, "moveBike", List.of(playerNumber)));

	}

	@Override
	public boolean isSuicidal(PlayerNumber playerNumber) {
		return (boolean) clientStub.invoke(new MethodCall(interfaceName, "isSuicidal", List.of(playerNumber)));

	}

	@Override
	public List<Coordinate> getTrail(PlayerNumber playerNumber) {
		return (List<Coordinate>) clientStub.invoke(new MethodCall(interfaceName, "getTrail", List.of(playerNumber)));

	}

	@Override
	public Coordinate getBikeFront(PlayerNumber playerNumber) {
		return (Coordinate) clientStub.invoke(new MethodCall(interfaceName, "getBikeFront", List.of(playerNumber)));

	}

	@Override
	public void setPlayerName(PlayerNumber playerNumber, String name) {
		clientStub.invoke(new MethodCall(interfaceName, "setPlayerName", List.of(playerNumber, name)));

	}

	@Override
	public Color getTrailColor(PlayerNumber playerNumber) {
		return (Color) clientStub.invoke(new MethodCall(interfaceName, "getTrailColor", List.of(playerNumber)));

	}

	@Override
	public PlayerDto getPlayerDto(PlayerNumber playerNumber) {
		return (PlayerDto) clientStub.invoke(new MethodCall(interfaceName, "getPlayerDto", List.of(playerNumber)));

	}

	@Override
	public String getRoomName() {
		return (String) clientStub.invoke(new MethodCall(interfaceName, "getRoomName", List.of()));

	}

	@Override
	public int getMAX() {
		return (int) clientStub.invoke(new MethodCall(interfaceName, "getMAX", List.of()));

	}

	@Override
	public boolean isEveryoneReady() {
		return (boolean) clientStub.invoke(new MethodCall(interfaceName, "isEveryoneReady", List.of()));

	}

	@Override
	public boolean isFull() {
		return (boolean) clientStub.invoke(new MethodCall(interfaceName, "isFull", List.of()));

	}

	@Override
	public boolean isOpen() {
		return (boolean) clientStub.invoke(new MethodCall(interfaceName, "isOpen", List.of()));

	}

	@Override
	public int size() {
		return (int) clientStub.invoke(new MethodCall(interfaceName, "size", List.of()));
	}

	@Override
	public List<String> getPlayerNames() {
		return (List<String>) clientStub.invoke(new MethodCall(interfaceName, "getPlayerNames", List.of()));
	}

	@Override
	public String getWinnerName() {
		return (String) clientStub.invoke(new MethodCall(interfaceName, "getWinnerName", List.of()));
	}

	@Override
	public RoomDto toDto() {
		return (RoomDto) clientStub.invoke(new MethodCall(interfaceName, "toDto", List.of()));

	}

	@Override
	public void close() {
		clientStub.invoke(new MethodCall(interfaceName, "close", List.of()));
	}

	@Override
	public boolean isGameStarted() {
		return (boolean) clientStub.invoke(new MethodCall(interfaceName, "isGameStarted", List.of()));
	}

	@Override
	public boolean isGameOver() {
		return (boolean) clientStub.invoke(new MethodCall(interfaceName, "isGameOver", List.of()));
	}

	@Override
	public void startGameLoop() {
		clientStub.invoke(new MethodCall(interfaceName, "startGameLoop", List.of()));
	}

	@Override
	public void stopGameLoop() {
		clientStub.invoke(new MethodCall(interfaceName, "stopGameLoop", List.of()));
	}
}
