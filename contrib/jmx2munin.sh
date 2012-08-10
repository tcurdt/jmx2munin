#!/bin/bash
# [cassandra_nodes_in_cluster]
# env.url service:jmx:rmi:///jndi/rmi://127.0.0.1:7199/jmxrmi
# env.query org.apache.cassandra.*:*
# env.config cassandra/nodes_in_cluster
# sets the 'config' and 'query' and 'url' variables for this script

if [ -z "$MUNIN_LIBDIR" ]; then
    MUNIN_LIBDIR="`dirname $(dirname "$0")`"
fi

if [ -f "$MUNIN_LIBDIR/plugins/plugin.sh" ]; then
    . $MUNIN_LIBDIR/plugins/plugin.sh
fi

if [ "$1" = "autoconf" ]; then
    echo yes
    exit 0
fi

if [ -z "$url" ]; then
  # this is very common so make it a default
  url="service:jmx:rmi:///jndi/rmi://127.0.0.1:7199/jmxrmi"
fi

[ -z "$config" ] && config="${0#*_}"

if [ -z "$config" -o -z "$query" -o -z "$url" ]; then
  echo "Configuration needs attributes config, query and optinally url"
  exit 1
fi

JMX2MUNIN_DIR="$MUNIN_LIBDIR/plugins"
CONFIG="$JMX2MUNIN_DIR/jmx2munin.cfg/$config"

if [ "$1" = "config" ]; then
    cat "$CONFIG"
    exit 0
fi

JAR="$MUNIN_LIBDIR/jmx2munin.jar"
CACHED="${MUNIN_STATEFILE}"

if test ! -f $CACHED || test `find "$CACHED" -mmin +2`; then

    java -jar "$JAR" \
      -url "$url" \
      -query "$query" \
      $ATTRIBUTES \
      > $CACHED

    echo "cached.value `date +%s`" >> $CACHED
fi

ATTRIBUTES=`awk '/\.label/ { gsub(/\.label/,""); print $1 }' $CONFIG`

if [ -z "$ATTRIBUTES" ]; then
  echo "Could not find any *.label lines in $CONFIG"
  exit 1
fi

for ATTRIBUTE in $ATTRIBUTES; do
  grep "$ATTRIBUTE\." $CACHED
done

exit 0
