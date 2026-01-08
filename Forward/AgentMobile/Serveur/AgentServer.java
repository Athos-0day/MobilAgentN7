import java.io.*;
import java.net.*;

public class AgentServer {
    private int port;
    private DatabaseServiceRem localService; // Le service que l'agent utilisera localement

    public AgentServer(int port, DatabaseServiceRem localService) {
        this.port = port;
        this.localService = localService;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur d'Agents prêt sur le port " + port + "...");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                    // 1. Création d'un nouveau Loader pour cet agent (Isolation)
                    Loader loader = new Loader();

                    // 2. Lecture du nom de la classe
                    String className = dis.readUTF();

                    // 3. Lecture du bytecode (.class)
                    int classLength = dis.readInt();
                    byte[] bytecode = new byte[classLength];
                    dis.readFully(bytecode);
                    
                    // On enregistre la classe dans le loader
                    loader.addClass(className, bytecode);

                    // 4. Désérialisation de l'objet Agent
                    // On utilise notre AgentInputStream avec le loader
                    AgentInputStream ois = new AgentInputStream(socket.getInputStream(), loader);
                    Agent agent = (Agent) ois.readObject();

                    // 5. Injection du service et exécution
                    System.out.println("Agent " + className + " reçu. Exécution locale...");
                    agent.setService(localService); 
                    
                    long start = System.nanoTime();
                    agent.execute(); // L'agent travaille ici (appels locaux)
                    long end = System.nanoTime();
                    
                    System.out.println("Exécution terminée en " + (end - start)/1000 + " µs.");

                    // Note : Dans un vrai projet, l'agent repartirait ici vers le client
                    // Pour votre test, vous pouvez afficher les résultats ici ou renvoyer l'agent.
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // On réutilise l'implémentation que vous avez déjà faite pour RMI
        // Mais ici elle est utilisée LOCALEMENT par le serveur d'agent
        DatabaseServiceRem serviceLocal = new DatabaseServiceImpl();
        
        AgentServer host = new AgentServer(9000, serviceLocal);
        host.start();
    }
}