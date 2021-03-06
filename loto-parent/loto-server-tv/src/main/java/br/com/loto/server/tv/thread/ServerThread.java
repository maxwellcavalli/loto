/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.server.tv.thread;

import br.com.loto.crypto.AdvancedEncryptionStandard;
import br.com.loto.server.tv.business.service.DeployService;
import br.com.loto.server.tv.business.service.EquipamentoService;
import br.com.loto.server.tv.business.service.ResultadoLoteriaService;
import br.com.loto.shared.ComandoDTO;
import br.com.loto.shared.DeployDTO;
import br.com.loto.shared.DeployPropagandaDTO;
import br.com.loto.shared.ResultadoLoteriaTransferDTO;
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
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    Properties properties;

    byte[] password;

    public ServerThread(Socket csocket, byte[] password, Properties properties) {
        this.properties = properties;
        this.csocket = csocket;
        this.password = password;
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
                                verifyResultados(comandoDTO);
                            }
                        } catch (SQLException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.INFO, "Conexao Fechada");
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

    void verify(ComandoDTO comandoDTO) throws SQLException, Exception {
        if (comandoDTO.getComando().equals("verify")) {
            Gson gson = new GsonBuilder().create();

            List<String> uuidsInClient = Collections.emptyList();
            String dataReceived = comandoDTO.getData();
            if (dataReceived  != null && !"".equals(dataReceived)) {
                byte[] bytes = Base64.getDecoder().decode(dataReceived);
                byte[] decripted = AdvancedEncryptionStandard.decrypt(password, bytes);
                DeployDTO dTO = gson.fromJson(new String(decripted), DeployDTO.class);

                List<DeployPropagandaDTO> propagandas = dTO.getPropagandas();
                propagandas = propagandas == null ? Collections.emptyList() : propagandas;
                uuidsInClient = propagandas.stream().map(dp -> dp.getUuidPropaganda()).collect(Collectors.toList());
            }

            DeployDTO deployDTO = DeployService.getInstance().loadDeployByUuid(comandoDTO.getUniqueId(), uuidsInClient);

            ComandoDTO c = new ComandoDTO();
            c.setUniqueId(comandoDTO.getUniqueId());

            if (deployDTO != null) {

                String data = gson.toJson(deployDTO);
                data = criptografar(data);

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

    String criptografar(String data) throws Exception {

        byte[] dataBytes = data.getBytes();

        final byte[] textoCriptografado = AdvancedEncryptionStandard.encrypt(password, dataBytes);
        String ret = new String(Base64.getEncoder().encode(textoCriptografado));

        return ret;

    }

    void lastDeploy(ComandoDTO comandoDTO) throws SQLException, Exception {
        if (comandoDTO.getComando().equals("last-deploy")) {
            Gson gson = new GsonBuilder().create();

            DeployDTO deployDTO = DeployService.getInstance().loadLastDeployByUuid(comandoDTO.getUniqueId());

            ComandoDTO c = new ComandoDTO();
            c.setUniqueId(comandoDTO.getUniqueId());

            if (deployDTO != null) {

                String data = gson.toJson(deployDTO);
                data = criptografar(data);
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

    void verifyResultados(ComandoDTO comandoDTO) throws SQLException, IOException, Exception {
        if (comandoDTO.getComando().equals("verify-resultados")) {
            Gson gson = new GsonBuilder().create();

            ResultadoLoteriaTransferDTO dto = ResultadoLoteriaService.getInstance().loadLastResults();
            ComandoDTO c = new ComandoDTO();
            c.setUniqueId(comandoDTO.getUniqueId());
            if (dto != null) {
                c.setComando("has-data");

                String data = gson.toJson(dto);
                data = criptografar(data);

                c.setData(data);

            } else {
                c.setComando("no-data");
            }

            String json = gson.toJson(c);

            out.println(json);
            out.flush();
        }
    }

}
