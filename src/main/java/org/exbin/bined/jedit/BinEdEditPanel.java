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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import javax.annotation.Nullable;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.exbin.bined.jedit.gui.BinEdComponentPanel;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.DockableWindowManager;

/**
 * BinEd edit panel class.
 *
 * @version 0.2.0 2020/06/01
 * @author ExBin Project (http://exbin.org)
 */
public class BinEdEditPanel extends JPanel implements EBComponent, BinEdActions, DefaultFocusComponent {

    @Nullable
    private String fileName;
    private View view;
    private boolean floating;

    private BinEdToolPanel toolPanel;
    private final BinEdFile editorFile;

    public BinEdEditPanel(View view, String position) {
        this.view = view;
        super.setLayout(new BorderLayout());
        this.floating = position.equals(DockableWindowManager.FLOATING);

        editorFile = new BinEdFile();

        this.toolPanel = new BinEdToolPanel(this);
        super.add(BorderLayout.NORTH, this.toolPanel);

        if (floating) {
            super.setPreferredSize(new Dimension(500, 250));
        }

        super.add(BorderLayout.CENTER, editorFile.getPanel());
    }

    @Override
    public void handleMessage(EBMessage ebm) {

    }

    @Override
    public void chooseFile() {
        String[] paths = GUIUtilities.showVFSFileDialog(view, null, JFileChooser.OPEN_DIALOG, false);

        if (paths != null && !paths[0].equals(fileName)) {
            editorFile.releaseFile();
            fileName = paths[0];
            toolPanel.propertiesChanged();
            editorFile.openFile(new File(fileName));
        }
    }

    @Override
    public void saveFile() {
        editorFile.saveFile();
    }

    @Override
    public void copyToBuffer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void focusOnDefaultComponent() {
        editorFile.requestFocus();
    }

    public String getFileName() {
        return fileName;
    }

    public BinEdComponentPanel getComponentPanel() {
        return (BinEdComponentPanel) editorFile.getPanel();
    }
}
