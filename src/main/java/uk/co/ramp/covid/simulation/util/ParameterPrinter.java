package uk.co.ramp.covid.simulation.util;

import java.lang.reflect.Field;

public class ParameterPrinter {
    public static void appendDoc(StringBuilder bld, Object o) {
        for (Field f : o.getClass().getFields()) {
            if (f.isAnnotationPresent(ParamDoc.class)) {
                bld.append(f.getName() + " | "
                        + f.getAnnotation(ParamDoc.class).value() + " | "
                        + f.getType().getTypeName());
                bld.append("\n");
            }
        }
    }

}
