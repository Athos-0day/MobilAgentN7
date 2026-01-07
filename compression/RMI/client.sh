#!/bin/sh

echo "Description: This script build and run src/GUI.java (the graphical applet)"
echo "             For testing, you may start the server (that contains the registry) using server.sh first."

# chemin du dossier ou est installe le script
WORKDIR="$(realpath "$(dirname "$0")")"

# compiler GUI.java
javac -d "$WORKDIR/target/classes/" -cp "$WORKDIR/src" "$WORKDIR/src/GUI.java"

# executer la classe GUI
java -cp "$WORKDIR/target/classes/" GUI "$@"
