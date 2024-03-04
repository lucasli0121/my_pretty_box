#!/bin/bash

cd ~/boxmgr

./kill_pid.sh

sleep 1s

rm -vf ./log/*

sleep 1s


(./boxmgr &)
