#!/bin/bash


#***********************************************************************************************************************
# One-Off script for generating the file stamps for the Kotlin source files (.kt/.kts)
#***********************************************************************************************************************
# The only reason for this file existence is that Linux API doesn NOT contain file creation date.
# So, in order to pick up creation dates of already presented files we need to reference EXT4 FS directly.
# This script is to put a stamp on already created sources. Further created files will be processed by some Gradle Task.
#***********************************************************************************************************************


if [ $EUID != 0 ]; then
    sudo "$0" "$@"
    exit $?
fi


WORDER_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." >/dev/null 2>&1 && pwd )"
STAMP_PATTERN_ORIGINAL=$(cat ../fileStampPattern.txt)
STAMP_PATTERN_LENGTH=$(echo "$STAMP_PATTERN_ORIGINAL" | wc --lines)
STAMP_PATTERN=$(echo "${STAMP_PATTERN_ORIGINAL//"*"/"\*"}" | sed -E 's/<[^>]*>/<.*>/g')


for file in $(find $WORDER_HOME -name '*.kt' -or -name '*.kts');
do
    if [[ $(grep --line-regexp --count "$STAMP_PATTERN" "$file") != $STAMP_PATTERN_LENGTH ]];
    then
        who=$(basename $BASH_SOURCE)
        when=$(date +"%a %b %d %H:%M:%S %Y")
        fs=$(df  --output=source "${file}"  | tail -1)
        creation_time=$(debugfs -R "stat $file" $fs 2>/dev/null | grep "crtime")
        creation_time=${creation_time##* -- }
        modifiction_time=$(stat --format="@%Y" $file)
        modifiction_time=$(date -d $modifiction_time +"%a %b %d %H:%M:%S %Y")

        new_stamp=${STAMP_PATTERN_ORIGINAL/"GENERATED_BY"/"$who"}
        new_stamp=${new_stamp/"CHECKED_BY"/"$who"}
        new_stamp=${new_stamp/"GENERATION_TIME"/"$when"}
        new_stamp=${new_stamp/"CHECK_TIME"/"$when"}

        new_stamp=${new_stamp/"CREATION_TIME"/"$creation_time"}
        new_stamp=${new_stamp/"MODIFICATION_TIME"/"$modifiction_time"}
        new_stamp=${new_stamp/"VERSION_NUMBER"/"1"}


        if [[ $(cat $file) == "/**"* ]];
        then
            echo ""
            echo ""
            echo "Can't add the stamp to: $file!"
            echo "File already contains a beginning comment! Please add it manually or remove the comment and try again!"
            echo "$new_stamp"
            echo ""
            echo ""
        else
            echo "Adding the stamp to: $file"
            # echo -e "$new_stamp\n\n$(cat $file)" > "$file"
        fi
    fi
done
