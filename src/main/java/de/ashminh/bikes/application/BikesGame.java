package de.ashminh.bikes.application;

import com.google.gson.*;
import de.ashminh.bikes.application.controller.ControllerImpl;
import de.ashminh.bikes.application.controller.IController;
import de.ashminh.bikes.application.model.IModel;
import de.ashminh.bikes.application.model.ModelImpl;
import de.ashminh.bikes.application.model.room.Direction;
import de.ashminh.bikes.application.model.room.PlayerNumber;
import de.ashminh.bikes.application.view.IView;
import de.ashminh.bikes.application.view.ViewImpl;
import de.ashminh.bikes.common.PlayerDto;
import de.ashminh.bikes.common.RoomDto;
import de.ashminh.bikes.common.Util;
import de.ashminh.bikes.middleware.stubs.ServerStub;
import edu.cads.bai5.vsp.tron.view.Coordinate;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BikesGame extends Application {
    public static ServerStub tcpStub;
    public static ServerStub udpStub;

    static {
        getCustomThings();
        tcpStub = new ServerStub(0, Util.getNameServiceAddress(), BikesGame.serializerMap, BikesGame.deserializerMap, true);
        udpStub = new ServerStub(0, Util.getNameServiceAddress(), BikesGame.serializerMap, BikesGame.deserializerMap, false);
    }

    private IModel model;
    private IView view;
    private IController controller;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(4);

    public static ExecutorService getThreadPool() {
        return threadPool;
    }

    // todo wenn man das Spiel schliesst, dann schliesst sich das Programm nicht mehr vollstaendig
    // vllt: weil wir beim Executor eine feste Anzahl an Thread benutzen, die erst mit shutdown runtergefahren werden
    @Override
    public void start(Stage stage) {
        try {
            stage.getIcons().add(new Image(new FileInputStream("src/main/resources/images/stage-icon.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // MVC
        model = ModelImpl.getInstance();
        view = ViewImpl.getInstance();
        controller = ControllerImpl.getInstance();

        model.initialize();
        controller.initialize();
        view.initialize();

        controller.registerKeyboardEvents();

        // CONFIGURE AND SHOW STAGE
        stage.setTitle("BIKES by Ash and Minh");
        stage.setScene(view.getScene());
        //stage.setX(0);
        //stage.setY(100);
        stage.show(); // zeigt das eigentliche Fenster
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        model.closeRoom();
        threadPool.shutdown();
        tcpStub.stop();
        udpStub.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // todo aufraeumen
    public static Map<Class<?>, JsonSerializer<?>> serializerMap;
    public static Map<Class<?>, JsonDeserializer<?>> deserializerMap;

    public static void getCustomThings() {
        JsonSerializer<Color> colorJsonSerializer = new JsonSerializer<Color>() {
            @Override
            public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.toString());
            }
        };

        JsonDeserializer<Color> colorJsonDeserializer = new JsonDeserializer<Color>() {
            @Override
            public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                System.out.println("Called!");
                var str = json.getAsString();
                return Color.valueOf(str);
            }
        };

        JsonSerializer<PlayerNumber> playerJsonSerializer = new JsonSerializer<PlayerNumber>() {
            @Override
            public JsonElement serialize(PlayerNumber src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.toString());
            }
        };

        JsonDeserializer<PlayerNumber> playerJsonDeserializer = new JsonDeserializer<PlayerNumber>() {
            @Override
            public PlayerNumber deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                var str = json.getAsString();
                return PlayerNumber.valueOf(str);
            }
        };

        var cs = new JsonSerializer<Coordinate>() {
            @Override
            public JsonElement serialize(Coordinate src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(List.of(src.x, src.y));
            }
        };

        var cd = new JsonDeserializer<Coordinate>() {

            @Override
            public Coordinate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                var ary = json.getAsJsonArray();
                return new Coordinate(ary.get(0).getAsInt(), ary.get(1).getAsInt());
            }
        };

        JsonSerializer<Direction> dirJsonSerializer = (src, typeOfSrc, context) -> context.serialize(src.toString());

        JsonDeserializer<Direction> dirJsonDeserializer = (json, typeOfT, context) -> {
            var str = json.getAsString();
            return Direction.valueOf(str);
        };

        JsonSerializer<PlayerDto> pJsonSerializer = (src, typeOfSrc, context) -> {
            return context.serialize(List.of(
                    context.serialize(src.coordinate()),
                    context.serialize(src.color()),
                    context.serialize(src.trail()),
                    context.serialize(src.trailColor()),
                    context.serialize(src.name()),
                    context.serialize(src.playerNumber())
            ));
        };

        JsonDeserializer<PlayerDto> pJsonDeserializer = (json, typeOfT, context) -> {
            var ary = json.getAsJsonArray();

            var trail = ary.get(2).getAsJsonArray();

            List<Coordinate> trailTrail = new ArrayList<>();

            for (var e : trail) {
                trailTrail.add(context.deserialize(e, Coordinate.class));
            }

            return new PlayerDto(
                    context.deserialize(ary.get(0), Coordinate.class),
                    context.deserialize(ary.get(1), Color.class),
                    trailTrail,
                    context.deserialize(ary.get(3), Color.class),
                    ary.get(4).getAsString(),
                    context.deserialize(ary.get(5), PlayerNumber.class)
            );
        };

        JsonSerializer<RoomDto> rJsonSerializer = (src, typeOfSrc, context) -> {
            //System.out.println("Wurde gecalled RoomDTO");
            return context.serialize(List.of(
                    src.playerNames(),
                    src.roomName(),
                    src.max(),
                    src.size(),
                    src.isEveryoneReady(),
                    src.isFull()
            ));
        };

        JsonDeserializer<RoomDto> rJsonDeserializer = (json, typeOfT, context) -> {
            var ary = json.getAsJsonArray();

            var names = ary.get(0).getAsJsonArray();
            List<String> names1 = new ArrayList<>();
            for (var name : names) {
                names1.add(name.getAsString());
            }
            return new RoomDto(
                    names1,
                    ary.get(1).getAsString(),
                    ary.get(2).getAsInt(),
                    ary.get(3).getAsInt(),
                    ary.get(4).getAsBoolean(),
                    ary.get(5).getAsBoolean()
            );
        };

        var s = Map.of(
                Color.class, colorJsonSerializer,
                PlayerNumber.class, playerJsonSerializer,
                Coordinate.class, cs,
                Direction.class, dirJsonSerializer,
                PlayerDto.class, pJsonSerializer,
                RoomDto.class, rJsonSerializer
        );

        var d = Map.of(
                Color.class, colorJsonDeserializer,
                PlayerNumber.class, playerJsonDeserializer,
                Coordinate.class, cd,
                Direction.class, dirJsonDeserializer,
                PlayerDto.class, pJsonDeserializer,
                RoomDto.class, rJsonDeserializer
        );

        serializerMap = s;
        deserializerMap = d;
    }

}
