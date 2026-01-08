package Common;
import java.io.*;
import java.net.Socket;

public abstract class Agent implements Serializable {
    private static final long serialVersionUID = 1L;
    protected transient Object localService; 

    public abstract void execute();
    public abstract byte[] getByteCode(); // L'agent doit transporter son propre code

    public void setService(Object service) {
        this.localService = service;
    }

    // --- PROTOCOLE ROBUSTE : ENVOI SANS MÉLANGE DE FLUX ---
    public void move(String destHost, int destPort) {
        System.out.println(">>> Agent : Je migre vers " + destHost + ":" + destPort);
        try (Socket socket = new Socket(destHost, destPort)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // 1. On sérialise l'objet en mémoire d'abord (tampon)
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            byte[] agentBytes = bos.toByteArray();

            // 2. On envoie tout via DataOutputStream
            // A. Le Nom de la classe
            dos.writeUTF(this.getClass().getName());
            
            // B. Le Bytecode (.class)
            byte[] code = this.getByteCode();
            dos.writeInt(code.length);
            dos.write(code);

            // C. L'objet sérialisé (Data)
            dos.writeInt(agentBytes.length);
            dos.write(agentBytes);
            
            dos.flush();
        } catch (Exception e) {
            System.err.println("Erreur migration : " + e.getMessage());
            e.printStackTrace();
        }
    }
}