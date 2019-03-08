package de.invesdwin.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;
import javax.swing.JRootPane;

import com.google.common.collect.ImmutableList;

@Immutable
public abstract class AComponentFinder {

    public Component find(final Component rootComponent) {
        final Set<Component> targetComponents = new LinkedHashSet<Component>();
        findComponents(rootComponent, targetComponents, true);
        if (targetComponents.size() > 0) {
            return targetComponents.iterator().next();
        } else {
            return null;
        }
    }

    public List<Component> findAll(final Component rootComponent) {
        final Set<Component> targetComponents = new LinkedHashSet<Component>();
        findComponents(rootComponent, targetComponents, false);
        return ImmutableList.copyOf(targetComponents);
    }

    protected abstract boolean matches(Component component);

    private void findComponents(final Component rootComponent, final Set<Component> targetComponents,
            final boolean onlyOne) {
        if (shouldIgnoreTree(rootComponent)) {
            return;
        }
        checkRoot(rootComponent, targetComponents, onlyOne);
        if (onlyOne && targetComponents.size() > 0) {
            return;
        }
        checkComponentsOfRoot(rootComponent, targetComponents, onlyOne);
    }

    protected boolean shouldIgnoreTree(final Component rootComponent) {
        return false;
    }

    private void checkRoot(final Component rootComponent, final Set<Component> targetComponents,
            final boolean onlyOne) {
        if (matches(rootComponent)) {
            targetComponents.add(rootComponent);
            if (onlyOne) {
                return;
            }
        }
        if (rootComponent instanceof JRootPane) {
            final JRootPane pane = (JRootPane) rootComponent;
            findComponents(pane.getJMenuBar(), targetComponents, onlyOne);
        }
    }

    private void checkComponentsOfRoot(final Component rootComponent, final Set<Component> targetComponents,
            final boolean onlyOne) {
        final Component[] srcComponents = AComponentVisitor.getComponentsOf(rootComponent);
        if (srcComponents != null) {
            for (int i = 0; i < srcComponents.length; i++) {
                final Component srcComponent = srcComponents[i];
                if (srcComponent instanceof Container) {
                    findComponents(srcComponent, targetComponents, onlyOne);
                    if (onlyOne && targetComponents.size() > 0) {
                        return;
                    }
                } else if (matches(srcComponent)) {
                    targetComponents.add(srcComponent);
                    if (onlyOne) {
                        return;
                    }
                }
            }
        }
    }

}
