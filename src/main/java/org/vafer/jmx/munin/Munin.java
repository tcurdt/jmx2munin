package org.vafer.jmx.munin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.vafer.jmx.*;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public final class Munin {

    @Parameter(names = "-list", description = "show as list")
    private boolean list;

    @Parameter(names = "-url", description = "jmx url", required = true)
    private String url;
    
    @Parameter(names = "-username", description = "jmx username")
    private String username;
    
    @Parameter(names = "-password", description = "jmx password")
    private String password;

    @Parameter(names = "-query", description = "query expression", required = true)
    private List<String> queries = new ArrayList<String>();

    @Parameter(names = "-enums", description = "file string to enum config")
    private String enumsPath;

    @Parameter(names = "-attribute", description = "attributes to return")
    private List<String> attributes = new ArrayList<String>();

    private void run() throws Exception {
        final Filter filter;
        if (attributes == null || attributes.isEmpty()) {
            filter = new NoFilter();
        } else {
            filter = new MuninAttributesFilter(attributes);
        }

        final Enums enums = new Enums();
        if (enumsPath != null) {
            enums.load(enumsPath);
        }

        final Output output;
        if (list) {
            output = new ListOutput();
        } else {
            output = new MuninOutput(enums);
        }
        
        final Server connection = new Server(url, username, password);

        for(String query : queries) {
            new Query().run(connection, query, filter, output);
        }
    }

    public static void main(String[] args) throws Exception {
        Munin m = new Munin();

        JCommander cli = new JCommander(m);
        try {
            cli.parse(args);
        } catch(Exception e) {
            cli.usage();
            System.exit(1);
        }

        m.run();
    }
}
