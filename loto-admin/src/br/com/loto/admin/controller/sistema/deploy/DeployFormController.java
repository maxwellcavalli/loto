/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.admin.controller.sistema.deploy;

import br.com.loto.admin.FxmlFiles;
import br.com.loto.admin.LotoAdmin;
import br.com.loto.admin.controller.sistema.cidade.CidadeFormController;
import br.com.loto.admin.controller.sistema.cliente.ClienteFormController;
import br.com.loto.admin.controller.sistema.estabelecimento.EstabelecimentoFormController;
import br.com.loto.admin.domain.Cidade;
import br.com.loto.admin.domain.Cliente;
import br.com.loto.admin.domain.ClientePropaganda;
import br.com.loto.admin.domain.Deploy;
import br.com.loto.admin.domain.DeployPropaganda;
import br.com.loto.admin.domain.Estabelecimento;
import br.com.loto.admin.domain.EstabelecimentoEndereco;
import br.com.loto.admin.domain.Estado;
import br.com.loto.admin.util.FxmlUtil;
import br.com.loto.admin.domain.helper.ComboHelper;
import br.com.loto.admin.domain.type.SituacaoDeploy;
import br.com.loto.admin.domain.type.TipoMidia;
import br.com.loto.admin.domain.type.TipoTransicao;
import br.com.loto.admin.service.CidadeService;
import br.com.loto.admin.service.ClienteService;
import br.com.loto.admin.service.DeployPropagandaService;
import br.com.loto.admin.service.DeployService;
import br.com.loto.admin.service.EstabelecimentoEnderecoService;
import br.com.loto.admin.service.EstabelecimentoService;
import br.com.loto.admin.service.EstadoService;
import br.com.loto.admin.util.DateUtils;
import br.com.loto.core.fx.datatable.ActionColumnButton;
import br.com.loto.core.fx.datatable.interfaces.IActionColumn;
import br.com.loto.core.fx.datatable.util.TableColumnUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import view.action.ConverterObjectToText;
import view.action.SearchActionListener;
import view.control.FMXField;

/**
 * FXML Controller class
 *
 * @author maxwe
 */
public class DeployFormController implements Initializable {

    @FXML
    public TextField txtDescricao;

    @FXML
    public CheckBox ckAtivo;

    @FXML
    public DatePicker dpValidade;

    //filtros
    @FXML
    public ComboBox<Estado> cbEstado;

    @FXML
    public FMXField<Cidade> txtCidade;

    @FXML
    public FMXField<Estabelecimento> txtEstabelecimento;

    //propaganda
    @FXML
    public FMXField<Cliente> txtCliente;

    @FXML
    public FMXField<ClientePropaganda> txtClientePropaganda;

    @FXML
    public ComboBox<ComboHelper> cbTipoTransicao;

    @FXML
    public ComboBox<ComboHelper> cbTipoMidia;

    @FXML
    public TextField txtDuracaoTransicao;

    @FXML
    public TextField txtDuracaoPropaganda;

    @FXML
    public TextField txtOrdem;

    @FXML
    public ComboBox<ComboHelper> cbSituacao;

    @FXML
    public TableView<DeployPropaganda> tablePropaganda;

    @FXML
    public Button btAdicionarPropaganda;

    //variaveis globais
    private Deploy deploy;

    private List<DeployPropaganda> deployPropagandas;
    private DeployPropaganda selectedDeployPropaganda;

    private List<ComboHelper> tiposTransicao;
    private List<ComboHelper> tiposMidia;
    private List<ComboHelper> situacoesDeploy;

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    //--
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        popularTipoTransicao();
        popularTipoMidia();
        popularSituacaoDeploy();

        configAutoCompleteCidade();
        configuraAutocompleteEstabelecimento();
        configuraAutocompleteCliente();

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
                    return CidadeService.getInstance().pesquisar(query, estado);
                } catch (SQLException ex) {
                    Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        });
    }

    void configuraAutocompleteEstabelecimento() {
        txtEstabelecimento.setConverterObjectToText((ConverterObjectToText<Estabelecimento>) (Estabelecimento t) -> t.getDescricao());

        txtEstabelecimento.setSearchActionListener((SearchActionListener<Estabelecimento>) (String query) -> {
            Cidade c = txtCidade.getObjectSelecionado();
            Estado e = cbEstado.getSelectionModel().getSelectedItem();

            Long cidade = c == null ? null : c.getId();
            Long estado = e == null ? null : e.getId();

            if (cidade == null) {
                return new ArrayList<>(0);
            } else {
                try {
                    return EstabelecimentoService.getInstance().pesquisar(query, estado, cidade, 10);
                } catch (SQLException ex) {
                    Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        });

    }

    void configuraAutocompleteCliente() {
        txtCliente.setConverterObjectToText((ConverterObjectToText<Cliente>) (Cliente t) -> t.getNome());

        txtCliente.setSearchActionListener((SearchActionListener<Cliente>) (String query) -> {
            Estabelecimento e = txtEstabelecimento.getObjectSelecionado();

            Long estabelecimento = e == null ? null : e.getId();

            if (estabelecimento == null) {
                return new ArrayList<>(0);
            } else {
                try {
                    return ClienteService.getInstance().pesquisar(query, null, null, estabelecimento, 10);
                } catch (SQLException ex) {
                    Logger.getLogger(EstabelecimentoFormController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        });

    }

    void popularSituacaoDeploy() {
        this.cbSituacao.getItems().clear();

        this.situacoesDeploy = new ArrayList<>(SituacaoDeploy.values().length);

        this.situacoesDeploy.add(new ComboHelper(SituacaoDeploy.CADASTRANDO.getKey(), SituacaoDeploy.CADASTRANDO.getDescription()));
        this.situacoesDeploy.add(new ComboHelper(SituacaoDeploy.PRONTO_DEPLOY.getKey(), SituacaoDeploy.PRONTO_DEPLOY.getDescription()));
        cbSituacao.getItems().addAll(this.tiposTransicao);

        cbSituacao.setConverter(new StringConverter<ComboHelper>() {
            @Override
            public String toString(ComboHelper object) {
                return object.getDescricao();
            }

            @Override
            public ComboHelper fromString(String string) {
                return null;
            }
        });

    }

    void popularTipoTransicao() {
        this.cbTipoTransicao.getItems().clear();

        this.tiposTransicao = new ArrayList<>(TipoTransicao.values().length);
        for (TipoTransicao tt : TipoTransicao.values()) {
            this.tiposTransicao.add(new ComboHelper(tt.getKey(), tt.getDescription()));
        }
        cbTipoTransicao.getItems().addAll(this.tiposTransicao);

        cbTipoTransicao.setConverter(new StringConverter<ComboHelper>() {
            @Override
            public String toString(ComboHelper object) {
                return object.getDescricao();
            }

            @Override
            public ComboHelper fromString(String string) {
                return null;
            }
        });

    }

    private void popularTipoMidia() {
        this.cbTipoMidia.getItems().clear();

        this.tiposMidia = new ArrayList<>(TipoMidia.values().length);
        for (TipoMidia tt : TipoMidia.values()) {
            this.tiposMidia.add(new ComboHelper(tt.getKey(), tt.getDescription()));
        }
        cbTipoTransicao.getItems().addAll(this.tiposMidia);

        cbTipoTransicao.setConverter(new StringConverter<ComboHelper>() {
            @Override
            public String toString(ComboHelper object) {
                return object.getDescricao();
            }

            @Override
            public ComboHelper fromString(String string) {
                return null;
            }
        });

    }

    public void salvar(ActionEvent event) {

        List<String> messages = new ArrayList<>();
        if (this.deploy == null) {
            this.deploy = new Deploy();
        }

        this.deploy.setDescricao(txtDescricao.getText());
        this.deploy.setAtivo(ckAtivo.selectedProperty().getValue());

        this.deploy.setDataValidade(DateUtils.asDate(dpValidade.getValue()));
        this.deploy.setEstabelecimento(txtEstabelecimento.getObjectSelecionado());
        this.deploy.setSituacao(cbSituacao.getSelectionModel().getSelectedItem().getCodigo());

        if ("".equals(this.deploy.getDescricao().trim())) {
            messages.add("Descrição Inválida");
        }

        if (this.deploy.getDataValidade() == null) {
            messages.add("Data Validade Inválida");
        }

        if (this.deploy.getEstabelecimento() == null) {
            messages.add("Estabelecimento Inválido");
        }

        if (messages.isEmpty()) {
            try {
                this.deploy = DeployService.getInstance().persistir(this.deploy, this.deployPropagandas);

                this.deployPropagandas = this.deploy.getDeployPropagandas();

                this.btAdicionarPropaganda.setText("Adicionar");
            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(DeployFormController.class.getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);
            } catch (Exception ex) {
                Logger.getLogger(DeployFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FxmlUtil.getInstance().openMessageDialog(event, messages);
        }
    }

    public void voltar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlFiles.DEPLOY_LIST));
        try {
            AnchorPane p = (AnchorPane) fxmlLoader.load();

            LotoAdmin.centerContainer.getChildren().clear();
            LotoAdmin.centerContainer.getChildren().add(p);
        } catch (IOException ex) {
            Logger.getLogger(DeployFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(Deploy deploy) {
        txtDescricao.setText(deploy.getDescricao());
        ckAtivo.setSelected(deploy.isAtivo());
        if (deploy.getDataValidade() != null) {
            dpValidade.setValue(DateUtils.asLocalDate(deploy.getDataValidade()));
        }

        ComboHelper situacaoDeploy = situacoesDeploy.stream().filter(el -> el.getCodigo() == deploy.getSituacao()).findFirst().get();
        cbSituacao.getSelectionModel().select(situacaoDeploy);

        Estabelecimento estabelecimento = deploy.getEstabelecimento();

        this.popularEstados();

        if (estabelecimento != null) {
            try {
                estabelecimento = EstabelecimentoService.getInstance().carregar(estabelecimento.getId());
                EstabelecimentoEndereco ee = EstabelecimentoEnderecoService.getInstance().carregar(estabelecimento);

                txtEstabelecimento.setObjectSelecionado(estabelecimento);
                txtEstabelecimento.setText(estabelecimento.getDescricao());

                txtCidade.setObjectSelecionado(ee.getCidade());
                txtCidade.setText(ee.getCidade().getNome());

                cbEstado.getSelectionModel().select(ee.getCidade().getEstado());

            } catch (SQLException ex) {
                Logger.getLogger(DeployFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (deploy != null && deploy.getId() != null) {
            this.popularPropagandas();
        }

        if (this.deployPropagandas == null) {
            this.deployPropagandas = new ArrayList<>(1);
        }

    }

    private void popularPropagandas() {
        try {
            this.deployPropagandas = DeployPropagandaService.getInstance().pesquisar(deploy);

            processaDatatablePropagandas();

            tablePropaganda.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    DeployPropaganda deployPropaganda = (DeployPropaganda) tablePropaganda.getSelectionModel().getSelectedItem();

                    this.selectedDeployPropaganda = deployPropaganda;

                    ComboHelper tipoTransicao = this.tiposTransicao.stream().filter(el -> el.getCodigo() == deployPropaganda.getIdTipoTransicao()).findFirst().get();
                    ComboHelper tipoMidia = this.tiposMidia.stream().filter(el -> el.getCodigo() == deployPropaganda.getIdTipoMidia()).findFirst().get();

                    this.txtClientePropaganda.setObjectSelecionado(deployPropaganda.getClientePropaganda());
                    this.cbTipoTransicao.getSelectionModel().select(tipoTransicao);
                    this.cbTipoMidia.getSelectionModel().select(deployPropaganda.getIdTipoMidia());
                    this.txtDuracaoPropaganda.setText(deployPropaganda.getDuracaoPropaganda().toString());
                    this.txtDuracaoTransicao.setText(deployPropaganda.getDuracaoTransicao().toString());

                    this.btAdicionarPropaganda.setText("Alterar");
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
            Logger.getLogger(CidadeFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeEstado(ActionEvent event) {
        txtCidade.clean();
        txtEstabelecimento.clean();
    }

    private void processaDatatablePropagandas() {

        TableColumn<DeployPropaganda, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 520, (DeployPropaganda s) -> s.getClientePropaganda().getPropaganda().getDescricao());

        TableColumn<DeployPropaganda, String> tipoTransicaoColumn = TableColumnUtil.createStringColumn("Tipo Transicao", 115, (DeployPropaganda s)
                -> TipoTransicao.get(s.getIdTipoTransicao()).getDescription());

        TableColumn<DeployPropaganda, String> tipoMidiaColumn = TableColumnUtil.createStringColumn("Tipo Midia", 115, (DeployPropaganda s)
                -> TipoMidia.get(s.getIdTipoMidia()).getDescription());

        TableColumn<DeployPropaganda, String> tempoPropagandaColumn = TableColumnUtil.createStringColumn("Tempo Propagnda", 115, (DeployPropaganda s)
                -> s.getDuracaoPropaganda().toString());

        TableColumn<DeployPropaganda, String> tempoTransicaoColumn = TableColumnUtil.createStringColumn("Tempo Transição", 115, (DeployPropaganda s)
                -> s.getDuracaoTransicao().toString());

        ActionColumnButton<DeployPropaganda> acDelete = new ActionColumnButton<>("Delete");
        acDelete.setAction((IActionColumn<DeployPropaganda>) (DeployPropaganda t) -> {
            deployPropagandas.remove(t);
            processaDatatablePropagandas();
        });

        List<ActionColumnButton<DeployPropaganda>> actionsColumnButton = new ArrayList<>(1);
        actionsColumnButton.add(acDelete);

        TableColumn<DeployPropaganda, Boolean> actionColumn = TableColumnUtil.createButtonColumn("Ação", 80, tablePropaganda, actionsColumnButton);

        tablePropaganda.getColumns().clear();
        tablePropaganda.getColumns().setAll(descricaoColumn, tipoTransicaoColumn, tipoMidiaColumn, tempoPropagandaColumn, tempoTransicaoColumn, actionColumn);
        tablePropaganda.setItems(FXCollections.observableArrayList(this.deployPropagandas));
    }

    public void adicionarPropaganda(ActionEvent event) {
        try {

            DeployPropaganda dp = new DeployPropaganda();
            dp.setDeploy(this.deploy);
            dp.setClientePropaganda(this.txtClientePropaganda.getObjectSelecionado());
            dp.setDuracaoPropaganda(Integer.parseInt(this.txtDuracaoPropaganda.getText()));
            dp.setDuracaoTransicao(Integer.parseInt(this.txtDuracaoTransicao.getText()));
            dp.setIdTipoMidia(cbTipoMidia.getSelectionModel().getSelectedItem().getCodigo());
            dp.setIdTipoTransicao(cbTipoTransicao.getSelectionModel().getSelectedItem().getCodigo());
            dp.setOrdem(Integer.parseInt(txtOrdem.getText()));

            List<String> messages = new ArrayList<>();

            if (dp.getClientePropaganda() == null) {
                messages.add("Propaganda Inválida");
            }

            if (dp.getDuracaoPropaganda() <= 0) {
                messages.add("Duração Propaganda Inválida");
            }

            if (dp.getDuracaoTransicao() <= 0) {
                messages.add("Duração Transição Inválida");
            }

            if (dp.getOrdem() <= 0) {
                messages.add("Ordem Inválida");
            }

            if (!messages.isEmpty()) {
                FxmlUtil.getInstance().openMessageDialog(event, messages);
            } else {
                if (this.selectedDeployPropaganda != null) {
                    selectedDeployPropaganda.setClientePropaganda(dp.getClientePropaganda());
                    selectedDeployPropaganda.setDuracaoPropaganda(dp.getDuracaoPropaganda());
                    selectedDeployPropaganda.setDuracaoTransicao(dp.getDuracaoTransicao());
                    selectedDeployPropaganda.setIdTipoMidia(dp.getIdTipoMidia());
                    selectedDeployPropaganda.setIdTipoTransicao(dp.getIdTipoTransicao());
                    selectedDeployPropaganda.setOrdem(dp.getOrdem());

                    int index = this.deployPropagandas.indexOf(this.selectedDeployPropaganda);
                    this.deployPropagandas.set(index, selectedDeployPropaganda);
                } else {
                    this.deployPropagandas.add(dp);
                }

                processaDatatablePropagandas();

                this.txtClientePropaganda.clean();
                this.txtDuracaoPropaganda.clear();
                this.txtDuracaoTransicao.clear();
                this.cbTipoMidia.getSelectionModel().clearSelection();
                this.cbTipoTransicao.getSelectionModel().clearSelection();
                this.txtOrdem.clear();

                this.selectedDeployPropaganda = null;

                this.btAdicionarPropaganda.setText("Adicionar");
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(ClienteFormController.class.getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

}
