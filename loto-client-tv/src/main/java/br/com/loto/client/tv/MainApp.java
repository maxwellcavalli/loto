package br.com.loto.client.tv;

import br.com.loto.client.tv.thread.ClientThread;
import br.com.loto.crypto.EncriptaDecriptaRSA;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {

    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());

    static ClientThread clientThread;
    static Properties properties;
    static byte[] password;

    public static void main(String[] args) {

        try {
            String configFilePath = System.getProperty("config.file");
            properties = new Properties();
            properties.load(new FileInputStream(new File(configFilePath)));

            byte[] bytes = Files.readAllBytes(new File(properties.getProperty("password.file")).toPath());

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(properties.getProperty("private.key")))) {
                PrivateKey chavePrivate = (PrivateKey) inputStream.readObject();
                password = EncriptaDecriptaRSA.decriptografaToBytes(bytes, chavePrivate);
            }

            String address = properties.getProperty("address");

            InetAddress addr = InetAddress.getByName(address);
            clientThread = new ClientThread(properties, password, addr);
            clientThread.start();

            launch(args);

        } catch (UnknownHostException ex) {
            LOG.log(Level.SEVERE, null, ex);
            System.exit(-1);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            System.exit(-1);

        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));

        AnchorPane p = (AnchorPane) fxmlLoader.load();

        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        FXMLController controller = fxmlLoader.<FXMLController>getController();
        controller.init(properties, password);

        Scene scene = new Scene(p);
        scene.getStylesheets().add("/styles/Styles.css");

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (primaryStage.isFullScreen()) {
                    primaryStage.setFullScreen(false);
                } else {
                    primaryStage.setFullScreen(true);
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            if (clientThread != null) {
                clientThread.setStopServer(true);
            }

            Platform.exit();
            System.exit(0);
        });

        //start();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stoping...");

        super.stop(); //To change body of generated methods, choose Tools | Templates.
        if (clientThread != null) {
            clientThread.setStopServer(true);
        }
    }

}
