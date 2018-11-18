package br.com.loto.client.tv;

import br.com.loto.shared.DeployDTO;
import br.com.loto.shared.DeployPropagandaDTO;
import br.com.loto.shared.ResultadoLoteriaDTO;
import br.com.loto.shared.ResultadoLoteriaTransferDTO;
import br.com.loto.shared.domain.type.TipoLoteria;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import static javafx.scene.paint.Color.color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Screen;
import javafx.util.Duration;

public class FXMLController implements Initializable {

    private static final Logger LOG = Logger.getLogger(FXMLController.class.getName());

    @FXML
    public Label lbData;

    @FXML
    public Label lbHora;

//    @FXML
//    public StackPane panelLoterias;
    @FXML
    public StackPane root;

    private String lastUuidCheck;

    String baseDirectory;
    String propagandaFile;
    String resultadosFile;
    Map<String, LocalTime> map = new HashMap<>();
    
    
    AnchorPane rootPaneDynamic;
    SequentialTransition sequentialTransitionDynamic; 
    

    public void init(Properties properties) {
        this.baseDirectory = properties.getProperty("base.directory");
        this.propagandaFile = properties.getProperty("propagandas.file");
        this.resultadosFile = properties.getProperty("resultados.file");
        start();
    }

    void reloadSlideShow(SequentialTransition slideshow) {
        try {

            slideshow.getChildren().forEach((animation) -> {

                if (animation instanceof SequentialTransition) {

                    javafx.animation.FadeTransition ft = (javafx.animation.FadeTransition) ((SequentialTransition) animation).getChildren().get(0);
                    Node n = ft.getNode();

                    if (n instanceof MediaView) {
                        ((MediaView) n).getMediaPlayer().stop();
                    }
                }
            });

            
            createSlideShows(slideshow);
            slideshow.play();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public void start() {

        SequentialTransition slideshow = new SequentialTransition();
        slideshow.setOnFinished((ActionEvent event) -> {
            reloadSlideShow(slideshow);
        });

        try {
            tryToLoadResultadosFile();

            createSlideShows(slideshow);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        slideshow.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfH = new SimpleDateFormat("HH:mm:ss");
        lbData.setText(sdf.format(new Date()));

        Runnable run = () -> {
            try {
                while (true) {
                    Platform.runLater(() -> {
                        lbHora.setText(sdfH.format(new Date()));
                    });

                    Thread.sleep(1000);
                }

            } catch (InterruptedException e) {
                System.out.println(" interrupted");
            }
        };

        new Thread(run).start();

    }

    DeployDTO tryToLoadPropagandasFile() throws IOException {
        File f = null;
        boolean fileExists = false;
        while (!fileExists) {
            LOG.log(Level.INFO, "Tentando ler arquivo de propagandas");
            f = new File(propagandaFile);
            if (f.exists()) {
                LOG.log(Level.INFO, "Arquivo de propagandas existente");
                fileExists = true;
            } else {
                LOG.log(Level.INFO, "Arquivo de propagandas nao encontrado");
                try {
                    Thread.sleep(5 * 1000l);
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }

        DeployDTO dTO = null;
        if (f != null && f.exists()) {
            dTO = loadPropagandaFromJson(f);
        }

        return dTO;
    }

    ResultadoLoteriaTransferDTO tryToLoadResultadosFile() throws IOException {

        File f = new File(resultadosFile);
        if (f.exists()) {
            LOG.log(Level.INFO, "Arquivo de resultados existente");

            ResultadoLoteriaTransferDTO dto;

            try {
                dto = loadResultadosFromJson(f);
                return dto;
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        
        return null;
    }

    void clearSlidesFromSlideShow(SequentialTransition slideshow) {
        List<Node> nodesToRemove = new ArrayList<>();
        this.root.getChildren().stream().filter((node) -> (node instanceof ImageView || node instanceof MediaView)).forEachOrdered((node) -> {
            nodesToRemove.add(node);
        });

        nodesToRemove.forEach((node) -> {
            if (node instanceof MediaView) {
                ((MediaView) node).getMediaPlayer().dispose();
            }
        });

        this.root.getChildren().removeAll(nodesToRemove);
        slideshow.getChildren().clear();

        nodesToRemove.clear();
    }

    Node loadResourceFromStream(DeployPropagandaDTO dp) {
        Node node = null;
        String conteudo = dp.getConteudo();
        byte[] arquivoBytes = Base64.getDecoder().decode(conteudo);

        try (InputStream in = new ByteArrayInputStream(arquivoBytes)) {

            if (dp.getTipoMidia() == 1) {
                ImageView imageView = new ImageView(new Image(in));
                imageView.setOpacity(0);

                imageView.setFitWidth(Screen.getPrimary().getBounds().getWidth());
                imageView.setFitHeight(Screen.getPrimary().getBounds().getHeight() - 100);

                //imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);

                node = imageView;
            } else {
                try {
                    String fName = baseDirectory + File.separator + "midia" + File.separator
                            + dp.getOrdem().toString() + "_" + dp.getNomeArquivo();
                    File fVideo = new File(fName);
                    if (!fVideo.exists()) {
                        try {
                            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fVideo))) {
                                outputStream.write(arquivoBytes);
                                outputStream.flush();
                            }

                        } catch (FileNotFoundException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }

                    MediaPlayer player = new MediaPlayer(new javafx.scene.media.Media(fVideo.toURI().toURL().toExternalForm()));
                    MediaView mediaView = new MediaView(player);
                    mediaView.setOpacity(0);

                    mediaView.setFitWidth(Screen.getPrimary().getBounds().getWidth());
                    mediaView.setFitHeight(Screen.getPrimary().getBounds().getHeight() - 100);
                    mediaView.setSmooth(false);
                    mediaView.setPreserveRatio(false);
                    mediaView.setCache(false);

                    player.stop();

                    player.statusProperty().addListener((observable, oldValue, newValue) -> {
                        LOG.log(Level.INFO, "oldValue {0}", oldValue);
                        LOG.log(Level.INFO, "newValue {0}", newValue);

                    });

                    //player.setAutoPlay(true);
                    node = mediaView;
                } catch (MalformedURLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return node;
    }

    DeployDTO loadPropagandaFromJson(File f) throws FileNotFoundException, IOException {
        StringBuilder builder;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            builder = new StringBuilder();
            String linha;
            while ((linha = reader.readLine()) != null) {
                builder.append(linha);
            }
        }

        Gson gson = new GsonBuilder().create();
        DeployDTO dTO = gson.fromJson(builder.toString(), DeployDTO.class);

        builder = null;
        gson = null;

        return dTO;
    }

    ResultadoLoteriaTransferDTO loadResultadosFromJson(File f) throws FileNotFoundException, IOException {
        StringBuilder builder;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            builder = new StringBuilder();
            String linha;
            while ((linha = reader.readLine()) != null) {
                builder.append(linha);
            }
        }

        Gson gson = new GsonBuilder().create();
        ResultadoLoteriaTransferDTO r = gson.fromJson(builder.toString(), ResultadoLoteriaTransferDTO.class);

        builder = null;
        gson = null;

        return r;
    }

    SequentialTransition createSlideShowTransaction(DeployPropagandaDTO dp, Node node) {
        BigDecimal db = new BigDecimal(dp.getDuracaoTransicao());
        db = db.divide(new BigDecimal(2), 0, RoundingMode.HALF_UP);

        SequentialTransition sequentialTransition = new SequentialTransition();
        FadeTransition fadeIn = getFadeTransition(node, 0.0, 1.0, db.intValue());
        PauseTransition stayOn = new PauseTransition(Duration.millis(dp.getDuracaoPropaganda()));
        FadeTransition fadeOut = getFadeTransition(node, 1.0, 0.0, db.intValue());

        sequentialTransition.getChildren().addAll(fadeIn, stayOn, fadeOut);

        sequentialTransition.statusProperty().addListener((ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) -> {
            ReadOnlyObjectProperty r = (ReadOnlyObjectProperty) observable;
            SequentialTransition st = (SequentialTransition) r.getBean();

            javafx.animation.FadeTransition ft = (javafx.animation.FadeTransition) st.getChildren().get(0);
            Node n = ft.getNode();

            if (n instanceof MediaView) {
                if (newValue.equals(Animation.Status.RUNNING)) {
                    LOG.log(Level.INFO, "Play midia player");

                    ((MediaView) n).getMediaPlayer().stop();
                    ((MediaView) n).getMediaPlayer().play();
                }
            }
        });

        return sequentialTransition;
    }

    void createDynamicSlideShow(SequentialTransition slideshow) throws IOException {
        ResultadoLoteriaTransferDTO dto = tryToLoadResultadosFile();
        
        if (dto != null && dto.getResultados() != null) {
            
            if (rootPaneDynamic != null){
                this.root.getChildren().remove(rootPaneDynamic);
            }
            
            if (sequentialTransitionDynamic != null){
                slideshow.getChildren().remove(sequentialTransitionDynamic);
            }
            
            //create dynamic information
            rootPaneDynamic = new AnchorPane();
            rootPaneDynamic.setStyle("-fx-background-color: #FFF");
            BorderPane borderPane = new BorderPane();
            AnchorPane.setTopAnchor(borderPane, 0.0);
            AnchorPane.setBottomAnchor(borderPane, 0.0);
            AnchorPane.setLeftAnchor(borderPane, 0.0);
            AnchorPane.setRightAnchor(borderPane, 0.0);

            Label lbLoterias = new Label("Confira os Últimos Resultados");
            lbLoterias.setFont(new Font("System", 75));

            StackPane topPane = new StackPane();
            topPane.setPrefHeight(100);
            topPane.getChildren().add(lbLoterias);
            borderPane.setTop(topPane);

            VBox centerPane = new VBox();

            for (ResultadoLoteriaDTO r : dto.getResultados()) {
                BorderPane panelJogo = new BorderPane();

                VBox.setMargin(panelJogo, new Insets(0, 0, 20, 0));

                TipoLoteria tl = TipoLoteria.get(r.getIdTipoLoteria());

                Label lbNome = new Label(tl.getDescription() + "(" + r.getConcurso().toString() + ")");
                lbNome.setFont(new Font("System", 50));

                switch (tl) {
                    case MEGA_SENA:
                        lbNome.setStyle("-fx-text-fill:  #209869");
                        break;
                    case LOTO_FACIL:
                        lbNome.setStyle("-fx-text-fill:  #930089");
                        break;
                    case QUINA:
                        lbNome.setStyle("-fx-text-fill:  #260085");
                        break;
                    case LOTO_MANIA:
                        lbNome.setStyle("-fx-text-fill:  #f78100");
                        break;
                    case TIME_MANIA:
                        lbNome.setStyle("-fx-text-fill: #00ff48");
                        break;
                    case DIA_DE_SORTE:
                        lbNome.setStyle("-fx-text-fill: #cb852b");
                        break;
                    default:
                        break;
                }

                StackPane pLabelJogo = new StackPane();
                pLabelJogo.setPrefWidth(450);
                pLabelJogo.setAlignment(Pos.CENTER_LEFT);
                pLabelJogo.getChildren().add(lbNome);

                panelJogo.setLeft(pLabelJogo);

                VBox paneLinhaNumeros = new VBox();
                HBox paneNumeros = new HBox();

                paneLinhaNumeros.getChildren().add(paneNumeros);
                int count = 1;

                for (Integer num : r.getNumeros()) {
                    Circle circle = new Circle(39, 39, 44, color(1, 0, 0));

                    switch (tl) {
                        case MEGA_SENA:
                            circle.setStyle("-fx-fill: #209869");
                            break;
                        case LOTO_FACIL:
                            circle.setStyle("-fx-fill: #930089");
                            break;
                        case QUINA:
                            circle.setStyle("-fx-fill: #260085");
                            break;
                        case LOTO_MANIA:
                            circle.setStyle("-fx-fill: #f78100");
                            break;
                        case TIME_MANIA:
                            circle.setStyle("-fx-fill: #00ff48");
                            break;
                        case DIA_DE_SORTE:
                            circle.setStyle("-fx-fill: #cb852b");
                            break;
                        default:
                            break;
                    }

                    //Label lbNumero = new Label(num.toString());
                    //lbNumero.setFont(new Font("System", 30));
                    Text text = new Text(num.toString());
                    text.setFont(new Font("System", 55));
                    text.setBoundsType(TextBoundsType.VISUAL);
                    text.setStyle("-fx-fill: #FFF");

                    StackPane stack = new StackPane();
                    stack.getChildren().addAll(circle, text);

                    HBox.setMargin(stack, new Insets(0, 15, 7, 0));

                    paneNumeros.getChildren().add(stack);
                    //paneNumeros.getChildren().add(circle);

                    if (count > 0 && count % 10 == 0) {
                        paneNumeros = new HBox();
                        paneLinhaNumeros.getChildren().add(paneNumeros);
                        count = 0;
                    }

                    count++;
                }

                VBox.setMargin(paneLinhaNumeros, new Insets(0, 0, 50, 0));
                panelJogo.setCenter(paneLinhaNumeros);

                centerPane.getChildren().add(panelJogo);

                // break;
            }

            borderPane.setCenter(centerPane);

            rootPaneDynamic.getChildren().add(borderPane);
            rootPaneDynamic.setOpacity(0);

            
            sequentialTransitionDynamic = new SequentialTransition();
            FadeTransition fadeIn = getFadeTransition(rootPaneDynamic, 0.0, 1.0, 500);
            PauseTransition stayOn = new PauseTransition(Duration.millis(15_000));
            FadeTransition fadeOut = getFadeTransition(rootPaneDynamic, 1.0, 0.0, 500);

            sequentialTransitionDynamic.getChildren().addAll(fadeIn, stayOn, fadeOut);

            this.root.getChildren().add(rootPaneDynamic);
            slideshow.getChildren().add(0, sequentialTransitionDynamic);
        }
    }

    void createSlideShows(SequentialTransition slideshow) throws FileNotFoundException, IOException {
        
        createDynamicSlideShow(slideshow);
        
        DeployDTO dTO = tryToLoadPropagandasFile();
        boolean importar = false;
        if (lastUuidCheck == null || lastUuidCheck.isEmpty()) {
            importar = true;
            lastUuidCheck = dTO.getUuidDeploy();
        } else {
            if (!lastUuidCheck.equals(dTO.getUuidDeploy())) {
                importar = true;
                lastUuidCheck = dTO.getUuidDeploy();
            }
        }

        if (!importar) {
            return;
        }

        LOG.log(Level.INFO, "lastUuidCheck : {0}", lastUuidCheck);
        LOG.log(Level.INFO, "dTO.getUuidDeploy() : {0}", dTO.getUuidDeploy());
        LOG.log(Level.INFO, "Iniciando a troca de propagandas...");

        slideshow.stop();
        clearSlidesFromSlideShow(slideshow);

        
        createStaticSlideShow(slideshow, dTO);

        dTO = null;

        LOG.log(Level.INFO, "Troca de propagandas finalizada");

    }

    void createStaticSlideShow(SequentialTransition slideshow, DeployDTO dTO) throws IOException {
        dTO.getPropagandas().forEach((dp) -> {

            Node node = loadResourceFromStream(dp);

            if (node != null) {
                SequentialTransition sequentialTransition = createSlideShowTransaction(dp, node);

                this.root.getChildren().add(node);
                slideshow.getChildren().add(sequentialTransition);
            }
        });
    }
    // the method in the Transition helper class:

    public FadeTransition getFadeTransition(Node node, double fromValue, double toValue, int durationInMilliseconds) {

        FadeTransition ft = new FadeTransition(Duration.millis(durationInMilliseconds), node);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);

        return ft;
    }

}
