/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.thread;

import br.com.loto.server.tv.business.service.DeployService;
import br.com.loto.server.tv.business.service.EquipamentoService;
import br.com.loto.shared.ComandoDTO;
import br.com.loto.shared.DeployDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwe
 */
public class ServerThread extends Thread {

    private static final Logger LOG = Logger.getLogger(ServerThread.class.getName());

    Socket csocket;
    boolean m_bRunThread = true;
    boolean ServerOn = true;

    BufferedReader in = null;
    PrintWriter out = null;

    public ServerThread(Socket csocket) {
        this.csocket = csocket;
    }

    @Override
    public void run() {

        LOG.log(Level.INFO, "Accepted Client Address - : {0}", csocket.getInetAddress());
        try {
            in = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(csocket.getOutputStream()));

            while (m_bRunThread) {

                String clientCommand = in.readLine();

                if (!ServerOn) {
                    System.out.print("Server has already stopped");
                    out.println("Server has already stopped");
                    out.flush();
                    m_bRunThread = false;
                } else {

                    LOG.log(Level.INFO, "Client Says : {0}", clientCommand);

                    Gson gson = new GsonBuilder().create();

                    ComandoDTO comandoDTO = null;
                    try {
                        comandoDTO = gson.fromJson(clientCommand, ComandoDTO.class);
                    } catch (JsonSyntaxException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }

                    if (comandoDTO != null) {
                        try {
                            boolean valid = EquipamentoService.getInstance().hasEquipamento(comandoDTO.getUniqueId());

                            if (!valid) {
                                out.println("Invalid UUID or Inactive ");
                                out.flush();
                                m_bRunThread = false;
                            } else {
                                verify(comandoDTO);
                                lastDeploy(comandoDTO);
                                updateDeploy(comandoDTO);
                            }
                        } catch (SQLException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        } finally {
            try {
                in.close();
                out.close();
                csocket.close();
                System.out.println("...Stopped");
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, null, ioe);
            }
        }

    }

    void verify(ComandoDTO comandoDTO) throws SQLException {
        if (comandoDTO.getComando().equals("verify")) {
            Gson gson = new GsonBuilder().create();

            DeployDTO deployDTO = DeployService.getInstance().loadDeployByUuid(comandoDTO.getUniqueId());

            ComandoDTO c = new ComandoDTO();
            c.setUniqueId(comandoDTO.getUniqueId());

            if (deployDTO != null) {

                String data = gson.toJson(deployDTO);
                c.setComando("has-data");
                c.setData(data);
            } else {
                c.setComando("no-data");
            }

            String json = gson.toJson(c);

            out.println(json);
            out.flush();
        }
    }
    
    void lastDeploy(ComandoDTO comandoDTO) throws SQLException {
        if (comandoDTO.getComando().equals("last-deploy")) {
            Gson gson = new GsonBuilder().create();

            DeployDTO deployDTO = DeployService.getInstance().loadLastDeployByUuid(comandoDTO.getUniqueId());

            ComandoDTO c = new ComandoDTO();
            c.setUniqueId(comandoDTO.getUniqueId());

            if (deployDTO != null) {

                String data = gson.toJson(deployDTO);
                c.setComando("has-data");
                c.setData(data);
            } else {
                c.setComando("no-data");
            }

            String json = gson.toJson(c);

            out.println(json);
            out.flush();
        }
    }
    

    void updateDeploy(ComandoDTO comandoDTO) throws SQLException {
        if (comandoDTO.getComando().equals("update-deploy")) {
            Gson gson = new GsonBuilder().create();

            String data = comandoDTO.getData();

            DeployDTO deployDTO = gson.fromJson(data, DeployDTO.class);

            DeployService.getInstance().updateDeploy(deployDTO.getUuidDeploy());

            ComandoDTO c = new ComandoDTO();
            c.setUniqueId(comandoDTO.getUniqueId());
            c.setComando("update");

            String json = gson.toJson(c);

            out.println(json);
            out.flush();
        }
    }

}
