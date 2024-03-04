#!/bin/bash

pid_val=0
get_pid() {
        s=`ps -e | grep boxmgr`
        echo $s
        if [ ${#s} == 0 ]; then
                return 0
        fi
        pid_val=`echo $s | cut -d ' ' -f1`
        return 1
}

get_pid
res=`echo $?`
i=0
while [ $res == 1 ]
do
        echo ready to kill $pid_val
	if [ $i -gt 5 ]; then
		kill -9 $pid_val
	else
		kill  $pid_val
	fi
	sleep 1s
        get_pid
        res=`echo $?`
	let i+=1
done
