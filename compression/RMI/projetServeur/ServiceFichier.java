import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceFichier extends Remote {
    // on définit la signature de la méthode distante
    byte[] envoyerFichier(String nomFichier, byte[] contenu)
            throws RemoteException;
}

