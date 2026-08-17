#!/usr/bin/env bash
set -euo pipefail

# Get the number from database changelogs and description from user
DESCRIPTION="${1:?Usage: $0 <description> e.g. create_users}"

# Root directory of this Spring Boot Gateway
GATEWAY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$GATEWAY_DIR"

# Load .env into shell environment
set -a
. ./.env
set +a

# Location to store migration files
MIGRATIONS_DIR="$GATEWAY_DIR/src/main/resources/db/changelog/migrations"
mkdir -p "$MIGRATIONS_DIR"

# Generate Liquibase properties file from .env (only on first run)
if [[ ! -f liquibase.properties ]]; then
    cat > liquibase.properties <<EOF
url=${POSTGRES_URL}
username=${POSTGRES_USER}
password=${POSTGRES_PASSWORD}
driver=org.postgresql.Driver
changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml
EOF
fi

LAST=$(ls "$MIGRATIONS_DIR" | grep -oE '^[0-9]+' | sort -n | tail -n 1 || true)
NEXT=$(printf '%03d' $(( ${LAST:-0} + 1 )))

SAFE=$(printf '%s' "$DESCRIPTION" | tr '[:upper:]' '[:lower:]' | tr -c 'a-z0-9' '_')
OUTFILE="$MIGRATIONS_DIR/${NEXT}_${SAFE}.xml"

./mvnw compile liquibase:diff \
    -Dliquibase.diffChangeLogFile="$OUTFILE" \
    -Dliquibase.referenceUrl='hibernate:spring:com.snapshoot.gateway.domain.entities?dialect=org.hibernate.dialect.PostgreSQLDialect&hibernate.physical_naming_strategy=org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy'

if [[ -f "$OUTFILE" ]]; then
    echo "Created: $OUTFILE"
else
    echo "No schema changes detected; no migration file created."
fi
