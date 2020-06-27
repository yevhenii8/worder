#!/bin/bash


if [ $EUID != 0 ]; then
    sudo "$0" "$@"
    exit $?
fi


WORDER_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." >/dev/null 2>&1 && pwd )"
STAMP_PATTERN_LENGTH=$(cat fileStampPattern.txt | wc --lines)
STAMP_PATTERN_ORIGINAL=$(cat fileStampPattern.txt)
STAMP_PATTERN=$(echo "${STAMP_PATTERN_ORIGINAL//"*"/"\*"}" | sed -E 's/<[^>]*>/<.*>/g')


for file in $(find $WORDER_HOME -name '*.kt' -or -name '*.kts');
    do
        if [[ $(grep --line-regexp --count "$STAMP_PATTERN" "$file") != $STAMP_PATTERN_LENGTH ]];
        then
            WHO=$(basename $BASH_SOURCE)
            WHEN=$(date +"%a %b %d %H:%M:%S %Y")
            CREATION_TIME=$(debugfs -R "stat $file" /dev/sda5 | grep "crtime")
            CREATION_TIME=${CREATION_TIME##* -- }
            MODIFICATION_TIME=$(stat --format="@%Y" $file)
            MODIFICATION_TIME=$(date -d $MODIFICATION_TIME +"%a %b %d %H:%M:%S %Y")

            NEW_STAMP=${STAMP_PATTERN_ORIGINAL/"GENERATED_BY"/"$WHO"}
            NEW_STAMP=${NEW_STAMP/"CHECKED_BY"/"$WHO"}
            NEW_STAMP=${NEW_STAMP/"GENERATION_TIME"/"$WHEN"}
            NEW_STAMP=${NEW_STAMP/"CHECK_TIME"/"$WHEN"}

            NEW_STAMP=${NEW_STAMP/"CREATION_TIME"/"$CREATION_TIME"}
            NEW_STAMP=${NEW_STAMP/"MODIFICATION_TIME"/"$MODIFICATION_TIME"}
            NEW_STAMP=${NEW_STAMP/"VERSION_NUMBER"/"1"}

            echo "Adding a stamp to a file: $file"
            echo -e "$NEW_STAMP\n\n$(cat $file)" > "$file"
            echo ""
        fi
    done
