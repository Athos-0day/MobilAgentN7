#!/bin/bash

# --- CONFIGURATION ---
IP_A="127.0.0.1"
PORT_A=8081
IP_B="127.0.0.1"
PORT_B=8082
IP_CLIENT="127.0.0.1"
PORT_CLIENT=9999

VILLES=("Toulouse" "Paris" "Bordeaux")
NB_REQUETES=100
LOG_FILE="resultats_agent.csv"

# --- INITIALISATION ---
echo "Iteration;Ville;Temps_ms" > $LOG_FILE
TOTAL_TIME=0
SUCCESS_COUNT=0

echo "----------------------------------------------------"
echo "Lancement du benchmark : $NB_REQUETES requêtes"
echo "----------------------------------------------------"

# --- BOUCLE DE TEST ---
for ((i=1; i<=NB_REQUETES; i++))
do
    VILLE=${VILLES[$RANDOM % ${#VILLES[@]}]}
    
    # Exécution et capture de la sortie
    # On cherche la ligne qui contient "MISSION TERMINÉE EN X ms"
    OUTPUT=$(java Client.AgentClient --ville "$VILLE" \
                            --ipA "$IP_A" --portA $PORT_A \
                            --ipB "$IP_B" --portB $PORT_B \
                            --ipClient "$IP_CLIENT" --portClient $PORT_CLIENT 2>&1)
    
    # Extraction du temps via grep et sed
    # On cherche le nombre juste avant "ms"
    TIME_MS=$(echo "$OUTPUT" | grep "MISSION TERMINÉE" | sed -E 's/.*EN ([0-9]+) ms.*/\1/')

    if [[ ! -z "$TIME_MS" ]]; then
        echo "[$i/$NB_REQUETES] $VILLE : ${TIME_MS}ms"
        echo "$i;$VILLE;$TIME_MS" >> $LOG_FILE
        TOTAL_TIME=$((TOTAL_TIME + TIME_MS))
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
    else
        echo "[$i/$NB_REQUETES] $VILLE : ÉCHEC (Pas de réponse de l'agent)"
        echo "$i;$VILLE;FAILED" >> $LOG_FILE
    fi

    # Petite pause pour éviter la saturation du port 9999
    sleep 0.1
done

# --- AFFICHAGE DES RÉSULTATS ---
echo "----------------------------------------------------"
echo "RÉSULTATS FINAUX"
echo "----------------------------------------------------"
if [ $SUCCESS_COUNT -gt 0 ]; then
    AVERAGE=$(echo "scale=2; $TOTAL_TIME / $SUCCESS_COUNT" | bc)
    echo "Requêtes réussies : $SUCCESS_COUNT / $NB_REQUETES"
    echo "Temps cumulé      : ${TOTAL_TIME}ms"
    echo "Temps moyen       : ${AVERAGE}ms"
else
    echo "Aucune requête n'a abouti."
fi
echo "Détails disponibles dans : $LOG_FILE"
echo "----------------------------------------------------"