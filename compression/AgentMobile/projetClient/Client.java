import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {

    public static void main(String[] args) throws Exception {

        byte[] fichier = Files.readAllBytes(new File("document.txt").toPath());

        AgentCompression agent =
                new AgentCompression("document.txt", fichier);

        byte[] codeClasse = Files.readAllBytes(
                new File("AgentCompression.class").toPath()
        );

    Socket socket = new Socket("localhost", 9000);

    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    // Écrire bytecode puis agent dans le même flux
    oos.writeObject(codeClasse);
    oos.writeObject(agent);
    oos.flush();

    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
    AgentMobile agentRetour = (AgentMobile) ois.readObject();

    // Sauvegarder le résultat
    Files.write(new File("resultat.zip").toPath(),
                agentRetour.getResultat());

    socket.close();
    }
}
