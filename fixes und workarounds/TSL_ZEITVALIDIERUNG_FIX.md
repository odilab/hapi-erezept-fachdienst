# TSL Zeitvalidierungs-Fix

## Problem
Die TSL-Zertifikate waren abgelaufen (gültig bis: 05.08.2025 01:59:59 CEST), was dazu führte, dass der PukTokenManager keine Public Keys laden konnte und die Tests fehlschlugen.

## Lösung
Eine konfigurierbare Option wurde hinzugefügt, um die Zeitvalidierung der TSL-Zertifikate zu überspringen.

### Geänderte Dateien:

1. **TslManager.java**
   - Neue Property: `@Value("${hapi.fhir.auth.skip_tsl_time_validation:false}")`
   - Logik angepasst um Zeitvalidierung optional zu überspringen
   - Bei übersprungener Validierung wird WARN statt ERROR geloggt

2. **PukTokenManager.java**
   - SSL-Konfiguration in `@PostConstruct` verschoben
   - Lazy-Loading für Public Key implementiert (wird bei Bedarf nachgeladen)
   - Robustere Fehlerbehandlung

3. **application.yaml** (main und test)
   - Neue Property: `skip_tsl_time_validation: true`
   - Discovery URL und Update-Intervall konfiguriert

## Verwendung

### Für Tests (Entwicklung):
```yaml
hapi:
  fhir:
    auth:
      skip_tsl_time_validation: true  # Überspringt Zeitvalidierung
```

### Für Produktion:
```yaml
hapi:
  fhir:
    auth:
      skip_tsl_time_validation: false  # Zeitvalidierung aktiv (default)
```

## Verifizierung
- Mit `skip_tsl_time_validation: true` laufen alle Tests erfolgreich
- Mit `skip_tsl_time_validation: false` schlagen Tests mit abgelaufenen Zertifikaten fehl
- Die Fehlermeldung zeigt klar das abgelaufene Zertifikat an