package org.vafer.jmx.munin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.vafer.jmx.Enums;
import org.vafer.jmx.Filter;
import org.vafer.jmx.ListOutput;
import org.vafer.jmx.NoFilter;
import org.vafer.jmx.Query;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public final class Munin {

    @Parameter(description = "")
    private List<String> args = new ArrayList<String>();
    
    @Parameter(names = "-query", description = "query expression")
    private String query;

    @Parameter(names = "-attributes", description = "file listing the attributes")
    private String attributesPath;

    @Parameter(names = "-config", description = "file with the munin config")
    private String configPath;

    @Parameter(names = "-enums", description = "file string to enum config")
    private String enumsPath;

    @Parameter(names = "-url", description = "jmx url", required = true)
    private String url;
    
    private void print(String filename) throws IOException {
        final FileInputStream input = new FileInputStream(filename);
        final byte[] buffer = new byte[1024];
        int n;
        while (-1 != (n = input.read(buffer))) {
            System.out.write(buffer, 0, n);
        }        
        input.close();
    }
    
    private void run() throws Exception {
        final Filter filter;
        if (attributesPath != null) {
            filter = new NoFilter();
        } else {
            filter = new NoFilter();
        }

        final Enums enums = new Enums();
        if (enumsPath != null) {
            enums.load(enumsPath);
        }
        
        final String cmd = args.toString().toLowerCase(Locale.US);
        if ("[config]".equals(cmd)) {
            if (configPath != null) {
                print(configPath);
            }
        } else if ("[autoconf]".equals(cmd)) {
            System.out.println("yes");
        } else if ("[list]".equals(cmd)) {
            new Query().run(url, query, filter, new ListOutput());
        } else {
            new Query().run(url, query, filter, new MuninOutput(enums));
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
