import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceFichier extends Remote {

    // on créer la signature de la méthode envoyerFichier
    byte[] envoyerFichier(String nomFichier, byte[] contenu)
            throws RemoteException;
}

