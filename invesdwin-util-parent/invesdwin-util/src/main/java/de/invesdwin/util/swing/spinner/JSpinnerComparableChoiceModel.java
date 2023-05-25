package de.invesdwin.util.swing.spinner;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.AbstractSpinnerModel;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.lang.Objects;

@NotThreadSafe
public class JSpinnerComparableChoiceModel extends AbstractSpinnerModel {

    protected boolean stateChangeEventFiring = false;
    private List<Comparable<Object>> choice;
    private Comparable<Object> selection;

    public JSpinnerComparableChoiceModel() {
        super();
    }

    public List<Comparable<Object>> getChoice() {
        return choice;
    }

    public boolean setStateChangeEventFiring(final boolean stateChangeEventFiring) {
        final boolean prevStateChangeEventFiring = this.stateChangeEventFiring;
        this.stateChangeEventFiring = stateChangeEventFiring;
        return prevStateChangeEventFiring;
    }

    public boolean isStateChangeEventFiring() {
        return stateChangeEventFiring;
    }

    public void setChoice(final List<Comparable<Object>> choice) {
        if (stateChangeEventFiring) {
            return;
        }
        final List<Comparable<Object>> prevChoice = this.choice;
        this.choice = choice;
        if (!Objects.equals(prevChoice, choice)) {
            stateChangeEventFiring = true;
            try {
                fireStateChanged();
            } finally {
                stateChangeEventFiring = false;
            }
        }
    }

    public Comparable<Object> getSelection() {
        return selection;
    }

    public void setSelection(final Comparable<Object> selection) {
        if (stateChangeEventFiring) {
            return;
        }
        if (!Objects.equals(this.selection, selection)) {
            this.selection = selection;
            stateChangeEventFiring = true;
            try {
                fireStateChanged();
            } finally {
                stateChangeEventFiring = false;
            }
        }
    }

    @Override
    public Object getValue() {
        return selection;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(final Object value) {
        setSelection((Comparable<Object>) value);
    }

    @Override
    public Object getNextValue() {
        if (choice == null || choice.isEmpty()) {
            return selection;
        }
        if (selection != null) {
            final int indexOf = choice.indexOf(selection);
            if (indexOf < 0) {
                // The date is not in the List. We look for the closest after it in the list.
                /*
                 * BinarySearch: If key is not present, the it returns "(-(insertion point) - 1)". The insertion point
                 * is defined as the point at which the key would be inserted into the list.
                 */
                final int index = Collections.binarySearch(choice, selection);
                // outOfBounds ? end of the list, we don't change the date
                final boolean outOfBounds = (Math.abs(index + 1)) >= choice.size();
                return outOfBounds ? selection : choice.get(Math.abs(index + 1));
            } else if (indexOf == choice.size() - 1) {
                //end of the list, we don't change the date
                return selection;
            } else {
                return choice.get(indexOf + 1);
            }
        }

        return choice.get(choice.size() - 1);
    }

    @Override
    public Object getPreviousValue() {
        if (choice == null || choice.isEmpty()) {
            return selection;
        }
        if (selection != null) {
            final int indexOf = choice.indexOf(selection);
            if (indexOf < 0) {
                // The date is not in the List. We look for the closest after it in the list.
                /*
                 * BinarySearch: If key is not present, the it returns "(-(insertion point) - 1)". The insertion point
                 * is defined as the point at which the key would be inserted into the list.
                 */
                final int index = Collections.binarySearch(choice, selection);
                // outOfBounds ? end of the list, we don't change the date
                final boolean outOfBounds = (Math.abs(index) - 2) < 0;
                return outOfBounds ? selection : choice.get(Math.abs(index) - 2);
            } else if (indexOf == 0) {
                //end of the list, we don't change the date
                return selection;
            } else {
                return choice.get(indexOf - 1);
            }
        }

        return choice.get(0);
    }

}
