#!/usr/bin/env bash
# Mac / Linux. Run from the folder that contains docker-compose.yml:   bash seed/seed.sh
set -e
for c in users student_profiles email_dispatches saved_posts; do
  docker exec pathfinder-mongo mongoimport --db user_db --collection "$c" --drop --jsonArray --file "/seed/user_db/$c.json"
done
docker exec pathfinder-mongo mongosh --quiet /seed/indexes.js
docker exec pathfinder-postgres psql -U pathfinder -d notice_db -v ON_ERROR_STOP=1 -f /seed/postgres/schema.sql
docker exec pathfinder-postgres psql -U pathfinder -d notice_db -v ON_ERROR_STOP=1 -f /seed/postgres/seed.sql
echo "Dummy data loaded."
