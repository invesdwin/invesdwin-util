package de.invesdwin.util.swing.text;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.LayoutFocusTraversalPolicy;

/**
 * Class is used to exclude elements from the default FocusTraversalPolicy on a Component.
 * https://www.pellissier.co.za/hermien/?p=610
 *
 * @author matze-Workwork
 *
 */

@NotThreadSafe
public class ExclusionFocusTraversalPolicy extends LayoutFocusTraversalPolicy {

    private final List<Component> components = new ArrayList<>();

    public void addExcludedComponent(final Component component) {
        components.add(component);
    }

    @Override
    protected boolean accept(final Component aComponent) {
        if (components.contains(aComponent)) {
            return false;
        }
        return super.accept(aComponent);
    }
}