package de.ashminh.bikes.stubs.callee;

import de.ashminh.bikes.application.model.lobby.ILobby;
import de.ashminh.bikes.application.model.lobby.LobbyImpl;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.middleware.common.ApplicationStubCallee;

import java.util.List;

public class LobbyStubCallee implements ApplicationStubCallee {

	@Override
	public Object call(String interfaceName, String method, List<Object> args) {

		if (interfaceName.equals(ILobby.class.getName())) {
			// todo
			return switch (method) {
				case "openRoom" -> LobbyImpl.getInstance().openRoom((String) args.get(0));
				case "closeRoom" -> {
					LobbyImpl.getInstance().closeRoom((String) args.get(0));
					yield null;
				}
				case "containsRoom" -> LobbyImpl.getInstance().containsRoom((String) args.get(0));

				case "joinRoom" -> LobbyImpl.getInstance().joinRoom((String) args.get(0), (String) args.get(1), (PlayerNumber) args.get(2));

				case "getRoomNames" -> LobbyImpl.getInstance().getRoomNames();
				case "getRoomDtos" -> LobbyImpl.getInstance().getRoomDtos();
				default -> throw new IllegalStateException("Unexpected Method invocation: " + method);
			};
		}

		return null;
	}
}
