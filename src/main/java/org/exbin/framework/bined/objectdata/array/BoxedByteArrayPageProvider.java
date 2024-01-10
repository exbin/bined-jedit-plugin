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
package org.exbin.framework.bined.objectdata.array;

import org.exbin.framework.bined.objectdata.PageProvider;
import org.exbin.framework.bined.objectdata.PageProviderBinaryData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Byte array as binary data provider.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BoxedByteArrayPageProvider implements PageProvider {

    private final Byte[] arrayRef;

    public BoxedByteArrayPageProvider(Byte[] arrayRef) {
        this.arrayRef = arrayRef;
    }

    @Nonnull
    @Override
    public byte[] getPage(long pageIndex) {
        int startPos = (int) (pageIndex * PageProviderBinaryData.PAGE_SIZE);
        int length = Math.min(arrayRef.length - startPos, PageProviderBinaryData.PAGE_SIZE);
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = arrayRef[startPos + i];
        }

        return result;
    }

    @Override
    public long getDocumentSize() {
        return arrayRef.length;
    }
}
