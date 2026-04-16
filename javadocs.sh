#!/bin/bash
mvn clean
mkdir -p javadocs
mvn javadoc:javadoc
cp -aux target/reports/apidocs/* javadocs/
