# PathFinder test data

- `user_db/*.json`: MongoDB (user-service): users, student_profiles, email_dispatches, saved_posts. Password of every account: `ChangeMe123!`.
- `postgres/schema.sql` and `postgres/seed.sql`: PostgreSQL (notice-service): job_categories (8), notices (15), notice_views (28), flyers (13, the images are read from `flyers/`).
- `intern_db/*.json`: MongoDB Extended JSON exports of the notice mock data for manual import into the intern service's Atlas database.
- `flyers/*.png`: the 13 dummy flyer images ("DEMO DATA - NOT A REAL VACANCY").

Load everything: `docker compose up -d`, then `.\seed\seed.ps1` (Mac/Linux: `bash seed/seed.sh`). It drops and re-creates the data each time.

Check:
```
docker exec pathfinder-mongo mongosh --quiet --eval "print(db.getSiblingDB('user_db').users.countDocuments())"                                  # 13
docker exec pathfinder-postgres psql -U pathfinder -d notice_db -c "select count(*) from notices"                                          # 15
docker exec pathfinder-postgres psql -U pathfinder -d notice_db -c "select count(*) from flyers"                                           # 13
```

Ids shared between the services are plain text: job category ids are slugs (`software-it`), notice ids are UUIDs, user ids are the hex text of the MongoDB id.
Supabase: run `postgres/schema.sql` in the SQL editor. The flyer rows use `pg_read_binary_file` (local Docker only), so on Supabase upload the flyers through the application instead.

## Import mock data into MongoDB Atlas manually

The `intern_db` files are line-delimited JSON. They contain 8 categories,
15 notices, 28 notice views, and 13 flyer metadata records converted from
`postgres/seed.sql`. The flyer image bytes are not included; only their file
names and metadata are imported.

1. In Atlas, open the target cluster's **Data Explorer** and create database
   `intern_db` with the first collection `job_categories`.
2. Open `job_categories`, select **Add Data** > **Import File**, choose JSON, and
   import `seed/intern_db/job_categories.json`.
3. Repeat for the other files, creating collections with the matching names:
   `notices`, `notice_views`, and `flyers`.
4. Confirm the document counts: 8, 15, 28, and 13, respectively.

Import `notices` before `notice_views` and `flyers` so the referenced notices
are present first. This manual Atlas import is separate from `seed.ps1` and
`seed.sh`, which seed local Docker databases.
