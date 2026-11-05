// Unique and search indexes. Run after the import:  docker exec pathfinder-mongo mongosh --quiet /seed/indexes.js
const u = db.getSiblingDB('user_db');
u.users.createIndex({ email: 1 }, { unique: true });
u.student_profiles.createIndex({ userId: 1 }, { unique: true });
u.email_dispatches.createIndex({ noticeId: 1 }, { unique: true });          // one email batch per notice
u.saved_posts.createIndex({ studentId: 1, noticeId: 1 }, { unique: true });  // a notice is saved once per student
u.saved_posts.createIndex({ reminderAt: 1, reminderSentAt: 1 });            // the reminder job looks for due reminders

const n = db.getSiblingDB('notice_db');
n.job_categories.createIndex({ name: 1 }, { unique: true });
n.notices.createIndex({ status: 1, deadline: 1 });
n.notices.createIndex({ categoryId: 1 });
n.notice_views.createIndex({ noticeId: 1, studentId: 1 }, { unique: true });   // first view only

const f = db.getSiblingDB('flyer_db');
f.flyers.createIndex({ noticeId: 1 }, { unique: true });                      // one flyer per notice
f.fs.files.createIndex({ filename: 1 }, { unique: true, name: 'uq_filename' });
print('indexes created');
