package com.madirex.masteroids.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Util {
    private Util(){
    }

    public static final boolean DEBUG = false; //MODO DEBUG
    public static final int SPRITE_RESIZE = 3_000;
    public static final double COLLISION_BOX_PLAYER_RESIZE = 3;
    public static final double COLLISION_BOX_OBJECT_RESIZE = 2;
    public static final String SOUNDS_FOLDER = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "sounds" + File.separator;
    public static final String MUSIC_FOLDER = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "music" + File.separator;
    public static final String SVG_FOLDER = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "svg" + File.separator;
    public static final String IMAGE_FOLDER = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "image" + File.separator;
    public static final String BACKGROUND_NAME = "space";
    public static final String BACKGROUND_NAME_TWO = "spaceTwo";
    public static final int POINTS_TO_KILL_ASTEROID = 20;

    //Player
    public static final int SECONDS_INMORTALITY_PLAYER = 2;

    //Objetos
    public static final int MAX_PLANETS = 1;
    public static final double PROBABILIDAD_SPAWN_PLANET = 100;
    public static final String PLANET_SPRITE_FOLDER = "planets" + File.separator + "planet";
    public static final double SPEED_PLANET = 0;

    public static final int MAX_ASTEROIDS = 15;
    public static final double PROBABILIDAD_SPAWN_ASTEROID = 100;
    public static final String ASTEROID_SPRITE_FOLDER = "asteroids" + File.separator + "asteroid";
    public static final double SPEED_ASTEROID = 0.5;

    public static final int MAX_POWERUPS = 1;
    public static final double PROBABILIDAD_SPAWN_POWERUP = 20;
    public static final String POWER_UP_SPRITE_FOLDER = "powerups" + File.separator + "powerup";
    public static final double SPEED_POWER_UP = 0.75;

    //Estilos
    public static final String BUTTON_STYLE = "-fx-padding: 8 10 10 10;" +
            "-fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 7 0;\n" +
            "-fx-background-radius: 10;\n" +
            "-fx-background-color: linear-gradient(from 0% 93% to 0% 100%, #afadac 0%, #b2b2b2 100%)," +
            "#a89e9b,#dcdbda, radial-gradient(center 50% 50%, radius 100%, #d7d7d7, #cbc5c3);\n";
    public static final String BUTTON_STYLE_HOVER = "-fx-background-insets: 0,0 0 5 0, 0 0 6 0, 0 0 4 0;\n" +
            "-fx-background-radius: 10;\n" +
            "-fx-background-color: linear-gradient(from 0% 93% to 0% 100%, #afa7a4 0%, #bdb5b5 100%)," +
            "#c9bebb,#d3d2d1, radial-gradient(center 50% 50%, radius 100%, #d0d0d0, #c5bfbc);\n";

    public static MediaPlayer sound(String soundURL, double volume) {
        File fileSound = new File(Util.SOUNDS_FOLDER + soundURL);
        Media sound = new Media(fileSound.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setAutoPlay(false);
        mediaPlayer.play();
        mediaPlayer.setVolume(volume);

        return mediaPlayer;
    }

    public static double random(int min, int max){
        return Math.floor(Math.random()*(max-min+1)+min);
    }
}
