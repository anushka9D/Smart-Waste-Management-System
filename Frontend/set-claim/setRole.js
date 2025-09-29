
const admin = require("firebase-admin");
const serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

async function main(uid, role) {
  if (!uid || !role) {
    console.log("Usage: node setRole.js <uid> <admin|user|driver>");
    process.exit(1);
  }
  await admin.auth().setCustomUserClaims(uid, { role });
  console.log(`Set role "${role}" for uid: ${uid}`);
  process.exit(0);
}

const [,, uid, role] = process.argv;
main(uid, role).catch((e) => { console.error("Failed:", e); process.exit(1); });
