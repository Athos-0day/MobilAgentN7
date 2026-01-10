package Serveur;
import Common.*;
import java.io.*;
import java.net.*;

public class AgentServer {
    private int port;
    private Object service;

    public AgentServer(int port, Object service) {
        this.port = port;
        this.service = service;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Affiche l'IP locale pour aider au debug
            String localIp = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Plateforme d'Agent prête sur " + localIp + ":" + port);
            
            while (true) {
                Socket socket = serverSocket.accept();
                // Information sur la provenance de l'agent
                String remoteIp = socket.getInetAddress().getHostAddress();
                new Thread(() -> handleAgent(socket, remoteIp)).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void handleAgent(Socket socket, String remoteIp) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            // 1. Lecture Protocole
            String className = dis.readUTF();
            int codeLen = dis.readInt();
            byte[] bytecode = new byte[codeLen];
            dis.readFully(bytecode);

            int agentLen = dis.readInt();
            byte[] agentBytes = new byte[agentLen];
            dis.readFully(agentBytes);

            // 2. Reconstruction dynamique
            Loader loader = new Loader();
            loader.addClass(className, bytecode);

            ByteArrayInputStream bis = new ByteArrayInputStream(agentBytes);
            AgentInputStream ais = new AgentInputStream(bis, loader);
            Agent agent = (Agent) ais.readObject();

            // 3. Exécution avec log de provenance
            System.out.println("\n>>> Agent [" + className + "] reçu de " + remoteIp);
            agent.setService(this.service);
            agent.execute();

        } catch (Exception e) {
            System.err.println("Erreur lors de la réception de l'agent : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Serveur.AgentServer <PORT>");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            
            // On crée le service de base de données en local.
            // L'agent l'utilisera directement une fois sur cette machine.
            DatabaseServiceRem db = new DatabaseServiceImpl(); 
            
            System.out.println("=== Démarrage du serveur d'accueil ===");
            new AgentServer(port, db).start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}