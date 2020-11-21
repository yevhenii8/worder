#!/bin/bash

# Init section
echo "Worder linker v1.0"

DB_DIR="MyDictionary/Backup/"
WORK_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

set +m
shopt -s lastpipe

cd "$WORK_DIR"
source utils.sh
source ftp.sh

# config uploading -> searching for device and backup folder
if [[ -f "devices.config" ]]; then
    grep -E "^[^#].*(MTP|FTP)" devices.config | while read line; do
        if [[ $line =~ .*MTP.* ]]; then
            if [[ -d "/run/user/1000/gvfs/${line#*=}${DB_DIR}" ]]; then
                MTP_PATH="/run/user/1000/gvfs/${line#*=}${DB_DIR}"
                printf "Connecting to ${line%MTP=*}MTP -> "
                green "done\n"
                break
            fi
        else
            if [[ -n $(testFtp $DB_DIR "${line#*=}") ]]; then
                FTP_PATH="${line#*=}"
                printf "Connecting to ${line%FTP=*}FTP -> "
                green "done\n"
                break
            fi
        fi
    done
else
    echo "Config file not found! Please provide devices.config"
    exit 1
fi

# Checking if we've found a device and backup folder at all...
if [[ -z ${MTP_PATH+x} ]] && [[ -z ${FTP_PATH+x} ]]; then
    echo "Neither FTP nor MTP device isn't connected OR backup folder is missing."
    exit 1
fi

# Searching for the last backup file
if [[ -n ${MTP_PATH+x} ]]; then
    RECENT_DB=$(ls "$MTP_PATH"*.bck 2>/dev/null | head -n1)
    if [[ -z $RECENT_DB ]]; then
        echo "There's no a database file in backup folder."
        exit 1
    fi
else
    RECENT_DB=$(getLatestDb $DB_DIR "$FTP_PATH")
    if [[ -z $RECENT_DB ]]; then
        echo "There's no a database file in backup folder."
        exit 1
    fi
fi

# Placing .bck in the work folder
printf "Removing all *.bck* from ${WORK_DIR%/} -> "
rm -d ${WORK_DIR}/*.bck 2>/dev/null
green "done\n"
printf "Copying ...${RECENT_DB#*storage} to ${WORK_DIR%/} -> "

if [[ -n ${MTP_PATH+x} ]]; then
    cp "$RECENT_DB" "$WORK_DIR"
else
    copyDbFrom ${RECENT_DB} "$FTP_PATH" "$WORK_DIR"
fi
green "done\n"

# Processing .bck with Worder App
echo "Starting Worder App!"
printf "\n\n"
if [[ -z $@ ]]; then
    java --enable-preview -jar worder-uber.jar
else
    java --enable-preview -jar worder-uber.jar $@
fi

# Clearing device folder
printf "\n\n"
printf "Removing all *.bck* from ...${DB_DIR%/} -> "
if [[ -n ${MTP_PATH+x} ]]; then
    rm "${MTP_PATH}"*bck* 2>/dev/null
else
    clearDir ${DB_DIR%/} "$FTP_PATH"
fi
green "done\n"

# Placing .bck back to device
printf "Copying back with the name updated.bck -> "
mv ${RECENT_DB##*/} updated.bck 2>/dev/null
if [[ -n ${MTP_PATH+x} ]]; then
    cp -f updated.bck "${MTP_PATH}" 2>/dev/null
else
    copyDbTo ${DB_DIR%/} "$FTP_PATH" "updated.bck"
fi
green "done\n"

echo "Everything is done!"
exit 0
