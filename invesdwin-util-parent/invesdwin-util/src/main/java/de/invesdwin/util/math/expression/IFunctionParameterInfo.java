package de.invesdwin.util.math.expression;

import java.util.ArrayList;
import java.util.List;

import de.invesdwin.util.lang.string.Strings;

public interface IFunctionParameterInfo {

    String getExpressionName();

    String getName();

    String getDescription();

    default String getExpressionNameWithDetails() {
        return isOptional() ? getExpressionName() + "?" : getExpressionName();
    }

    default String getDescriptionWithDetails() {
        final StringBuilder sb = new StringBuilder();
        final String description = getDescription();
        if (Strings.isNotBlank(description)) {
            sb.append(description);
        }
        if (sb.length() > 0) {
            sb.append(" ");
        }
        final List<String> modifiers = new ArrayList<>();
        final String defaultValue = getDefaultValue();
        if (isVarArgs()) {
            modifiers.add("VarArgs");
        }
        if (isOptional()) {
            modifiers.add("Optional");
        }
        if (Strings.isNotBlank(defaultValue)) {
            final String defaultModifier;
            if (isOptional()) {
                defaultModifier = "Default";
            } else {
                defaultModifier = "Example";
            }
            modifiers.add(defaultModifier + "=" + defaultValue);
        }
        if (!modifiers.isEmpty()) {
            sb.append("(");
            for (int i = 0; i < modifiers.size(); i++) {
                if (i != 0) {
                    sb.append("; ");
                }
                sb.append(modifiers.get(i));
            }
            sb.append(")");
        }
        return sb.toString();
    }

    String getType();

    default String getTypeWithDetails() {
        if (isVarArgs()) {
            return getType() + "...";
        } else {
            return getType();
        }
    }

    String getDefaultValue();

    boolean isOptional();

    boolean isVarArgs();

}
