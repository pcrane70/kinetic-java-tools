#!/usr/bin/env bash

BASE_DIR=`dirname "$0"`/..
BASE_DIR=`cd "$BASE_DIR"; pwd`
ROOT_DIR=`cd "$BASE_DIR/.."; pwd`

THREADS_NUM=10

if [ $# != 1 ]; then
    echo "Parameters error for run_ycsb.sh"
    echo "Usage: sh run_ycsb.sh [threads_number]"
    exit 1
else
    THREADS_NUM=$1
fi

$ROOT_DIR/kinetic-performance/bin/ycsb-kinetic.sh -P $BASE_DIR/bin/workloadkinetic -threads $THREADS_NUM
