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

import java.util.Optional;
import org.exbin.framework.api.Preferences;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.gjt.sp.jedit.jEdit;

/**
 * Wrapper for jEdit preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class JEditPreferencesWrapper implements Preferences {

    public static final String OPTION_PREFIX = "options.bined.";

    public JEditPreferencesWrapper() {
    }

    @Override
    public boolean exists(String key) {
        return jEdit.getProperty(OPTION_PREFIX + key, (String) null) != null;
    }

    @Nonnull
    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(jEdit.getProperty(OPTION_PREFIX + key, (String) null));
    }

    @Nonnull
    @Override
    public String get(String key, String def) {
        return jEdit.getProperty(OPTION_PREFIX + key, def);
    }

    @Override
    public void put(String key, @Nullable String value) {
        if (value == null) {
            jEdit.unsetProperty(OPTION_PREFIX + key);
        } else {
            jEdit.setProperty(OPTION_PREFIX + key, value);
        }
    }

    @Override
    public void remove(String key) {
        jEdit.unsetProperty(OPTION_PREFIX + key);
    }

    @Override
    public void putInt(String key, int value) {
        jEdit.setProperty(OPTION_PREFIX + key, Integer.toString(value));
    }

    @Override
    public int getInt(String key, int def) {
        return Integer.valueOf(jEdit.getProperty(OPTION_PREFIX + key, Integer.toString(def)));
    }

    @Override
    public void putLong(String key, long value) {
        jEdit.setProperty(OPTION_PREFIX + key, Long.toString(value));
    }

    @Override
    public long getLong(String key, long def) {
        return Long.valueOf(jEdit.getProperty(OPTION_PREFIX + key, Long.toString(def)));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        jEdit.setProperty(OPTION_PREFIX + key, Boolean.toString(value));
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return Boolean.valueOf(jEdit.getProperty(OPTION_PREFIX + key, Boolean.toString(def)));
    }

    @Override
    public void putFloat(String key, float value) {
        jEdit.setProperty(OPTION_PREFIX + key, Float.toString(value));
    }

    @Override
    public float getFloat(String key, float def) {
        return Float.valueOf(jEdit.getProperty(OPTION_PREFIX + key, Float.toString(def)));
    }

    @Override
    public void putDouble(String key, double value) {
        jEdit.setProperty(OPTION_PREFIX + key, Double.toString(value));
    }

    @Override
    public double getDouble(String key, double def) {
        return Double.valueOf(jEdit.getProperty(OPTION_PREFIX + key, Double.toString(def)));
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] getByteArray(String key, byte[] def) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Makes any changes permanent (stores cached changes to permanent storage).
     */
    @Override
    public void flush() {
    }

    /**
     * Forces reloading of cache from permanent storage.
     */
    @Override
    public void sync() {
    }
}
