package de.ashminh.bikes.application.model.room;

import de.ashminh.bikes.common.PlayerDto;
import de.ashminh.bikes.common.RoomDto;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.scene.paint.Color;

import java.util.List;

public interface IRoom {
	/**
	 * Bestimmt den Status der Spieler, ob diese ready sind für das Spiel oder nicht
	 */
	void updateStatus();

	/**
	 * Füge einen neuen Spieler zum Raum hinzu
	 */
	void addPlayer(String name, PlayerNumber playerNumber);

	/**
     * Lass den Spieler an seiner Startposition neustarten und setzt alles zurück
     */
    void restartAt();

	// Delegationsmethoden

	Direction getDirection(PlayerNumber playerNumber);

	void moveBike(PlayerNumber playerNumber);

	boolean isSuicidal(PlayerNumber playerNumber);

	List<Coordinate> getTrail(PlayerNumber playerNumber);

	Coordinate getBikeFront(PlayerNumber playerNumber);


	// zwangsweise Delegationsmethoden
	Color getTrailColor(PlayerNumber playerNumber);

	PlayerDto getPlayerDto(PlayerNumber playerNumber);

	boolean droveIntoWall(PlayerNumber playerNumber, int rows, int cols);

	void setPlayerName(PlayerNumber playerNumber, String name);

	void setDirection(PlayerNumber playerNumber, Direction direction);

	// GETTER & SETTER
	String getRoomName();

	int getMAX();

	boolean isEveryoneReady();

	boolean isFull();

	boolean isOpen();

	int size();

	List<String> getPlayerNames();

	String getWinnerName();

	RoomDto toDto();

	void close();

	boolean isGameStarted();

	boolean isGameOver();

	void startGameLoop();

	void stopGameLoop();
}
