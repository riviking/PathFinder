// MongoDB indexes for the user-service (user_db). Run after the import:  docker exec pathfinder-mongo mongosh --quiet /seed/indexes.js
const u = db.getSiblingDB('user_db');
u.users.createIndex({ email: 1 }, { unique: true });
u.student_profiles.createIndex({ userId: 1 }, { unique: true });
u.email_dispatches.createIndex({ noticeId: 1 }, { unique: true });          // one email batch per notice
u.saved_posts.createIndex({ studentId: 1, noticeId: 1 }, { unique: true });  // a notice is saved once per student
u.saved_posts.createIndex({ reminderAt: 1, reminderSentAt: 1 });            // the reminder job looks for due reminders
print('indexes created');
