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
package org.exbin.framework.bined.search;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Search condition history model.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SearchHistoryModel implements ComboBoxModel<SearchCondition> {

    public static final int HISTORY_LIMIT = 10;
    private final List<SearchCondition> searchHistory;
    private final List<ListDataListener> listDataListeners = new ArrayList<>();
    private SearchCondition selectedItem = null;

    public SearchHistoryModel(List<SearchCondition> searchHistory) {
        this.searchHistory = searchHistory;
    }

    @Override
    public void setSelectedItem(Object selectedItem) {
        this.selectedItem = (SearchCondition) selectedItem;
    }

    @Nullable
    @Override
    public SearchCondition getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getSize() {
        return searchHistory.size();
    }

    @Nullable
    @Override
    public SearchCondition getElementAt(int index) {
        return searchHistory.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener listDataListener) {
        listDataListeners.add(listDataListener);
    }

    @Override
    public void removeListDataListener(ListDataListener listDataListener) {
        listDataListeners.remove(listDataListener);
    }

    public void addSearchCondition(SearchCondition condition) {
        if (condition.isEmpty()) {
            return;
        }

        boolean replaced = false;
        for (int i = 0; i < searchHistory.size(); i++) {
            SearchCondition searchCondition = searchHistory.get(i);
            if (searchCondition.equals(condition)) {
                if (i == 0) {
                    return;
                }

                searchHistory.remove(i);
                for (ListDataListener listDataListener : listDataListeners) {
                    listDataListener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, i, i));
                }
                replaced = true;
            }
        }
        if (searchHistory.size() == HISTORY_LIMIT && !replaced) {
            int removePosition = searchHistory.size() - 1;
            searchHistory.remove(removePosition);
            for (ListDataListener listDataListener : listDataListeners) {
                listDataListener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, removePosition, removePosition));
            }
        }

        searchHistory.add(0, new SearchCondition(condition));
        for (ListDataListener listDataListener : listDataListeners) {
            listDataListener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, 0));
        }
    }
}
