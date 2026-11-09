-- PathFinder notice-service database (PostgreSQL). Local: docker compose (pathfinder-postgres). Hosted: Supabase (run this file in the SQL editor).
DROP TABLE IF EXISTS flyers CASCADE;
DROP TABLE IF EXISTS notice_views CASCADE;
DROP TABLE IF EXISTS notices CASCADE;
DROP TABLE IF EXISTS job_categories CASCADE;

CREATE TABLE job_categories (
    id      text PRIMARY KEY,                      -- fixed slug, e.g. software-it (the user-service stores the same text)
    name    text NOT NULL UNIQUE,
    active  boolean NOT NULL DEFAULT true
);

CREATE TABLE notices (
    id                       uuid PRIMARY KEY,     -- the user-service stores this as text
    title                    text,                 -- a Draft may be incomplete, so most columns allow NULL
    company                  text,
    description              text,
    category_id              text REFERENCES job_categories(id),
    work_mode                text CHECK (work_mode IN ('ONSITE', 'REMOTE', 'HYBRID')),
    location                 text,
    duration                 text,
    compensation             text NOT NULL DEFAULT 'NOT_STATED' CHECK (compensation IN ('PAID', 'UNPAID', 'NOT_STATED')),
    compensation_detail      text,
    deadline                 timestamptz,          -- end of that day in Sri Lanka time
    application_link         text,
    application_email        text,
    application_instructions text,
    eligible_years           integer[] NOT NULL DEFAULT '{}',
    eligible_faculties       text[] NOT NULL DEFAULT '{}',
    status                   text NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED', 'CLOSED')),
    targeted_count           integer NOT NULL DEFAULT 0,   -- students emailed when the notice was published
    viewed_count             integer NOT NULL DEFAULT 0,   -- students who opened it (each counted once)
    created_by               text,                 -- user id from the user-service
    created_by_name          text,
    created_by_role          text,
    created_at               timestamptz,
    updated_at               timestamptz,
    published_at             timestamptz,
    closed_at                timestamptz
);
CREATE INDEX idx_notices_status_deadline ON notices (status, deadline);
CREATE INDEX idx_notices_category ON notices (category_id);

CREATE TABLE notice_views (
    notice_id       uuid NOT NULL REFERENCES notices(id) ON DELETE CASCADE,
    student_id      text NOT NULL,                 -- user id from the user-service
    first_viewed_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (notice_id, student_id)            -- first view only
);

CREATE TABLE flyers (
    notice_id    uuid PRIMARY KEY REFERENCES notices(id) ON DELETE CASCADE,   -- one flyer per notice
    file_name    text NOT NULL,
    content_type text NOT NULL CHECK (content_type IN ('image/png', 'image/jpeg')),
    size         bigint NOT NULL,
    description  text,
    data         bytea NOT NULL,                   -- the image (2 MB at most)
    uploaded_by  text,
    uploaded_at  timestamptz NOT NULL DEFAULT now()
);
