# Forward

L'application **Forward** décrit un système distribué permettant de récupérer les restaurants et les numéros de téléphone associés à une ville (données disponibles pour **Bordeaux**, **Paris** et **Toulouse**). Bien qu'il soit possible d'interroger d'autres villes, le résultat sera vide car la base de données est volontairement minimale.

L'enjeu de ce scénario n'est pas la gestion de données massives, mais la mise en application et la comparaison de deux paradigmes de communication différents :

- **Client / Serveur (RMI)**
- **Agents Mobiles**

---

## 1. Approche RMI

Dans l'architecture **RMI**, le client joue le rôle de *chef d'orchestre*. Il rapatrie les données à chaque étape afin de décider de la suite du traitement.

### Fonctionnement technique

Le client effectue des appels successifs :

1. Un appel au **Serveur A** pour récupérer la liste des noms de restaurants  
   (stockés dans une `Map<String, List<String>>`).
2. Pour chaque nom reçu, un nouvel appel réseau vers le **Serveur B** pour interroger l'annuaire  
   (stocké dans une `Map<String, String>`).

Si une ville contient **N** restaurants, le coût réseau est :

1 + N allers-retours

### Guide de lancement

#### Compilation
```bash
javac RMI/*.java
```

#### Lancement en local

- **Serveur (terminal 1)**
```bash
java RMI/DatabaseServiceImpl
```

- **Client (terminal 2)**
```bash
java RMI/ClientRMI --ville Toulouse
```

#### Lancement distribué

- **Serveur**  
Configurer l’IP réelle dans le code de `DatabaseServiceImpl`.

- **Client**
```bash
java RMI/ClientRMI --ip <IP_SERVEUR> --ville Toulouse
```

#### Benchmark
Configurer benchmark pour le nombre de requête notamment.

```bash
chmod +x benchmark.sh
./benchmark.sh
```

---

## 2. Approche Agents Mobiles

Le paradigme des **agents mobiles** inverse la logique classique :  
au lieu de déplacer les données vers le code, on déplace le **code vers les données**.

### Fonctionnement technique

L’agent est un objet sérialisé transportant son propre **bytecode**. Son exécution s’effectue en **trois sauts fixes** :

1. **Client → Serveur A** : récupération locale des restaurants  
2. **Serveur A → Serveur B** : enrichissement avec l’annuaire  
3. **Serveur B → Client** : retour avec les résultats

Le coût réseau est limité à **3 transferts**, indépendamment du volume de données.

### Guide de lancement

#### Compilation
```bash
javac Common/*.java Serveur/*.java Client/*.java
```

#### Lancement

```bash
java Serveur.AgentServer 8081
java Serveur.AgentServer 8082
java Client.AgentClient --ville Toulouse --ipA <IP_A> --ipB <IP_B> --ipClient <VOTRE_IP>
```

#### Benchmark
```bash
chmod +x benchmark.sh
./benchmark.sh
```

