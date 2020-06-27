#!/bin/bash


if [ $EUID != 0 ]; then
    sudo "$0" "$@"
    exit $?
fi


# WORDER_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." >/dev/null 2>&1 && pwd )"
WORDER_HOME=/home/yevhenii/Projects/worder/build/tests/GradleUpdateFileStampsTest
STAMP_PATTERN=$(cat fileStampPattern.txt)
STAMP_PATTERN=$(echo "${STAMP_PATTERN//"*"/"\*"}" | sed -E 's/<[^>]*>/<.*>/g')

echo "$STAMP_PATTERN"
echo ""
echo "*************************************************"
echo ""
oldFile=$(cat /home/yevhenii/Projects/worder/build/tests/GradleUpdateFileStampsTest/oldFile.kts)
echo "$oldFile"
echo ""
echo "*************************************************"
echo ""
grep --color=always --line-regexp --count "$STAMP_PATTERN" /home/yevhenii/Projects/worder/build/tests/GradleUpdateFileStampsTest/oldFile.kts
cat fileStampPattern.txt | wc --lines

# if [[ $file == "$STAMP_PATTERN"* ]];
#     then
#         echo "MATCHED"
#     else
#         echo "DO NOT MATCH"
#     fi
# egrep "$STAMP_PATTERN" "$WORDER_HOME/oldFile.kt"

# for file in $(find $WORDER_HOME -name '*.kt' -or -name '*.kts');
#     do
#         if egrep --quiet "$STAMP_PATTERN" "$file";
#             then
#                 echo "didn't find in $file"
#             fi
#     done

# CRTIME=$(sudo debugfs -R "stat $FILE" /dev/sda5 | grep "crtime")
# echo "build.gradle.kts was created: ${CRTIME##* -- }"
# echo "GradleUpdateFileStampsTest/build.gradle.kts was created: ${CRTIME##* -- }"
