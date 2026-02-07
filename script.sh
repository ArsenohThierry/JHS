#!/usr/bin/env bash
set -euo pipefail

# Simple helper to build the Maven project, copy dependencies and run the HttpServer
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

echo "Packaging project..."
mvn -q -DskipTests package

echo "Copying dependencies to target/dependency..."
mvn -q dependency:copy-dependencies -DoutputDirectory=target/dependency

echo "Starting HttpServer..."
java -cp "target/classes:target/dependency/*" com.jhs.http.HttpServer
