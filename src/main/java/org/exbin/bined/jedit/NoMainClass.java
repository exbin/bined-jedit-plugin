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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Class for warning about plugin use only.
 *
 * @version 0.2.0 2020/06/01
 * @author ExBin Project (http://exbin.org)
 */
public class NoMainClass {
    
    public static void main(String[] args) {
        String message = "This is plugin for jedit and cannot be executed as a standalone application. See. plugins.jedit.org";
        System.err.println(message);
        JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
