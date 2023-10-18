package de.ashminh.bikes.application.model.lobby;

import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.common.RoomDto;

import java.util.Collection;
import java.util.List;

public interface ILobby {
    /**
     * Erstellt ein neues Raumobjekt, insofern es noch keins mit dem selben Namen gibt und fügt es zur Lobby hinzu
     *
     * @return
     */
    String openRoom(String roomName);

    /**
     * Der gewählte Raum wird geschlossen, also aus der Lobby entfernt
     */
    void closeRoom(String roomName);

    /**
     * Wenn der Raum geöffnet ist, tritt der Spieler diesem bei (bzw. wird ein neuer Spieler erstellt)
     *
     * @return
     */
    String joinRoom(String roomName, String name, PlayerNumber number);

    List<String> getRoomNames();

    Collection<RoomDto> getRoomDtos();

    boolean containsRoom(String roomName);
}
