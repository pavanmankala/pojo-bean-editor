package org.apache.pojo.beaneditor;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.TextAction;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class PBEEditorKit extends DefaultEditorKit {
    public static final String undoEditAction = "PBE.action.undo-edit";
    public static final String redoEditAction = "PBE.action.redo-edit";

    private static final Action[] actions;
    private static final Action[] pbeActions = new Action[] { new UndoAction(), new RedoAction() };

    static {
        Map<String, Action> mergedActions = new HashMap<String, Action>();

        for (Action nativeAction : new DefaultEditorKit().getActions()) {
            mergedActions.put(nativeAction.getValue(Action.NAME).toString(), nativeAction);
        }

        for (Action libAction : pbeActions) {
            mergedActions.put(libAction.getValue(Action.NAME).toString(), libAction);
        }

        actions = mergedActions.values().toArray(new Action[0]);
    }

    @Override
    public Action[] getActions() {
        return actions;
    }

    public Action[] getPbeactions() {
        return pbeActions;
    }

    static class UndoAction extends TextAction {
        UndoAction() {
            super(undoEditAction);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof PojoBeanEditor) {
                PojoBeanEditor editor = (PojoBeanEditor) e.getSource();
                if (editor.isEnabled() && editor.isEditable()) {
                    try {
                        UndoManager mgr = editor.getUndoManager();

                        if (mgr.canUndo()) {
                            mgr.undo();
                        }
                    } catch (CannotUndoException cre) {
                        cre.printStackTrace();
                    } finally {
                        editor.requestFocusInWindow();
                    }
                }
            }
        }
    }

    static class RedoAction extends TextAction {
        RedoAction() {
            super(redoEditAction);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof PojoBeanEditor) {
                PojoBeanEditor editor = (PojoBeanEditor) e.getSource();
                if (editor.isEnabled() && editor.isEditable()) {
                    try {
                        UndoManager mgr = editor.getUndoManager();

                        if (mgr.canRedo()) {
                            mgr.redo();
                        }
                    } catch (CannotRedoException cre) {
                        cre.printStackTrace();
                    } finally {
                        editor.requestFocusInWindow();
                    }
                }
            }
        }

    }
}
