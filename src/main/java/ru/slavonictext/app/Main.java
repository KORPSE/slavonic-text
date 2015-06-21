package ru.slavonictext.app;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import ru.slavonictext.controllers.EditorController;
import ru.slavonictext.modules.MainModule;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        BuilderFactory builderFactory = new JavaFXBuilderFactory();
        Injector injector = Guice.createInjector(new MainModule());
        Callback<Class<?>, Object> guiceControllerFactory = clazz -> injector.getInstance(clazz);

        //Editor stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"), null, builderFactory, guiceControllerFactory);
        Parent root = (Parent) loader.load();

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("CSPad");
        primaryStage.show();

        // Settings stage
        final Stage settings = new Stage(StageStyle.UTILITY);
        settings.initModality(Modality.APPLICATION_MODAL);
        settings.initOwner(primaryStage);
        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/settings.fxml"), null, builderFactory, guiceControllerFactory);
        Parent settingsRoot = (Parent) settingsLoader.load();

        Scene settingsScene = new Scene(settingsRoot, 600, 420);

        settings.setScene(settingsScene);
        settings.setTitle("Настройки");

        ((EditorController) loader.getController()).setSettingsStage(settings);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
