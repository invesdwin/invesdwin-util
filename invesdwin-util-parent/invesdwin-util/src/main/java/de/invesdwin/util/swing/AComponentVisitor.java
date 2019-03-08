package de.invesdwin.util.swing;

import java.awt.Component;
import java.awt.Container;

import javax.annotation.concurrent.Immutable;
import javax.swing.JMenu;
import javax.swing.JRootPane;

@Immutable
public abstract class AComponentVisitor {

    protected abstract void visit(Component component);

    public void visitAll(final Component rootComponent) {
        visit(rootComponent);
        if (rootComponent instanceof JRootPane) {
            final JRootPane pane = (JRootPane) rootComponent;
            visitAll(pane.getJMenuBar());
        }
        final Component[] srcComponents = getComponentsOf(rootComponent);
        if (srcComponents != null) {
            for (int i = 0; i < srcComponents.length; i++) {
                final Component srcComponent = srcComponents[i];
                if (srcComponent instanceof Container) {
                    visitAll(srcComponent);
                } else {
                    visit(srcComponent);
                }
            }
        }
    }

    public static Component[] getComponentsOf(final Component rootComponent) {
        if (rootComponent instanceof JMenu) {
            final JMenu menu = (JMenu) rootComponent;
            return menu.getMenuComponents();
        } else if (rootComponent instanceof Container) {
            final Container container = (Container) rootComponent;
            return container.getComponents();
        } else {
            return null;
        }
    }

}
