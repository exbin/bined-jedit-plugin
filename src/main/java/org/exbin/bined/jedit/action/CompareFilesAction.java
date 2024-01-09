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
package org.exbin.bined.jedit.action;

import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.paged.PagedData;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.gui.CompareFilesPanel;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;

/**
 * Compare files action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CompareFilesAction implements ActionListener {

    private final ExtCodeArea codeArea;
    private View view;

    public CompareFilesAction(ExtCodeArea codeArea) {
        this.codeArea = Objects.requireNonNull(codeArea);
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final CompareFilesPanel compareFilesPanel = new CompareFilesPanel();
        ResourceBundle panelResourceBundle = compareFilesPanel.getResourceBundle();
        CloseControlPanel controlPanel = new CloseControlPanel(panelResourceBundle);
        JPanel dialogPanel = WindowUtils.createDialogPanel(compareFilesPanel, controlPanel);
        Dimension preferredSize = dialogPanel.getPreferredSize();
        dialogPanel.setPreferredSize(new Dimension(preferredSize.width, preferredSize.height + 450));
        final WindowUtils.DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, (Component) event.getSource(), panelResourceBundle.getString("dialog.title"), Dialog.ModalityType.APPLICATION_MODAL);
        controlPanel.setHandler(dialog::close);

        List<String> availableFiles = new ArrayList<>();
        availableFiles.add("Current File");
        compareFilesPanel.setControl(new CompareFilesPanel.Control() {
            @Nullable
            @Override
            public CompareFilesPanel.FileRecord openFile() {
                final File[] result = new File[1];
                String[] paths = GUIUtilities.showVFSFileDialog(view, null, JFileChooser.OPEN_DIALOG, false);

                if (paths != null && paths.length > 0) {
                    result[0] = new File(paths[0]);
                }

                if (result[0] == null) {
                    return null;
                }

                try (FileInputStream stream = new FileInputStream(result[0])) {
                    PagedData pagedData = new PagedData();
                    pagedData.loadFromStream(stream);
                    return new CompareFilesPanel.FileRecord(result[0].getAbsolutePath(), pagedData);
                } catch (IOException ex) {
                    Logger.getLogger(CompareFilesAction.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Nonnull
            @Override
            public BinaryData getFileData(int index) {
                return codeArea.getContentData();
            }
        });
        compareFilesPanel.setAvailableFiles(availableFiles);
        compareFilesPanel.setLeftIndex(1);
        dialog.showCentered((Component) event.getSource());
    }
}
