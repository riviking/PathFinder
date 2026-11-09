# Windows PowerShell. Run from the folder that contains docker-compose.yml:   .\seed\seed.ps1
# Needs:  docker compose up -d   (containers pathfinder-mongo and pathfinder-postgres)
$ErrorActionPreference = 'Stop'

# MongoDB: user-service data (user_db)
foreach ($c in @('users', 'student_profiles', 'email_dispatches', 'saved_posts')) {
  docker exec pathfinder-mongo mongoimport --db user_db --collection $c --drop --jsonArray --file "/seed/user_db/$c.json"
}
docker exec pathfinder-mongo mongosh --quiet /seed/indexes.js

# PostgreSQL: notice-service data (notice_db): tables, then test data (including the flyer images)
docker exec pathfinder-postgres psql -U pathfinder -d notice_db -v ON_ERROR_STOP=1 -f /seed/postgres/schema.sql
docker exec pathfinder-postgres psql -U pathfinder -d notice_db -v ON_ERROR_STOP=1 -f /seed/postgres/seed.sql
Write-Host 'Dummy data loaded.'
