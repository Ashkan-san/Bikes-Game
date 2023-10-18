package de.ashminh.bikes.application.controller;

import de.ashminh.bikes.application.model.room.Direction;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.common.PlayerDto;
import de.ashminh.bikes.common.RoomDto;

import java.util.Collection;

public interface IViewController {
    /**
     * Was Ingame regelmäßig passieren/überprüft werden soll
     */
    void tick();

    /**
     * Startet das Spiel
     */
    void startGame(boolean isRestart);

    /**
     * Beendet das Spiel
     */
    void endGame();

    /**
     * Pausiert das Spiel
     */
    void pauseGame();

    /**
     * Setzt das Spiel nach Pausierung fort
     */
    void resumeGame();

    /**
     * Registriert allgemeine Keyboard Events (Escape zum Schließen z.B.)
     */
    void registerKeyboardEvents();

    /**
     * Registriert die jeweiligen Keyboard Events für den Spieler (Richtung des Bikes verändern)
     */
    void registerGameKeyboardEvents(PlayerNumber playerNumber);

    /**
     * Ruft in Model die Methode zum Setzen des Spielernamens auf
     */
    void handleInputName(String playerName);

    /**
     * Ruft in Model die JoinRoom Methode für einen bestimmten Raum auf
     */
    void handlePressedJoin(String roomName);

    /**
     * Ruft in Model die OpenRoom Methode für einen bestimmten Raum auf
     */
    void handlePressedOpen(String roomName);

    /**
     * Wenn das Spiel läuft wird der Pause Screen gezeigt, sonst wird immer die Anwendung geschlossen
     */
    void handlePressedEscape();

    void setDirection(PlayerNumber playerNumber, Direction direction);

    Collection<RoomDto> getRoomDtos();

    PlayerDto getPlayerDto(PlayerNumber playerNumber);
}
