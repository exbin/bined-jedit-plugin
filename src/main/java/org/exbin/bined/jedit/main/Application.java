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
package org.exbin.bined.jedit.main;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.bined.jedit.JEditPreferencesWrapper;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModuleRepository;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.WindowUtils;

/**
 * Application wrapper.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class Application implements XBApplication {
    
    private BinedModule binedModule = new BinedModule();
    private FrameModuleApi frameModule = new FrameModuleApi() {
        @Override
        public void createMainMenu() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void notifyFrameUpdated() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public WindowUtils.DialogWrapper createDialog() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public WindowUtils.DialogWrapper createDialog(JPanel panel) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public WindowUtils.DialogWrapper createDialog(JPanel panel, JPanel controlPanel) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public WindowUtils.DialogWrapper createDialog(Component parentComponent, Dialog.ModalityType modalityType, JPanel panel) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public WindowUtils.DialogWrapper createDialog(Component parentComponent, Dialog.ModalityType modalityType, JPanel panel, JPanel controlPanel) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Frame getFrame() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Action getExitAction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void registerExitAction() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void registerBarsVisibilityActions() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void registerToolBarVisibilityActions() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void registerStatusBarVisibilityActions() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void registerStatusBar(String moduleId, String statusBarId, JPanel panel) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void switchStatusBar(String statusBarId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void loadFramePosition() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void saveFramePosition() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setDialogTitle(WindowUtils.DialogWrapper dialog, ResourceBundle resourceBundle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public Application() {
    }

    @Nonnull
    @Override
    public Preferences getAppPreferences() {
        return new JEditPreferencesWrapper();
    }

    @Nonnull
    @Override
    public XBApplicationModuleRepository getModuleRepository() {
        return new XBApplicationModuleRepository() {
            @Override
            public Object getModuleRecordById(String moduleId) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Object getModuleById(String moduleId) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public <T> T getModuleByInterface(Class<T> interfaceClass) {
                if (interfaceClass.equals(BinedModule.class)) {
                    return (T) binedModule;
                } else if (interfaceClass.equals(FrameModuleApi.class)) {
                    return (T) frameModule;
                }
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
