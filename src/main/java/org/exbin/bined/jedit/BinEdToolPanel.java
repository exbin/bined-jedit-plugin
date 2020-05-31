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
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

    public static final String OPTION_PREFIX = "options.bined.";

    private final BinEdEditPanel editPanel;
    private JLabel label;

    public BinEdToolPanel(BinEdEditPanel editPanel) {
        this.editPanel = editPanel;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        Box labelBox = new Box(BoxLayout.Y_AXIS);
        labelBox.add(Box.createGlue());

        label = new JLabel(editPanel.getFileName());
        //label.setVisible(jEdit.getProperty(OPTION_PREFIX + "show-filepath").equals("true"));

        labelBox.add(label);
        labelBox.add(Box.createGlue());

        add(labelBox);

        add(Box.createGlue());

        add(makeCustomButton("quicknotepad.choose-file", (ActionEvent evt) -> {
            editPanel.chooseFile();
        }));
        add(makeCustomButton("quicknotepad.save-file", (ActionEvent evt) -> {
            editPanel.saveFile();
        }));
        add(makeCustomButton("quicknotepad.copy-to-buffer", (ActionEvent evt) -> {
            editPanel.copyToBuffer();
        }));
    }

    void propertiesChanged() {
        label.setText(editPanel.getFileName());
        label.setVisible(jEdit.getProperty(OPTION_PREFIX + "show-filepath").equals("true"));
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
