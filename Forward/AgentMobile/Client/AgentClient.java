import java.io.*;
import java.net.*;
import java.nio.file.*;

public class AgentClient {
    public static void main(String[] args) {
        try {
            ForwardAgent agent = new ForwardAgent("Paris");
            Socket s = new Socket("localhost", 9000);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // Envoi du nom et du bytecode
            String name = agent.getClass().getName();
            byte[] code = Files.readAllBytes(Paths.get(name + ".class"));
            
            dos.writeUTF(name);
            dos.writeInt(code.length);
            dos.write(code);

            // Envoi de l'objet agent
            ObjectOutputStream oos = new ObjectOutputStream(dos);
            oos.writeObject(agent);
            oos.flush();
            
            System.out.println("Agent envoyé !");
            s.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}