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
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;

/**
 * Anchor class for BinEd edit panel.
 *
 * @version 0.2.0 2020/06/01
 * @author ExBin Project (http://exbin.org)
 */
public class BinEdEditPanel extends JPanel implements EBComponent, BinEdActions, DefaultFocusComponent {

    private String fileName;
    private String defaultFilename;
    private View view;
    private boolean floating;

    private BinEdToolPanel toolPanel;
    private ExtCodeArea codeArea;

    public BinEdEditPanel(View view, String position) {
        this.view = view;
        setLayout(new BorderLayout());
        this.floating = position.equals(
                DockableWindowManager.FLOATING);

        this.fileName = jEdit.getProperty(
                "bined."
                + "filepath");
        if (this.fileName == null || this.fileName.length() == 0) {
            this.fileName = new String(jEdit.getSettingsDirectory()
                    + File.separator + "qn.txt");
            jEdit.setProperty("bined."
                    + "filepath", this.fileName);
        }
        this.defaultFilename = new String(this.fileName);

        this.toolPanel = new BinEdToolPanel(this);
        super.add(BorderLayout.NORTH, this.toolPanel);

        if (floating) {
            this.setPreferredSize(new Dimension(500, 250));
        }

        codeArea = new ExtCodeArea();
        codeArea.setContentData(new ByteArrayEditableData(new byte[]{0x20, 0x21, 0x22}));
        super.add(BorderLayout.CENTER, codeArea);
    }

    @Override
    public void handleMessage(EBMessage ebm) {

    }

    @Override
    public void chooseFile() {
        String[] paths = GUIUtilities.showVFSFileDialog(view, null,
                JFileChooser.OPEN_DIALOG, false);
        if (paths != null && !paths[0].equals(fileName)) {
            saveFile();
            fileName = paths[0];
            toolPanel.propertiesChanged();
            readFile();
        }
    }

    @Override
    public void saveFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyToBuffer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void focusOnDefaultComponent() {
        // TODO
    }

    String getFileName() {
        return fileName;
    }

    private void readFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
