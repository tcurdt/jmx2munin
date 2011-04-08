package org.vafer.jmx.munin;

import java.util.HashSet;
import java.util.List;

import javax.management.ObjectName;

import org.vafer.jmx.Filter;

public final class MuninAttributesFilter implements Filter {

    private final HashSet<String> attributes;

    public MuninAttributesFilter(List<String> pAttributes) {
        attributes = new HashSet<String>(pAttributes);
    }

    public boolean include(ObjectName bean, String attribute) {
        return attributes.contains(MuninOutput.attributeName(bean, attribute));
    }

}
