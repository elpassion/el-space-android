openssl aes-256-cbc -d -k "$FABRIC_GOOGLE_PASSWORD" -in el-space-app/src/release/google-server-client-id.xml.enc -out el-space-app/src/release/res/values/google-server-client-id.xml
openssl aes-256-cbc -d -k "$FABRIC_GOOGLE_PASSWORD" -in el-space-app/src/release/google-services-release.json.enc -out el-space-app/google-services.json
openssl aes-256-cbc -d -k "$FABRIC_GOOGLE_PASSWORD" -in el-space-app/src/release/google-signing-credentials.gradle.enc -out el-space-app/src/release/google-signing-credentials.gradle
openssl aes-256-cbc -d -k "$FABRIC_GOOGLE_PASSWORD" -in el-space-app/src/release/google-play-api-key.json.enc -out el-space-app/src/release/google-play-api-key.json
openssl aes-256-cbc -d -k "$FABRIC_GOOGLE_PASSWORD" -in el-space-app/src/release/release-keystore.jks.enc -out el-space-app/src/release/release-keystore.jks
openssl aes-256-cbc -d -k "$PUSHER_PSW" -in el-space-android/pusher.properties.enc -out el-space-android/pusher.properties
