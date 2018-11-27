/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.client.tv.thread;

import br.com.loto.crypto.AdvancedEncryptionStandard;
import br.com.loto.shared.ComandoDTO;
import br.com.loto.shared.DeployDTO;
import br.com.loto.shared.DeployPropagandaDTO;
import br.com.loto.shared.domain.type.AcaoDeploy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwe
 */
public class ClientThread extends Thread {

    private static final Logger LOG = Logger.getLogger(ClientThread.class.getName());

    InetAddress address;
    Socket s1 = null;
    String line = null;
    //BufferedReader br = null;
    BufferedReader is = null;
    PrintWriter os = null;

    String localUUID;
    byte[] password;

    Map<String, LocalTime> map = new HashMap<>();

    Properties properties;

    boolean connected;
    boolean stopServer = false;

    public ClientThread(Properties properties, byte[] password, InetAddress address) {
        this.address = address;
        this.password = password;

        this.properties = properties;
        this.localUUID = properties.getProperty("uuid");
    }

    void tryToConnect() {
        while (true) {
            if (stopServer) {
                break;
            }

            try {
                LOG.log(Level.INFO, "Trying to connect: {0}", address);

                s1 = new Socket(address, Integer.parseInt(properties.getProperty("client.port")));
                os = new PrintWriter(s1.getOutputStream());
                is = new BufferedReader(new InputStreamReader(s1.getInputStream()));

                connected = true;

                LOG.log(Level.INFO, "Client Connected: {0}", address);

                break;
            } catch (IOException e) {
                s1 = null;
                os = null;
                is = null;

                LOG.log(Level.SEVERE, "Connection error: {0}", address);
                connected = false;

                try {
                    Thread.sleep(Long.parseLong(properties.getProperty("time.to.try.socket.connection")));
                } catch (InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void run() {

        LOG.log(Level.INFO, "Client Address: {0}", address);

        tryToConnect();

        if (connected) {

            while (true) {
                if (stopServer) {
                    break;
                }

                try {
                    if (s1.isClosed()) {
                        tryToConnect();
                    }

                    verify();
                    forceToLoadLastDeploy();
                    verifyResultados();

                    Thread.sleep(Long.parseLong(properties.getProperty("time.to.run.main.sync.thread")));
                } catch (InterruptedException | IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);

                    if (ex instanceof IOException) {
                        try {
                            s1.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, null, ex1);
                        }
                    }
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    void verify() throws IOException {

        String cmd = "verify";

        boolean run = false;
        if (map.containsKey(cmd)) {
            LocalTime anterior = map.get(cmd);
            LocalTime agora = LocalTime.now();

            long minutos = ChronoUnit.MINUTES.between(anterior, agora);
            //long segundos = ChronoUnit.SECONDS.between(anterior, agora);
            long timeToRunVerify = Long.parseLong(properties.getProperty("time.to.run.verify"));

            if (minutos > timeToRunVerify) {
                run = true;

            }
        } else {
            run = true;
        }

        if (!run) {
            return;
        }

        LOG.log(Level.INFO, "Running verify...");
        map.put(cmd, LocalTime.now());

        String dataToSend = null;
        File fControl = new File(properties.getProperty("propagandas.control.file"));
        if (fControl.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(fControl.toPath());
                //byte[] encrypted = AdvancedEncryptionStandard.encrypt(password, bytes);

                dataToSend = new String(Base64.getEncoder().encode(bytes));

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }

        ComandoDTO comando = new ComandoDTO();
        comando.setUniqueId(localUUID);
        comando.setComando("verify");
        comando.setData(dataToSend);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(comando);

        os.println(json);
        os.flush();

        String response = is.readLine();

        ComandoDTO comandoDTO = gson.fromJson(response, ComandoDTO.class);

        if (comandoDTO != null) {
            if (comandoDTO.getComando().equals("has-data")) {
                LOG.log(Level.INFO, "Running verify has data");
                String data = comandoDTO.getData();
                try {
                    DeployDTO savedDTO = savePropagandaToFile(data);
                    updateDeploy(savedDTO.getUuidDeploy());
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, null, e);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            } else {
                LOG.log(Level.INFO, "Running verify has not data");
                forceToLoadLastDeploy();
            }
        }

        gson = null;
    }

    void forceToLoadLastDeploy() throws IOException {
        File f = new File(properties.getProperty("propagandas.file"));
        if (!f.exists()) {
            lastDeploy();
        }
    }

    void writeControlPropaganda(DeployDTO dTO) throws FileNotFoundException, IOException, Exception {
        if (dTO.getPropagandas() != null) {
            File fControl = new File(properties.getProperty("propagandas.control.file"));
            try {
                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fControl))) {
                    List<DeployPropagandaDTO> newPropagandas = new ArrayList<>();

                    dTO.getPropagandas().forEach(dp -> {
                        DeployPropagandaDTO newDp = new DeployPropagandaDTO();
                        newDp.setUuidPropaganda(dp.getUuidPropaganda());

                        newPropagandas.add(newDp);
                    });

                    DeployDTO newDeploy = new DeployDTO();
                    newDeploy.setPropagandas(newPropagandas);

                    Gson gson = new GsonBuilder().create();
                    String json = gson.toJson(newDeploy);

                    //byte[] bytes = Base64.getEncoder().encode(json.getBytes());
                    byte[] encrypted = AdvancedEncryptionStandard.encrypt(password, json.getBytes());
                    out.write(encrypted);
                    out.flush();
                }
            } catch (IOException e) {
                throw e;
            }

        }

    }

    void lastDeploy() throws IOException {

        LOG.log(Level.INFO, "Running last-deploy...");

        ComandoDTO comando = new ComandoDTO();
        comando.setUniqueId(localUUID);
        comando.setComando("last-deploy");

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(comando);

        os.println(json);
        os.flush();

        String response = is.readLine();

        ComandoDTO comandoDTO = gson.fromJson(response, ComandoDTO.class);

        if (comandoDTO != null) {
            if (comandoDTO.getComando().equals("has-data")) {
                LOG.log(Level.INFO, "Running last-deploy has data");
                String data = comandoDTO.getData();
                try {
                    DeployDTO savedDTO = savePropagandaToFile(data);
                    updateDeploy(savedDTO.getUuidDeploy());
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, null, e);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            } else {
                LOG.log(Level.INFO, "Running last-deploy has not data");

            }
        }

        gson = null;
    }

    DeployDTO savePropagandaToFile(String receivedData) throws IOException, Exception {
        Gson gson = new GsonBuilder().create();

        File f = new File(properties.getProperty("propagandas.file"));
        DeployDTO savedDTO = null;
        if (f.exists()) {
            byte[] savedBytes = Files.readAllBytes(f.toPath());

            byte[] decriptedSaved = AdvancedEncryptionStandard.decrypt(password, savedBytes);
            savedDTO = gson.fromJson(new String(decriptedSaved), DeployDTO.class);
        }

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f))) {

            if (savedDTO == null) {
                savedDTO = new DeployDTO();
            }

            byte[] bytes = Base64.getDecoder().decode(receivedData);
            byte[] decripted = AdvancedEncryptionStandard.decrypt(password, bytes);

            DeployDTO receivedDTO = gson.fromJson(new String(decripted), DeployDTO.class);
            savedDTO.setDataValidade(receivedDTO.getDataValidade());
            savedDTO.setId(receivedDTO.getId());
            savedDTO.setUuid(receivedDTO.getUuid());
            savedDTO.setUuidDeploy(receivedDTO.getUuidDeploy());
            
            for (DeployPropagandaDTO dp : receivedDTO.getPropagandas()) {
                AcaoDeploy acaoDeploy = AcaoDeploy.get(dp.getAcao());

                int index = -1;
                for (int i = 0; i < savedDTO.getPropagandas().size(); i++) {
                     DeployPropagandaDTO dpV = savedDTO.getPropagandas().get(i);
                    if (dpV.getUuidPropaganda().equals(dp.getUuidPropaganda())) {
                        index = i;
                        break;
                    }
                }

                switch (acaoDeploy) {
                    case EXCLUSAO:
                        if (index > -1) {
                            savedDTO.getPropagandas().remove(index);
                        }
                        break;
                    case INSERCAO:
                        savedDTO.getPropagandas().add(dp);
                        break;
                    case ALTERACAO:
                        if (index > -1) {
                            savedDTO.getPropagandas().set(index, dp);
                        }
                        break;
                    default:
                        break;
                }
            }

            String jsonToSave = gson.toJson(savedDTO);
            //byte[] encodedToSave = Base64.getEncoder().encode(jsonToSave.getBytes());

            byte[] encryptedToSave = AdvancedEncryptionStandard.encrypt(password, jsonToSave.getBytes());

            out.write(encryptedToSave);
            out.flush();

            LOG.log(Level.INFO, "Data saved");

            writeControlPropaganda(savedDTO);

            return savedDTO;
        }

    }

    void verifyResultados() throws IOException, Exception {

        String cmd = "verify-resultados";

        boolean run = false;
        if (map.containsKey(cmd)) {
            LocalTime anterior = map.get(cmd);
            LocalTime agora = LocalTime.now();

            long minutos = ChronoUnit.MINUTES.between(anterior, agora);
            long timeToRunVerifyResultados = Long.parseLong(properties.getProperty("time.to.run.verify.resultados"));

            if (minutos > timeToRunVerifyResultados) {
                run = true;

            }
        } else {
            run = true;
        }

        if (!run) {
            return;
        }

        map.put(cmd, LocalTime.now());
        LOG.log(Level.INFO, "Running verify-resultados...");

        ComandoDTO comando = new ComandoDTO();
        comando.setUniqueId(localUUID);
        comando.setComando("verify-resultados");

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(comando);

        os.println(json);
        os.flush();

        String response = is.readLine();

        ComandoDTO comandoDTO = gson.fromJson(response, ComandoDTO.class);

        if (comandoDTO != null) {
            if (comandoDTO.getComando().equals("has-data")) {
                LOG.log(Level.INFO, "Running verify-resultados has data");
                String data = comandoDTO.getData();
                byte[] bytes = Base64.getDecoder().decode(data);

                try {
                    File f = new File(properties.getProperty("resultados.file"));
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f))) {
                        out.write(bytes);
                        out.flush();

                        LOG.log(Level.INFO, "Data saved");
                    }
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            } else {
                LOG.log(Level.INFO, "Running verify-resultados has not data");
            }
        }

        gson = null;
    }

    void updateDeploy(String uuidDeploy) throws IOException {
        LOG.log(Level.INFO, "Running update deploy...");
        Gson gson = new GsonBuilder().create();

        DeployDTO d = new DeployDTO();
        d.setUuidDeploy(uuidDeploy);

        String data = gson.toJson(d);

        ComandoDTO comando = new ComandoDTO();
        comando.setUniqueId(localUUID);
        comando.setComando("update-deploy");
        comando.setData(data);

        String json = gson.toJson(comando);

        os.println(json);
        os.flush();

        String response = is.readLine();
        LOG.log(Level.INFO, "Updated {0}", response);

        gson = null;
    }

    public void setStopServer(boolean stopServer) {
        this.stopServer = stopServer;
    }

}
