#!/bin/bash

# Verzeichnis für die Zertifikate
CERT_DIR="."

# Root CA erstellen
echo "Erstelle Root CA..."
openssl genrsa -out $CERT_DIR/root-ca.key 4096
openssl req -x509 -new -nodes -key $CERT_DIR/root-ca.key -sha256 -days 1024 -out $CERT_DIR/root-ca.crt \
    -subj "/C=DE/ST=Berlin/L=Berlin/O=gematik Test/OU=E-Rechnung Test/CN=E-Rechnung Root CA"

# Fachdienst-Zertifikat erstellen
echo "Erstelle Fachdienst-Zertifikat..."
openssl genrsa -out $CERT_DIR/fachdienst.key 4096

# CSR für Fachdienst erstellen
openssl req -new -key $CERT_DIR/fachdienst.key -out $CERT_DIR/fachdienst.csr \
    -subj "/C=DE/ST=Berlin/L=Berlin/O=gematik Test/OU=E-Rechnung Test/CN=E-Rechnung Fachdienst"

# Konfigurationsdatei für Zertifikatserweiterungen
cat > $CERT_DIR/fachdienst-ext.cnf << EOL
[v3_ext]
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage=critical,digitalSignature,nonRepudiation
extendedKeyUsage=critical,clientAuth
subjectKeyIdentifier=hash
EOL

# Fachdienst-Zertifikat von Root CA signieren lassen
openssl x509 -req -in $CERT_DIR/fachdienst.csr \
    -CA $CERT_DIR/root-ca.crt -CAkey $CERT_DIR/root-ca.key -CAcreateserial \
    -out $CERT_DIR/fachdienst.crt -days 365 -sha256 \
    -extfile $CERT_DIR/fachdienst-ext.cnf -extensions v3_ext

# PKCS#12 Bundle erstellen für Java-Keystore
openssl pkcs12 -export -out $CERT_DIR/fachdienst.p12 \
    -inkey $CERT_DIR/fachdienst.key \
    -in $CERT_DIR/fachdienst.crt \
    -certfile $CERT_DIR/root-ca.crt \
    -password pass:changeit

# Aufräumen
rm $CERT_DIR/fachdienst.csr $CERT_DIR/fachdienst-ext.cnf $CERT_DIR/root-ca.srl

echo "Zertifikate wurden erfolgreich erstellt:"
echo "- Root CA: root-ca.crt"
echo "- Fachdienst-Zertifikat: fachdienst.crt"
echo "- PKCS#12 Bundle: fachdienst.p12 (Passwort: changeit)" 