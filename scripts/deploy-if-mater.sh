#!/bin/bash
if [ "master" = `git rev-parse --abbrev-ref HEAD` ]; then
    ./scripts/decrypt-el-space-release.sh
    ./gradlew :el-space-app:publishApkRelease
fi
