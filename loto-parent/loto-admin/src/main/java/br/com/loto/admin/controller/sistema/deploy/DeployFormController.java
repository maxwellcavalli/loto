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
import br.com.loto.shared.domain.type.TipoMidia;
import br.com.loto.shared.domain.type.TipoTransicao;
import br.com.loto.admin.service.CidadeService;
import br.com.loto.admin.service.ClientePropagandaService;
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
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public TextField txtVersao;

    @FXML
    public TableView<DeployPropaganda> tablePropaganda;

    @FXML
    public Button btAdicionarPropaganda;

    @FXML
    public Button btCancelar;

    @FXML
    public JFXButton btSalvar;

    @FXML
    public JFXButton btClonar;

    @FXML
    public TableView<Deploy> tableVersoes;

    //variaveis globais
    private Deploy deploy;

    private List<DeployPropaganda> deployPropagandas;
    private DeployPropaganda selectedDeployPropaganda;

    private List<ComboHelper> tiposTransicao;
    private List<ComboHelper> tiposMidia;
    private List<ComboHelper> situacoesDeploy;

    private boolean disabled = false;

    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    final SimpleDateFormat sdfNH = new SimpleDateFormat("dd/MM/yyyy");

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
        configAutoCompletePropagandaCliente();

        inicializaDadosPropaganda();
    }

    void popularOutrasVersoes() {
        Estabelecimento estabelecimentoO = deploy.getEstabelecimento();
        if (estabelecimentoO != null) {

            try {
                Long estabelecimento = estabelecimentoO == null ? null : estabelecimentoO.getId();

                boolean somenteUltVersao = false;
                String descricao = null;
                Long estado = null;
                Long cidade = null;
                Integer situacao = null;

                List<Deploy> list = DeployService.getInstance().pesquisar(descricao, estado, cidade, estabelecimento, situacao, somenteUltVersao);

                TableColumn<Deploy, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 200, (Deploy s) -> s.getDescricao());
                TableColumn<Deploy, String> estabelecimentoColumn = TableColumnUtil.createStringColumn("Estabelecimento", 200, (Deploy s)
                        -> s.getEstabelecimento().getDescricao());

                TableColumn<Deploy, String> cidadeColumn = TableColumnUtil.createStringColumn("Cidade", 200, (Deploy s)
                        -> s.getEstabelecimento().getEstabelecimentoEndereco() == null ? "" : s.getEstabelecimento().getEstabelecimentoEndereco().getCidade().getNome());

                TableColumn<Deploy, String> estadoColumn = TableColumnUtil.createStringColumn("UF", 60, (Deploy s)
                        -> s.getEstabelecimento().getEstabelecimentoEndereco() == null ? ""
                        : s.getEstabelecimento().getEstabelecimentoEndereco().getCidade().getEstado().getSigla());

                TableColumn<Deploy, String> dataColumn = TableColumnUtil.createStringColumn("Data", 120, (Deploy s)
                        -> sdf.format(s.getData()));

                TableColumn<Deploy, String> dataValidadeColumn = TableColumnUtil.createStringColumn("Data Validade", 120, (Deploy s)
                        -> s.getDataValidade() == null ? "" : sdfNH.format(s.getDataValidade()));

                TableColumn<Deploy, String> situacaoColumn = TableColumnUtil.createStringColumn("Situação", 140, (Deploy s)
                        -> SituacaoDeploy.get(s.getSituacao()).getDescription());

                TableColumn<Deploy, String> versaoColumn = TableColumnUtil.createStringColumn("Versão", 60, (Deploy s)
                        -> s.getVersao().toString());

                TableColumn<Deploy, String> ativoColumn = TableColumnUtil.createStringColumn("Ativo", 50, (Deploy s) -> s.getAtivoStr());

                tableVersoes.getColumns().clear();
                tableVersoes.getColumns().setAll(estabelecimentoColumn, descricaoColumn, cidadeColumn, estadoColumn,
                        dataColumn, dataValidadeColumn, situacaoColumn, versaoColumn, ativoColumn);
                tableVersoes.setItems(FXCollections.observableArrayList(list));

                try {
                    tableVersoes.setOnMouseClicked((MouseEvent event) -> {
                        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {

                            Deploy d = (Deploy) tableVersoes.getSelectionModel().getSelectedItem();

                            initData(d, true);
                            
                        }
                    });

                } catch (Exception e) {
                    throw e;
                }

            } catch (Exception ex) {
                Logger.getLogger(DeployListController.class.getName()).log(Level.SEVERE, null, ex);
                
                //FxmlUtil.getInstance().openMessageDialog(acEvent, ex);
            }

        }

    }

    public void clonar(ActionEvent event) {
        try {
            Deploy newDeploy = DeployService.getInstance().clonar(this.deploy);
            this.disabled = false;
            this.initData(newDeploy, false);

        } catch (SQLException | CloneNotSupportedException ex) {
            FxmlUtil.getInstance().openMessageDialog(event, ex);
        } catch (Exception ex) {
            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void cancelarPropaganda(ActionEvent event) {
        this.txtCliente.clean();
        this.txtClientePropaganda.clean();
        this.txtDuracaoPropaganda.clear();
        this.txtDuracaoTransicao.clear();
        this.cbTipoMidia.getSelectionModel().clearSelection();
        this.cbTipoTransicao.getSelectionModel().clearSelection();
        this.txtOrdem.clear();

        this.selectedDeployPropaganda = null;

        this.btAdicionarPropaganda.setText("Adicionar");
        this.btCancelar.setVisible(false);

        inicializaDadosPropaganda();
    }

    void validaDisableEstabelecimento(SituacaoDeploy situacaoDeploy) {
        boolean disable = !situacaoDeploy.equals(SituacaoDeploy.CADASTRANDO)
                || (this.deployPropagandas != null && !this.deployPropagandas.isEmpty());

        this.cbEstado.setDisable(disable);
        this.txtCidade.setDisable(disable);
        this.txtEstabelecimento.setDisable(disable);
    }

    void alterarSituacaoComponentes(SituacaoDeploy situacaoDeploy) {
        disabled = !situacaoDeploy.equals(SituacaoDeploy.CADASTRANDO);

        boolean disabledSituacao = situacaoDeploy.equals(SituacaoDeploy.LIBERADO_DEPLOY)
                || situacaoDeploy.equals(SituacaoDeploy.IMPLANTADO);

        this.cbSituacao.setDisable(disabledSituacao);
        this.btSalvar.setDisable(disabledSituacao);

        this.txtDescricao.setDisable(disabled);
        this.ckAtivo.setDisable(disabled);
        this.dpValidade.setDisable(disabled);
        this.cbEstado.setDisable(disabled);
        this.txtCidade.setDisable(disabled);
        this.txtEstabelecimento.setDisable(disabled);
        this.txtCliente.setDisable(disabled);
        this.txtClientePropaganda.setDisable(disabled);
        this.cbTipoTransicao.setDisable(disabled);
        this.cbTipoMidia.setDisable(disabled);
        this.txtDuracaoTransicao.setDisable(disabled);
        this.txtDuracaoPropaganda.setDisable(disabled);
        this.txtOrdem.setDisable(disabled);

//        this.tablePropaganda.getC setDisable(disabled);
        this.btAdicionarPropaganda.setDisable(disabled);
        this.btCancelar.setDisable(disabled);

        validaDisableEstabelecimento(situacaoDeploy);

    }

    void configAutoCompletePropagandaCliente() {
        txtClientePropaganda.setConverterObjectToText((ConverterObjectToText<ClientePropaganda>) (ClientePropaganda t)
                -> t.getPropaganda().getDescricao());

        txtClientePropaganda.setSearchActionListener((SearchActionListener<ClientePropaganda>) (String query) -> {
            Cliente c = txtCliente.getObjectSelecionado();
            Long cliente = c == null ? null : c.getId();

            if (cliente == null) {
                return new ArrayList<>(0);
            } else {
                try {
                    return ClientePropagandaService.getInstance().pesquisar(c, query, true, 10);

                } catch (SQLException ex) {
                    Logger.getLogger(EstabelecimentoFormController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        });
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
                    Logger.getLogger(EstabelecimentoFormController.class
                            .getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(EstabelecimentoFormController.class
                            .getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(EstabelecimentoFormController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
        });

    }

    void popularSituacaoDeploy() {
        this.cbSituacao.getItems().clear();

        this.situacoesDeploy = new ArrayList<>(SituacaoDeploy.values().length);

        this.situacoesDeploy.add(new ComboHelper(SituacaoDeploy.CADASTRANDO.getKey(), SituacaoDeploy.CADASTRANDO.getDescription()));
        this.situacoesDeploy.add(new ComboHelper(SituacaoDeploy.BLOQUEADO_DEPLOY.getKey(), SituacaoDeploy.BLOQUEADO_DEPLOY.getDescription()));
        this.situacoesDeploy.add(new ComboHelper(SituacaoDeploy.LIBERADO_DEPLOY.getKey(), SituacaoDeploy.LIBERADO_DEPLOY.getDescription()));
        cbSituacao.getItems().addAll(this.situacoesDeploy);

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
        cbTipoMidia.getItems().addAll(this.tiposMidia);

        cbTipoMidia.setConverter(new StringConverter<ComboHelper>() {
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

        if (dpValidade.getValue() != null) {
            this.deploy.setDataValidade(DateUtils.asDate(dpValidade.getValue()));
        }
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

                txtVersao.setText(this.deploy.getVersao().toString());
                
                this.deployPropagandas = this.deploy.getDeployPropagandas();

                SituacaoDeploy sd = SituacaoDeploy.get(this.deploy.getSituacao());
                alterarSituacaoComponentes(sd);
                processaDatatablePropagandas();

                this.btClonar.setVisible(deploy.getId() != null && deploy.getId() > 0);

                popularOutrasVersoes();
            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                Logger.getLogger(DeployFormController.class
                        .getName()).log(Level.SEVERE, null, ex);

                FxmlUtil.getInstance().openMessageDialog(event, ex);

            } catch (Exception ex) {
                Logger.getLogger(DeployFormController.class
                        .getName()).log(Level.SEVERE, null, ex);
            } finally {
                this.btAdicionarPropaganda.setText("Adicionar");
                this.btCancelar.setVisible(false);
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
            Logger.getLogger(DeployFormController.class
                    .getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

    public void initData(Deploy deploy, boolean reloadPropagandas) {
        this.deploy = deploy;

        txtDescricao.setText(deploy.getDescricao());
        ckAtivo.setSelected(deploy.isAtivo());
        if (deploy.getDataValidade() != null) {
            dpValidade.setValue(DateUtils.asLocalDate(deploy.getDataValidade()));
        }

        txtVersao.setText(deploy.getVersao().toString());
        txtVersao.setDisable(true);

        this.btClonar.setVisible(deploy.getId() != null && deploy.getId() > 0);

        Optional<ComboHelper> oSituacao = situacoesDeploy.stream().filter(el -> el.getCodigo() == deploy.getSituacao()).findFirst();
        if (oSituacao.isPresent()) {
            ComboHelper situacaoDeploy = situacoesDeploy.stream().filter(el -> el.getCodigo() == deploy.getSituacao()).findFirst().get();
            cbSituacao.getSelectionModel().select(situacaoDeploy);
            cbSituacao.setDisable(false);
        } else {
            cbSituacao.setDisable(true);
        }

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
                Logger.getLogger(DeployFormController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.deploy = deploy;
        if (this.deployPropagandas == null) {
            this.deployPropagandas = new ArrayList<>(1);
        }

        SituacaoDeploy sd = SituacaoDeploy.get(deploy.getSituacao());
        alterarSituacaoComponentes(sd);

        this.popularPropagandas(reloadPropagandas);

        validaDisableEstabelecimento(sd);

        
        if (this.deploy.getId() != null){
            popularOutrasVersoes();
        }
        
    }

    void inicializaDadosPropaganda() {
        DeployPropaganda dp = new DeployPropaganda();

        ComboHelper tipoTransicao = this.tiposTransicao.stream().filter(el -> el.getCodigo() == dp.getIdTipoTransicao()).findFirst().get();
        ComboHelper tipoMidia = this.tiposMidia.stream().filter(el -> el.getCodigo() == dp.getIdTipoMidia()).findFirst().get();

        this.cbTipoTransicao.getSelectionModel().select(tipoTransicao);
        this.cbTipoMidia.getSelectionModel().select(tipoMidia);
        this.txtDuracaoPropaganda.setText(dp.getDuracaoPropaganda().toString());
        this.txtDuracaoTransicao.setText(dp.getDuracaoTransicao().toString());
    }

    private void popularPropagandas(boolean reloadFromDB) {
        try {
            if (reloadFromDB) {
                this.deployPropagandas = DeployPropagandaService.getInstance().pesquisar(deploy);
            }

            processaDatatablePropagandas();

            tablePropaganda.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    DeployPropaganda deployPropaganda = (DeployPropaganda) tablePropaganda.getSelectionModel().getSelectedItem();

                    this.selectedDeployPropaganda = deployPropaganda;

                    ComboHelper tipoTransicao = this.tiposTransicao.stream().filter(el -> el.getCodigo() == deployPropaganda.getIdTipoTransicao()).findFirst().get();
                    ComboHelper tipoMidia = this.tiposMidia.stream().filter(el -> el.getCodigo() == deployPropaganda.getIdTipoMidia()).findFirst().get();

                    this.txtCliente.setObjectSelecionado(deployPropaganda.getClientePropaganda().getCliente());
                    this.txtCliente.setText(deployPropaganda.getClientePropaganda().getCliente().getNome());

                    this.txtClientePropaganda.setObjectSelecionado(deployPropaganda.getClientePropaganda());
                    this.txtClientePropaganda.setText(deployPropaganda.getClientePropaganda().getPropaganda().getDescricao());

                    this.cbTipoTransicao.getSelectionModel().select(tipoTransicao);
                    this.cbTipoMidia.getSelectionModel().select(tipoMidia);
                    this.txtDuracaoPropaganda.setText(deployPropaganda.getDuracaoPropaganda().toString());
                    this.txtDuracaoTransicao.setText(deployPropaganda.getDuracaoTransicao().toString());

                    this.txtOrdem.setText(deployPropaganda.getOrdem().toString());

                    this.btAdicionarPropaganda.setText("Alterar");
                    this.btCancelar.setVisible(true);
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(ClienteFormController.class
                    .getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(CidadeFormController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeEstado(ActionEvent event) {
        txtCidade.clean();
        txtEstabelecimento.clean();
    }

    private void processaDatatablePropagandas() {

        TableColumn<DeployPropaganda, String> ordemColumn = TableColumnUtil.createStringColumn("Ordem", 80, (DeployPropaganda s)
                -> s.getOrdem().toString());

        TableColumn<DeployPropaganda, String> clienteColumn = TableColumnUtil.createStringColumn("Cliente", 200, (DeployPropaganda s)
                -> s.getClientePropaganda().getCliente().getNome());

        TableColumn<DeployPropaganda, String> descricaoColumn = TableColumnUtil.createStringColumn("Descrição", 200, (DeployPropaganda s) -> s.getClientePropaganda().getPropaganda().getDescricao());

        TableColumn<DeployPropaganda, String> tipoTransicaoColumn = TableColumnUtil.createStringColumn("Tipo Transicao", 90, (DeployPropaganda s)
                -> TipoTransicao.get(s.getIdTipoTransicao()).getDescription());

        TableColumn<DeployPropaganda, String> tipoMidiaColumn = TableColumnUtil.createStringColumn("Tipo Midia", 80, (DeployPropaganda s)
                -> TipoMidia.get(s.getIdTipoMidia()).getDescription());

        TableColumn<DeployPropaganda, String> tempoPropagandaColumn = TableColumnUtil.createStringColumn("Tempo Propagnda", 115, (DeployPropaganda s)
                -> s.getDuracaoPropaganda().toString());

        TableColumn<DeployPropaganda, String> tempoTransicaoColumn = TableColumnUtil.createStringColumn("Tempo Transição", 115, (DeployPropaganda s)
                -> s.getDuracaoTransicao().toString());

        ActionColumnButton<DeployPropaganda> acDelete = new ActionColumnButton<>("Delete");
        acDelete.setDisabled(this.disabled);
        acDelete.setAction((IActionColumn<DeployPropaganda>) (DeployPropaganda t) -> {
            deployPropagandas.remove(t);
            processaDatatablePropagandas();

            inicializaDadosPropaganda();

            SituacaoDeploy situacaoDeploy = SituacaoDeploy.get(this.deploy.getSituacao());
            validaDisableEstabelecimento(situacaoDeploy);

        });

        List<ActionColumnButton<DeployPropaganda>> actionsColumnButton = new ArrayList<>(1);
        actionsColumnButton.add(acDelete);

        TableColumn<DeployPropaganda, DeployPropaganda> actionColumn = TableColumnUtil.createButtonColumn("Ação", 80, tablePropaganda, actionsColumnButton);

        tablePropaganda.getColumns().clear();
        tablePropaganda.getColumns().setAll(ordemColumn, clienteColumn, descricaoColumn, tipoTransicaoColumn, tipoMidiaColumn, tempoPropagandaColumn, tempoTransicaoColumn, actionColumn);
        tablePropaganda.setItems(FXCollections.observableArrayList(this.deployPropagandas));
    }

    public void adicionarPropaganda(ActionEvent event) {
        try {

            DeployPropaganda dp = new DeployPropaganda();
            dp.setDeploy(this.deploy);
            dp.setClientePropaganda(this.txtClientePropaganda.getObjectSelecionado());

            if (!"".equals(this.txtDuracaoPropaganda.getText())) {
                dp.setDuracaoPropaganda(Integer.parseInt(this.txtDuracaoPropaganda.getText()));
            } else {
                dp.setDuracaoPropaganda(null);
            }

            if (!"".equals(this.txtDuracaoTransicao.getText())) {
                dp.setDuracaoTransicao(Integer.parseInt(this.txtDuracaoTransicao.getText()));
            } else {
                dp.setDuracaoTransicao(null);
            }

            if (!"".equals(txtOrdem.getText())) {
                dp.setOrdem(Integer.parseInt(txtOrdem.getText()));
            } else {
                dp.setOrdem(null);
            }

            dp.setIdTipoMidia(cbTipoMidia.getSelectionModel().getSelectedItem().getCodigo());
            dp.setIdTipoTransicao(cbTipoTransicao.getSelectionModel().getSelectedItem().getCodigo());

            List<String> messages = new ArrayList<>();

            if (dp.getClientePropaganda() == null) {
                messages.add("Propaganda Inválida");
            }

            if (dp.getDuracaoPropaganda() == null || dp.getDuracaoPropaganda() <= 0) {
                messages.add("Duração Propaganda Inválida");
            }

            if (dp.getDuracaoTransicao() == null || dp.getDuracaoTransicao() <= 0) {
                messages.add("Duração Transição Inválida");
            }

            if (dp.getOrdem() == null || dp.getOrdem() <= 0) {
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

                this.txtCliente.clean();
                this.txtClientePropaganda.clean();
                this.txtDuracaoPropaganda.clear();
                this.txtDuracaoTransicao.clear();
                this.cbTipoMidia.getSelectionModel().clearSelection();
                this.cbTipoTransicao.getSelectionModel().clearSelection();
                this.txtOrdem.clear();

                this.selectedDeployPropaganda = null;

                this.btAdicionarPropaganda.setText("Adicionar");
                this.btCancelar.setVisible(false);

                inicializaDadosPropaganda();

                SituacaoDeploy situacaoDeploy = SituacaoDeploy.get(this.deploy.getSituacao());
                validaDisableEstabelecimento(situacaoDeploy);

            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(ClienteFormController.class
                    .getName()).log(Level.SEVERE, null, ex);

            FxmlUtil.getInstance().openMessageDialog(event, ex);
        }
    }

}
