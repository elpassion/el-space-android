#!/bin/bash
if [ "master" = `git rev-parse --abbrev-ref HEAD` ]; then
    ./scripts/decrypt-el-space-release.sh
    ./scripts/decrypt-el-debate-release.sh
    ./gradlew :el-space-app:publishApkRelease
    ./gradlew :el-debate-app:publishApkRelease
fi
