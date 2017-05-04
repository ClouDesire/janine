#!/bin/bash -e
echo $PRIVATE_GPG | base64 -d > ./.gpg/private.gpg
chmod 600 ./.gpg/private.gpg
echo Running maven deploy
./mvnw -B deploy -Dmaven.test.skip=true -Dgpg.skip=false -Dgpg.publicKeyring=./.gpg/public.gpg -Dgpg.secretKeyring=./.gpg/private.gpg
echo Running dockerbuild
cd ./janine-server && ./dockerbuild.sh
