#!/usr/bin/env bash
# Avvia InnovianSearcher su macOS / Linux.
# Al primo avvio costruisce l'applicazione (serve collegamento a Internet),
# poi la apre. Le volte successive parte subito.
set -e
cd "$(dirname "$0")"

if [ ! -f target/InnovianSearcher.jar ]; then
  echo "Primo avvio: costruzione dell'applicazione in corso (puo' richiedere qualche minuto)..."
  ./mvnw -q clean package
fi

java -jar target/InnovianSearcher.jar
