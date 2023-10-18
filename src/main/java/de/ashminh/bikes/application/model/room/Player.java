package de.ashminh.bikes.application.model.room;

import de.ashminh.bikes.common.PlayerDto;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Player hat:
 * Namen, Startrichtung, Farbe, Startposition x und y, Playernummer
 */
public class Player {
    private String name;
    private final PlayerNumber playerNumber;
    private Color color;
    private Color trailColor;
    private Direction direction;

    private boolean isReady;
    private boolean isWinner;

    private Coordinate startPosition;
    private Coordinate bikeFront;
    private final List<Coordinate> trail = new ArrayList<>();

    public Player(String name, PlayerNumber playerNumber) {
        this.name = name;
        this.playerNumber = playerNumber;
        this.isReady = false;
        setPlayerStuff();
        bikeFront = new Coordinate(startPosition);
    }

    // METHODEN
    public void moveBike() {
        trail.add(bikeFront);

        Coordinate right = new Coordinate(1, 0);
        Coordinate down = new Coordinate(0, 1);

        Coordinate newBikeFront = new Coordinate(bikeFront);

        switch (direction) {
            case UP:
                newBikeFront = bikeFront.sub(down);
                break;
            case DOWN:
                newBikeFront = bikeFront.add(down);
                break;
            case LEFT:
                newBikeFront = bikeFront.sub(right);
                break;
            case RIGHT:
                newBikeFront = bikeFront.add(right);
                break;
        }

        bikeFront = newBikeFront;
    }

    public void setPlayerStuff() {
        // wenn Spieler 1 ist Direction LEFT
        if (playerNumber == PlayerNumber.PLAYER_1) {
            direction = Direction.RIGHT;
            color = Color.web("#c82578");
            startPosition = new Coordinate(10, 15);
        } else {
            // wenn Spieler 2 ist Direction RIGHT
            direction = Direction.LEFT;
            color = Color.web("#2cade5");
            startPosition = new Coordinate(20, 15);
        }

        trail.clear();
        trailColor = color.deriveColor(0.0, 0.5, 2.0, 1.0);
        bikeFront = new Coordinate(startPosition);
    }

    // PRÜFMETHODEN
    public boolean isSuicidal() {
        return trail.contains(bikeFront);
    }

    public boolean droveIntoWall(int rows, int cols) {
        return 0 > bikeFront.x || bikeFront.x > cols || 0 > bikeFront.y || bikeFront.y > rows;
    }

    // GETTER
    public String getName() {
        return name;
    }

    public PlayerNumber getPlayerNumber() {
        return playerNumber;
    }

    public Color getColor() {
        return color;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isReady() {
        return isReady;
    }

    public Coordinate getBikeFront() {
        return bikeFront;
    }

    public List<Coordinate> getTrail() {
        return trail;
    }

    public Color getTrailColor() {
        return trailColor;
    }

    // SETTER
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Sonstiges
    public PlayerDto toDto() {
        return new PlayerDto(bikeFront, color, trail, trailColor, name, playerNumber);
    }
}
