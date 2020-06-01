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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import org.exbin.auxiliary.paged_data.BinaryData;
import org.exbin.auxiliary.paged_data.ByteArrayData;
import org.exbin.auxiliary.paged_data.EditableBinaryData;
import org.exbin.auxiliary.paged_data.PagedData;
import org.exbin.auxiliary.paged_data.delta.DeltaDocument;
import org.exbin.auxiliary.paged_data.delta.FileDataSource;
import org.exbin.auxiliary.paged_data.delta.SegmentsRepository;
import org.exbin.bined.EditationMode;
import org.exbin.bined.jedit.gui.BinEdComponentFileApi;
import org.exbin.bined.jedit.gui.BinEdComponentPanel;
import org.exbin.bined.operation.swing.CodeAreaUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.framework.bined.FileHandlingMode;

/**
 * File editor wrapper using BinEd editor component.
 *
 * @version 0.2.0 2020/06/01
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinEdFile implements BinEdComponentFileApi {

    public static final String ACTION_CLIPBOARD_CUT = "cut-to-clipboard";
    public static final String ACTION_CLIPBOARD_COPY = "copy-to-clipboard";
    public static final String ACTION_CLIPBOARD_PASTE = "paste-from-clipboard";

    private static SegmentsRepository segmentsRepository = null;

    private final BinEdComponentPanel componentPanel;

    private File file;

    public BinEdFile() {
        componentPanel = new BinEdComponentPanel();
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        CodeAreaUndoHandler undoHandler = new CodeAreaUndoHandler(codeArea);
        componentPanel.setFileApi(this);
        componentPanel.setUndoHandler(undoHandler);

        getSegmentsRepository();

        ActionMap actionMap = componentPanel.getActionMap();
        actionMap.put(ACTION_CLIPBOARD_COPY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.copy();
            }
        });
        actionMap.put(ACTION_CLIPBOARD_CUT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.cut();
            }
        });
        actionMap.put(ACTION_CLIPBOARD_PASTE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.paste();
            }
        });
    }

    public boolean isModified() {
        return componentPanel.isModified();
    }

    public boolean releaseFile() {
        return componentPanel.releaseFile();
    }

    public JPanel getPanel() {
        return componentPanel;
    }

    public static synchronized SegmentsRepository getSegmentsRepository() {
        if (segmentsRepository == null) {
            segmentsRepository = new SegmentsRepository();
        }

        return segmentsRepository;
    }

    public void openFile(File file) {
        this.file = file;
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        boolean editable = file.canWrite();
        codeArea.setEditationMode(editable ? EditationMode.EXPANDING : EditationMode.READ_ONLY);
        if (file.isFile() && file.exists()) {
            try {
                openDocument(file, editable);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            closeData();
        }
        // updateModified();
        componentPanel.getUndoHandler().clear();
    }

    public void openDocument(File file, boolean editable) throws IOException {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        FileHandlingMode fileHandlingMode = componentPanel.getFileHandlingMode();

        BinaryData oldData = codeArea.getContentData();
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            FileDataSource fileSource = segmentsRepository.openFileSource(file, editable ? FileDataSource.EditationMode.READ_WRITE : FileDataSource.EditationMode.READ_ONLY);
            DeltaDocument document = segmentsRepository.createDocument(fileSource);
            componentPanel.setContentData(document);
            if (oldData != null) {
                oldData.dispose();
            }
        } else {
            try (FileInputStream fileStream = new FileInputStream(file)) {
                BinaryData data = codeArea.getContentData();
                if (!(data instanceof PagedData)) {
                    data = new PagedData();
                    if (oldData != null) {
                        oldData.dispose();
                    }
                }
                ((EditableBinaryData) data).loadFromStream(fileStream);
                componentPanel.setContentData(data);
            }
        }
        codeArea.setEditationMode(editable ? EditationMode.EXPANDING : EditationMode.READ_ONLY);
    }

    public void openDocument(InputStream stream, boolean editable) throws IOException {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        setNewData();
        EditableBinaryData data = Objects.requireNonNull((EditableBinaryData) codeArea.getContentData());
        data.loadFromStream(stream);
        codeArea.setEditationMode(editable ? EditationMode.EXPANDING : EditationMode.READ_ONLY);
        componentPanel.setContentData(data);
    }

    public void saveFile() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryData data = codeArea.getContentData();
        if (data instanceof DeltaDocument) {
            try {
                segmentsRepository.saveDocument((DeltaDocument) data);
            } catch (IOException ex) {
                Logger.getLogger(BinEdFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            OutputStream stream;
            try {
                stream = new FileOutputStream(file);
                try {
                    BinaryData contentData = codeArea.getContentData();
                    if (contentData != null) {
                        contentData.saveToStream(stream);
                    }
                    stream.flush();
                } catch (IOException ex) {
                    Logger.getLogger(BinEdFile.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(BinEdFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void closeData() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        BinaryData data = codeArea.getContentData();
        componentPanel.setContentData(new ByteArrayData());
        if (data instanceof DeltaDocument) {
            FileDataSource fileSource = ((DeltaDocument) data).getFileSource();
            data.dispose();
            segmentsRepository.detachFileSource(fileSource);
            segmentsRepository.closeFileSource(fileSource);
        } else {
            if (data != null) {
                data.dispose();
            }
        }
    }

    @Override
    public void saveDocument() {
        if (file == null) {
            return;
        }

        saveFile();
    }

    @Override
    public void switchFileHandlingMode(FileHandlingMode newHandlingMode) {
        FileHandlingMode fileHandlingMode = componentPanel.getFileHandlingMode();
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        if (newHandlingMode != fileHandlingMode) {
            // Switch memory mode
            if (file != null) {
                // If document is connected to file, attempt to release first if modified and then simply reload
                if (isModified()) {
                    if (releaseFile()) {
                        openFile(file);
                        codeArea.clearSelection();
                        codeArea.setCaretPosition(0);
                        componentPanel.setFileHandlingMode(newHandlingMode);
                    }
                } else {
                    componentPanel.setFileHandlingMode(newHandlingMode);
                    openFile(file);
                }
            } else {
                // If document unsaved in memory, switch data in code area
                if (codeArea.getContentData() instanceof DeltaDocument) {
                    BinaryData oldData = codeArea.getContentData();
                    PagedData data = new PagedData();
                    data.insert(0, codeArea.getContentData());
                    componentPanel.setContentData(data);
                    if (oldData != null) {
                        oldData.dispose();
                    }
                } else {
                    BinaryData oldData = codeArea.getContentData();
                    DeltaDocument document = segmentsRepository.createDocument();
                    if (oldData != null) {
                        document.insert(0, oldData);
                        oldData.dispose();
                    }
                    componentPanel.setContentData(document);
                }

                componentPanel.getUndoHandler().clear();
                componentPanel.setFileHandlingMode(newHandlingMode);
            }
        }
    }

    @Override
    public boolean isSaveSupported() {
        return true;
    }

    private void setNewData() {
        ExtCodeArea codeArea = componentPanel.getCodeArea();
        FileHandlingMode fileHandlingMode = componentPanel.getFileHandlingMode();
        if (fileHandlingMode == FileHandlingMode.DELTA) {
            componentPanel.setContentData(segmentsRepository.createDocument());
        } else {
            componentPanel.setContentData(new PagedData());
        }
    }

    public void setModifiedChangeListener(BinEdComponentPanel.ModifiedStateListener modifiedChangeListener) {
        componentPanel.setModifiedChangeListener(modifiedChangeListener);
    }

    public void requestFocus() {
        componentPanel.getCodeArea().requestFocus();
    }
}
