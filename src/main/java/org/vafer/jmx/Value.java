package org.vafer.jmx;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;

public final class Value {

    public interface Listener {
        public void value(ObjectName beanName, String attributeName, String value);
        public void value(ObjectName beanName, String attributeName, Number value);
    }

    public static void flatten(ObjectName beanName, String attributeName, Object value, Listener listener) {
        if (value instanceof Number) {

            listener.value(beanName, attributeName, (Number) value);

        } else if (value instanceof String) {

            listener.value(beanName, attributeName, (String) value);

        } else if (value instanceof Set) {

            final Set set = (Set) value;
            flatten(beanName, attributeName + ".size", set.size(), listener);
            for(Object entry : set) {
                flatten(beanName, attributeName + "[" + entry + "]", 1, listener);
            }

        } else if (value instanceof List) {

            final List list = (List)value;
            listener.value(beanName, attributeName + ".size", list.size());
            for(int i = 0; i<list.size(); i++) {
                flatten(beanName, attributeName + "[" + i + "]", list.get(i), listener);
            }

        } else if (value instanceof Map) {

            final Map<?,?> map = (Map<?,?>) value;
            listener.value(beanName, attributeName + ".size", map.size());
            for(Map.Entry<?, ?> entry : map.entrySet()) {
                flatten(beanName, attributeName + "[" + entry.getKey() + "]", entry.getValue(), listener);
            }

        } else if (value instanceof CompositeData) {

            CompositeData composite = (CompositeData) value;
            CompositeType type = composite.getCompositeType();
            TreeSet<String> keysSet = new TreeSet<String>(type.keySet());
            for(String key : keysSet) {
                flatten(beanName, attributeName + "[" + key + "]", composite.get(key), listener);
            }

        } else {
            // System.err.println("Failed to convert " + beanName + "." + attributeName);
        }
    }
}
