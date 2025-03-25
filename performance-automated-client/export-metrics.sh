#!/bin/bash

echo "Compiling and running MetricsExporter..."

# Compile the metrics exporter
mvn compile

# Run the metrics exporter
mvn exec:java -Dexec.mainClass="com.hasandag.performance.automated.MetricsExporter"

echo "Metrics export complete." 