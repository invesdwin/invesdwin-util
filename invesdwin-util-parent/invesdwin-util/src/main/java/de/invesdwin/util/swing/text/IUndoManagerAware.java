package de.invesdwin.util.swing.text;

import javax.swing.undo.UndoManager;

/**
 * Marker interface for a text component to allow/disallow a framework to replace the undo manager.
 */
public interface IUndoManagerAware {

    UndoManager getUndoManager();

    void replaceUndoManager(UndoManager undoManager);

    boolean isUndoManagerReplaceable();

}
