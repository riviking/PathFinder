# Windows PowerShell. Run from the folder that contains docker-compose.yml:   .\seed\seed.ps1
# Needs:  docker compose up -d   (the container name is pathfinder-mongo)
$ErrorActionPreference = 'Stop'
$cols = @(
  @('user_db','users'), @('user_db','student_profiles'), @('user_db','email_dispatches'), @('user_db','saved_posts'),
  @('notice_db','job_categories'), @('notice_db','notices'), @('notice_db','notice_views'),
  @('flyer_db','flyers')
)
foreach ($c in $cols) {
  docker exec pathfinder-mongo mongoimport --db $c[0] --collection $c[1] --drop --jsonArray --file "/seed/$($c[0])/$($c[1]).json"
}
# flyer images go into GridFS (database flyer_db, bucket fs)
docker exec pathfinder-mongo mongosh flyer_db --quiet --eval "db.fs.files.drop(); db.fs.chunks.drop()"
Get-ChildItem .\seed\flyers\*.png | ForEach-Object {
  docker exec pathfinder-mongo mongofiles --db flyer_db --local "/seed/flyers/$($_.Name)" --type image/png put $_.Name
}
docker exec pathfinder-mongo mongosh --quiet /seed/indexes.js
Write-Host 'Dummy data loaded.'
