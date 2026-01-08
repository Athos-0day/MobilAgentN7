package Serveur;
import Common.*;
import java.io.*;
import java.net.*;
import java.rmi.RemoteException;

public class AgentServer {
    private int port;
    private Object service;

    public AgentServer(int port, Object service) {
        this.port = port;
        this.service = service;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur d'Agent en écoute sur le port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                // On traite chaque agent dans un thread pour ne pas bloquer le serveur
                new Thread(() -> handleAgent(socket)).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void handleAgent(Socket socket) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            
            // --- PROTOCOLE ROBUSTE : LECTURE SÉQUENTIELLE ---
            
            // 1. Lire le nom de la classe
            String className = dis.readUTF();

            // 2. Lire le Bytecode
            int codeLen = dis.readInt();
            byte[] bytecode = new byte[codeLen];
            dis.readFully(bytecode);

            // 3. Lire l'objet sérialisé (blob d'octets)
            int agentLen = dis.readInt();
            byte[] agentBytes = new byte[agentLen];
            dis.readFully(agentBytes);

            // --- RECONSTRUCTION ---
            Loader loader = new Loader();
            loader.addClass(className, bytecode);

            // On désérialise le blob en mémoire, isolé du réseau
            ByteArrayInputStream bis = new ByteArrayInputStream(agentBytes);
            AgentInputStream ais = new AgentInputStream(bis, loader);
            Agent agent = (Agent) ais.readObject();

            // --- EXÉCUTION ---
            System.out.println("\n--- Agent " + className + " arrivé ---");
            agent.setService(this.service);
            agent.execute();

        } catch (Exception e) {
            e.printStackTrace();
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
            // On instancie la base de données (même si le serveur n'est pas forcément celui des restos)
            // Dans un vrai cas, on injecterait le bon service selon le serveur.
            DatabaseServiceRem db = new DatabaseServiceImpl();
            new AgentServer(port, db).start();
        } catch (Exception e) { e.printStackTrace(); }
    }
}