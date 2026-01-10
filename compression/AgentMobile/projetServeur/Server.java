import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9000);
        System.out.println("=== SERVEUR D'AGENTS DÉMARRÉ ===");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());

                    // 1. Lecture du nom
                    String className = dis.readUTF();
                    System.out.println("[" + className + "] Réception en cours...");

                    // 2. Lecture du bytecode
                    int codeLen = dis.readInt();
                    byte[] bytecode = new byte[codeLen];
                    dis.readFully(bytecode);

                    // 3. Lecture de l'agent
                    int agentLen = dis.readInt();
                    byte[] agentBytes = new byte[agentLen];
                    dis.readFully(agentBytes);

                    // --- RECONSTRUCTION ---
                    AgentClassLoader loader = new AgentClassLoader();
                    loader.addClass(className, bytecode);

                    ByteArrayInputStream bis = new ByteArrayInputStream(agentBytes);
                    AgentObjectInputStream ois = new AgentObjectInputStream(bis, loader);
                    AgentMobile agent = (AgentMobile) ois.readObject();

                    // --- ACTION ---
                    System.out.println("[" + className + "] Exécution...");
                    agent.executer();

                    // --- RENVOI ---
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(agent);
                    oos.flush();
                    System.out.println("[" + className + "] Terminé et renvoyé.");

                    socket.close();
                } catch (Exception e) {
                    System.err.println("Erreur avec un agent :");
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
