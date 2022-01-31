package com.madirex.masteroids;

import com.madirex.masteroids.utils.SVGUtils;
import com.madirex.masteroids.view.MenuView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage){

        int width = 800;
        int height = 600;

        MenuView menu = new MenuView(width, height);

        Scene scene = new Scene(menu, width, height);
        stage.setTitle("Masteroids");
        stage.setScene(scene);
        stage.show();

        stage.getIcons().add(SVGUtils.svg2image("appIcon",1).getImage());

    }
}
