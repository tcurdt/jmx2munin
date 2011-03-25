package org.vafer.jmx.munin;

import java.text.NumberFormat;

import javax.management.ObjectName;

import org.vafer.jmx.Enums;
import org.vafer.jmx.Output;
import org.vafer.jmx.Value;

public final class MuninOutput implements Output {

    private final Enums enums;

    public MuninOutput(Enums enums) {
        this.enums = enums;
    }

    private String fieldname(String s) {
        return s.replaceAll("[^A-Za-z0-9]", "_");
    }

    private String beanString(ObjectName beanName) {
        StringBuilder sb = new StringBuilder();
        sb.append(beanName.getDomain());
        sb.append('.');
        sb.append(beanName.getKeyProperty("type"));
        return sb.toString();
    }
    
    public void output(ObjectName beanName, String attributeName, Object value) {
        Value.flatten(beanName, attributeName, value, new Value.Listener() {
            public void value(ObjectName beanName, String attributeName, String value) {
                final Number v = enums.resolve(Enums.id(beanName, attributeName), value);
                if (v != null) {
                    value(beanName, attributeName, v);
                } else {
                    value(beanName, attributeName, Double.NaN);                    
                }
            }
            public void value(ObjectName beanName, String attributeName, Number value) {
                final String v;

                if (Double.isNaN(value.doubleValue())) {
                    v = "U";
                } else {
                    final NumberFormat f = NumberFormat.getInstance();
                    f.setMaximumFractionDigits(2);
                    f.setGroupingUsed(false);
                    v = f.format(value);            
                }
                
                StringBuilder sb = new StringBuilder();
                sb.append(fieldname(beanString(beanName)));
                sb.append('_');
                sb.append(fieldname(attributeName));
                sb.append(".value ");
                sb.append(v);
                System.out.println(sb);
            }
        });        
   }
}