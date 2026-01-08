package Client;
import Serveur.AgentServer;
import java.nio.file.*;
import java.io.*;
import java.net.Socket;

public class AgentClient {
    public static void main(String[] args) {
        String ville = "Toulouse";
        String host = "localhost";
        int portA = 8081; 
        int portB = 8082; 
        int portClient = 9999;

        // Lecture des arguments (optionnel)
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--ville") && i+1 < args.length) ville = args[i+1];
        }

        System.out.println("SCENARIO FORWARD : Client -> ServA -> ServB -> Client");
        System.out.println("Recherche pour : " + ville);

        try {
            // 1. Lancer le serveur de retour sur le client
            new Thread(() -> new AgentServer(portClient, null).start()).start();
            Thread.sleep(1000); // Pause technique

            // 2. Charger le bytecode de l'agent
            // ATTENTION : Compilez d'abord pour avoir le .class !
            String classPath = "Client/ForwardAgent.class";
            byte[] code = Files.readAllBytes(Paths.get(classPath));

            // 3. Instancier l'agent
            ForwardAgent agent = new ForwardAgent(ville, host, portA, portB, portClient, code);

            // 4. PREMIER ENVOI (Même protocole robuste que move())
            System.out.println("Envoi initial vers Serveur A (" + portA + ")...");
            
            try (Socket s = new Socket(host, portA)) {
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                // A. Sérialisation tampon
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(agent);
                oos.flush();
                byte[] agentBytes = bos.toByteArray();

                // B. Envoi paquet
                dos.writeUTF(agent.getClass().getName());
                dos.writeInt(code.length);
                dos.write(code);
                dos.writeInt(agentBytes.length);
                dos.write(agentBytes);
                dos.flush();
            }
            
            System.out.println("Agent parti ! Attente du retour...");

        } catch (Exception e) {
            System.err.println("Erreur Client : " + e);
            e.printStackTrace();
        }
    }
}