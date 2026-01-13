import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9000);
        System.out.println("SERVEUR D'AGENTS DÉMARRÉ");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try {
			// notre socket est prêt à communiquer et le data input stream  va nous permettre de lire les données envoyées par le client
                    DataInputStream dis = new DataInputStream(socket.getInputStream());

                    // 1. lecture du nom
                    String className = dis.readUTF();
                    System.out.println("(" + className + ") réception en cours");

                    // 2. lecture du bytecode
                    int codeLen = dis.readInt();
                    byte[] bytecode = new byte[codeLen];
                    dis.readFully(bytecode);

                    // 3. lecture de l'agent
                    int agentLen = dis.readInt();
                    byte[] agentBytes = new byte[agentLen];
                    dis.readFully(agentBytes);

                    // ------------------------- reconstruction de l'agent
                    AgentClassLoader loader = new AgentClassLoader();
                    loader.addClass(className, bytecode);

                    ByteArrayInputStream bis = new ByteArrayInputStream(agentBytes);
                    AgentObjectInputStream ois = new AgentObjectInputStream(bis, loader);
                    AgentMobile agent = (AgentMobile) ois.readObject();

                    // -------------------------- exécution de l'agent
                    System.out.println("[" + className + "] Exécution...");
                    agent.executer();

                    // --------------------------- renvoi de l'agent au client
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
