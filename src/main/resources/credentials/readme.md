# Docker Compose Konfiguration für Credentials

Um die Credentials-Ordner korrekt in die Docker-Container für das Fachdiensttool und den Fachdienst zu mounten, erweitere deine `docker-compose.yml` wie folgt:

```yaml
services:
  # Beispiel-Service für das Fachdiensttool (Name anpassen!)
  fachdiensttool-service:
    # ... andere Service-Konfigurationen ...
    volumes:
      # Passe den Pfad auf der linken Seite an den tatsächlichen Speicherort
      # des 'service_qes'-Ordners auf deinem Server an!
      - "/pfad/zum/server/credentials/service_qes:/app/credentials" # Mount für das Fachdiensttool

  # Beispiel-Service für den Fachdienst (Name anpassen!)
  fachdienst-service:
    # ... andere Service-Konfigurationen ...
    volumes:
      # Passe den Pfad auf der linken Seite an den tatsächlichen Speicherort
      # des 'credentials'-Hauptordners auf deinem Server an!
      - "/pfad/zum/server/credentials:/app/credentials" # Mount für den Fachdienst

# ... weitere Services oder Konfigurationen ...
```

**Wichtige Schritte & Erläuterung:**

1.  **Ordnerstruktur kopieren:** Kopiere die gesamte `credentials`-Verzeichnisstruktur (inklusive Unterordnern wie `service_qes`, falls vorhanden) aus diesem Repository auf deinen Server, auf dem Docker Compose ausgeführt wird. Lege sie z.B. unter `/pfad/zum/server/credentials` ab.
2.  **Service-Namen anpassen:** Ersetze `fachdiensttool-service` und `fachdienst-service` in der obigen `docker-compose.yml` mit den tatsächlichen Namen deiner Services.
3.  **Host-Pfade anpassen:** Die Pfade auf der linken Seite der `volumes`-Definition im obigen Beispiel (`/pfad/zum/server/...`) sind *Platzhalter*. **Du musst diese Pfade an den tatsächlichen Speicherort anpassen**, an den du die `credentials`-Ordner auf deinem Server in Schritt 1 kopiert hast.
4.  **Container-Pfade überprüfen:** Die Zielpfade innerhalb der Container (rechts vom Doppelpunkt, z.B. `/app/credentials`) sind wahrscheinlich korrekt und sollten nur geändert werden, wenn die Anwendung die Credentials an einem anderen Ort erwartet.


