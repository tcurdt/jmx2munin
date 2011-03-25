package org.vafer.jmx;

public interface Filter {

    public boolean include(String bean, String attribute);

}
