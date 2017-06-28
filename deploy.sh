#!/bin/bash -e
echo Deploy server container
cd ./janine-server && ./dockerbuild.sh
