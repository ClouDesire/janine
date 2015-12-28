#!/bin/bash -ex
mvn deploy -Dmaven.test.skip=true -Dgpg.keyname=$CI_DEPLOY_GPG_KEYID -Dgpg.skip=false -Dgpg.passphrase=$CI_DEPLOY_GPG_SECRET -Dgpg.publicKeyring=../.travis/public.gpg -Dgpg.secretKeyring=../.travis/private.gpg --settings ../.travis/settings.xml
if [ -x dockerbuild.sh ]; then
  ./dockerbuild.sh
fi
