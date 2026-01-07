#!/bin/sh

echo "Description: This script build and run src/Server.java (the pad server)"

# chemin du dossier ou est installe le script
WORKDIR="$(realpath "$(dirname "$0")")"

# compiler Server.java
javac -d "$WORKDIR/target/classes/" -cp "$WORKDIR/src" "$WORKDIR/src/Server.java"

# executer la classe Server
java -cp "$WORKDIR/target/classes/" Server "$@"
