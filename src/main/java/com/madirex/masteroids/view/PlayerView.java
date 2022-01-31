package com.madirex.masteroids.view;

import com.madirex.masteroids.utils.SVGUtils;
import com.madirex.masteroids.utils.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PlayerView extends Group {
    private int life;
    private boolean inmortal;
    private Point2D point2D;
    private final StackPane stackPane;
    private ImageView imagePlayer;
    private final Rectangle collisionBox;

    public boolean isInmortal() {
        return inmortal;
    }

    public void setInmortal(boolean inmortal) {
        this.inmortal = inmortal;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public PlayerView(StackPane stackPane) {
        this.life = 3;
        this.point2D = new Point2D(Math.cos(Math.toRadians(getRotate())),
                Math.sin(Math.toRadians(getRotate())));
        this.stackPane = stackPane;
        this.imagePlayer = SVGUtils.svg2image("player",
                (float) (stackPane.heightProperty().get() / Util.SPRITE_RESIZE));
        this.collisionBox = new Rectangle();
        this.inmortal = false;
        initObjects();
        addListeners();
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    private void addListeners() {
        ChangeListener<Number> listener = ((ObservableValue<? extends Number> prop, Number oldVal, Number newVal) -> {
            //Recolocar la imagen
            this.getChildren().remove(imagePlayer);
            imagePlayer = SVGUtils.svg2image("player",(float) (stackPane.heightProperty().get() / Util.SPRITE_RESIZE));
            this.getChildren().add(imagePlayer);

            //Recolocar CollisionBox
            collisionBox.setWidth(imagePlayer.getImage().getWidth() / Util.COLLISION_BOX_PLAYER_RESIZE);
            collisionBox.setHeight(imagePlayer.getImage().getHeight() / Util.COLLISION_BOX_PLAYER_RESIZE);
            collisionBox.setTranslateX(imagePlayer.getTranslateX() + imagePlayer.getImage().getWidth() / 5);
            collisionBox.setTranslateY(imagePlayer.getTranslateX() + imagePlayer.getImage().getHeight() / 3);

            imagePlayer.toFront();
            collisionBox.toFront();
        });

        stackPane.heightProperty().addListener(listener);
        stackPane.widthProperty().addListener(listener);
    }

    private void initObjects() {
        if (!Util.DEBUG) {
            collisionBox.setVisible(false);
        }
        collisionBox.setFill(Color.RED);
        this.setTranslateX(imagePlayer.getTranslateX() - imagePlayer.getImage().getWidth() / 10);

        //Agregar a grupo
        this.getChildren().add(imagePlayer);
        this.getChildren().add(collisionBox);

        //Agregar a StackPane
        stackPane.getChildren().add(this);
    }

    public Point2D getPoint2D() {
        return point2D;
    }

    public void setPoint2D(Point2D point2D) {
        this.point2D = point2D;
    }

}
