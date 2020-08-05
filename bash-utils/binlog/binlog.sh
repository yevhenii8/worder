#!/bin/bash


#**************************************************************************************************
# Allows to set up a logger upon an arbitrary binary\command.
#**************************************************************************************************


BINLOG_HOME="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
BINLOG_LOGS="$BINLOG_HOME/logs"
BINLOG_ACTIVE="$BINLOG_HOME/active-loggers.list"
BINLOG_BLANK="$BINLOG_HOME/blank.sh"
BINARY_PATH=$(type -P "$2")
BINARY=$(readlink -f "$BINARY_PATH")


function putLogger() {
    binary=$1

    if grep -q "$binary" "$BINLOG_ACTIVE" "$binary"; then
        echo "There is already a logger for this binary: $binary"
        echo "Please check $BINLOG_LOGS"
        return 1
    fi

    echo "$binary" >>"$BINLOG_ACTIVE"
    mv "$binary" "${binary}-original"
    cp "$BINLOG_BLANK" "$binary"
    sed -i "s|LOG_FILE_LOCATION=.*|LOG_FILE_LOCATION=\"$BINLOG_LOGS\"|" "$binary"
    echo "Logger has been added!"
    echo "Associated log file can be found in $BINLOG_LOGS"
}

function removeLogger() {
    binary=$1

    if ! grep -q "$binary" "$BINLOG_ACTIVE"; then
        echo "There's no logger for this binary: $binary"
        echo "Please type 'binlog list' to check active loggers!"
        return 1
    fi

    rm "$binary"
    mv "${binary}-original" "$binary"
    sed -i "/${binary////\\/}/d" "$BINLOG_ACTIVE"
    echo "Logger $binary has been removed!"
}

function reset() {
    while read -r binary; do
        removeLogger "$binary"
    done <"$BINLOG_ACTIVE"

    rm "$BINLOG_LOGS/"* 2&>/dev/null
}

function help() {
    echo "binlog - Allows to set up a logger upon an arbitrary binary\command."
    echo "It stores all the logs in $BINLOG_LOGS"
    echo "Use pattern: binlog [command] (path/to/binary | command). Available commands: "
    echo "      put"
    echo "      remove"
    echo "      list"
    echo "      reset"
    echo "      help"
}


case "$1" in
    put) # put a logger on the binary
        putLogger "$BINARY"
        ;;

    remove) # remove the logger from the binary
        removeLogger "$BINARY"
        ;;

    list) # show all active loggers
        cat "$BINLOG_ACTIVE"
        ;;

    reset) # turns off all active loggers
        reset
        ;;

    help) #shows simple help page how to use
        help
        ;;

    *) # unknown operation
        echo "Unknown command '$1' occurred!"
        echo "Type 'binlog help' to get quick guide."
        return 1
        ;;
esac
