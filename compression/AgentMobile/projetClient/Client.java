import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    public static void main(String[] args) {
        // vérification des arguments : ip, port, fichier
        if (args.length < 3) {
            System.out.println("Usage: java Client <ip> <port> <fichier>");
            System.out.println("Exemple: java Client localhost 8081 document.txt");
            return;
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String nomFichier = args[2];

        try {
            System.out.println("1. Préparation de l'agent pour le fichier : " + nomFichier);
            File doc = new File(nomFichier);
            
            if (!doc.exists()) {
                System.err.println("Erreur : Le fichier '" + nomFichier + "' n'existe pas.");
                return;
            }

            byte[] contenu = Files.readAllBytes(doc.toPath());
            AgentCompression agent = new AgentCompression(nomFichier, contenu);

            System.out.println("2. Connexion au serveur " + ip + ":" + port + "...");
            Socket socket = new Socket(ip, port);
            // le data output stream pour envoyer des données 
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // ------------------------ envoi de l'agent 
            // Nom de la classe
            dos.writeUTF("AgentCompression");

            // Bytecode
            byte[] bytecode = Files.readAllBytes(new File("AgentCompression.class").toPath());
            dos.writeInt(bytecode.length);
            dos.write(bytecode);

            // L'objet Agent (sérialisation en mémoire)
            // bytearrayoutputstream pour sérialiser l'agent en mémoire
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // objectoutputstream pour écrire l'agent dans le bytearrayoutputstream
            ObjectOutputStream oosTemp = new ObjectOutputStream(bos);
            // écriture de l'agent
            oosTemp.writeObject(agent);
            oosTemp.flush();
            // récupération des bytes de l'agent
            byte[] agentBytes = bos.toByteArray();
            // envoi de la taille et des bytes de l'agent
            dos.writeInt(agentBytes.length);
            dos.write(agentBytes);
            dos.flush();

            System.out.println("3. Agent envoyé au serveur. Attente du retour...");

            // -------------------------- réception de l'agent 
            // objetinputstream pour lire l'agent retourné
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            AgentMobile agentRetour = (AgentMobile) ois.readObject();
            
            if (agentRetour.getResultat() != null) {
                // Création du nom de sortie document.txt.zip
                String nomSortie = nomFichier + ".zip";
                
                Files.write(new File(nomSortie).toPath(), agentRetour.getResultat());
                System.out.println("4. Succès ! Fichier '" + nomSortie + "' créé (" + agentRetour.getResultat().length + " octets).");
            } else {
                System.out.println("4. L'agent est revenu, mais le résultat est vide...");
            }

            socket.close();
        } catch (NumberFormatException e) {
            System.err.println("Erreur : Le port doit être un nombre entier.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
