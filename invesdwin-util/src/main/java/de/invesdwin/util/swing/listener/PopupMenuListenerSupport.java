package de.invesdwin.util.swing.listener;

import javax.annotation.concurrent.Immutable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

@Immutable
public class PopupMenuListenerSupport implements PopupMenuListener {

    @Override
    public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {}

    @Override
    public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {}

    @Override
    public void popupMenuCanceled(final PopupMenuEvent e) {}

}
