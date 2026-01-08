package Serveur;
import Common.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.Naming;          
import java.rmi.registry.LocateRegistry;

public class DatabaseServiceImpl extends UnicastRemoteObject implements DatabaseServiceRem {
    private Map<String, List<String>> restaurantData; // Pour le serveur A (la clé est la ville et la valeur est le restaurant)
    private Map<String, String> numeroData;       // Pour le serveur B (la clé est le restaurant et la valeur est le numéro)

    public DatabaseServiceImpl() throws RemoteException {
        super();
        this.restaurantData = new HashMap<>();
        this.numeroData = new HashMap<>();

        // DONNÉES POUR PARIS
        this.restaurantData.put("Paris", Arrays.asList("L'Ambroisie", "Septime", "Le Comptoir"));
        this.numeroData.put("L'Ambroisie", "01 42 78 51 45");
        this.numeroData.put("Septime", "01 43 67 38 29");
        this.numeroData.put("Le Comptoir", "01 44 27 07 97");

        // DONNÉES POUR TOULOUSE
        this.restaurantData.put("Toulouse", Arrays.asList("Michel Sarran", "Le Bibent", "Le Py-R"));
        this.numeroData.put("Michel Sarran", "05 61 12 32 32");
        this.numeroData.put("Le Bibent", "05 34 30 18 37");
        this.numeroData.put("Le Py-R", "05 61 25 51 52");

        // DONNÉES POUR BORDEAUX
        this.restaurantData.put("Bordeaux", Arrays.asList("Le Quatrième Mur", "Garopapilles", "Le Petit Commerce"));
        this.numeroData.put("Le Quatrième Mur", "05 56 02 49 70");
        this.numeroData.put("Garopapilles", "09 72 45 55 36");
        this.numeroData.put("Le Petit Commerce", "05 56 79 76 58");
    }

    //Méthode pour récupérer les restaurants dans une ville
    @Override 
    public List<String> getRestaurantParVille(String ville) throws RemoteException {
        System.out.println("Serveur : Consultation des restaurants pour " + ville);
        return restaurantData.getOrDefault(ville, new ArrayList<>());
    }

    //Méthode pour récupérer le numéro de téléphone associé à un restaurant
    @Override
    public String getNumeroParRestaurant(String restaurant) throws RemoteException {
        System.out.println("Serveur : Consultation annuaire pour " + restaurant);
        return numeroData.getOrDefault(restaurant, "Numéro non trouvé");
    }

}