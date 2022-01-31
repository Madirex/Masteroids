package com.madirex.masteroids.view;

import com.madirex.masteroids.utils.SVGUtils;
import com.madirex.masteroids.utils.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ObjectView extends Group {
    private final StackPane stackPane;
    private final Rectangle collisionBox;
    private ImageView image;
    private final boolean small;

    private final String svgSpriteName;
    private final double svgSpriteResize;

    //Vida
    private int life;
    private final boolean inmortal;

    public int getLife() {
        return life;
    }

    public boolean isInmortal() {
        return inmortal;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public String getSvgSpriteName() {
        return svgSpriteName;
    }

    public double getSvgSpriteResize() {
        return svgSpriteResize;
    }

    public ObjectView(StackPane stackPane, String svgSpriteName, double svgSpriteResize, int life, boolean inmortal,
                      boolean small){
        this.stackPane = stackPane;
        this.collisionBox = new Rectangle();
        this.small = small;

        this.svgSpriteName = svgSpriteName;
        this.svgSpriteResize = svgSpriteResize;
        this.life = life;
        this.inmortal = inmortal;
        this.image = SVGUtils.svg2image(svgSpriteName, (float) (stackPane.heightProperty().get() / Util.SPRITE_RESIZE / svgSpriteResize));
        initObject();
        addListeners();
    }

    public boolean isSmall() {
        return small;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    private void addListeners() {
        ChangeListener<Number> listener = ((ObservableValue<? extends Number> prop, Number oldVal, Number newVal) -> {
            //Recolocar la imagen
            getChildren().remove(image);
            image = SVGUtils.svg2image(svgSpriteName, (float) (stackPane.heightProperty().get() / Util.SPRITE_RESIZE / svgSpriteResize));
            image.setTranslateX(image.getTranslateX() - image.getImage().getWidth() / 4);
            image.setTranslateY(image.getTranslateY() - image.getImage().getHeight() / 4);
            getChildren().add(image);

            //Recolocar CollisionBox
            collisionBox.setWidth(image.getImage().getWidth() / Util.COLLISION_BOX_OBJECT_RESIZE);
            collisionBox.setHeight(image.getImage().getHeight() / Util.COLLISION_BOX_OBJECT_RESIZE);

            collisionBox.toFront();
        });

        stackPane.heightProperty().addListener(listener);
        stackPane.widthProperty().addListener(listener);
    }


    private void initObject() {
        if (!Util.DEBUG) {
            collisionBox.setVisible(false);
        }
        collisionBox.setWidth(image.getImage().getWidth() / Util.COLLISION_BOX_OBJECT_RESIZE);
        collisionBox.setHeight(image.getImage().getHeight() / Util.COLLISION_BOX_OBJECT_RESIZE);
        image.setTranslateX(image.getTranslateX() - image.getImage().getWidth() / 4);
        image.setTranslateY(image.getTranslateY() - image.getImage().getHeight() / 4);

        collisionBox.setFill(Color.RED);
        getChildren().add(image); //Agregar imagen
        getChildren().add(collisionBox); //Agregar caja de colisión

        //Agregar a StackPane
        stackPane.getChildren().add(this);
    }

}
