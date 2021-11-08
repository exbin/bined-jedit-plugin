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
package org.exbin.framework.bined.options.gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.exbin.auxiliary.paged_data.ByteArrayEditableData;
import org.exbin.bined.EditMode;
import org.exbin.bined.RowWrappingMode;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightCodeAreaPainter;
import org.exbin.bined.highlight.swing.extended.ExtendedHighlightNonAsciiCodeAreaPainter;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;

/**
 * Preview panel for code area.
 *
 * @version 0.2.0 2021/09/21
 * @author ExBin Project (http://exbin.org)
 */
public class PreviewPanel extends javax.swing.JPanel {

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(PreviewPanel.class);

    private ExtCodeArea codeArea;
    private final PreviewType previewType;

    public PreviewPanel() {
        this(PreviewType.DEFAULT);
    }

    public PreviewPanel(PreviewType previewType) {
        this.previewType = previewType;
        initComponents();
        init();
    }

    @Nonnull
    public ExtCodeArea getCodeArea() {
        return codeArea;
    }

    private void init() {
        codeArea = new ExtCodeArea();
        initPreviewCodeArea();
        this.add(codeArea, BorderLayout.CENTER);
    }

    private void initPreviewCodeArea() {
        codeArea.setEditMode(EditMode.READ_ONLY);
        if (previewType == PreviewType.WITH_SEARCH) {
            ExtendedHighlightNonAsciiCodeAreaPainter painter = new ExtendedHighlightNonAsciiCodeAreaPainter(codeArea);
            codeArea.setPainter(painter);
            List<ExtendedHighlightCodeAreaPainter.SearchMatch> exampleMatches = new ArrayList<>();
            // Set manual search matches for "ligula"
            exampleMatches.add(new ExtendedHighlightCodeAreaPainter.SearchMatch(145, 6));
            exampleMatches.add(new ExtendedHighlightCodeAreaPainter.SearchMatch(480, 6));
            exampleMatches.add(new ExtendedHighlightCodeAreaPainter.SearchMatch(1983, 6));
            painter.setMatches(exampleMatches);
            painter.setCurrentMatchIndex(1);
        }
        ByteArrayEditableData exampleData = new ByteArrayEditableData();
        try {
            exampleData.loadFromStream(getClass().getResourceAsStream("/org/exbin/framework/bined/resources/preview/lorem.txt"));
        } catch (IOException ex) {
            Logger.getLogger(PreviewPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        codeArea.setContentData(exampleData);
        codeArea.setRowWrapping(RowWrappingMode.WRAPPING);
        codeArea.setEnabled(false);
        codeArea.setShowUnprintables(true);
        codeArea.setSelection(new SelectionRange(200, 300));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        previewLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        previewLabel.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow"));
        previewLabel.setText(resourceBundle.getString("previewLabel.text")); // NOI18N
        previewLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        previewLabel.setOpaque(true);
        add(previewLabel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new PreviewPanel());
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel previewLabel;
    // End of variables declaration//GEN-END:variables


    public enum PreviewType {
        DEFAULT,
        WITH_SEARCH
    }
}
