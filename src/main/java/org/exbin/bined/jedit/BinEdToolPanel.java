/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.bined.jedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.exbin.bined.jedit.gui.BinEdComponentPanel;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.jEdit;

/**
 * BinEd plugin tool panel.
 *
 * @version 0.2.0 2020/06/01
 * @author ExBin Project (http://exbin.org)
 */
public class BinEdToolPanel extends JPanel {

    private final BinEdEditPanel editPanel;
    private final BinEdComponentPanel componentPanel;
    private JLabel label;

    public BinEdToolPanel(BinEdEditPanel editPanel) {
        this.editPanel = editPanel;
        componentPanel = editPanel.getComponentPanel();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        initComponents();
    }

    private void initComponents() {
        Box labelBox = new Box(BoxLayout.Y_AXIS);
        labelBox.add(Box.createGlue());

        label = new JLabel(editPanel.getFileName());
        //label.setVisible(jEdit.getProperty(OPTION_PREFIX + "show-filepath").equals("true"));

        labelBox.add(label);
        labelBox.add(Box.createGlue());

        add(labelBox);

        add(Box.createGlue());

        add(makeCustomButton("bined.choose-file", (ActionEvent evt) -> {
            editPanel.chooseFile();
        }));
        add(makeCustomButton("bined.save-file", (ActionEvent evt) -> {
            editPanel.saveFile();
        }));
        add(makeCustomButton("bined.copy-to-buffer", (ActionEvent evt) -> {
            editPanel.copyToBuffer();
        }));

        add(new JToolBar.Separator());

        add(makeCustomButton("bined.undo", (ActionEvent evt) -> {
            try {
                componentPanel.getUndoHandler().performUndo();
            } catch (BinaryDataOperationException ex) {
                Logger.getLogger(BinEdToolPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }));

        add(makeCustomButton("bined.redo", (ActionEvent evt) -> {
            try {
                componentPanel.getUndoHandler().performRedo();
            } catch (BinaryDataOperationException ex) {
                Logger.getLogger(BinEdToolPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }));

        add(new JToolBar.Separator());

        add(makeCustomButton("bined.cut", (ActionEvent evt) -> {
            componentPanel.getCodeArea().cut();
        }));

        add(makeCustomButton("bined.copy", (ActionEvent evt) -> {
            componentPanel.getCodeArea().copy();
        }));

        add(makeCustomButton("bined.paste", (ActionEvent evt) -> {
            componentPanel.getCodeArea().paste();
        }));
    }

    void propertiesChanged() {
        label.setText(editPanel.getFileName());
//        label.setVisible(jEdit.getProperty(OPTION_PREFIX + "show-filepath").equals("true"));
    }

    private AbstractButton makeCustomButton(String name, ActionListener listener) {
        String toolTip = jEdit.getProperty(name.concat(".label"));
        AbstractButton button = new RolloverButton(GUIUtilities.loadIcon(jEdit.getProperty(name + ".icon")));
        if (listener != null) {
            button.addActionListener(listener);
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
        button.setToolTipText(toolTip);
        return button;
    }

}
