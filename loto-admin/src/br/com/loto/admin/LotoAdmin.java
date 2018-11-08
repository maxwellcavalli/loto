/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin;

import br.com.loto.core.util.JdbcUtil;
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

    private MenuItem createMenuItem(String caption, String fxmFile) {
        MenuItem menuItem = new MenuItem(caption);
        menuItem.setOnAction((ActionEvent event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmFile));

                AnchorPane p = (AnchorPane) fxmlLoader.load();

                centerContainer.getChildren().clear();
                centerContainer.getChildren().add(p);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return menuItem;
    }

    @Override
    public void start(Stage primaryStage) {
        centerContainer.setFillWidth(true);
        centerContainer.setAlignment(Pos.CENTER);
        //centerContainer.setStyle("-fx-background-color:  #19b38a");

        //Setup the VBox Container and BorderPane
        BorderPane root = new BorderPane();
        VBox topContainer = new VBox();

        //Setup the Main Menu bar and the ToolBar
        MenuBar mainMenu = new MenuBar();
        //ToolBar toolBar = new ToolBar();

        //Create SubMenu File.
        Menu menuCadastros = new Menu("Cadastros");

        MenuItem menuEstado = createMenuItem("Estado", FxmlFiles.ESTADO_LIST);
        MenuItem menuCidade = createMenuItem("Cidade", FxmlFiles.CIDADE_LIST);

        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem menuEquipamento = createMenuItem("Equipamento", FxmlFiles.EQUIPAMENTO_LIST);
        MenuItem menuEstabelecimento = createMenuItem("Estabelecimento", FxmlFiles.ESTABELECIMENTO_LIST);
        MenuItem menuCliente = createMenuItem("Cliente", FxmlFiles.CLIENTE_LIST);

        menuCadastros.getItems().addAll(menuEstado, menuCidade, separator, menuEquipamento, menuEstabelecimento, menuCliente);

        //Create SubMenu Help.
        Menu help = new Menu("Help");
        MenuItem visitWebsite = new MenuItem("Visit Website");
        help.getItems().add(visitWebsite);

        mainMenu.getMenus().addAll(menuCadastros, help);

        //Create some toolbar buttons
        //Add the ToolBar and Main Meu to the VBox
        topContainer.getChildren().add(mainMenu);
        //topContainer.getChildren().add(toolBar);

        //Apply the VBox to the Top Border
        root.setTop(topContainer);

        root.setCenter(centerContainer);

        //int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        //int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
        int width = 1024;
        int height = 768;

        Scene scene = new Scene(root, width, height);

        primaryStage.getIcons().add(new Image("/br/com/loto/admin/resources/icon.png"));

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
//        String dbUrl = "jdbc:postgresql://localhost/loto";
//        Properties props = new Properties();
//        props.setProperty("user", "postgres");
//        props.setProperty("password", "apollo");

        String dbUrl = "jdbc:hsqldb:file:/home/mcavalli/dbloto";
        Properties props = new Properties();
        props.setProperty("user", "SA");
        props.setProperty("password", "");
        props.setProperty("ssl", "true");
        try {
            JdbcUtil.getInstance().init(dbUrl, props);
            launch(args);
        } catch (SQLException ex) {
            Logger.getLogger(LotoAdmin.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

}
