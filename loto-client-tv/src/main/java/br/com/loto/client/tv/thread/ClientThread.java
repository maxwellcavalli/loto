/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.client.tv.thread;

import br.com.loto.shared.ComandoDTO;
import br.com.loto.shared.DeployDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
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
    String jsonFile;

    Map<String, LocalTime> map = new HashMap<>();

    boolean connected;

    boolean stopServer = false;

    public ClientThread(String localUUID, String jsonFile, InetAddress address) {
        this.localUUID = localUUID;
        this.address = address;
        this.jsonFile = jsonFile;
    }

    void tryToConnect() {
        while (true) {
            if (stopServer) {
                break;
            }

            try {
                LOG.log(Level.INFO, "Trying to connect: {0}", address);

                s1 = new Socket(address, 12033);
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

//                Logger.getLogger(ClientThread.class.getName()).log(Level.INFO, null, e);
                connected = false;

                try {
                    Thread.sleep(10 * 1000l);
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

                    Thread.sleep(1000l);
                } catch (InterruptedException | IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);

                    if (ex instanceof IOException) {
                        try {
                            s1.close();
                        } catch (IOException ex1) {
                            LOG.log(Level.SEVERE, null, ex1);
                        }
                    }
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

            //long minutos = ChronoUnit.MINUTES.between(anterior, agora);
            long segundos = ChronoUnit.SECONDS.between(anterior, agora);

            if (segundos > 10) {
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

        ComandoDTO comando = new ComandoDTO();
        comando.setUniqueId(localUUID);
        comando.setComando("verify");

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
                    File f = new File(jsonFile);
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f))) {
                        out.write(data.getBytes());
                        out.flush();

                        DeployDTO dTO = gson.fromJson(data, DeployDTO.class);

                        LOG.log(Level.INFO, "Data saved");

                        updateDeploy(dTO.getUuidDeploy());
                    }
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            } else {
                LOG.log(Level.INFO, "Running verify has not data");

                File f = new File(jsonFile);
                if (!f.exists()) {
                    lastDeploy();
                }
            }
        }

        gson = null;
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
                    File f = new File(jsonFile);
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f))) {
                        out.write(data.getBytes());
                        out.flush();

                        DeployDTO dTO = gson.fromJson(data, DeployDTO.class);

                        LOG.log(Level.INFO, "Data saved");

                        updateDeploy(dTO.getUuidDeploy());
                    }
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            } else {
                LOG.log(Level.INFO, "Running last-deploy has not data");

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
