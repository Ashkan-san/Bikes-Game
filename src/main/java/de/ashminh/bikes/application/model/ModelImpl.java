package de.ashminh.bikes.application.model;

import de.ashminh.bikes.application.controller.ControllerImpl;
import de.ashminh.bikes.application.controller.IModelController;
import de.ashminh.bikes.application.model.lobby.ILobby;
import de.ashminh.bikes.application.model.room.Direction;
import de.ashminh.bikes.application.model.room.IRoom;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.application.model.room.RoomImpl;
import de.ashminh.bikes.common.PlayerDto;
import de.ashminh.bikes.common.RoomDto;
import de.ashminh.bikes.stubs.callee.ApplicationStubCallee;
import de.ashminh.bikes.stubs.caller.LobbyStub;
import de.ashminh.bikes.stubs.caller.RoomStub;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.List;

public class ModelImpl implements IModel {
	private static final IModel instance = new ModelImpl();

	// EINE LOBBY FÜR ALLE ROOMS
	ILobby lobby = LobbyStub.getInstance();
	IRoom room = null;
	private IModelController controller;

	private ModelImpl() {

	}

	public static IModel getInstance() {
		return instance;
	}

	@Override
	public void initialize() {
		controller = ControllerImpl.getInstance();
	}

    @Override
	public void restartAt() {
		room.restartAt();
	}

    @Override
    public void setDirection(PlayerNumber playerNumber, Direction direction) {
		room.setDirection(playerNumber, direction);
    }

    @Override
    public Direction getDirection(PlayerNumber playerNumber) {
		return room.getDirection(playerNumber);
    }

	@Override
	public void openRoom(String roomName) {
		// todo wenn room existiert, schliesse ihn
		if (room != null) {
			closeRoom();
		}

		String playerName = "HostPlayer";
		room = new RoomImpl(roomName, playerName, PlayerNumber.PLAYER_1, 2);

		String roomName2 = lobby.openRoom(roomName);

		if (roomName2 == null) {
			System.out.println("Fehlgeschlagen...");
			room = null;
			controller.handleOpenRoomFailed(roomName);
		} else {
			System.out.println("Erfolgreich...");
			ApplicationStubCallee.getInstance().registerRoom(roomName2, room);
			controller.handleOpenRoomSuccessful(roomName, PlayerNumber.PLAYER_1);
		}
	}

	@Override
	public void joinRoom(String roomName) {
		String playerName = "JoinerPlayer";

		// todo in remote muss hier die peer to peer Weiche sein
		String roomName2 = lobby.joinRoom(roomName, playerName, PlayerNumber.PLAYER_2);
		if (roomName2 == null) {
			controller.handleJoinRoomFailed(roomName);
		} else {
			room = new RoomStub(roomName);
			controller.handleJoinRoomSuccessful(roomName, PlayerNumber.PLAYER_2);
		}
	}

    @Override
    public void closeRoom() {
		if (room != null) {
			lobby.closeRoom(room.getRoomName());
			room = null;
		}
	}

	@Override
	public List<String> getAllRoomNames() {
		return lobby.getRoomNames();
	}

	@Override
	public Collection<RoomDto> getRoomDtos() {
		return lobby.getRoomDtos();
	}

    @Override
    public boolean droveIntoWall(PlayerNumber playerNumber, int rows, int cols) {
		return room.droveIntoWall(playerNumber, rows, cols);
    }

    @Override
    public void moveBike(PlayerNumber playerNumber) {
		room.moveBike(playerNumber);
    }

    @Override
    public boolean isSuicidal(PlayerNumber playerNumber) {
		return room.isSuicidal(playerNumber);
    }

    @Override
    public List<Coordinate> getTrail(PlayerNumber playerNumber) {
		return room.getTrail(playerNumber);
    }

    @Override
    public Coordinate getBikeFront(PlayerNumber playerNumber) {
		return room.getBikeFront(playerNumber);
    }

    @Override
    public void setPlayerName(PlayerNumber playerNumber, String name) {
		room.setPlayerName(playerNumber, name);
	}

	@Override
	public String getWinnerName() {
		if (room != null) {
			return room.getWinnerName();
		}
		return "";
	}

	@Override
	public Color getTrailColor(PlayerNumber playerNumber) {
		return room.getTrailColor(playerNumber);
	}

	@Override
	public PlayerDto getPlayerDto(PlayerNumber playerNumber) {
		if (room == null) {
			return null;
		}
		return room.getPlayerDto(playerNumber);
	}

	@Override
	public boolean isGameStarted() {
		if (room == null)
			return false;
		return room.isGameStarted();
	}

	@Override
	public boolean isGameOver() {
		if (room == null)
			return false;
		return room.isGameOver();
	}

	@Override
	public void startGameLoop() {
		if (room != null)
			room.startGameLoop();
	}

	@Override
	public void stopGameLoop() {
		if (room != null)
			room.stopGameLoop();
	}
}
