package de.ashminh.bikes.application.model.room;

import de.ashminh.bikes.common.PlayerDto;
import de.ashminh.bikes.common.RoomDto;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomImpl implements IRoom {
	private String roomName;
	public Map<PlayerNumber, Player> players;

	private boolean isFull = false;
	private boolean everyoneReady = false;
	private boolean gameStarted = false;
	private boolean gameOver = false;

	private String winner = "Nobody";

	private final int MAX; // sollte 2 sein

	private Timeline timeline;

	public RoomImpl(String roomName, String name, PlayerNumber number, int MAX) {
		this.roomName = roomName;
		players = new HashMap<>();
		this.MAX = MAX;

		addPlayer(name, number);

		// TODO: muss regelmäßig überprüft werden
		updateStatus();

		timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> updateGame()));
		timeline.setCycleCount(Animation.INDEFINITE);
	}

	private void updateGame() {
		// BIKE BEWEGUNG
		moveBike(PlayerNumber.PLAYER_1);
		moveBike(PlayerNumber.PLAYER_2);

		// collision check

		boolean p1_droveIntoWall = droveIntoWall(PlayerNumber.PLAYER_1, 30, 30);
		boolean p2_droveIntoWall = droveIntoWall(PlayerNumber.PLAYER_2, 30, 30);

		boolean p1_suicidal = isSuicidal(PlayerNumber.PLAYER_1);
		boolean p2_suicidal = isSuicidal(PlayerNumber.PLAYER_2);

		PlayerNumber dead = hitOpponent();

		boolean p1_died = p1_droveIntoWall || p1_suicidal || PlayerNumber.PLAYER_1.equals(dead) || PlayerNumber.ALL.equals(dead);
		boolean p2_died = p2_droveIntoWall || p2_suicidal || PlayerNumber.PLAYER_2.equals(dead) || PlayerNumber.ALL.equals(dead);

		if (p1_died && p2_died) {
			winner = "Nobody";
			gameOver();
			return;
		}

		if (p1_died) {
			winner = players.get(PlayerNumber.PLAYER_2).getName();
			gameOver();
			return;
		}

		if (p2_died) {
			winner = players.get(PlayerNumber.PLAYER_1).getName();
			gameOver();
			return;
		}

	}

	// METHODEN
	@Override
	public void updateStatus() {
		isFull = players.size() == MAX;
		everyoneReady = false;

		if (isFull) {
			for (Player player : players.values()) {
				if (!player.isReady()) {
					return;
				}
			}
			everyoneReady = true;
		}
	}

    // Integritaetschecks passieren in Lobby#joinRoom
    @Override
    public void addPlayer(String name, PlayerNumber playerNumber) {
        if (isFull) {
            // todo was macht man hier?
        } else {
            players.put(playerNumber, new Player(name, playerNumber));
        }
    }

	@Override
	public void restartAt() {
		Player player1 = players.get(PlayerNumber.PLAYER_1);
		Player player2 = players.get(PlayerNumber.PLAYER_2);
		player1.setPlayerStuff();
		player2.setPlayerStuff();

		gameOver = false;
	}

	/**
	 * Bestimme den  Gewinner, jenachdem wer wen berührt
	 */
	public PlayerNumber hitOpponent() {
		Player player1 = players.get(PlayerNumber.PLAYER_1);
		Player player2 = players.get(PlayerNumber.PLAYER_2);

		boolean p1_dead = player2.getTrail().contains(player1.getBikeFront());
		boolean p2_dead = player1.getTrail().contains(player2.getBikeFront());

		if (p1_dead && p2_dead) {
			return PlayerNumber.ALL;
		} else if (p1_dead) {
			return PlayerNumber.PLAYER_1;
		} else if (p2_dead) {
			return PlayerNumber.PLAYER_2;
		} else {
			return null;
		}
	}

    // GETTER
    @Override
    public String getRoomName() {
        return roomName;
    }

    @Override
    public int getMAX() {
        return MAX;
    }

    @Override
    public boolean isEveryoneReady() {
        return everyoneReady;
    }

    @Override
    public boolean isFull() {
        return isFull;
    }

    @Override
    public boolean isOpen() {
        return !isFull;
    }

    @Override
    public int size() {
        return players.size();
    }

    @Override
    public List<String> getPlayerNames() {
        return players.values().stream().map(Player::getName).collect(Collectors.toList());
    }

    @Override
    public String getWinnerName() {
		return winner;
    }

	@Override
	public RoomDto toDto() {
		return new RoomDto(getPlayerNames(), roomName, MAX, size(), everyoneReady, isFull());
	}

	@Override
	public void close() {
		// todo
		timeline.stop();
		System.out.println("CLOSE ROOM");
	}

	@Override
	public boolean isGameStarted() {
		return gameStarted;
	}

	@Override
	public boolean isGameOver() {
		return gameOver;
	}

	@Override
	public void startGameLoop() {
		gameStarted = true;
		timeline.play();
	}

	private void gameOver() {
		gameOver = true;
		gameStarted = false;
		stopGameLoop();
	}

	@Override
	public void stopGameLoop() {
		timeline.stop();
	}

	// Delegationsmethoden
	@Override
	public void setDirection(PlayerNumber playerNumber, Direction direction) {
		players.get(playerNumber).setDirection(direction);
	}

	@Override
	public Direction getDirection(PlayerNumber playerNumber) {
		return players.get(playerNumber).getDirection();
	}

    @Override
    public boolean droveIntoWall(PlayerNumber playerNumber, int rows, int cols) {
        return players.get(playerNumber).droveIntoWall(rows, cols);
    }

    @Override
    public void moveBike(PlayerNumber playerNumber) {
        players.get(playerNumber).moveBike();
    }

    @Override
    public boolean isSuicidal(PlayerNumber playerNumber) {
        return players.get(playerNumber).isSuicidal();
    }

    @Override
    public List<Coordinate> getTrail(PlayerNumber playerNumber) {
        return players.get(playerNumber).getTrail();
    }

    @Override
    public Coordinate getBikeFront(PlayerNumber playerNumber) {
        return players.get(playerNumber).getBikeFront();
    }

    @Override
    public void setPlayerName(PlayerNumber playerNumber, String name) {
        players.get(playerNumber).setName(name);
    }

    @Override
    public Color getTrailColor(PlayerNumber playerNumber) {
        return players.get(playerNumber).getTrailColor();
    }

    @Override
    public PlayerDto getPlayerDto(PlayerNumber playerNumber) {
        if (players.containsKey(playerNumber)) {
            return players.get(playerNumber).toDto();
        }
        return null;
    }
}
