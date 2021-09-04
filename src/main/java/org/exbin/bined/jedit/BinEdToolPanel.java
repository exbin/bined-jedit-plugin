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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import org.exbin.bined.CodeType;
import org.exbin.bined.jedit.gui.BinEdComponentPanel;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.operation.undo.BinaryDataUndoUpdateListener;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.gui.action.gui.DropDownButton;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.jEdit;

/**
 * BinEd plugin tool panel.
 *
 * @version 0.2.0 2021/09/03
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdToolPanel extends JPanel {

    private final BinEdEditPanel editPanel;
    private final BinEdComponentPanel componentPanel;
    private JLabel label;

    private final AbstractAction showUnprintablesAction;
    private AbstractButton showUnprintablesButton;

    private final AbstractAction optionsAction;

    private final AbstractAction cycleCodeTypesAction;
    private final JRadioButtonMenuItem binaryCodeTypeAction;
    private final JRadioButtonMenuItem octalCodeTypeAction;
    private final JRadioButtonMenuItem decimalCodeTypeAction;
    private final JRadioButtonMenuItem hexadecimalCodeTypeAction;
    private final ButtonGroup codeTypeButtonGroup;
    private DropDownButton codeTypeDropDown;

    private AbstractButton undoButton;
    private AbstractButton redoButton;
    private AbstractButton cutButton;
    private AbstractButton copyButton;
    private AbstractButton pasteButton;
    private AbstractButton saveButton;

    public BinEdToolPanel(BinEdEditPanel editPanel) {
        this.editPanel = editPanel;
        componentPanel = editPanel.getComponentPanel();
        super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        codeTypeButtonGroup = new ButtonGroup();
        binaryCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Binary") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtCodeArea codeArea = componentPanel.getCodeArea();
                codeArea.setCodeType(CodeType.BINARY);
                updateCycleButtonState();
            }
        });
        codeTypeButtonGroup.add(binaryCodeTypeAction);
        octalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Octal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtCodeArea codeArea = componentPanel.getCodeArea();
                codeArea.setCodeType(CodeType.OCTAL);
                updateCycleButtonState();
            }
        });
        codeTypeButtonGroup.add(octalCodeTypeAction);
        decimalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Decimal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtCodeArea codeArea = componentPanel.getCodeArea();
                codeArea.setCodeType(CodeType.DECIMAL);
                updateCycleButtonState();
            }
        });
        codeTypeButtonGroup.add(decimalCodeTypeAction);
        hexadecimalCodeTypeAction = new JRadioButtonMenuItem(new AbstractAction("Hexadecimal") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtCodeArea codeArea = componentPanel.getCodeArea();
                codeArea.setCodeType(CodeType.HEXADECIMAL);
                updateCycleButtonState();
            }
        });
        codeTypeButtonGroup.add(hexadecimalCodeTypeAction);
        cycleCodeTypesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtCodeArea codeArea = componentPanel.getCodeArea();
                int codeTypePos = codeArea.getCodeType().ordinal();
                CodeType[] values = CodeType.values();
                CodeType next = codeTypePos + 1 >= values.length ? values[0] : values[codeTypePos + 1];
                codeArea.setCodeType(next);
                updateCycleButtonState();
            }
        };

        cycleCodeTypesAction.putValue(Action.SHORT_DESCRIPTION, "Cycle thru code types");
        JPopupMenu cycleCodeTypesPopupMenu = new JPopupMenu();
        cycleCodeTypesPopupMenu.add(binaryCodeTypeAction);
        cycleCodeTypesPopupMenu.add(octalCodeTypeAction);
        cycleCodeTypesPopupMenu.add(decimalCodeTypeAction);
        cycleCodeTypesPopupMenu.add(hexadecimalCodeTypeAction);
        codeTypeDropDown = new DropDownButton(cycleCodeTypesAction, cycleCodeTypesPopupMenu);
        updateCycleButtonState();

        optionsAction = componentPanel.createOptionsAction();

        showUnprintablesAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUnprintablesToggleButtonActionPerformed(e);
            }
        };

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
        saveButton = makeCustomButton("bined.save-file", (ActionEvent evt) -> {
            editPanel.saveFile();
        });
        saveButton.setEnabled(false);
        add(saveButton);
        AbstractButton copyToBufferButton = makeCustomButton("bined.copy-to-buffer", (ActionEvent evt) -> {
            editPanel.copyToBuffer();
        });
        copyToBufferButton.setEnabled(false);
        add(copyToBufferButton);

        add(new JToolBar.Separator());

        undoButton = makeCustomButton("bined.undo", (ActionEvent evt) -> {
            try {
                componentPanel.getUndoHandler().performUndo();
            } catch (BinaryDataOperationException ex) {
                Logger.getLogger(BinEdToolPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        undoButton.setEnabled(false);
        add(undoButton);

        redoButton = makeCustomButton("bined.redo", (ActionEvent evt) -> {
            try {
                componentPanel.getUndoHandler().performRedo();
            } catch (BinaryDataOperationException ex) {
                Logger.getLogger(BinEdToolPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        redoButton.setEnabled(false);
        add(redoButton);
        componentPanel.getUndoHandler().addUndoUpdateListener(new BinaryDataUndoUpdateListener() {
            @Override
            public void undoCommandPositionChanged() {
                saveButton.setEnabled(componentPanel.isModified());
                undoButton.setEnabled(componentPanel.getUndoHandler().canUndo());
                redoButton.setEnabled(componentPanel.getUndoHandler().canRedo());
            }

            @Override
            public void undoCommandAdded(BinaryDataCommand bdc) {
                saveButton.setEnabled(componentPanel.isModified());
                undoButton.setEnabled(componentPanel.getUndoHandler().canUndo());
                redoButton.setEnabled(componentPanel.getUndoHandler().canRedo());
            }
        });

        add(new JToolBar.Separator());

        cutButton = makeCustomButton("bined.cut", (ActionEvent evt) -> {
            componentPanel.getCodeArea().cut();
        });
        cutButton.setEnabled(false);
        add(cutButton);

        copyButton = makeCustomButton("bined.copy", (ActionEvent evt) -> {
            componentPanel.getCodeArea().copy();
        });
        copyButton.setEnabled(false);
        add(copyButton);

        pasteButton = makeCustomButton("bined.paste", (ActionEvent evt) -> {
            componentPanel.getCodeArea().paste();
        });
        pasteButton.setEnabled(false);
        add(pasteButton);

        add(new JToolBar.Separator());

        showUnprintablesButton = makeCustomButton("bined.show-unprintables", showUnprintablesAction);
        showUnprintablesButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource(jEdit.getProperty("bined.show-unprintables.selectedIcon"))));
        showUnprintablesButton.setSelected(true);
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        codeArea.setShowUnprintables(true);
        add(showUnprintablesButton);

        add(codeTypeDropDown);

        add(makeCustomButton("bined.options", optionsAction));
    }

    private void updateCycleButtonState() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        CodeType codeType = codeArea.getCodeType();
        codeTypeDropDown.setActionText(codeType.name().substring(0, 3));
        switch (codeType) {
            case BINARY: {
                if (!binaryCodeTypeAction.isSelected()) {
                    binaryCodeTypeAction.setSelected(true);
                }
                break;
            }
            case OCTAL: {
                if (!octalCodeTypeAction.isSelected()) {
                    octalCodeTypeAction.setSelected(true);
                }
                break;
            }
            case DECIMAL: {
                if (!decimalCodeTypeAction.isSelected()) {
                    decimalCodeTypeAction.setSelected(true);
                }
                break;
            }
            case HEXADECIMAL: {
                if (!hexadecimalCodeTypeAction.isSelected()) {
                    hexadecimalCodeTypeAction.setSelected(true);
                }
                break;
            }
        }
    }

    void propertiesChanged() {
        label.setText(editPanel.getFileName());
//        label.setVisible(jEdit.getProperty(OPTION_PREFIX + "show-filepath").equals("true"));
    }

    private void showUnprintablesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        boolean selected = showUnprintablesButton.isSelected();
        showUnprintablesButton.setSelected(!selected);
        codeArea.setShowUnprintables(!selected);
    }

    @Nonnull
    private AbstractButton makeCustomButton(String name, ActionListener listener) {
        String toolTip = jEdit.getProperty(name.concat(".label"));
        String iconPath = jEdit.getProperty(name + ".icon");
        AbstractButton button = new RolloverButton(iconPath.startsWith("/") ? new javax.swing.ImageIcon(getClass().getResource(iconPath)) : GUIUtilities.loadIcon(iconPath));
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
