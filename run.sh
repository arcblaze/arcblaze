#!/bin/sh

echo "Starting Application..."

# Go to the correct directory.
cd $(dirname $0)

# This is the class that will be launched.
CLASS="com.arcblaze.arctime.server.ArcTimeServer"

# Set Java configuration options.
JAVA_OPTS=
JAVA_OPTS="$JAVA_OPTS -ea"
JAVA_OPTS="$JAVA_OPTS -Xmx256m"

# Build the classpath.
CLASSPATH="arctime/arctime-dist/target/lib/*"

# Start the app.
java $JAVA_OPTS -classpath "$CLASSPATH" $CLASS
