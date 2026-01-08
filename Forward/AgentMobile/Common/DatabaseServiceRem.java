package Common;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.List;

// Cette classe correspond à l'interface de méthode accessible par le client
public interface DatabaseServiceRem extends Remote {
    // Retourne les restaurants d'une ville 
    // Gérer par le premier serveur (A)
    List<String> getRestaurantParVille(String ville) throws RemoteException;

    // Retourne le numéro de téléphone d'un restaurant
    // Gérer par le deuxième serveur (B)
    String getNumeroParRestaurant(String restaurant) throws RemoteException;

} 