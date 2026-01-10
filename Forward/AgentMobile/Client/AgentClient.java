package Client;
import Serveur.AgentServer;
import java.nio.file.*;
import java.io.*;
import java.net.Socket;

public class AgentClient {
    public static void main(String[] args) {
        // Valeurs par défaut
        String ville = "Toulouse";
        String ipA = "localhost";
        String ipB = "localhost";
        String ipClient = "localhost";
        
        int portA = 8081; 
        int portB = 8082; 
        int portClient = 9999;

        // --- GESTION DES ARGUMENTS ---
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--ville": if (i+1 < args.length) ville = args[i+1]; break;
                case "--ipA": if (i+1 < args.length) ipA = args[i+1]; break;
                case "--ipB": if (i+1 < args.length) ipB = args[i+1]; break;
                case "--ipClient": if (i+1 < args.length) ipClient = args[i+1]; break;
                case "--portA": if (i+1 < args.length) portA = Integer.parseInt(args[i+1]); break;
                case "--portB": if (i+1 < args.length) portB = Integer.parseInt(args[i+1]); break;
                case "--portClient": if (i+1 < args.length) portClient = Integer.parseInt(args[i+1]); break;
            }
        }

        System.out.println("=== CONFIGURATION DU SCÉNARIO ===");
        System.out.println("Ville cible    : " + ville);
        System.out.println("Serveur A (IP) : " + ipA + ":" + portA);
        System.out.println("Serveur B (IP) : " + ipB + ":" + portB);
        System.out.println("Client Ret (IP): " + ipClient + ":" + portClient);
        System.out.println("=================================");

        try {
            // 1. Lancer le serveur de retour localement (sur cette machine)
            // On écoute sur toutes les interfaces, mais l'agent devra viser 'ipClient' pour revenir
            final int fixedPortClient = portClient;

            new Thread(() -> new AgentServer(fixedPortClient, null).start()).start();
            Thread.sleep(1000);

            // 2. Charger le bytecode de l'agent
            String classPath = "Client/ForwardAgent.class";
            byte[] code = Files.readAllBytes(Paths.get(classPath));

            // 3. Instancier l'agent avec l'itinéraire complet (IPs et Ports)
            ForwardAgent agent = new ForwardAgent(ville, ipA, portA, ipB, portB, ipClient, portClient, code);

            // 4. PREMIER ENVOI VERS SERVEUR A
            System.out.println("\nDépart de l'agent vers Serveur A...");
            
            try (Socket s = new Socket(ipA, portA)) {
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                // Sérialisation de l'objet agent en mémoire
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(agent);
                oos.flush();
                byte[] agentBytes = bos.toByteArray();

                // Envoi du paquet complet (Nom, Bytecode, Objet)
                dos.writeUTF(agent.getClass().getName());
                dos.writeInt(code.length);
                dos.write(code);
                dos.writeInt(agentBytes.length);
                dos.write(agentBytes);
                dos.flush();
            }
            
            System.out.println("Agent injecté ! En attente du résultat final...");

        } catch (Exception e) {
            System.err.println("Erreur Client : " + e.getMessage());
            e.printStackTrace();
        }
    }
}