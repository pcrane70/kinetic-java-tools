#! /usr/bin/env bash

BASE_DIR=`dirname "$0"`/..
BASE_DIR=`cd "$BASE_DIR"; pwd`
DEBUG=false

JAVA=""
if [ "$JAVA_HOME" != "" ]; then
    JAVA=$JAVA_HOME/bin/java
else
   echo "JAVA_HOME must be set."
   exit 1
fi

#Set the classpath

if [ "$CLASSPATH" != "" ]; then
   CLASSPATH=${CLASSPATH}:$JAVA_HOME/lib/tools.jar
else
   CLASSPATH=$JAVA_HOME/lib/tools.jar
fi

for f in $BASE_DIR/target/*.jar; do
   CLASSPATH=${CLASSPATH}:$f
done

function getName() {
	for item in $@ ; do
		if [[ "$item" = "-Dkinetic.debug=true" ]]; then
			DEBUG=true;
		fi
	done
}

getName $@

if [[ "$DEBUG" = "true" ]]; then
    echo "Turn on kinetic-tools debug mode: "
    exec "$JAVA" -Dkinetic.tools.out="$BASE_DIR" -classpath "$CLASSPATH" -Dkinetic.io.in=true -Dkinetic.io.out=true com.seagate.kinetic.tools.management.cli.KineticToolCLI "${@/-Dkinetic.debug*}"
else
    exec "$JAVA" -Dkinetic.tools.out="$BASE_DIR" -Djava.util.logging.config.file="$BASE_DIR"/config/kinetic.logging.properties -classpath "$CLASSPATH" com.seagate.kinetic.tools.management.cli.KineticToolCLI "${@/-Dkinetic.debug*}"
fi


