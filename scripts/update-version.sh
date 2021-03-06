#!/bin/bash

if [ $# -ne 1 ]
  then
    echo "USAGE: ./scripts/update-version.sh 1.1.1"
   exit
fi

VERSION_NAME=$1
ARR=(${VERSION_NAME//./ })
VERSION_CODE=$((  100000000*(${ARR[0]}) + 10000*${ARR[1]} + ${ARR[2]}  ))


echo \
"android {
    defaultConfig {
        versionCode $VERSION_CODE
        versionName \"$VERSION_NAME\"
    }
}" > gradle/app-version.gradle