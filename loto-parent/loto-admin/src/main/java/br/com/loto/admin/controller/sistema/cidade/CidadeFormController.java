/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.cidade;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.service.CidadeService;
import br.com.loto.admin.service.EstadoService;
import br.com.loto.admin.util.FxmlUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class CidadeFormController implements Initializable {

    @FXML
    public TextField txtDescricao;

    @FXML
    public CheckBox ckAtivo;

    @FXML
    public ComboBox<Estado> cbEstado;

    //variaveis globais
    private Cidade cidade;

    private Estado estado;

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        
        
    }

    public void salvar(ActionEvent event) {

        List<String> messages = new ArrayList<>();
        if (this.cidade == null) {
            this.cidade = new Cidade();
        }

        this.cidade.setNome(txtDescricao.getText());
        this.cidade.setAtivo(ckAtivo.selectedProperty().getValue());
        this.cidade.setEstado(cbEstado.getSelectionModel().getSelectedItem());
        
        if ("".equals(this.cidade.getNome().trim())) {
            messages.add("Nome Inválido");
        }

        if (this.cidade.getEstado() == null) {
            messages.add("Estado Inválido");
        }

        if (messages.isEmpty()) {
            try {
                this.cidade = CidadeService.getInstance().persistir(this.cidade);
            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(CidadeFormController.class.getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);
            } catch (Exception ex) {
                Logger.getLogger(CidadeFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FxmlUtil.getInstance().openMessageDialog(event, messages);
        }
    }

    public void voltar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.CIDADE_LIST));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);
        } catch (IOException ex) {
            Logger.getLogger(CidadeFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(Cidade cidade) {
        txtDescricao.setText(cidade.getNome());
        ckAtivo.setSelected(cidade.isAtivo());
        
        popularEstados();
        
        cbEstado.getSelectionModel().select(cidade.getEstado());

        this.cidade = cidade;
        this.estado = this.cidade.getEstado();

    }
    
    private void popularEstados(){
         List<Estado> listEstado;
        try {
            listEstado = EstadoService.getInstance().pesquisar("");
            listEstado.stream().forEach(el -> cbEstado.getItems().add(el));
        } catch (SQLException ex) {
            Logger.getLogger(CidadeFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         cbEstado.setConverter(new StringConverter<Estado>() {
                @Override
                public String toString(Estado object) {
                    return object.getSigla();
                }

                @Override
                public Estado fromString(String string) {
                    return null;
                }
            });
    }
}
