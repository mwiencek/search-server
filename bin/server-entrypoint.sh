#!/bin/bash

WATCH_FILE=${@:1:1}
PROG=${@:2:1}
CURRENT_TS=0

while [ 1 ]
do
    rsync rsync://search@$RSYNC_SERVER:/data/$INDEXES_VERSION/ --list-only > /tmp/data-list
    if [ $? != "0" ]
    then
	echo "== Failed to get list of data sets. sleeping"
	sleep 5
	continue
    fi
    TS=`cat /tmp/data-list | colrm 1 46 | grep -i . | sort -r | head -1`
    rm /tmp/data-list

    if [ "$CURRENT_TS" == "$TS" ]
    then
        sleep 10
        continue
    fi

    # Sync over the indexes
    mkdir -p $SEARCH_HOME/data/$TS
    rsync -rv rsync://search@$RSYNC_SERVER:/data/$INDEXES_VERSION/$TS $SEARCH_HOME/data
    if [ $? != "0" ]
    then
	echo "== Failure during sync of dataset. Starting over again."
	sleep 5
	continue
    fi

    # remove the old data set and move the current one into place
    rm -rf $SEARCH_HOME/data/current
    mv $SEARCH_HOME/data/$TS $SEARCH_HOME/data/current
    ln -fs $SEARCH_HOME/data/current $SEARCH_HOME/indexdata

    CURRENT_TS=$TS

    # this has got to be my favorite command EVAR
    echo "Kill the search server... \ø/"
    killall -9 java
  
    # Start the search server again
    cd $JETTY_HOME
    echo $PROG "${@:3}" 
    $PROG "${@:3}" &

    # take a well deserved break!
    sleep 3

done   

#    rm -f "$WATCH_FILE"
#    while [ ! -e "$WATCH_FILE" ]; do
#        sleep 1
#    done
#    rm -f "$WATCH_FILE"
#    