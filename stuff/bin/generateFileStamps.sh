#!/bin/bash


#*********************************************************************************************************
# One-Shout script for generating the file stamps for the Kotlin source files (.kt/.kts)
#*********************************************************************************************************
# The only reason for this file existence is that Linux API doesn NOT contain file creation date.
# So, in order to pick up creation dates of already presented files we need to reference EXT4 FS directly.
# This script is to put a source file stamp on already created source files.
#*********************************************************************************************************


function getFileCreationDate() {
    file=$(realpath $1)
    fs=$(df  --output=source "${file}"  | tail -1)
    creation_date=$(debugfs -R "stat $file" $fs 2>/dev/null | grep "crtime")
    creation_date=${creation_date##* -- }

    if [[ -n $creation_date ]]; then
        echo "$creation_date"
        return 0
    else
        echo "Error! Couldn't obtain creation date for $file" >&2
        return 1
    fi
}

function getFileModificationDate() {
    file=$(realpath $1)
    modifiction_date=$(stat --format="@%Y" $file)
    
    if [[ -n $modifiction_date ]]; then
        echo "$modifiction_date"
        return 0
    else
        echo "Error! Couldn't obtain modification date for $file" >&2
        return 1
    fi
}

function formatDateToShort() {
    date=$1
    date --date "$date" +"%-m/%-d/%-g, %-I:%-M %p"
}


if [[ $EUID != 0 ]]; then
    sudo "$0" "$@"
    exit $?
fi


# WORDER_HOME="/home/yevhenii/Projects/worder/buildSrc/build/tests/StampedSourceFileTest"
WORDER_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." >/dev/null 2>&1 && pwd )"
STAMP_PATTERN_ORIGINAL=$(cat ../../buildSrc/src/main/resources/sourceFileStampPattern.txt)
STAMP_PATTERN_LENGTH=$(echo "$STAMP_PATTERN_ORIGINAL" | wc --lines)
STAMP_PATTERN=$(echo "${STAMP_PATTERN_ORIGINAL//"*"/"\*"}" | sed -E 's/<[^>]*>/<.*>/g')


for file in $(find $WORDER_HOME -name '*.kt' -or -name '*.kts');
do
    if [[ $(cat $file) == "/**"* ]]; then
        echo ""
        echo "Can't add the stamp to $file!"
        echo "File already contains a beginning comment! Please check and remove it!"
        echo ""
    else
        echo "Adding the stamp to $file"
        
        who=$(basename $BASH_SOURCE)
        when=$(date +"%-m/%-d/%-g, %-I:%-M %p")
        creation_date_raw=$(getFileCreationDate "$file")
        creation_date=$(formatDateToShort "$creation_date_raw")

        new_stamp=${STAMP_PATTERN_ORIGINAL/"STAMP_GENERATED_BY"/"$who"}
        new_stamp=${new_stamp/"STAMP_LAST_MODIFIED_BY"/"$who"}

        new_stamp=${new_stamp/"FILE_CREATION_TIME"/"$creation_date"}
        new_stamp=${new_stamp/"FILE_MODIFICATION_TIME"/"$when"}
        new_stamp=${new_stamp/"FILE_VERSION_NUMBER"/"1"}
        new_stamp=$new_stamp$'\n'$'\n'
        
        # echo "$new_stamp"
        # echo ""
        # echo ""
        echo "$new_stamp$(cat $file)" > "$file"
    fi
done
