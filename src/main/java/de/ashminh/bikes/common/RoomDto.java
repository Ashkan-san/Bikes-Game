package de.ashminh.bikes.common;

import java.util.List;

public record RoomDto(List<String> playerNames, String roomName, int max, int size, boolean isEveryoneReady,
					  boolean isFull) {
}
