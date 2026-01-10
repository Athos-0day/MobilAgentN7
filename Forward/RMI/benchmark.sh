#!/bin/bash

# --- CONFIGURATION ---
# Ici on utilise un seul host car ton test manuel montre un seul flag --ip
HOST="localhost"
VILLES=("Toulouse" "Paris" "Bordeaux")
NB_REQUETES=100
LOG_FILE="resultats_rmi.csv"

# --- INITIALISATION ---
echo "Iteration;Ville;Temps_ms" > $LOG_FILE
TOTAL_TIME=0
SUCCESS_COUNT=0

echo "----------------------------------------------------"
echo "Lancement du benchmark RMI : $NB_REQUETES requêtes"
echo "----------------------------------------------------"

for ((i=1; i<=NB_REQUETES; i++))
do
    VILLE=${VILLES[$RANDOM % ${#VILLES[@]}]}
    
    # Exécution du client RMI (Adapté à ta commande : java ClientRMI --ip localhost --ville Toulouse)
    OUTPUT=$(java ClientRMI --ip "$HOST" --ville "$VILLE" 2>&1)
    
    # EXTRACTION DU TEMPS : 
    # On cherche le nombre situé entre "(RMI): " et " ms"
    TIME_MS=$(echo "$OUTPUT" | grep "Temps total d'exécution (RMI)" | sed -E 's/.*\(RMI\): ([0-9]+) ms.*/\1/')

    if [[ ! -z "$TIME_MS" ]]; then
        echo "[$i/$NB_REQUETES] RMI - $VILLE : ${TIME_MS}ms"
        echo "$i;$VILLE;$TIME_MS" >> $LOG_FILE
        TOTAL_TIME=$((TOTAL_TIME + TIME_MS))
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
    else
        echo "[$i/$NB_REQUETES] RMI - $VILLE : ÉCHEC"
        # Optionnel : décommenter la ligne suivante pour debugger si ça échoue encore
        # echo "DEBUG: $OUTPUT"
        echo "$i;$VILLE;FAILED" >> $LOG_FILE
    fi
done

# --- STATISTIQUES FINALES ---
echo "----------------------------------------------------"
echo "RÉSULTATS FINAUX RMI"
echo "----------------------------------------------------"
if [ $SUCCESS_COUNT -gt 0 ]; then
    AVERAGE=$(echo "scale=2; $TOTAL_TIME / $SUCCESS_COUNT" | bc)
    echo "Requêtes réussies : $SUCCESS_COUNT / $NB_REQUETES"
    echo "Temps cumulé      : ${TOTAL_TIME}ms"
    echo "Temps moyen       : ${AVERAGE}ms"
else
    echo "Aucune requête RMI n'a abouti. Vérifiez le format de sortie de votre Java."
fi
echo "----------------------------------------------------"