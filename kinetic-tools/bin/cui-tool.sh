#! /usr/bin/env bash

BASE_DIR=`dirname "$0"`/..
BASE_DIR=`cd "$BASE_DIR"; pwd`
PYTHON_SRC_DIR="$BASE_DIR/src/main/python"
JAVA_TOOLS_BASE_DIR="$BASE_DIR/.."
JAVA_TOOLS_BASE_DIR=`cd "$JAVA_TOOLS_BASE_DIR"; pwd`
JAVA_CLIENT_BASE_DIR="$JAVA_TOOLS_BASE_DIR/../kinetic-java"
JAVA_CLIENT_BASE_DIR=`cd "$JAVA_CLIENT_BASE_DIR"; pwd`
#echo "BASE_DIR=$BASE_DIR"
#echo "PYTHON_SRC_DIR=$PYTHON_SRC_DIR"
#echo "JAVA_TOOLS_BASE_DIR=$JAVA_TOOLS_BASE_DIR"
#echo "JAVA_CLIENT_BASE_DIR=$JAVA_CLIENT_BASE_DIR"
cd "$PYTHON_SRC_DIR"
python cui-tool.py $JAVA_TOOLS_BASE_DIR $JAVA_CLIENT_BASE_DIR


#exec "$JAVA" -classpath "$CLASSPATH" -Dkinetic.io.in=true -Dkinetic.io.out=true com.seagate.kinetic.tools.management.cli.KineticToolCLI "$@"

