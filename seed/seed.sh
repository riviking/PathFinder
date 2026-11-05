#!/usr/bin/env bash
# Mac / Linux. Run from the folder that contains docker-compose.yml:   bash seed/seed.sh
set -e
for pair in user_db/users user_db/student_profiles user_db/email_dispatches user_db/saved_posts notice_db/job_categories notice_db/notices notice_db/notice_views flyer_db/flyers; do
  db="${pair%/*}"; col="${pair#*/}"
  docker exec pathfinder-mongo mongoimport --db "$db" --collection "$col" --drop --jsonArray --file "/seed/$db/$col.json"
done
docker exec pathfinder-mongo mongosh flyer_db --quiet --eval "db.fs.files.drop(); db.fs.chunks.drop()"
for f in seed/flyers/*.png; do
  name="$(basename "$f")"
  docker exec pathfinder-mongo mongofiles --db flyer_db --local "/seed/flyers/$name" --type image/png put "$name"
done
docker exec pathfinder-mongo mongosh --quiet /seed/indexes.js
echo "Dummy data loaded."
