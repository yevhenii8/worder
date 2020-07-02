#!/bin/bash


#*********************************************************************************************************
# Detects file creation date on Linux with Exct4 file system
#*********************************************************************************************************
# The only reason for this file existence is that Linux API doesn NOT contain file creation date.
# So, in order to pick up creation dates of already presented files we need to reference EXT4 FS directly.
# It's meant to be used generateFileStamps.sh, although one-shot use.
#*********************************************************************************************************


if [[ $EUID != 0 ]]; then
    sudo "$0" "$@"
    exit $?
fi

file=$(realpath $1)
fs=$(df  --output=source "${file}"  | tail -1)
creation_time=$(debugfs -R "stat $file" $fs 2>/dev/null | grep "crtime")
creation_time=${creation_time##* -- }

if [[ -n $creation_time ]];
then
    date --date "$creation_time" +"%-m/%-d/%-g, %-I:%-M %p"
else
    echo "couldn't obtain creation date for $file" >2
fi
