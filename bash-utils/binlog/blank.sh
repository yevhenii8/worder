#!/bin/bash


#**************************************************************************************************
# This file will represent a fake executable.
# Its task is just to log a call and execute it on the original file.
#**************************************************************************************************


ORIGINAL_BINARY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ORIGINAL_BINARY_DIR=${ORIGINAL_BINARY_DIR:1}
ORIGINAL_EXECUTABLE_NAME=$(basename "$0")
LOG_FILE_NAME="${ORIGINAL_BINARY_DIR////.}.${ORIGINAL_EXECUTABLE_NAME}.log"
LOG_FILE_LOCATION="/home/yevhenii/Projects/ubuntu-scripts/binlog/logs"
LOG_FILE="${LOG_FILE_LOCATION}/${LOG_FILE_NAME}"


echo "[$(date "+%F %T")] $ORIGINAL_EXECUTABLE_NAME $*" >> "$LOG_FILE"
"${0}-original" "$@"
