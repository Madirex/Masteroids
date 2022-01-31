package com.madirex.masteroids.view;

import com.madirex.masteroids.controller.GameController;
import com.madirex.masteroids.utils.SVGUtils;
import com.madirex.masteroids.utils.Util;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.io.File;

public class GameView extends BorderPane {
    private final IntegerProperty scoreValue;
    private final Text score;
    private final IntegerProperty lifeValue;
    private final Text amountLife;

    private final StackPane stackPane;

    private MediaView mediaView;
    private final ImageView gameOverImg;
    private final ImageView playerLifeImg;
    private final Button btnBackMenu;

    //BACKGROUNDS
    private final StackPane backgroundPane;
    private final StackPane backgroundPaneTwo;

    public GameView(int width, int height) {
        this.stackPane = new StackPane();
        PlayerView player = new PlayerView(stackPane);

        //Tamaño
        this.setWidth(width);
        this.setHeight(height);

        //BACKGROUNDS
        this.backgroundPane = new StackPane();
        this.backgroundPaneTwo = new StackPane();

        //Botón volver al menú
        int textSizeBtn = (int) (this.getHeight() / 30);
        btnBackMenu = new Button("Volver al menú");
        btnBackMenu.setFocusTraversable(false);
        btnBackMenu.setPrefSize(100,50);
        btnBackMenu.setAlignment(Pos.CENTER);
        btnBackMenu.setStyle(Util.BUTTON_STYLE);
        btnBackMenu.setOnMouseEntered(e -> {btnBackMenu.setStyle(Util.BUTTON_STYLE_HOVER); this.setCursor(Cursor.HAND);});
        btnBackMenu.setOnMouseExited(e -> {btnBackMenu.setStyle(Util.BUTTON_STYLE); this.setCursor(Cursor.DEFAULT);});
        btnBackMenu.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, textSizeBtn));
        btnBackMenu.setDisable(true);
        btnBackMenu.setVisible(false);
        stackPane.getChildren().add(btnBackMenu);

        //Score
        this.scoreValue = new SimpleIntegerProperty(0);
        int textSize = (int) (stackPane.getHeight() / 20);
        this.score = new Text(scoreValue.asString().get());
        score.toFront();
        score.setTextAlignment(TextAlignment.CENTER);
        score.setFill(Color.WHITE);
        score.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, textSize));
        score.setTranslateY(textSize - height / 2.0);
        stackPane.getChildren().add(score);



        this.playerLifeImg = SVGUtils.svg2image(Util.POWER_UP_SPRITE_FOLDER + "1",
                (float) (stackPane.getHeight() / Util.SPRITE_RESIZE * 5));

        //Life
        this.lifeValue = new SimpleIntegerProperty(0);
        this.amountLife = new Text(lifeValue.asString().get());
        amountLife.toFront();
        amountLife.setTextAlignment(TextAlignment.CENTER);
        amountLife.setFill(Color.WHITE);
        amountLife.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, textSize));
        amountLife.setTranslateY(textSize - height / 2.0);
        amountLife.setTranslateX(textSize - width / 2.0 + playerLifeImg.getImage().getWidth() / 2);
        stackPane.getChildren().add(amountLife);
        stackPane.getChildren().add(playerLifeImg);

        //Game Over
        this.gameOverImg = SVGUtils.svg2image("gameOver",
                (float) (stackPane.getWidth() / Util.SPRITE_RESIZE * 5));
        stackPane.getChildren().add(gameOverImg);


        initMusic();
        new GameController(player, stackPane, backgroundPane, backgroundPaneTwo, scoreValue, gameOverImg
                , mediaView, btnBackMenu, lifeValue);
        putObjects();
        initBackground();
        addListeners();
        addBinds();
    }

    private void addBinds() {
        score.textProperty().bind(scoreValue.asString());
        amountLife.textProperty().bind(lifeValue.asString());
    }

    private void addListeners() {
        ChangeListener<Number> listener = ((ObservableValue<? extends Number> prop, Number oldVal, Number newVal) -> {
            //Recolocar la imagen del background
            this.getChildren().remove(backgroundPane);
            this.getChildren().remove(backgroundPaneTwo);
            putObjects();
            initBackground();

            //Recolocar score
            int textSize = (int) (stackPane.getHeight() / 20);
            score.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, textSize));
            score.setTranslateY(textSize - stackPane.getHeight() / 2);

            //Life
            this.playerLifeImg.setImage(SVGUtils.svg2image(Util.POWER_UP_SPRITE_FOLDER + "1",
                    (float) (stackPane.getHeight() / Util.SPRITE_RESIZE * 2)).getImage());
            playerLifeImg.setTranslateX(playerLifeImg.getImage().getWidth() - stackPane.getWidth() / 2);
            playerLifeImg.setTranslateY(playerLifeImg.getImage().getHeight() - stackPane.getHeight() / 2);
            playerLifeImg.toFront();

            amountLife.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, textSize));
            amountLife.setTranslateY(textSize - stackPane.getHeight() / 2.1);
            amountLife.setTranslateX(textSize - stackPane.getWidth() / 2 + playerLifeImg.getImage().getWidth());
            amountLife.toFront();

            //Game Over
            this.gameOverImg.setImage(SVGUtils.svg2image("gameOver",
                    (float) (stackPane.getWidth() / Util.SPRITE_RESIZE * 3)).getImage());

            //Botón
            btnBackMenu.prefHeightProperty().bind(this.heightProperty().divide(10));
            btnBackMenu.prefWidthProperty().bind(this.widthProperty().divide(2));
            btnBackMenu.toFront();
        });

        stackPane.heightProperty().addListener(listener);
        stackPane.widthProperty().addListener(listener);
    }

    private void initBackground() {

        File file = new File(Util.IMAGE_FOLDER + Util.BACKGROUND_NAME + ".png");
        File fileTwo = new File(Util.IMAGE_FOLDER + Util.BACKGROUND_NAME_TWO + ".png");
        Image imageBackground = new Image(file.toURI().toString());
        Image imageBackgroundTwo = new Image(fileTwo.toURI().toString());

        fitBackground(imageBackground, backgroundPane); //Rellenar fondo Background 1
        fitBackground(imageBackgroundTwo, backgroundPaneTwo); //Rellenar fondo Background 2
    }

    private void fitBackground(Image imageBackground, StackPane backgroundPane) {
        for (double x = stackPane.getTranslateX(); x < stackPane.getTranslateX() + stackPane.getWidth();
             x += imageBackground.getWidth()) {
            for (double y = stackPane.getTranslateY(); y < stackPane.getTranslateY() + stackPane.getHeight();
                 y += imageBackground.getHeight()) {
                ImageView imageView = new ImageView(imageBackground);
                imageView.setTranslateX(x);
                imageView.setTranslateY(y);
                imageView.setOpacity(0.4);
                backgroundPane.getChildren().add(imageView);
                StackPane.setAlignment(imageView, Pos.TOP_LEFT);
            }
        }
    }

    private void initMusic() {
        File fileMusic = new File(Util.MUSIC_FOLDER + "space.wav");
        Media sound = new Media(fileMusic.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.play();
        mediaPlayer.setVolume(0.6);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView = new MediaView(mediaPlayer);
        getChildren().add(mediaView);
    }

    private void putObjects() {
        this.setCenter(stackPane);
        this.getChildren().addAll(backgroundPane, backgroundPaneTwo);
        backgroundPane.toBack();
        backgroundPaneTwo.toBack();
        this.setStyle("-fx-background-color: #10111f;");
    }
}
