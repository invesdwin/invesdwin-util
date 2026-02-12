package de.invesdwin.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import com.google.common.collect.ImmutableList;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.swing.button.JSplitButton;

@Immutable
public abstract class AComponentFinder {

    public static final AComponentFinder DEFAULT_FOCUS = new AComponentFinder() {
        @Override
        public boolean matches(final Component component) {
            if (component instanceof JComponent
                    && (component instanceof JTextComponent || component instanceof JToggleButton)) {
                final JComponent cComponent = (JComponent) component;
                if (cComponent.isFocusable()) {
                    return true;
                }
            }
            return false;
        }
    };

    public Component find(final Component rootComponent) {
        final Set<Component> targetComponents = ILockCollectionFactory.getInstance(false).newLinkedSet();
        findComponents(rootComponent, targetComponents, true);
        if (targetComponents.size() > 0) {
            return targetComponents.iterator().next();
        } else {
            return null;
        }
    }

    public List<Component> findAll(final Component rootComponent) {
        final Set<Component> targetComponents = ILockCollectionFactory.getInstance(false).newLinkedSet();
        findComponents(rootComponent, targetComponents, false);
        return ImmutableList.copyOf(targetComponents);
    }

    public abstract boolean matches(Component component);

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
        if (rootComponent instanceof JSplitButton) {
            final JSplitButton component = (JSplitButton) rootComponent;
            final JPopupMenu popupMenu = component.getPopupMenu();
            findComponents(popupMenu, targetComponents, onlyOne);
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
