package br.com.loto.client.tv;

import static br.com.loto.client.tv.MainApp.jsonFile;
import br.com.loto.shared.DeployDTO;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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

    private String baseDirectory;

    public void init(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        start();
    }

    public void start() {

        SequentialTransition slideshow = new SequentialTransition();
        slideshow.setOnFinished((ActionEvent event) -> {
            try {
                System.out.println("play...");

                slideshow.getChildren().forEach((animation) -> {

                    if (animation instanceof SequentialTransition) {

                        javafx.animation.FadeTransition ft = (javafx.animation.FadeTransition) ((SequentialTransition) animation).getChildren().get(0);
                        Node n = ft.getNode();

                        if (n instanceof MediaView) {
                            ((MediaView) n).getMediaPlayer().stop();
                        }
                    }
                });

                loadFromJson(slideshow);
                slideshow.play();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });

        try {
            loadFromJson(slideshow);
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

    void loadFromJson(SequentialTransition slideshow) throws FileNotFoundException, IOException {
        boolean fileExists = false;
        File f = null;

        while (!fileExists) {
            LOG.log(Level.INFO, "Tentando ler arquivo de propagandas");
            f = new File(jsonFile);
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

        if (f.exists()) {
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
            LOG.log(Level.INFO, "importar : {0}", importar);

            LOG.log(Level.INFO, "Iniciando a troca de propagandas...");

            List<Node> nodesToRemove = new ArrayList<>();
            this.root.getChildren().stream().filter((node) -> (node instanceof ImageView || node instanceof MediaView)).forEachOrdered((node) -> {
                nodesToRemove.add(node);
            });

            slideshow.stop();

            nodesToRemove.forEach((node) -> {
                if (node instanceof MediaView) {
                    ((MediaView) node).getMediaPlayer().dispose();
                }
            });

            this.root.getChildren().removeAll(nodesToRemove);
            slideshow.getChildren().clear();

            nodesToRemove.clear();

            dTO.getPropagandas().forEach((dp) -> {
                String conteudo = dp.getConteudo();
                byte[] arquivoBytes = Base64.getDecoder().decode(conteudo);

                Node node = null;
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
                
               

                if (node != null) {

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

                    this.root.getChildren().add(node);
                    slideshow.getChildren().add(sequentialTransition);
                }
            });

             dTO = null;
            LOG.log(Level.INFO, "Troca de propagandas finalizada");
        }
    }

    // the method in the Transition helper class:
    public FadeTransition getFadeTransition(Node node, double fromValue, double toValue, int durationInMilliseconds) {

        FadeTransition ft = new FadeTransition(Duration.millis(durationInMilliseconds), node);
        ft.setFromValue(fromValue);
        ft.setToValue(toValue);

        return ft;
    }

}
