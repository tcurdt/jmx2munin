package org.vafer.jmx;

import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXConnector;

public class Server {
    private final String url;
    private final String username;
    private final String password;
    
    public Server(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    public Map<String, String[]> getCredentials() {
        Map<String, String[]> map = new HashMap<String, String[]>();
        
        if (username != null || password != null) {            
            map.put(JMXConnector.CREDENTIALS, new String[]{username,password});
        }
        
        return map;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
}
