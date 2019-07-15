package de.invesdwin.util.math.expression;

import de.invesdwin.util.lang.Strings;

public interface IFunctionParameterInfo {

    String getExpressionName();

    String getName();

    String getDescription();

    default String getDescriptionWithDetails() {
        final StringBuilder sb = new StringBuilder();
        final String description = getDescription();
        if (Strings.isNotBlank(description)) {
            sb.append(description);
        }
        if (sb.length() > 0) {
            sb.append(" ");
        }
        final String defaultValue = getDefaultValue();
        if (Strings.isNotBlank(defaultValue)) {
            if (isOptional()) {
                sb.append("<i>(Optional; Default=");
            } else {
                sb.append("<i>(Example=");
            }
            sb.append(defaultValue);
            sb.append(")</i>");
        } else {
            if (isOptional()) {
                sb.append("<i>(Optional)</i>");
            }
        }
        return sb.toString();
    }

    String getType();

    String getDefaultValue();

    boolean isOptional();

}
