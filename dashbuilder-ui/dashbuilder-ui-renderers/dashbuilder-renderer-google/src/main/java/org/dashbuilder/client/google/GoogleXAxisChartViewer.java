/**
 * Copyright (C) 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.google;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selectable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.events.SelectHandler;
import org.dashbuilder.model.displayer.DataDisplayerColumn;
import org.dashbuilder.model.displayer.XAxisChartDisplayer;

public abstract class GoogleXAxisChartViewer<T extends XAxisChartDisplayer> extends GoogleViewer<T> {

    protected List<String> intervalSelectedList = new ArrayList<String>();

    public DataTable createTable() {
        List<DataDisplayerColumn> displayerColumns = dataDisplayer.getColumnList();
        if (displayerColumns.size() == 1) {
            throw new IllegalArgumentException("XAxis charts require to specify at least 2 columns. The X axis plus one ore more columns for the Y axis.");
        }
        return super.createTable();
    }

    public SelectHandler createSelectHandler(final Selectable selectable) {
        return new SelectHandler() {
            public void onSelect(SelectEvent event) {
                JsArray<Selection> selections = selectable.getSelections();
                for (int i = 0; i < selections.length(); i++) {
                    Selection selection = selections.get(i);
                    int row = selection.getRow();

                    String intervalSelected = getValueString(row, 0);
                    if (!intervalSelectedList.contains(intervalSelected)) {
                        intervalSelectedList.add(intervalSelected);
                        selectGroupIntervals(googleTable.getColumnId(0), intervalSelectedList);
                    } else {
                        intervalSelectedList.remove(intervalSelected);
                        if (!intervalSelectedList.isEmpty()) {
                            selectGroupIntervals(googleTable.getColumnId(0), intervalSelectedList);
                        } else {
                            resetGroupIntervals(googleTable.getColumnId(0));
                        }
                    }
                }
            }
        };
    }
}
