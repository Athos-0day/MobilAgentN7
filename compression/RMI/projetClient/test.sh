#!/bin/bash

# Vérification des arguments
if [ "$#" -ne 4 ]; then
    echo "Usage: $0 <nb_clients> <ip> <port> <fichier>"
    echo "Exemple: $0 10 localhost 8081 document.txt"
    exit 1
fi

NB_CLIENTS=$1
IP=$2
PORT=$3
FICHIER=$4

# Vérifier si le fichier existe
if [ ! -f "$FICHIER" ]; then
    echo "Erreur : Le fichier '$FICHIER' est introuvable."
    exit 1
fi

# Compiler au cas où (optionnel)
# javac Client.java

echo "--- Lancement de $NB_CLIENTS clients sur $IP:$PORT ---"

# Début du chronométrage
START_TIME=$(date +%s.%N)

# Boucle pour lancer les clients en parallèle
for i in $(seq 1 $NB_CLIENTS)
do
    echo "Lancement client #$i..."
    # On redirige la sortie vers un log pour ne pas polluer la console
    java Client "$IP" "$PORT" "$FICHIER" > "client_$i.log" 2>&1 &
done

# Attendre que tous les processus fils (les clients) se terminent
wait

# Fin du chronométrage
END_TIME=$(date +%s.%N)
DURATION=$(echo "$END_TIME - $START_TIME" | bc)

echo "--- Travail terminé ---"
echo "Temps total pour $NB_CLIENTS clients : $DURATION secondes"

# Nettoyage optionnel des fichiers .zip générés (décommenter si besoin)
# rm *.zip
