package de.ashminh.bikes.application.model.lobby;

import de.ashminh.bikes.application.model.room.IRoom;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.common.RoomDto;
import de.ashminh.bikes.stubs.caller.RoomStub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LobbyImpl implements ILobby {

	private static ILobby instance = new LobbyImpl();

	private LobbyImpl() {
	}

	public static ILobby getInstance() {
		return instance;
	}

	// Roomname -> Room
	private final List<String> roomNames = new ArrayList<>();

	// METHODEN
	@Override
	public String openRoom(String roomName) {
		// Beim Raum erstellen muss folgendes passieren:
		// 1. Raum muss erstellt werden, Raum ist offen
		// 2. Ersteller wird zu PLAYER_1

		if (containsRoom(roomName)) {
			System.out.println("Lobby: Name ist schon vergeben!");
			return null;
		} else {
			// player halten ja ihre Coordinates
			// deswegen ist Player ja in Game
			// aber der Joiner hat noch gar kein Game object

			roomNames.add(roomName);

			return roomName;
		}
	}

	@Override
	public void closeRoom(String roomName) {
		// Wenn der Host Raum schließt
		// Wenn Host Spiel abstürzt
		// 1. Raum löschen
		// 2. Zurück zum Homescreen
		// 3. evt. Fehlermeldung oder Alert
		if (containsRoom(roomName)) {
			var room = new RoomStub(roomName);
			room.close();
			roomNames.remove(roomName);
		} else {
			System.out.println("Room: Den Raum gibt es garnicht!");
		}

	}

	@Override
	public String joinRoom(String roomName, String name, PlayerNumber number) {
		if (containsRoom(roomName)) {
			var room = new RoomStub(roomName);

			if (room.isOpen()) {
				// sich selbst in das Spiel und den Raum adden
				room.addPlayer(name, number);
				room.updateStatus();

				return roomName;
			} else {
				// todo Exception oder Warnung
				return null;
			}
		} else {
			System.out.println("Room: Den Raum gibt es garnicht!");
			return null;
		}
	}

	@Override
	public List<String> getRoomNames() {
		return roomNames;
	}

	// GETTER
	// wird vom Controller aufgerufen
	@Override
	public Collection<RoomDto> getRoomDtos() {
		Collection<RoomDto> rooms = new ArrayList<>();

		for (var name : roomNames) {
			IRoom room = new RoomStub(name);
			rooms.add(room.toDto());
		}
		return rooms;
	}

	@Override
	public boolean containsRoom(String roomName) {
		return roomNames.contains(roomName);
	}

}
