package Serveur;
import Common.*;
import java.io.*;
import java.net.*;

/**
 * AgentServer fait office d'accueil de l'agent.
 * Il écoute sur un port, réceptionne le code et l'état de l'agent, 
 * puis lui fournit les ressources locales pour qu'il travaille.
 */
public class AgentServer {
    private int port;
    private Object service; // La ressource locale

    public AgentServer(int port, Object service) {
        this.port = port;
        this.service = service;
    }

    /**
     * Boucle principale d'écoute réseau.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            String localIp = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Plateforme d'Agent prête sur " + localIp + ":" + port);
            
            while (true) {
                // Attente bloquante d'une connexion (arrivée d'un agent)
                Socket socket = serverSocket.accept();
                String remoteIp = socket.getInetAddress().getHostAddress();
                
                // Chaque agent est traité dans un thread séparé
                // pour ne pas bloquer l'arrivée d'autres agents.
                new Thread(() -> handleAgent(socket, remoteIp)).start();
            }
        } catch (IOException e) { 
            System.err.println("Erreur fatale serveur : " + e.getMessage());
        }
    }

    /**
     * Méthode de désérialisation dynamique.
     * C'est ici que l'agent est reconstruit 
     */
    private void handleAgent(Socket socket, String remoteIp) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            
            // RÉCEPTION DES COMPOSANTS 
            // On lit dans l'ordre exact défini dans Agent.move()
            String className = dis.readUTF(); // Nom de la classe
            
            int codeLen = dis.readInt();
            byte[] bytecode = new byte[codeLen]; // Le code binaire (.class)
            dis.readFully(bytecode);

            int agentLen = dis.readInt();
            byte[] agentBytes = new byte[agentLen]; // L'état (les données)
            dis.readFully(agentBytes);

            // RECONSTRUCTION DE L'AGENT
            // On instancie un nouveau chargeur de classe pour cet agent précis
            Loader loader = new Loader();
            loader.addClass(className, bytecode);

            // On utilise notre InputStream spécialisé pour lier le flux au loader
            ByteArrayInputStream bis = new ByteArrayInputStream(agentBytes);
            AgentInputStream ais = new AgentInputStream(bis, loader);
            
            // L'agent reprend vie ici 
            Agent agent = (Agent) ais.readObject();

            // ACTIVATION 
            System.out.println("\n>>> Agent [" + className + "] reçu de " + remoteIp);
            
            agent.setService(this.service);
            
            // On lance l'exécution métier de l'agent
            agent.execute();

        } catch (Exception e) {
            System.err.println("Échec du traitement de l'agent : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    /**
     * Point d'entrée du programme Serveur.
     * @param args [0] : Le port d'écoute du serveur.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Serveur.AgentServer <PORT>");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);
            
            // On instancie l'implémentation de la base de données.
            // Ce service sera "offert" à chaque agent qui arrive sur cette plateforme.
            DatabaseServiceRem db = new DatabaseServiceImpl(); 
            
            System.out.println("=== DÉMARRAGE DE LA PLATEFORME D'ACCUEIL ===");
            System.out.println("Service local chargé : DatabaseServiceImpl");

            // On crée l'instance du serveur et on lance l'écoute réseau
            AgentServer server = new AgentServer(port, db);
            server.start();
            
        } catch (NumberFormatException e) {
            System.err.println("ERREUR : Le port doit être un nombre entier.");
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE lors de l'initialisation : " + e.getMessage());
            e.printStackTrace();
        }
    }
}