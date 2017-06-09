#!/bin/bash
# USAGE: ./release.sh 1.1.1

if [ $# -eq 0 ]
  then
    echo "USAGE: ./release.sh 1.1.1"
   exit
fi

function checkResult {
    if [ $? -ne 0 ]
      then
        echo "!!!!!     >>>ERROR<<<    !!!!!"
       exit
    fi
}

VERSION_NAME=$1

git stash
checkResult
git checkout develop -b release/$VERSION_NAME
checkResult
./scripts/update-version.sh $VERSION_NAME
checkResult

git commit -a -m "Update version in gradle.properties to $VERSION_NAME."
checkResult
git checkout master
checkResult
git pull --rebase
checkResult
git merge --no-ff --no-edit release/$VERSION_NAME
checkResult
git push origin master
checkResult
git tag $VERSION_NAME
checkResult
git push --tags
checkResult
git checkout develop
checkResult
git merge --no-ff --no-edit release/$VERSION_NAME
checkResult
git push origin develop
checkResult
git branch -d release/$VERSION_NAME
checkResult