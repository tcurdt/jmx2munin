package org.vafer.jmx;

public final class NoFilter implements Filter {

    public boolean include(String bean, String attribute) {
        return true;
    }
}
