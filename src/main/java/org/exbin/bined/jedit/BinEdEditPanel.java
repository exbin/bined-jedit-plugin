/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.bined.jedit.gui.BinEdComponentPanel;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.DockableWindowManager;

/**
 * BinEd edit panel class.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdEditPanel extends JPanel implements EBComponent, BinEdActions, DefaultFocusComponent {

    @Nullable
    private File file;
    private View view;
    private boolean floating;

    private BinEdToolPanel toolPanel;
    private final BinEdFile editorFile;

    public BinEdEditPanel(View view, String position) {
        this.view = view;
        super.setLayout(new BorderLayout());
        this.floating = position.equals(DockableWindowManager.FLOATING);

        editorFile = new BinEdFile();
        editorFile.setView(view);

        BinEdComponentPanel editorFilePanel = editorFile.getPanel();

        this.toolPanel = new BinEdToolPanel(this, editorFilePanel.createOptionsAction());
        super.add(BorderLayout.NORTH, this.toolPanel);

        if (floating) {
            super.setPreferredSize(new Dimension(500, 250));
        }

        super.add(BorderLayout.CENTER, editorFilePanel);
        editorFile.newFile();
    }

    @Override
    public void handleMessage(EBMessage ebm) {

    }

    @Override
    public void chooseFile() {
        String[] paths = GUIUtilities.showVFSFileDialog(view, file == null ? null : file.getParent(), VFSBrowser.OPEN_DIALOG, false);

        if (paths != null && paths.length > 0) {
            editorFile.releaseFile();
            file = new File(paths[0]);
            toolPanel.propertiesChanged();
            editorFile.openFile(file);
        }
    }

    @Override
    public void saveFile() {
        String[] paths = GUIUtilities.showVFSFileDialog(view, file == null ? null : file.getAbsolutePath(), VFSBrowser.SAVE_DIALOG, false);

        if (paths != null && paths.length > 0) {
            file = new File(paths[0]);
            toolPanel.propertiesChanged();
            editorFile.saveToFile(file);
        }
    }

    @Override
    public void copyToBuffer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void focusOnDefaultComponent() {
        editorFile.requestFocus();
    }

    @Nonnull
    public String getFileName() {
        return file == null ? "" : file.getAbsolutePath();
    }

    @Nonnull
    public BinEdComponentPanel getComponentPanel() {
        return (BinEdComponentPanel) editorFile.getPanel();
    }
}
