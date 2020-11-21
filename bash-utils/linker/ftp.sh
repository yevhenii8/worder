#!/bin/bash

WORK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
LOG="$WORK_DIR/ftp.log"

function testFtp() {
    ftp -inv <<EOF &>$LOG
    open $2
    user anonymous anonymous
    ls $1
    disconnect
EOF

    if grep -q "150 File status okay; about to open data connection." $LOG; then
        echo 0
    fi
}

function getLatestDb() {
    ftp -inv <<EOF &>$LOG
    open $2
    user anonymous anonymous
    ls $1/*.bck
    disconnect
EOF

    str=$(sort $LOG -r | grep ^-rw------- | head -n1)
    if [[ -n $str ]]; then
        echo $1${str##* }
    fi
}

function copyDbFrom() {
    cd "$3"

    ftp -inv <<EOF &>$LOG
    open $2
    user anonymous anonymous
    cd ${1%/*}
    binary
    get ${1##*/}
    disconnect
EOF
}

function clearDir() {

    ftp -inv <<EOF &>$LOG
    open $2
    user anonymous anonymous
    cd $1
    mdelete *
    disconnect
EOF
}

function copyDbTo() {
    ftp -inv <<EOF &>$LOG
    open $2
    user anonymous anonymous
    cd $1
    binary
    put $3
    disconnect
EOF
}
