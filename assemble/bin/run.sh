#!/bin/sh

###  ------------------------------- ###
###  leo-im-server launcher script   ###
###  ------------------------------- ###

cd `dirname $0`
cd ../
if [ -z "$JAVA_OPS" ]; then
  JAVA_OPS="-Dfile.encoding=utf-8 -Dio.netty.noUnsafe=true -Xms128M -Xmx128M -Xss256K"
fi
java $JAVA_OPS -Dconf.home=$(pwd)/conf/ -jar  $(pwd)/lib/leo-im-starter-1.0.jar