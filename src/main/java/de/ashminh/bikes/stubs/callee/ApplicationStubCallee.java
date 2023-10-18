package de.ashminh.bikes.stubs.callee;

import de.ashminh.bikes.application.BikesGame;
import de.ashminh.bikes.application.model.room.Direction;
import de.ashminh.bikes.application.model.room.IRoom;
import de.ashminh.bikes.application.model.room.PlayerNumber;

import java.util.List;

public class ApplicationStubCallee implements de.ashminh.bikes.middleware.common.ApplicationStubCallee {
	private static ApplicationStubCallee instance = new ApplicationStubCallee();

	private String roomName;
	private IRoom room;

	private ApplicationStubCallee() {

	}

	public static ApplicationStubCallee getInstance() {
		return instance;
	}

	public void registerRoom(String roomName, IRoom room) {
		this.roomName = roomName;
		this.room = room;
		BikesGame.udpStub.register(String.format("%s#%s", roomName, IRoom.class.getName()), ApplicationStubCallee.getInstance());
	}

	@Override
	public Object call(String interfaceName, String method, List<Object> args) {

		if (interfaceName.equals(String.format("%s#%s", roomName, IRoom.class.getName()))) {
			// todo
			return switch (method) {
				case "updateStatus" -> {
					room.updateStatus();
					yield null;
				}
				case "addPlayer" -> {
					room.addPlayer((String) args.get(0), (PlayerNumber) args.get(1));
					yield null;
				}
				case "restartAt" -> {
                    room.restartAt();
                    yield null;
                }
                case "getDirection" -> room.getDirection((PlayerNumber) args.get(0));
				case "moveBike" -> {
					room.moveBike((PlayerNumber) args.get(0));
					yield null;
				}
				case "isSuicidal" -> room.isSuicidal((PlayerNumber) args.get(0));
				case "getTrail" -> room.getTrail((PlayerNumber) args.get(0));
				case "getBikeFront" -> room.getBikeFront((PlayerNumber) args.get(0));
				case "getTrailColor" -> room.getTrailColor((PlayerNumber) args.get(0));
				case "getPlayerDto" -> room.getPlayerDto((PlayerNumber) args.get(0));

				case "setPlayerName" -> {
					room.setPlayerName((PlayerNumber) args.get(0), (String) args.get(1));
					yield null;
				}
				case "setDirection" -> {
					room.setDirection((PlayerNumber) args.get(0), (Direction) args.get(1));
					yield null;
				}
				case "droveIntoWall" -> room.droveIntoWall((PlayerNumber) args.get(0), (int) args.get(1), (int) args.get(2));

				case "getRoomName" -> room.getRoomName();
				case "getMAX" -> room.getMAX();
				case "isEveryoneReady" -> room.isEveryoneReady();
				case "isFull" -> room.isFull();
				case "isOpen" -> room.isOpen();
				case "size" -> room.size();
				case "getPlayerNames" -> room.getPlayerNames();
				case "getWinnerName" -> room.getWinnerName();
				case "toDto" -> room.toDto();
				case "isGameStarted" -> room.isGameStarted();
				case "isGameOver" -> room.isGameOver();

				case "close" -> {
					room.close();
					yield null;
				}
				case "startGameLoop" -> {
					room.startGameLoop();
					yield null;
				}
				case "stopGameLoop" -> {
					room.stopGameLoop();
					yield null;
				}
				default -> throw new IllegalStateException("Unexpected method invocation: " + method);
			};
		}


		return null;
	}
}
