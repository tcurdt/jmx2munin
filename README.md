# jmx2munin

The [jmx2munin](http://github.com/tcurdt/jmx2munin) project exposes JMX MBean attributes to [Munin](http://munin-monitoring.org/).
Some of it's features:

 * strictly complies to the plugin format
 * exposes composite types like Lists, Maps, Set as useful as possible
 * String values can be mapped to numbers

# How to use

This is what Munin will call. So you should test this first. Of course with your parameters. This example expose all Cassandra information to Munin.

    java -jar jmx2munin.jar \
         -url service:jmx:rmi:///jndi/rmi://localhost:8080/jmxrmi \
         -query "org.apache.cassandra.*:*" \
         -config /path/to/plugin.cfg \

The "url" parameters specifies the JMX URL, the query selects the MBean to expose and the config points to the Munin plugin configuration. Something along the lines of:

    graph_title Load average
    graph_args --base 1000 -l 0
    graph_vlabel load
    graph_scale no
    graph_category system
    load.label load
    load.warning 10
    load.critical 120
    graph_info The load average.
    load.info Average load for the five minutes.

# More advanced

Sometimes it can be useful to track String values by mapping them into an enum as they really describe states. To find this possible candidates you can call:

    java -jar jmx2munin.jar \
         -url service:jmx:rmi:///jndi/rmi://localhost:8080/jmxrmi \
         -query "org.apache.cassandra.*:*" \
         list

It should output a list of possible candidates. This can now be turned into a enum configuration file:

    [org.apache.cassandra.db.StorageService:OperationMode]
    0 = ^Normal
    1 = ^Client
    2 = ^Joining
    3 = ^Bootstrapping
    4 = ^Leaving
    5 = ^Decommissioned
    6 = ^Starting drain
    7 = ^Node is drained

Which we then provide:

    java -jar jmx2munin.jar \
         -url service:jmx:rmi:///jndi/rmi://localhost:8080/jmxrmi \
         -query "org.apache.cassandra.*:*" \
         -config /path/to/plugin.cfg \
         -enums /path/to/enums.cfg

Now matching values get replaced by their numerical representation. On the left needs to be a unique number on the right side is a regular expression. If a string cannot be matched according to the spec "U" for "undefined" will be returned.

# License

Licensed under the Apache License, Version 2.0 (the "License")
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
