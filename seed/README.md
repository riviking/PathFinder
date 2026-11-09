# PathFinder test data

- `user_db/*.json`: MongoDB (user-service): users, student_profiles, email_dispatches, saved_posts. Password of every account: `ChangeMe123!`.
- `postgres/schema.sql` and `postgres/seed.sql`: PostgreSQL (notice-service): job_categories (8), notices (15), notice_views (28), flyers (13, the images are read from `flyers/`).
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
