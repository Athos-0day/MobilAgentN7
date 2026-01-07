import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServiceFichier extends Remote {

    void envoyerFichier(String nomFichier, byte[] contenu)
            throws RemoteException;
}
