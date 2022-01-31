package com.madirex.masteroids.controller;

import com.madirex.masteroids.utils.Util;
import com.madirex.masteroids.view.*;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class GameController {
    private boolean gameOver;
    private boolean pauseGame;
    private final Random random;

    private final PlayerView player;
    private Timeline timeline;
    private final StackPane stackPane;

    private final IntegerProperty score;
    private final IntegerProperty lifesValue;

    private double deslizamientoPlayer = 0;

    private boolean playerLeft = false;
    private boolean playerRight = false;
    private boolean playerUp = false;
    private boolean playerDown = false;
    private boolean playerShoot = false;

    private boolean playerShootClicked = false;
    private double playerShootClickedX;
    private double playerShootClickedY;

    private double gameSpeed = 3;

    private final ArrayList<BulletView> bullets;

    //Temporizadores
    private int waitTemp;
    private double timerToSpawnObject;
    private final double speedToSpawnObject;
    private double recentViewChangeTimer;
    private double powerUpShootTimer;

    //
    private int powerUpShootQuantity;

    //Backgrounds
    private final StackPane backgroundPane;
    private final StackPane backgroundPaneTwo;
    private final Image imageBackground;
    private final Image imageBackgroundTwo;
    private static final double OPACITY_BG = 0.6;
    private static final double OPACITY_BG_TWO = 0.4;
    private static final int DIV_VELOCITY = 600;

    //Objetos
    private final ObjectView[] planet;
    private final ObjectView[] powerup;
    private final ObjectView[] asteroid;

    //Otros
    private final ImageView gameOverImg;
    private final javafx.scene.shape.Rectangle blackBackground;
    private final MediaView mediaView;
    private final Button btnBackMenu;

    public GameController(PlayerView player, StackPane stackPane, StackPane backgroundPane,
                          StackPane backgroundPaneTwo, IntegerProperty score, ImageView gameOverImg,
                          MediaView mediaView, Button btnBackMenu, IntegerProperty lifesValue) {
        this.gameOver = false;
        this.pauseGame = false;
        this.random = new Random();
        this.player = player;
        this.stackPane = stackPane;
        this.bullets = new ArrayList<>();
        this.score = score;
        this.lifesValue = lifesValue;
        lifesValue.setValue(player.getLife());

        this.mediaView = mediaView;
        this.btnBackMenu = btnBackMenu;
        this.btnBackMenu.setOnAction(e -> returnAction());

        //Backgrounds
        this.backgroundPane = backgroundPane;
        this.backgroundPaneTwo = backgroundPaneTwo;
        this.imageBackground = new Image(new File(Util.IMAGE_FOLDER + Util.BACKGROUND_NAME + ".png")
                .toURI().toString());
        this.imageBackgroundTwo = new Image(new File(Util.IMAGE_FOLDER + Util.BACKGROUND_NAME_TWO + ".png")
                .toURI().toString());

        //Temporizadores
        this.waitTemp = 0;
        this.timerToSpawnObject = 0;
        this.speedToSpawnObject = 0.02;
        this.recentViewChangeTimer = 0;
        this.powerUpShootTimer = 0;


        this.powerUpShootQuantity = 0;

        //Objetos
        planet = new ObjectView[Util.MAX_PLANETS];
        powerup = new ObjectView[Util.MAX_POWERUPS];
        asteroid = new ObjectView[Util.MAX_ASTEROIDS];

        //Black background para game over y menú de pausa
        blackBackground = new javafx.scene.shape.Rectangle();
        stackPane.getChildren().add(blackBackground);
        blackBackground.setOpacity(0);
        blackBackground.toFront();
        this.gameOverImg = gameOverImg;
        this.gameOverImg.setOpacity(0);

        initGame();
        initControlls();
    }


    private void initGame(){
        this.timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> {

            if (pauseGame || gameOver){

                if (mediaView.getMediaPlayer().getVolume() > 0){
                    mediaView.getMediaPlayer().setVolume(mediaView.getMediaPlayer().getVolume() - 0.01);
                }
                //GAME OVER
                gameOverSetup();
                blackBackgroundSetup();
                pauseGameSetup();

            }else{
                returnGameStatus();
                checkOutBoundsPlayer();
                playerMovement();
                playerShootBullet();
                bulletsMovement();

                double vel = getSpeedGameAdapted();

                if (vel < 1){
                    vel = 1;
                }

                backgroundMovement(vel, true);
                backgroundMovement(vel + 1, false);
                spawnearEnemigos();
                playerCollisionCheck();
                speedGameIncrease();
                powerUpsTimer();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        addListeners();
    }

    private void pauseGameSetup() {
        if (pauseGame){
            btnBackMenu.toFront();

            if (!btnBackMenu.isVisible()){
                btnBackMenu.setVisible(true);
                btnBackMenu.setOpacity(0);
            }else{
                if (btnBackMenu.getOpacity() < 1){
                    btnBackMenu.setOpacity(btnBackMenu.getOpacity() + 0.03);
                }else{
                    btnBackMenu.setDisable(false);
                }
            }
        }
    }

    private void blackBackgroundSetup() {
        blackBackground.toFront();
        blackBackground.setTranslateX(0);
        blackBackground.setTranslateY(0);
        blackBackground.setWidth(stackPane.getWidth());
        blackBackground.setHeight(stackPane.getHeight());

        if (blackBackground.getOpacity() < 0.7){
            blackBackground.toFront();
            blackBackground.setOpacity(blackBackground.getOpacity() + 0.02);
        }
    }

    private void gameOverSetup() {
        if (gameOver){
            gameOverImg.toFront();
            gameOverImg.setTranslateX(0);
            gameOverImg.setTranslateY(0);

            //Animación Game Over
            if (gameOverImg.getOpacity() < 1.5){
                gameOverImg.setOpacity(gameOverImg.getOpacity() + 0.01);
                gameOverImg.setScaleX(gameOverImg.getScaleX() - 0.005);
                gameOverImg.setScaleY(gameOverImg.getScaleY() - 0.0075);
            }else{
                restartGame();
            }
        }
    }

    private void powerUpsTimer() {
        if (powerUpShootTimer > 0){
            powerUpShootTimer -=0.01;
        }else{
            powerUpShootQuantity = 0;
            powerUpShootTimer = 0;
        }
    }

    private void returnGameStatus() {
        if (blackBackground.getOpacity() > 0){
            blackBackground.toFront();
            blackBackground.setOpacity(blackBackground.getOpacity() - 0.02);
        }

        if (mediaView.getMediaPlayer().getVolume() < 1){
            mediaView.getMediaPlayer().setVolume(mediaView.getMediaPlayer().getVolume() + 0.01);
        }

        if (btnBackMenu.isVisible()){
            btnBackMenu.setDisable(true);

            if (btnBackMenu.getOpacity() > 0){
                btnBackMenu.setOpacity(btnBackMenu.getOpacity() - 0.03);
            }else{
                btnBackMenu.setVisible(false);
            }
        }
    }

    private void speedGameIncrease() {
        if (gameSpeed < 8){
            gameSpeed += 0.0004;
        }
    }

    private void initControlls() {
        stackPane.setOnKeyPressed(e -> checkControllsPressed(e.getCode()));

        stackPane.setOnKeyReleased(e -> checkControllsReleased(e.getCode()));

        stackPane.setOnMouseMoved(e -> {
            playerShootClickedX = e.getSceneX();
            playerShootClickedY = e.getSceneY();
        });

        stackPane.setFocusTraversable(true);
    }

    private double getSpeedGameAdapted(){
        double vel = (stackPane.getHeight() / DIV_VELOCITY * gameSpeed);
        if (vel < 1){
            vel = 1;
        }
        return vel;
    }

    private void spawnearEnemigos() {
        double vel = getSpeedGameAdapted();

        //Asteroides
        if (timerToSpawnObject >= 10){
            spawnearObjeto(Util.PROBABILIDAD_SPAWN_ASTEROID, asteroid, Util.ASTEROID_SPRITE_FOLDER, 3,0.2,
                    1, false,10,false);
            timerToSpawnObject = 0;
        }else{
            timerToSpawnObject += speedToSpawnObject * vel;
        }

        movementObject(asteroid, vel, false, Util.SPEED_ASTEROID);
    }

    private void backgroundMovementHorizontal(int id, boolean right, boolean firstBackground){
        StackPane background;
        Image imageBg;
        double opacityBg;

        if (firstBackground){
            background = backgroundPane;
            imageBg = imageBackground;
            opacityBg = OPACITY_BG;
        }else{
            background = backgroundPaneTwo;
            imageBg = imageBackgroundTwo;
            opacityBg = OPACITY_BG_TWO;
        }

        double xPosition = stackPane.getTranslateX() - background.getChildren().get(id).getBoundsInParent().getWidth();

        if (right){
            xPosition = stackPane.getTranslateX() + stackPane.getWidth()
                    + background.getChildren().get(id).getBoundsInParent().getWidth();
        }

        //Agregando fondo arriba a la izquierda/derecha
        for (double y = background.getChildren().get(id).getTranslateY();
             y > stackPane.getTranslateY() - background.getChildren().get(id).getTranslateY()
                     - background.getChildren().get(id).getBoundsInLocal().getHeight();
             y -= background.getChildren().get(id).getBoundsInLocal().getHeight()){

            //Creación de imagen

            ImageView imageView = new ImageView(imageBg);
            background.getChildren().add(imageView);
            imageView.setOpacity(opacityBg);
            imageView.setTranslateX(xPosition);
            imageView.setTranslateY(y);
        }

        //Agregando fondo abajo a la izquierda/derecha
        for (double y = background.getChildren().get(id).getTranslateY() +
                background.getChildren().get(id).getBoundsInLocal().getHeight();
             y < stackPane.getTranslateY() + stackPane.getHeight()  + background.getChildren()
                     .get(id).getBoundsInLocal().getHeight();
             y += background.getChildren().get(id).getBoundsInLocal().getHeight()){

            //Creación de imagen
            ImageView imageView = new ImageView(imageBg);
            background.getChildren().add(imageView);
            imageView.setOpacity(opacityBg);
            imageView.setTranslateX(xPosition);
            imageView.setTranslateY(y);
        }
    }

    private void backgroundMovementVertical(int id, boolean down, boolean firstBackground){
        StackPane background;
        Image imageBg;
        double opacityBg;

        if (firstBackground){
            background = backgroundPane;
            imageBg = imageBackground;
            opacityBg = OPACITY_BG;
        }else{
            background = backgroundPaneTwo;
            imageBg = imageBackgroundTwo;
            opacityBg = OPACITY_BG_TWO;
        }

        double yPosition = stackPane.getTranslateY() - imageBg.getHeight();

        if (down){
            yPosition = stackPane.getTranslateY() + stackPane.getHeight() + background.getChildren().get(id).getBoundsInParent().getHeight();
        }

        //Agregando fondo arriba a la izquierda
        for (double x = background.getChildren().get(id).getTranslateX();
             x > stackPane.getTranslateX() - background.getChildren().get(id).getTranslateX()
                     - background.getChildren().get(id).getBoundsInLocal().getWidth();
             x -= background.getChildren().get(id).getBoundsInLocal().getWidth()){

            //Creación de imagen
            ImageView imageView = new ImageView(imageBg);
            background.getChildren().add(imageView);
            imageView.setOpacity(opacityBg);
            imageView.setTranslateX(x); // background.getChildren().get(id).getTranslateX()
            imageView.setTranslateY(yPosition);
        }

        //Agregando fondo abajo a la izquierda
        for (double x = background.getChildren().get(id).getTranslateX() ;
             x < stackPane.getTranslateX() + stackPane.getWidth() + background.getChildren().get(id).getBoundsInLocal().getWidth();
             x += background.getChildren().get(id).getBoundsInLocal().getWidth()){

            //Creación de imagen
            ImageView imageView = new ImageView(imageBg);
            background.getChildren().add(imageView);
            imageView.setOpacity(opacityBg);
            imageView.setTranslateX(x);
            imageView.setTranslateY(yPosition);
        }
    }

    private void restartGame() {
        timeline.stop();
        Stage stage = (Stage) stackPane.getScene().getWindow();
        GameView game = new GameView((int) stackPane.getWidth(), (int) stackPane.getHeight());
        stage.getScene().setRoot(game);
    }

    private void returnAction() {
        timeline.stop();
        Stage stage = (Stage) stackPane.getScene().getWindow();
        MenuView menu = new MenuView((int) stackPane.getWidth(), (int) stackPane.getHeight());
        stage.getScene().setRoot(menu);
    }

    private void backgroundMovement(double speed, boolean firstBackground){

        if (recentViewChangeTimer <= 0){
        StackPane background;

        if (firstBackground){
            background = backgroundPane;
        }else{
            background = backgroundPaneTwo;
        }

        if (!background.getChildren().isEmpty()){
            boolean isLeftBackground = false;
            boolean isRightBackground = false;
            boolean isDownBackground = false;
            boolean isUpBackground = false;
            int idPositionLeft = 0;
            int idPositionRight = 0;
            int idPositionUp = 0;
            int idPositionDown = 0;

            for(int n = 0; n < background.getChildren().size(); n++){

                //Movimiento
                double posX = background.getChildren().get(n).getTranslateX();
                double posY = background.getChildren().get(n).getTranslateY();
                double screenHStart = stackPane.getTranslateX();
                double screenVStart = stackPane.getTranslateY();
                double screenHLength = screenHStart + stackPane.getWidth();
                double screenVLength = screenVStart + stackPane.getHeight();

                background.getChildren().get(n).setTranslateX(posX - player.getPoint2D().multiply(speed).getX());
                background.getChildren().get(n).setTranslateY(posY - player.getPoint2D().multiply(speed).getY());

                //Chequear si hay imagen alrededor de la screen
                if (posX < screenHStart) {
                    isLeftBackground = true;
                    if (background.getChildren().get(idPositionLeft).getTranslateX() < posX) {
                        idPositionLeft = n;
                    }
                }
                if (posX > screenHLength){
                    isRightBackground = true;
                    if (background.getChildren().get(idPositionRight).getTranslateX() > posX) {
                        idPositionRight = n;
                    }
                }
                if (posY < screenVStart){
                    isUpBackground = true;
                    if (background.getChildren().get(idPositionUp).getTranslateY() < posY) {
                        idPositionUp = n;
                    }
                }
                if (posY > screenVLength){
                    isDownBackground = true;
                    if (background.getChildren().get(idPositionDown).getTranslateY() > posY) {
                        idPositionDown = n;
                    }
                }

                //Eliminar si se sale de los límites visibles
                if (posX < screenHStart - background.getChildren()
                        .get(n).getBoundsInParent().getWidth() - screenHLength / 2
                        || posX > screenHLength + background.getChildren()
                        .get(n).getBoundsInParent().getWidth() + screenHLength / 2
                        || posY < screenVStart - background.getChildren()
                        .get(n).getBoundsInParent().getHeight() - screenVLength / 2
                        || posY > screenVLength + background.getChildren()
                        .get(n).getBoundsInParent().getHeight() + screenVLength / 2){
                    background.getChildren().remove(n);
                }
            }

            //Ahora crear las imágenes si en la siguiente posición no se encuentran
            if (!isLeftBackground) {
                backgroundMovementHorizontal(idPositionLeft, false, firstBackground);
            }
            if (!isRightBackground){
                backgroundMovementHorizontal(idPositionRight, true, firstBackground);
            }
            if (!isUpBackground){
                backgroundMovementVertical(idPositionUp, false, firstBackground);
            }

            if (!isDownBackground){
                backgroundMovementVertical(idPositionDown, true, firstBackground);
            }

            //Agregar planeta random y powerup
            if (!isLeftBackground || !isRightBackground || !isUpBackground || !isDownBackground) {

                spawnearObjeto(Util.PROBABILIDAD_SPAWN_PLANET, planet,Util.PLANET_SPRITE_FOLDER, 5,0.5,
                        0.6, false,1,true);

                spawnearObjeto(Util.PROBABILIDAD_SPAWN_POWERUP, powerup, Util.POWER_UP_SPRITE_FOLDER, 3,0.3,
                        0.6, false,1,true);
            }
            movementObject(planet, speed / 2, false, Util.SPEED_PLANET);
            movementObject(powerup, speed / 2, false, Util.SPEED_POWER_UP);

        }
        }
        else{
            recentViewChangeTimer -=0.1;
        }
    }

    private void fullscreenToggle() {
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void playerLife(boolean subtract) {

        if (subtract && !player.isInmortal()){

            //Sonido
            MediaView media1 = new MediaView();
            media1.setMediaPlayer(Util.sound("playerkick.mp3", 0.7));

            player.setLife(player.getLife() - 1);
            lifesValue.setValue(player.getLife());

            FadeTransition parpadeo = new FadeTransition(Duration.millis(250), player);
            parpadeo.setFromValue(1.0);
            parpadeo.setToValue(0.0);
            parpadeo.setAutoReverse(true);

            // 4 ciclos * segundos de inmortalidad
            int ciclos = 4 * Util.SECONDS_INMORTALITY_PLAYER;

            if (ciclos % 2 != 0){
                ciclos +=1;
            }

            parpadeo.setCycleCount(ciclos); // 4 ciclos * segundos de inmortalidad

            parpadeo.setOnFinished(actionEvent -> player.setInmortal(false));
            parpadeo.play();
            player.setInmortal(true);
        }else if (!subtract){
            player.setLife(player.getLife() + 1);
        }

        //Game Over
        if (player.getLife() <= 0){
            //Sonido muerte jugador
            MediaView media1 = new MediaView();
            media1.setMediaPlayer(Util.sound("playerdied.wav",0.7));

            stackPane.getChildren().remove(player);
            gameOver = true;

            //Sonido Game Over
            MediaView media2 = new MediaView();
            media2.setMediaPlayer(Util.sound("gameover.wav",0.7));
        }
    }

    /**
     *
     * @param obj ObjectView
     * @param subtract ¿Restar?
     * @return ¿Eliminar?
     */
    private boolean objectLife(ObjectView obj, boolean subtract) {
        if (subtract){
            obj.setLife(obj.getLife() - 1);
            FadeTransition parpadeo = new FadeTransition(Duration.millis(200), obj);
            parpadeo.setFromValue(1.0);
            parpadeo.setToValue(0.5);
            parpadeo.setAutoReverse(true);
            parpadeo.setCycleCount(2);
            parpadeo.play();
        }else{
            obj.setLife(obj.getLife() + 1);
        }

        if (obj.getLife() <= 0){
            if (!obj.isInmortal()) { //Inmortal == no es un meteorito
                if (!obj.isSmall()) {
                    //Crear 2 meteoritos
                    int added = 0;
                    int n = 0;

                    while (added < 2 && n < asteroid.length) {
                        if (asteroid[n] == null) {

                            asteroid[n] = new ObjectView(stackPane, obj.getSvgSpriteName(),
                                    obj.getSvgSpriteResize() * 1.5, 3, false, true);

                            if (added == 0) {
                                asteroid[n].setTranslateX(obj.getTranslateX() - obj.getCollisionBox().getWidth() / 2.2);
                                asteroid[n].setTranslateY(obj.getTranslateY() - obj.getCollisionBox().getHeight() / 2.2);
                            } else {
                                asteroid[n].setTranslateX(obj.getTranslateX() + obj.getCollisionBox().getWidth() / 2.2);
                                asteroid[n].setTranslateY(obj.getTranslateY() + obj.getCollisionBox().getHeight() / 2.2);
                            }

                            added += 1;
                        }
                        n += 1;
                    }
                }

                //Sonido
                MediaView media1 = new MediaView();
                media1.setMediaPlayer(Util.sound("meteoDying.mp3",0.7));
            }

            //Eliminar
            stackPane.getChildren().remove(obj);
            return true;
        }else{
            return false;
        }
    }


    private void spawnearObjeto(double probabilidad, ObjectView[] objeto, String spriteUrl, int spriteMax, double resize,
                                double alpha, boolean back, int vida, boolean inmortal){

        if (probabilidad >= 0 && probabilidad <= 100){
            if (Util.random(0, 100) < probabilidad) {
                boolean exit = false;
                int n = 0;
                while(!exit && n < objeto.length){
                    if (objeto[n] == null){
                        objeto[n] = new ObjectView(stackPane,spriteUrl + (int) Util.random(1, spriteMax),
                                resize, 5, inmortal, false);

                        objeto[n].setOpacity(alpha);

                        double xDirection = player.getPoint2D().getX();
                        double yDirection = player.getPoint2D().getY();

                        if (back){
                            xDirection *= -1;
                            yDirection *= -1;
                        }

                        objeto[n].setTranslateX(xDirection * stackPane.widthProperty().get());
                        objeto[n].setTranslateY(yDirection * stackPane.heightProperty().get());

                            if (!stackPane.getChildren().contains(objeto[n])) {
                                stackPane.getChildren().add(objeto[n]);
                            }
                        exit = true; //Salir del bucle
                    }
                    n++;
                }
            }
            orderDepthObjects();
        }else{
            if (Util.DEBUG){
                System.err.println("La probabilidad no es una probabilidad entre 0 y 100 -> " +
                        "SpawnearObjeto en clase Player del paquete View");
            }
        }
    }

    private void movementObject(ObjectView[] objeto, double speedBackground, boolean back, double speedObject){
        for (int n = 0; n < objeto.length; n++){
            if (objeto[n] != null){

                double goX = player.getPoint2D().multiply(speedBackground).subtract(speedObject, speedObject).getX();
                double goY = player.getPoint2D().multiply(speedBackground).subtract(speedObject, speedObject).getY();
                double rotate = speedBackground / 10;

                if (!back){
                    goX *= -1;
                    goY *= -1;
                    rotate *= -1;
                }

                double div = 1;
                //En el caso de que sea mini
                if (objeto[n].isSmall()){
                    div = 1.7; //Irá más lento
                }

                objeto[n].setTranslateX(objeto[n].getTranslateX() + goX / div);
                objeto[n].setTranslateY(objeto[n].getTranslateY() + goY / div);
                objeto[n].setRotate(objeto[n].getRotate() + rotate / div);

                //Si se sale de la room, eliminar
                if (outBoundsDestroy(objeto[n], false)){
                    stackPane.getChildren().remove(objeto[n]);
                    objeto[n] = null;
                }
            }
        }
    }

    private void orderDepthObjects(){
        //Profundidad: Cuanto más bajo, mayor profundidad

        //Asteroids
        for (ObjectView value : asteroid) {
            if (value != null) {
                value.toBack(); //Mandar al fondo
            }
        }

        //Player
        player.toBack();

        //Bullets
        if (!bullets.isEmpty()){
            for (BulletView bullet : bullets){
                bullet.toBack();
            }
        }

        //Power Ups
        for (ObjectView view : powerup) {
            if (view != null) {
                view.toBack(); //Mandar al fondo
            }
        }

        //Planeta
        for (ObjectView objectView : planet) {
            if (objectView != null) {
                objectView.toBack(); //Mandar al fondo
            }
        }

    }

    private boolean outBoundsDestroy(Node objeto, boolean isBullet) {
        double marginToDeleteX = 0;
        double marginToDeleteY = 0;

        if (!isBullet){
            marginToDeleteX = (stackPane.getWidth()) / 2;
            marginToDeleteY = (stackPane.getHeight()) / 2;
        }

        double scenePosX = objeto.localToScene(objeto.getBoundsInLocal()).getCenterX();
        double scenePosY = objeto.localToScene(objeto.getBoundsInLocal()).getCenterY();

        boolean outBoundsLeft = scenePosX < stackPane.getTranslateX() - marginToDeleteX;
        boolean outBoundsRight = scenePosX > stackPane.getTranslateX() + stackPane.getWidth() + marginToDeleteX;
        boolean outBoundsTop = scenePosY < stackPane.getTranslateY() - marginToDeleteY;
        boolean outBoundsDown = scenePosY > stackPane.getTranslateY() + stackPane.getHeight() + marginToDeleteY;

        return outBoundsLeft || outBoundsRight || outBoundsTop || outBoundsDown;
    }

    private void bulletsMovement() {

        if (!bullets.isEmpty()) {
            for (int n = 0; n < bullets.size(); n++) {
                if (bullets.get(n).getOpacity() < 1) {
                    bullets.get(n).setOpacity(bullets.get(n).getOpacity() + 0.2);
                }else{
                    //Si el objeto ha terminado de crearse correctamente...
                    //Comprobar outBounds
                    if (outBoundsDestroy(bullets.get(n), true)){
                        bullets.get(n).destroyObject();
                        bullets.remove(bullets.get(n));
                        break;
                    }
                }

                bullets.get(n).setTranslateX(bullets.get(n).getTranslateX() + bullets.get(n).getPoint2D().getX()
                        * getPlayerVel() / 3);
                bullets.get(n).setTranslateY(bullets.get(n).getTranslateY() + bullets.get(n).getPoint2D().getY()
                        * getPlayerVel() / 3);

                bulletsCollisionCheck(n);

            }
        }
    }

    private void addListeners() {
        ChangeListener<Number> listener = ((ObservableValue<? extends Number> prop, Number oldVal, Number newVal) ->
                recentViewChangeTimer = 1);
        stackPane.heightProperty().addListener(listener);
        stackPane.widthProperty().addListener(listener);
    }

    private boolean intersectsCollisions(Node one, Node two){
        Bounds objA = one.localToScene(one.getBoundsInLocal());
        Bounds objB = two.localToScene(two.getBoundsInLocal());

        return objA.intersects(objB);
    }

    private void bulletsCollisionCheck(int num){
        //Comprobar colisión con meteoritos
        for (int n = 0; n < asteroid.length; n++){
            if (asteroid[n] != null){
                if (num < bullets.size()) {
                    if (intersectsCollisions(bullets.get(num), asteroid[n].getCollisionBox())) {
                            //Sonido
                            MediaView media1 = new MediaView();
                            media1.setMediaPlayer(Util.sound("bulletCollision.mp3",0.7));

                            //Pitch distinto para que el sonido no se haga muy repetitivo
                            int nRandom = random.nextInt(20);
                            if (nRandom >= 15){
                                media1.getMediaPlayer().setRate(0.8);
                            }else if (nRandom >= 10){
                                media1.getMediaPlayer().setRate(0.9);
                            } else{
                                media1.getMediaPlayer().setRate(1);
                            }

                            //Eliminar bullet
                            stackPane.getChildren().remove(bullets.get(num));
                            bullets.remove(num);
                            if (objectLife(asteroid[n],true)){
                                asteroid[n] = null;
                                score.setValue(score.getValue() + Util.POINTS_TO_KILL_ASTEROID);
                            }
                    }
                }
            }
        }
    }
    private void playerCollisionCheck(){
            //Colisiones con jugador
            if (player.getLife() > 0) {
                asteroidsCollision(); //Comprobar colisión con asteroides
                powerUpCollision(); //Comprobar colisión con powerups
            }
    }

    private void asteroidsCollision() {
        for (int n = 0; n < asteroid.length; n++) {
            if (asteroid[n] != null) {
                if (intersectsCollisions(player.getCollisionBox(), asteroid[n].getCollisionBox())) {
                    playerLife(true);

                    while(asteroid[n] != null){

                        if (objectLife(asteroid[n],true)){
                            asteroid[n] = null;
                            score.setValue(score.getValue() + Math.round(Util.POINTS_TO_KILL_ASTEROID / 2.0));
                        }
                    }
                }
            }
        }
    }

    private void powerUpCollision() {
        for (int n = 0; n < powerup.length; n++) {
            if (powerup[n] != null) {
                if (intersectsCollisions(player.getCollisionBox(), powerup[n].getCollisionBox())) {

                    //Power ups
                    if (powerup[n].getSvgSpriteName().equals(Util.POWER_UP_SPRITE_FOLDER + 1)){
                        playerLife(false); // +1 de vida
                    } else if (powerup[n].getSvgSpriteName().equals(Util.POWER_UP_SPRITE_FOLDER + 2)){
                        powerUpShootTimer = 10;
                        powerUpShootQuantity = 2;
                    } else if (powerup[n].getSvgSpriteName().equals(Util.POWER_UP_SPRITE_FOLDER + 3)){
                        powerUpShootTimer = 10;
                        powerUpShootQuantity = 3;
                    }

                    while(!objectLife(powerup[n],true)) {
                        //Esperar
                    }

                    powerup[n] = null;

                    //Sonido
                    MediaView media = new MediaView();
                    media.setMediaPlayer(Util.sound("popup.wav",0.7));
                }
            }
        }
    }

    private void checkControllsPressed(KeyCode e) {
        switch(e){
            case W:
                playerUp = true;
                break;
            case A:
                playerLeft = true;
                break;
            case S:
                playerDown = true;
                break;
            case D:
                playerRight = true;
                break;
            case SPACE:
                playerShoot = true;
                break;
            case ENTER:
                pauseGame = !pauseGame;
                break;
            case SHIFT:
                playerShootClicked = true;
                break;
            case F11:
                fullscreenToggle();
                break;
            default:
                break;
        }
    }

    private void checkControllsReleased(KeyCode e) {
        switch(e){
            case W:
                playerUp = false;
                break;
            case A:
                playerLeft = false;
                break;
            case S:
                playerDown = false;
                break;
            case D:
                playerRight = false;
                break;
            case SPACE:
                playerShoot = false;
                break;
            case SHIFT:
                playerShootClicked = false;
                break;
            default:
                break;
        }
    }

    private void playerShootBullet(){
        if ((playerShoot || playerShootClicked) && waitTemp <= 0){
            BulletView bullet = new BulletView(stackPane);


            if (playerShootClicked){
                double deltaX = playerShootClickedX - player.localToScene(player.getBoundsInLocal()).getMinX()
                        - player.localToScene(player.getBoundsInLocal()).getWidth() / 2;
                double deltaY = playerShootClickedY - player.localToScene(player.getBoundsInLocal()).getMinY()
                        - player.localToScene(player.getBoundsInLocal()).getHeight() / 2;

                player.setRotate((int) (360 + Math.toDegrees(Math.atan2(deltaY, deltaX))) % 360);
                player.setPoint2D(new Point2D(Math.cos(Math.toRadians(player.getRotate())),
                        Math.sin(Math.toRadians(player.getRotate()))));
            }

            Point2D point2D = player.getPoint2D();
            bullet.setPoint2D(point2D.normalize().multiply(8));
            bullet.setTranslateX(player.getTranslateX() + point2D
                    .multiply(player.getCollisionBox().getWidth() * 1).getX());
            bullet.setTranslateY(player.getTranslateY() + point2D
                    .multiply(player.getCollisionBox().getHeight() * 1).getY());

            bullet.setOpacity(0);
            //bullet.set
            bullet.setRotate(player.getRotate());
            orderDepthObjects();
            bullets.add(bullet);
            waitTemp = 10 / ( 1 + powerUpShootQuantity);
        }else{
            waitTemp -= 0.05;
        }
    }

    private void playerMovement() {

        if (deslizamientoPlayer > 0){
            deslizamientoPlayer -=0.01;
            double xMovement = player.getPoint2D().multiply(deslizamientoPlayer * getSpeedGameAdapted() / 5).getX();
            double yMovement = player.getPoint2D().multiply(deslizamientoPlayer * getSpeedGameAdapted() / 5).getY();
            player.setTranslateX(player.getTranslateX() - xMovement);
            player.setTranslateY(player.getTranslateY() - yMovement);
        }

        if(playerDown){
            double playerSpeedDown = getPlayerVel() / 1.5;

            double xMovement = player.getPoint2D().multiply(playerSpeedDown).getX();
            double yMovement = player.getPoint2D().multiply(playerSpeedDown).getY();

            player.setTranslateX(player.getTranslateX() - xMovement);
            player.setTranslateY(player.getTranslateY() - yMovement);

        } else if (playerUp){
            double xMovement = player.getPoint2D().multiply(getPlayerVel()).getX();
            double yMovement = player.getPoint2D().multiply(getPlayerVel()).getY();

            player.setTranslateX(player.getTranslateX() + xMovement);
            player.setTranslateY(player.getTranslateY() + yMovement);

            //Agregar deslizamiento
            deslizamientoPlayer = 1;
        }

        int playerRotateSpeed = 4;
        if(playerLeft){
            player.setRotate(player.getRotate() - playerRotateSpeed);
            player.setPoint2D(new Point2D(Math.cos(Math.toRadians(player.getRotate())),
                    Math.sin(Math.toRadians(player.getRotate()))));
        } else if (playerRight){
            player.setRotate(player.getRotate() + playerRotateSpeed);
            player.setPoint2D(new Point2D(Math.cos(Math.toRadians(player.getRotate())),
                    Math.sin(Math.toRadians(player.getRotate()))));
        }

    }

    private void checkOutBoundsPlayer() {
        //Detectar posición jugador respecto a la escena
        double distancePlayer = player.getCollisionBox().getHeight();
        double playerScenePosX = player.localToScene(player.getBoundsInLocal()).getCenterX();
        double playerScenePosY = player.localToScene(player.getBoundsInLocal()).getCenterY();

        //Si llega al final de la pantalla, pasar al otro lado
        if (playerScenePosX + distancePlayer < stackPane.getTranslateX()){
            player.setTranslateX(player.getTranslateX() + distancePlayer * 2 + stackPane.getWidth());
        } else if (playerScenePosX - distancePlayer > stackPane.getTranslateX() + stackPane.getWidth()){
            player.setTranslateX(player.getTranslateX() - distancePlayer * 2 - stackPane.getWidth());
        }

        if (playerScenePosY + distancePlayer < stackPane.getTranslateY()){
            player.setTranslateY(player.getTranslateY() + distancePlayer * 2 + stackPane.getHeight());
        } else if (playerScenePosY - distancePlayer > stackPane.getTranslateY() + stackPane.getHeight()){
            player.setTranslateY(player.getTranslateY() - distancePlayer * 2 - stackPane.getHeight());
        }

    }

    private double getPlayerVel(){
        int playerSpeed = 4;
        double speed = playerSpeed * getSpeedGameAdapted();

        if (speed < 2){
            speed = 2;
        }else if (speed > 5){
            speed = 5;
        }

        return speed;
    }
}