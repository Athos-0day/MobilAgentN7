import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(9000);
        System.out.println("Serveur prêt...");

        while (true) {
            Socket socket = serverSocket.accept();

            // Créer UN seul ObjectInputStream avec ClassLoader
            AgentClassLoader loader = new AgentClassLoader();
            ObjectInputStream ois = new AgentObjectInputStream(socket.getInputStream(), loader);

            // Lire bytecode + agent dans le même flux
            byte[] codeClasse = (byte[]) ois.readObject();
            loader.chargerClasse(codeClasse); // Charger la classe dynamique
            AgentMobile agent = (AgentMobile) ois.readObject();

            // Exécuter l’agent
            agent.executer();

            // Répondre au client
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(agent);
            oos.flush();

            socket.close();
        }
    }
}
