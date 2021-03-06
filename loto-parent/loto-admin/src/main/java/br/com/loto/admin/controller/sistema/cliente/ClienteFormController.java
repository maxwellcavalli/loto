/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.cliente;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.ClientePropaganda;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.domain.Propaganda;
import br.com.loto.admin.service.CidadeService;
import br.com.loto.admin.service.ClientePropagandaService;
import br.com.loto.admin.service.ClienteService;
import br.com.loto.admin.service.EstadoService;
import br.com.loto.core.fx.datatable.ActionColumnButton;
import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
import br.com.loto.core.fx.datatable.interfaces.IActiveColumn;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import view.action.ConverterObjectToText;
import view.action.SearchActionListener;
import view.control.FMXField;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class ClienteFormController implements Initializable {

    @FXML
    public TextField txtDescricao;

    @FXML
    public CheckBox ckAtivo;

    @FXML
    public CheckBox ckTodasLocalidades;

    @FXML
    public ComboBox<Estado> cbEstado;

    @FXML
    public FMXField<Cidade> txtCidade;

    //propaganda
    @FXML
    public TableView<ClientePropaganda> tablePropaganda;

    @FXML
    public TextField txtDescricaoPropaganda;

    @FXML
    public TextField txtNomeArquivo;

    @FXML
    public CheckBox cbPropaganda;

    @FXML
    public Button btAdicionarPropaganda;

    @FXML
    public Button btCancelar;

    //variaveis globais
    private Cliente cliente;

    private ClientePropaganda selectedClientePropaganda;
    private List<ClientePropaganda> propagandas;

    private File file;

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configAutoCompleteCidade();
    }

    public void cancelarPropaganda(ActionEvent event) {
        txtDescricaoPropaganda.setText("");
        cbPropaganda.setSelected(true);
        txtNomeArquivo.setText("");

        this.selectedClientePropaganda = null;

        this.btCancelar.setVisible(false);
        this.btAdicionarPropaganda.setText("Adicionar");
    }

    void configAutoCompleteCidade() {
        txtCidade.setConverterObjectToText((ConverterObjectToText<Cidade>) (Cidade t) -> t.getNome());

        txtCidade.setSearchActionListener((SearchActionListener<Cidade>) (String query) -> {
            Estado e = cbEstado.getSelectionModel().getSelectedItem();
            Long estado = e == null ? null : e.getId();

            if (estado == null) {
                return new ArrayList<>(0);
            } else {
                try {
                    return CidadeService.getInstance().pesquisar(query, estado, 10);
                } catch (SQLException ex) {
                    Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        });
    }

    public void salvar(ActionEvent event) {

        List<String> messages = new ArrayList<>();
        if (this.cliente == null) {
            this.cliente = new Cliente();
        }

        this.cliente.setNome(txtDescricao.getText());
        this.cliente.setAtivo(ckAtivo.selectedProperty().getValue());
        this.cliente.setMostrarTodasLocalidades(this.ckTodasLocalidades.selectedProperty().getValue());

        Cidade cidade = txtCidade.getObjectSelecionado();
        this.cliente.setCidade(cidade);

        if ("".equals(this.cliente.getNome().trim())) {
            messages.add("Nome Inválido");
        }

        if (this.cliente.getCidade() == null || this.cliente.getCidade().getId() == null) {
            messages.add("Estado/Cidade Inválidos");
        }

        if (messages.isEmpty()) {
            try {
                this.cliente = ClienteService.getInstance().persistir(this.cliente, this.propagandas);

                this.propagandas = this.cliente.getListClientePropaganda();

            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);
            } catch (Exception ex) {
                Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                this.btCancelar.setVisible(false);
                this.btAdicionarPropaganda.setText("Adicionar");
            }
        } else {
            FxmlUtil.getInstance().openMessageDialog(event, messages);
        }
    }

    public void voltar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.CLIENTE_LIST));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);
        } catch (IOException ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(Cliente cliente) {
        txtDescricao.setText(cliente.getNome());
        ckAtivo.setSelected(cliente.isAtivo());
        ckTodasLocalidades.setSelected(cliente.isMostrarTodasLocalidades());

        cbPropaganda.setSelected(true);

        this.cliente = cliente;

        this.popularPropagandas();

        this.popularEstados();
        cbEstado.getSelectionModel().clearSelection();
        txtCidade.clean();

        if (cliente.getCidade() != null) {
            cbEstado.getSelectionModel().select(cliente.getCidade().getEstado());

            txtCidade.setObjectSelecionado(cliente.getCidade());
            txtCidade.setText(cliente.getCidade().getNome());
        }
    }

    private void popularPropagandas() {
        try {
            this.propagandas = ClientePropagandaService.getInstance().pesquisar(cliente);
            if (this.propagandas == null) {
                this.propagandas = new ArrayList<>(1);
            }

            processaDatatablePropagandas();

            tablePropaganda.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    ClientePropaganda clientePropaganda = (ClientePropaganda) tablePropaganda.getSelectionModel().getSelectedItem();

                    if (this.selectedClientePropaganda != null) {
                        this.selectedClientePropaganda = clientePropaganda;

                        this.txtDescricaoPropaganda.setText(selectedClientePropaganda.getPropaganda().getDescricao());
                        this.txtNomeArquivo.setText(selectedClientePropaganda.getPropaganda().getNomeArquivo());
                        this.cbPropaganda.setSelected(selectedClientePropaganda.getPropaganda().isAtivo());

                        this.btAdicionarPropaganda.setText("Alterar");
                        this.btCancelar.setVisible(true);

                    }
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void popularEstados() {
        List<Estado> listEstado;
        try {
            cbEstado.getItems().add(new Estado());

            listEstado = EstadoService.getInstance().pesquisar("");
            listEstado.stream().forEach(el -> cbEstado.getItems().add(el));

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

        } catch (SQLException ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeEstado(ActionEvent event) {
        txtCidade.clean();
    }

    private void viewContent(ClientePropaganda t) {
        FileOutputStream fOut = null;
        try {
            Propaganda p = t.getPropaganda();
            String fileName = p.getNomeArquivo();
            byte[] conteudo = p.getConteudo();
            String tmpPath = System.getProperty("java.io.tmpdir");
            File f = new File(tmpPath + File.separatorChar + fileName);
            fOut = new FileOutputStream(f);
            fOut.write(conteudo);
            fOut.flush();
            fOut.close();

            Desktop desktop = Desktop.getDesktop();
            desktop.open(f);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void processaDatatablePropagandas() {

        TableColumn<ClientePropaganda, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 520, (ClientePropaganda s) -> s.getPropaganda().getDescricao());
        TableColumn<ClientePropaganda, String> dataColumn = TableColumnUtil.createStringColumn("Data", 115, (ClientePropaganda s) -> sdf.format(s.getPropaganda().getData()));
        TableColumn<ClientePropaganda, String> dataInativacaoColumn = TableColumnUtil.createStringColumn("Inativação", 115, (ClientePropaganda s)
                -> s.getPropaganda().getDataInativacao() != null ? sdf.format(s.getPropaganda().getData()) : "");

        TableColumn<ClientePropaganda, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 50, (ClientePropaganda s) -> s.getPropaganda().getAtivoStr());

        ActionColumnButton<ClientePropaganda> acView = new ActionColumnButton<>("View");
        acView.setAction((IActionColumn<ClientePropaganda>) (ClientePropaganda t) -> {
            viewContent(t);
        });

        ActionColumnButton<ClientePropaganda> acDelete = new ActionColumnButton<>("Delete");
        acDelete.setAction((IActionColumn<ClientePropaganda>) (ClientePropaganda t) -> {
            
            if (t.getPropaganda().isAtivo()){
                t.getPropaganda().setAtivo(false);
                t.getPropaganda().setDataInativacao(new Date());
            } else {
                t.getPropaganda().setAtivo(true);
                t.getPropaganda().setDataInativacao(null);
            }
            //propagandas.remove(t);
            processaDatatablePropagandas();
        });
        acDelete.setActiveColumn((ClientePropaganda t) -> {
            return t.isHasDeploy();
        });
        
        acDelete.setConditionalLabel((ClientePropaganda t) -> {
            return t.getPropaganda().isAtivo() == true ? "Inativar" : "Ativar";
        });

        List<ActionColumnButton<ClientePropaganda>> actionsColumnButton = new ArrayList<>(2);
        actionsColumnButton.add(acView);
        actionsColumnButton.add(acDelete);

        TableColumn<ClientePropaganda, ClientePropaganda> actionColumn = TableColumnUtil.createButtonColumn("Ação", 140, tablePropaganda, actionsColumnButton);

//        actionColumn.setSortable(false);
//        descricaoColumn.setSortable(false);
//        dataColumn.setSortable(false);
//        dataInativacaoColumn.setSortable(false);
//        ativoColumn.setSortable(false);

        tablePropaganda.getColumns().clear();
        tablePropaganda.getColumns().setAll(descricaoColumn, dataColumn, dataInativacaoColumn, ativoColumn, actionColumn);
        tablePropaganda.setItems(FXCollections.observableArrayList(this.propagandas));
    }

    public void adicionarPropaganda(ActionEvent event) {
        try {

            Propaganda propaganda = new Propaganda();
            propaganda.setDescricao(txtDescricaoPropaganda.getText());
            propaganda.setAtivo(cbPropaganda.selectedProperty().getValue());
            propaganda.setData(new Date());

            if (file != null) {
                try {
                    Path fileLocation = Paths.get(file.getPath());
                    byte[] conteudo = Files.readAllBytes(fileLocation);

                    propaganda.setNomeArquivo(file.getName());
                    propaganda.setConteudo(conteudo);

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            List<String> messages = new ArrayList<>();

            if (propaganda.getDescricao() == null || propaganda.getDescricao().isEmpty()) {
                messages.add("Descrição Inválida");
            }

            if ((propaganda.getConteudo() == null || propaganda.getConteudo().length == 0) && this.selectedClientePropaganda == null) {
                messages.add("Arquivo Inválido");
            }

            if (!propaganda.isAtivo()) {
                propaganda.setDataInativacao(new Date());
            } else {
                propaganda.setDataInativacao(null);
            }

            if (!messages.isEmpty()) {
                FxmlUtil.getInstance().openMessageDialog(event, messages);
            } else {
                if (this.selectedClientePropaganda != null) {
                    this.selectedClientePropaganda.getPropaganda().setAtivo(propaganda.isAtivo());
                    this.selectedClientePropaganda.getPropaganda().setDescricao(propaganda.getDescricao());
                    this.selectedClientePropaganda.getPropaganda().setData(new Date());
                    this.selectedClientePropaganda.getPropaganda().setDataInativacao(propaganda.getDataInativacao());
                    if (file != null) {
                        this.selectedClientePropaganda.getPropaganda().setConteudo(propaganda.getConteudo());
                        this.selectedClientePropaganda.getPropaganda().setNomeArquivo(propaganda.getNomeArquivo());
                    }

                    int index = this.propagandas.indexOf(this.selectedClientePropaganda);
                    this.propagandas.set(index, selectedClientePropaganda);
                } else {
                    ClientePropaganda clientePropaganda = new ClientePropaganda();
                    clientePropaganda.setCliente(cliente);
                    clientePropaganda.setPropaganda(propaganda);

                    this.propagandas.add(clientePropaganda);
                }

                processaDatatablePropagandas();

                txtDescricaoPropaganda.setText("");
                cbPropaganda.setSelected(true);
                txtNomeArquivo.setText("");

                this.selectedClientePropaganda = null;

                this.btAdicionarPropaganda.setText("Adicionar");
                this.btCancelar.setVisible(false);
            }
        } catch (Exception ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void selecionarArquivo(ActionEvent event) {
        Stage stage = new Stage(StageStyle.UTILITY);

        txtNomeArquivo.setText("");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Arquivo");
        this.file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            txtNomeArquivo.setText(file.getName());
        }

    }

}
