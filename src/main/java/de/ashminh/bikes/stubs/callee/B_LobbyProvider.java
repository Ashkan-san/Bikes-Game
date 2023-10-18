package de.ashminh.bikes.stubs.callee;

import de.ashminh.bikes.application.BikesGame;
import de.ashminh.bikes.application.model.lobby.ILobby;
import de.ashminh.bikes.common.Util;
import de.ashminh.bikes.middleware.common.ApplicationStubCallee;
import de.ashminh.bikes.middleware.stubs.ServerStub;

public class B_LobbyProvider {
    public static void main(String[] args) {
        BikesGame.getCustomThings();
        ServerStub s = new ServerStub(0, Util.getNameServiceAddress(), BikesGame.serializerMap, BikesGame.deserializerMap, true);

        ApplicationStubCallee callee = new LobbyStubCallee();

//		s.start();
        s.register(ILobby.class.getName(), callee);
    }
}
