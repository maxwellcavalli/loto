package br.com.loto.client.tv;

import br.com.loto.client.tv.thread.ClientThread;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {

    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());

    static ClientThread clientThread;
    static String baseDirectory;
    static String jsonFile;

    public static void main(String[] args) {

        try {
            String configFilePath = System.getProperty("config.file");
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(configFilePath)));

            String uuid = properties.getProperty("uuid");
            String address = properties.getProperty("address");
            baseDirectory = properties.getProperty("base.directory");
            jsonFile = properties.getProperty("json.file");

            InetAddress addr = InetAddress.getByName(address);
            clientThread = new ClientThread(uuid, jsonFile, addr);
            clientThread.start();

            launch(args);

        } catch (UnknownHostException ex) {
            LOG.log(Level.SEVERE, null, ex);
            System.exit(-1);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        

        AnchorPane p = (AnchorPane) fxmlLoader.load();
        
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        FXMLController controller = fxmlLoader.<FXMLController>getController();
        controller.init(baseDirectory);

        Scene scene = new Scene(p);
        scene.getStylesheets().add("/styles/Styles.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);

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
