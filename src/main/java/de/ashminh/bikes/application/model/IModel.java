package de.ashminh.bikes.application.model;

import de.ashminh.bikes.application.model.room.Direction;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.common.PlayerDto;
import de.ashminh.bikes.common.RoomDto;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.List;

/**
 * Die Schnittstelle unseres Models
 */
public interface IModel {
	void initialize();

	// Game Funktionen
    void restartAt();

	void setDirection(PlayerNumber playerNumber, Direction direction);

	Direction getDirection(PlayerNumber playerNumber);

	// Lobby Funktionen
	void openRoom(String roomName);

	void joinRoom(String roomName);

	List<String> getAllRoomNames();

	Collection<RoomDto> getRoomDtos();

	boolean droveIntoWall(PlayerNumber playerNumber, int rows, int cols);

	void moveBike(PlayerNumber playerNumber);

	boolean isSuicidal(PlayerNumber playerNumber);

	List<Coordinate> getTrail(PlayerNumber playerNumber);

	Coordinate getBikeFront(PlayerNumber playerNumber);

	void setPlayerName(PlayerNumber playerNumber, String name);

	String getWinnerName();

	void closeRoom();

	Color getTrailColor(PlayerNumber playerNumber);

	PlayerDto getPlayerDto(PlayerNumber playerNumber);

	boolean isGameStarted();

	boolean isGameOver();

	void startGameLoop();

	void stopGameLoop();
}
