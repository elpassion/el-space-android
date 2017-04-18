#!/bin/bash
DIR=$(dirname $0)
echo $DIR
COMMIT_MESSAGE=`-1 --pretty=%B`
RELEASE_NOTES=$(git log --format="%cn @ "$CIRCLE_BRANCH"
------
%B" -n 1 $COMMIT_MESSAGE)
echo "$RELEASE_NOTES" > $DIR/../crashlytics_release_notes.txt
./gradlew :app:crashlyticsUploadDistributionDebug