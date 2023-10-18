package de.ashminh.bikes.application.controller;

import de.ashminh.bikes.application.model.room.PlayerNumber;

public interface IModelController {
    void handleOpenRoomSuccessful(String roomName, PlayerNumber playerNumber);

    void handleOpenRoomFailed(String roomName);

    void handleJoinRoomSuccessful(String roomName, PlayerNumber playerNumber);

    void handleJoinRoomFailed(String roomName);
}
