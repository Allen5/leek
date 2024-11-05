#!/bin/sh

WORKDIR=$(pwd)
echo "${WORKDIR}"
echo "======> step1: build production dist"
npm run build
echo "======> step2: transfer to app-admin"
DEST_DIR=${WORKDIR}/../Leek/application/app-admin/src/main/resources/static
cp -r ${WORKDIR}/dist/* ${DEST_DIR}
ls -al ${DEST_DIR}

