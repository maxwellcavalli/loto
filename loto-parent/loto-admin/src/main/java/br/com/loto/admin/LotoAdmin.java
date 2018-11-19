/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin;

import br.com.loto.core.util.JdbcUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author maxwe
 */
public class LotoAdmin extends Application {

    public static VBox centerContainer = new VBox();
    static Properties properties;
    private static final Logger LOG = Logger.getLogger(LotoAdmin.class.getName());

    private MenuItem createMenuItem(String caption, String fxmFile) {
        MenuItem menuItem = new MenuItem(caption);
        menuItem.setOnAction((ActionEvent event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmFile));

                AnchorPane p = (AnchorPane) fxmlLoader.load();

                centerContainer.getChildren().clear();
                centerContainer.getChildren().add(p);

            } catch (IOException e) {
                LOG.log(Level.SEVERE, null, e);
            }
        });

        return menuItem;
    }

    @Override
    public void start(Stage primaryStage) {
        centerContainer.setFillWidth(true);
        centerContainer.setAlignment(Pos.CENTER);
        //centerContainer.setStyle("-fx-background-color:  #19b38a");

        BorderPane root = new BorderPane();
        VBox topContainer = new VBox();

        MenuBar mainMenu = new MenuBar();

        Menu menuCadastros = new Menu("Cadastros");

        MenuItem menuEstado = createMenuItem("Estado", FxmlFiles.ESTADO_LIST);
        MenuItem menuCidade = createMenuItem("Cidade", FxmlFiles.CIDADE_LIST);

        SeparatorMenuItem separatorLocalidade = new SeparatorMenuItem();

        MenuItem menuEquipamento = createMenuItem("Equipamento", FxmlFiles.EQUIPAMENTO_LIST);
        SeparatorMenuItem separatorEquipamento = new SeparatorMenuItem();

        MenuItem menuEstabelecimento = createMenuItem("Estabelecimento", FxmlFiles.ESTABELECIMENTO_LIST);
        MenuItem menuCliente = createMenuItem("Cliente", FxmlFiles.CLIENTE_LIST);

        SeparatorMenuItem separatorCliente = new SeparatorMenuItem();

        MenuItem menuDeploy = createMenuItem("Deploy", FxmlFiles.DEPLOY_LIST);

        SeparatorMenuItem separatorDeploy = new SeparatorMenuItem();

        MenuItem menuResultadoLoteria = createMenuItem("Resultado Loterias", FxmlFiles.RESULTADO_LOTERIA_LIST);

        menuCadastros.getItems().addAll(
                menuEstado, menuCidade, separatorLocalidade,
                menuEquipamento, separatorEquipamento,
                menuEstabelecimento, menuCliente, separatorCliente,
                menuDeploy, separatorDeploy,
                menuResultadoLoteria
        );

        Menu help = new Menu("Help");
        MenuItem visitWebsite = new MenuItem("Visit Website");
        help.getItems().add(visitWebsite);

        mainMenu.getMenus().addAll(menuCadastros, help);

        topContainer.getChildren().add(mainMenu);
        root.setTop(topContainer);

        root.setCenter(centerContainer);

        //int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        //int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        int width = 1024;
        int height = 768;

        Scene scene = new Scene(root, width, height);

        primaryStage.getIcons().add(new Image("icon.png"));

        //Setup the Stage.
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Loto-Admin");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String configFilePath = System.getProperty("config.file");
            properties = new Properties();
            properties.load(new FileInputStream(new File(configFilePath)));

        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            System.exit(0);
        }

        String dbUrl = properties.getProperty("db.connection.url");
        Properties props = new Properties();
        props.setProperty("user", properties.getProperty("db.connection.user"));
        props.setProperty("password", properties.getProperty("db.connection.pwd"));
        

//        String dbUrl = "jdbc:hsqldb:file:/home/mcavalli/hsqldb/dbloto";
//        Properties props = new Properties();
//        props.setProperty("user", "SA");
//        props.setProperty("password", "");
//        props.setProperty("ssl", "true");
        try {
            JdbcUtil.getInstance().init(dbUrl, props);
            launch(args);
        } catch (SQLException ex) {
            Logger.getLogger(LotoAdmin.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

}
