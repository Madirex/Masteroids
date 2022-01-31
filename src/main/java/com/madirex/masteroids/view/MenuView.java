package com.madirex.masteroids.view;

import com.madirex.masteroids.utils.SVGUtils;
import com.madirex.masteroids.utils.Util;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MenuView extends StackPane {

    private final Button btnPlay;
    private final Button btnFullScreen;
    private final Button btnExit;
    private ImageView logo;

    public MenuView(int width, int height){
        btnPlay = new Button("Jugar");
        btnFullScreen = new Button("Fullscreen");
        btnExit = new Button("Salir");

        btnPlay.setFocusTraversable(false);
        btnFullScreen.setFocusTraversable(false);
        btnExit.setFocusTraversable(false);

        //Tamaño
        this.setWidth(width);
        this.setHeight(height);

        setImageView();

        btnPlay.setPrefSize(100,50);
        btnFullScreen.setPrefSize(100,50);
        btnExit.setPrefSize(100,50);

        //Asignar estilo a los botones
        int textSize = (int) (this.getHeight() / 30);

        //Asignar posiciones
        btnPlay.setAlignment(Pos.CENTER);
        btnFullScreen.setAlignment(Pos.CENTER);
        btnExit.setAlignment(Pos.CENTER);

        //Asignar estilos
        Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, textSize);

        btnPlay.setStyle(Util.BUTTON_STYLE);
        btnPlay.setOnMouseEntered(e -> {btnPlay.setStyle(Util.BUTTON_STYLE_HOVER); this.setCursor(Cursor.HAND);});
        btnPlay.setOnMouseExited(e -> {btnPlay.setStyle(Util.BUTTON_STYLE); this.setCursor(Cursor.DEFAULT);});
        btnPlay.setFont(font);

        btnFullScreen.setStyle(Util.BUTTON_STYLE);
        btnFullScreen.setOnMouseEntered(e -> {btnFullScreen.setStyle(Util.BUTTON_STYLE_HOVER); this.setCursor(Cursor.HAND);});
        btnFullScreen.setOnMouseExited(e -> {btnFullScreen.setStyle(Util.BUTTON_STYLE); this.setCursor(Cursor.DEFAULT);});
        btnFullScreen.setFont(font);

        btnExit.setStyle(Util.BUTTON_STYLE);
        btnExit.setOnMouseEntered(e -> {btnExit.setStyle(Util.BUTTON_STYLE_HOVER); this.setCursor(Cursor.HAND);});
        btnExit.setOnMouseExited(e -> {btnExit.setStyle(Util.BUTTON_STYLE); this.setCursor(Cursor.DEFAULT);});
        btnExit.setFont(font);

        //Asignar acciones
        btnPlay.setOnAction(e -> playAction());
        btnFullScreen.setOnAction(e -> fullscreenToggle());
        btnExit.setOnAction(e -> Platform.exit());

        //Background
        this.setStyle("-fx-background-color: #10111f;");
        this.getChildren().addAll(btnPlay, btnFullScreen, btnExit);

        addBinds();
        addListeners();
    }

    private void setImageView() {
        this.getChildren().remove(logo);
        logo = new ImageView(SVGUtils.svg2image("logo",
                (float) (this.heightProperty().get() / Util.SPRITE_RESIZE * 2)).getImage());
        this.getChildren().add(logo);
        logo.translateYProperty().bind(this.heightProperty().multiply(-0.1).subtract(logo.getImage().getHeight()));
    }

    private void playAction() {
        Stage stage = (Stage) this.getScene().getWindow();
        GameView game = new GameView((int) getWidth(), (int) getHeight());
        stage.getScene().setRoot(game);
    }

    private void fullscreenToggle() {
        Stage stage = (Stage) this.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());

        if (stage.isFullScreen()){
            btnFullScreen.setText("Widescreen");
        }else{
            btnFullScreen.setText("Fullscreen");
        }
    }

    private void addBinds() {
        int margin = 10;

        btnPlay.translateYProperty().bind(this.heightProperty().multiply(-0.1).add(btnPlay.getPrefHeight()));
        btnPlay.prefHeightProperty().bind(this.heightProperty().divide(10));
        btnPlay.prefWidthProperty().bind(this.widthProperty().divide(2));

        btnFullScreen.translateYProperty().bind(btnPlay.translateYProperty().add(this.heightProperty().divide(10))
                .add(margin));
        btnFullScreen.prefHeightProperty().bind(this.heightProperty().divide(10));
        btnFullScreen.prefWidthProperty().bind(this.widthProperty().divide(2));

        btnExit.translateYProperty().bind(btnFullScreen.translateYProperty().add(this.heightProperty().divide(10))
                .add(margin));
        btnExit.prefHeightProperty().bind(this.heightProperty().divide(10));
        btnExit.prefWidthProperty().bind(this.widthProperty().divide(2));
    }

    private void addListeners() {
        ChangeListener<Number> listener = ((ObservableValue<? extends Number> prop, Number oldVal, Number newVal) -> {
            setImageView();

            int textSize = (int) (this.getHeight() / 30);
            Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, textSize);
            btnPlay.setFont(font);
            btnFullScreen.setFont(font);
            btnExit.setFont(font);
        });

        this.heightProperty().addListener(listener);
        this.widthProperty().addListener(listener);
    }

}
