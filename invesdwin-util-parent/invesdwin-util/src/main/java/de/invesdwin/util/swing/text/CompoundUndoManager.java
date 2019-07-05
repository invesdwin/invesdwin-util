package de.invesdwin.util.swing.text;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.Timer;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

@NotThreadSafe
public class CompoundUndoManager extends UndoManager {

    private final int delay;
    private CompoundEdit cache;
    private Timer timer;

    public CompoundUndoManager() {
        this(500);
    }

    public CompoundUndoManager(final int delay) {
        this.delay = delay;
    }

    /**
     * Checks if there are pending edits in the DelayUndoManager.
     *
     * @return true if there are pending edits. Otherwise false.
     */
    public boolean isCacheEmpty() {
        return cache == null;
    }

    /**
     * Commits the cached edit.
     */
    public synchronized void commitCache() {
        if (cache != null) {
            cache.end();
            addEditWithoutCaching();
            cache = null;
        }
    }

    /**
     * Calls super.addEdit without caching.
     */
    public void addEditWithoutCaching() {
        CompoundUndoManager.super.addEdit(cache);
    }

    public synchronized void discardCache() {
        cache = null;
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    @Override
    public synchronized boolean addEdit(final UndoableEdit anEdit) {
        if (cache == null) {
            cache = new CompoundEdit();
            final boolean ret = cache.addEdit(anEdit);
            if (ret) {
                timer = new Timer(delay, new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        commitCache();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
            return ret;
        } else {
            if (timer != null) {
                timer.restart();
            }
            return cache.addEdit(anEdit);
        }
    }

    /**
     * Override to commit the cache before checking undo status.
     *
     * @return true if an undo operation would be successful now, false otherwise
     */
    @Override
    public synchronized boolean canUndo() {
        return cache != null || super.canUndo();
    }

    /**
     * Override to commit the cache before checking redo status.
     *
     * @return true if an redo operation would be successful now, false otherwise
     */
    @Override
    public synchronized boolean canRedo() {
        return super.canRedo();
    }

    /**
     * Override to commit the cache before undo.
     *
     * @throws CannotUndoException
     */
    @Override
    public synchronized void undo() throws CannotUndoException {
        commitCache();
        super.undo();
    }

    /**
     * Override to commit the cache before redo.
     *
     * @throws CannotRedoException
     */
    @Override
    public synchronized void redo() throws CannotRedoException {
        commitCache();
        super.redo();
    }

    @Override
    public synchronized void discardAllEdits() {
        super.discardAllEdits();
        discardCache();
    }
}
