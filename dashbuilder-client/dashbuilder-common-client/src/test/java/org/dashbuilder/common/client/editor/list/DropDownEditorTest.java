/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.common.client.editor.list;

import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.event.Event;

import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DropDownEditorTest {

    @Mock
    DropDownEditor.View view;

    @Mock
    LiveSearchDropDown liveSearchDropDown;

    @Mock
    Event<ValueChangeEvent<String>> valueChangeEvent;

    DropDownEditor presenter;
    Collection<DropDownEditor.Entry> entries = new ArrayList<>();

    @Before
    public void setup() {
        presenter = new DropDownEditor(view, liveSearchDropDown, valueChangeEvent);
        entries.add(presenter.newEntry("entry1", "Entry 1"));
        entries.add(presenter.newEntry("entry2", "Entry 2"));
        presenter.setEntries(entries);
        presenter.init();
    }

    @Test
    public void testInit() {
        verify(liveSearchDropDown).setSearchEnabled(false);
        verify(liveSearchDropDown).setSearchService(any());
        assertNull(presenter.getValue());
    }

    @Test
    public void testEntries() {
        presenter.getDropDownEntries("", -1, itemList -> {
            assertEquals(itemList.size(), 2);
            assertEquals(itemList.get(0), "Entry 1");
            assertEquals(itemList.get(1), "Entry 2");
        });
    }

    @Test
    public void testSelect() {
        when(liveSearchDropDown.getSelectedItem()).thenReturn("Entry 1");
        presenter.setValue("entry2");
        presenter.onEntrySelected();

        ArgumentCaptor<ValueChangeEvent> ac =  ArgumentCaptor.forClass(ValueChangeEvent.class);
        verify(valueChangeEvent).fire(ac.capture());
        assertEquals(presenter.getValue(), "entry1");
        ValueChangeEvent event = ac.getValue();
        assertEquals(event.getValue(), "entry1");
        assertEquals(event.getOldValue(), "entry2");
    }

    @Test
    public void testSetValue() {
        presenter.setValue("entry2");
        verify(liveSearchDropDown).setSelectedItem("Entry 2");
        assertEquals(presenter.getValue(), "entry2");

        presenter.setSelectHint("- select - ");
        presenter.setValue(null);
        verify(liveSearchDropDown).setSelectedItem("- select - ");
        assertNull(presenter.getValue());

        reset(liveSearchDropDown);
        presenter.clear();
        presenter.setValue("entry2");
        presenter.setEntries(entries);
        verify(liveSearchDropDown).setSelectedItem("Entry 2");

        reset(liveSearchDropDown);
        presenter.clear();
        presenter.setEntries(entries);
        verify(liveSearchDropDown, never()).setSelectedItem(anyString());
    }

    @Test
    public void testClear() {
        presenter.setValue("entry2");
        presenter.clear();
        verify(liveSearchDropDown).clear();;
        assertNull(presenter.getValue());
    }
}
