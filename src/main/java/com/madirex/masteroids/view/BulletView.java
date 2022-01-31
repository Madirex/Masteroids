package com.madirex.masteroids.view;

import com.madirex.masteroids.utils.Util;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class BulletView extends Rectangle {
    private Point2D point2D;
    private final StackPane stackPane;
    private final MediaView mediaView;

    public BulletView(StackPane stackPane){
        this.stackPane = stackPane;
        this.mediaView = new MediaView();
        initBullet();
    }

    private void initBullet() {
        //Sound
        Random random = new Random();
        MediaPlayer sound = Util.sound("shoot.mp3",0.1);
        mediaView.setMediaPlayer(sound);

        //Pitch distinto para que el sonido no se haga muy repetitivo
        int nRandom = random.nextInt(20);
        if (nRandom >= 15){
            sound.setRate(0.8);
        }else if (nRandom >= 10){
            sound.setRate(0.9);
        } else{
            sound.setRate(1);
        }

        //Size
        this.setHeight(28 * (stackPane.getHeight() / Util.SPRITE_RESIZE));
        this.setWidth(80 * (stackPane.getHeight() / Util.SPRITE_RESIZE));

        //Bullet settings
        setStroke(Color.WHITE); //Color del borde de la bala
        setStrokeWidth(1); //Tamaño del borde de la bala
        setFill(Color.GREEN); //Color de la bala

        //Agregar a StackPane
        stackPane.getChildren().add(this);
        StackPane.setAlignment(this, Pos.CENTER);
    }

    public Point2D getPoint2D() {
        return point2D;
    }

    public void setPoint2D(Point2D point2D) {
        this.point2D = point2D;
    }

    public void destroyObject(){
        stackPane.getChildren().remove(this);
    }

}
