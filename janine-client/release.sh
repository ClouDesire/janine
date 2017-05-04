#!/bin/bash -exu
echo Releasing $VERSION and updating $NEXT_VERSION
mvn versions:set -DnewVersion=${VERSION} scm:checkin -Dmessage="Release $VERSION" -DgenerateBackupPoms=false
mvn scm:tag
mvn versions:set -DnewVersion=${NEXT_VERSION}-SNAPSHOT scm:checkin -Dmessage="Preparing for next iteration" -DgenerateBackupPoms=false
echo Finished!
