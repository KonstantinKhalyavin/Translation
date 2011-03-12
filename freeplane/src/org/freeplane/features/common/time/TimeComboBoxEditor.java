/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.common.time;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ComboBoxEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.freeplane.core.resources.NamedObject;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.components.ContainerComboBoxEditor;
import org.freeplane.core.util.FreeplaneDate;
import org.freeplane.features.common.time.swing.JCalendar;

/**
 * @author Dimitry Polivaev
 * Mar 5, 2009
 */
public class TimeComboBoxEditor implements ComboBoxEditor {
	private class ShowCalendarAction implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			calendarPopupMenu.show(showEditorBtn, 0, showEditorBtn.getHeight());
		}
	}

	final private List<ActionListener> actionListeners;
	final private JPopupMenu calendarPopupMenu;
	final private JCalendar calenderComponent;
	private FreeplaneDate date;
	final private JButton showEditorBtn;

	public TimeComboBoxEditor(boolean timeVisible) {
		showEditorBtn = new JButton();
		showEditorBtn.addActionListener(new ShowCalendarAction());
		calenderComponent = new JCalendar(null, null, true, true, timeVisible);
		calenderComponent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				calendarPopupMenu.setVisible(false);
			}
		});
		calendarPopupMenu = calenderComponent.createPopupMenu();
		calendarPopupMenu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
			
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				updateDate();
			}
			
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		actionListeners = new LinkedList<ActionListener>();
	}

	public void addActionListener(final ActionListener l) {
		actionListeners.add(l);
	}

	public Component getEditorComponent() {
		return showEditorBtn;
	}

	public Object getItem() {
		return date;
	}

	public void removeActionListener(final ActionListener l) {
		actionListeners.remove(l);
	}

	public void selectAll() {
	}

	public void setItem(final Object date) {
		if(! (date instanceof FreeplaneDate))
			return;
		this.date = (FreeplaneDate) date;
		showEditorBtn.setText(date == null ? "" : date.toString());
	}

	private void updateDate() {
		date = new FreeplaneDate(calenderComponent.getDate(), calenderComponent.isTimeVisible() 
			? FreeplaneDate.ISO_DATE_TIME_FORMAT_PATTERN
			        : FreeplaneDate.ISO_DATE_FORMAT_PATTERN);
	    if (actionListeners.size() == 0) {
	    	return;
	    }
	    final ActionEvent actionEvent = new ActionEvent(this, 0, null);
	    for (final ActionListener l : actionListeners) {
	    	l.actionPerformed(actionEvent);
	    }
    }

	public void setItem() {
	    updateDate();
    }
	
	final static Icon dateIcon = new ImageIcon(ResourceController.getResourceController().getResource("/images/calendar_red.png"));
	final static Icon dateTimeIcon = new ImageIcon(ResourceController.getResourceController().getResource("/images/calendar_clock_red.png"));
	public static ComboBoxEditor getTextDateTimeEditor() {
	    final ContainerComboBoxEditor editor = new ContainerComboBoxEditor();
		final NamedObject keyText = new NamedObject("text", "Abc");
		final BasicComboBoxEditor textEditor = new BasicComboBoxEditor();
		editor.put(keyText, textEditor);
		
		final NamedObject keyDate = new NamedObject("date", ""); 
		keyDate.setIcon(dateIcon);
		final TimeComboBoxEditor dateComboBoxEditor = new TimeComboBoxEditor(false);
		dateComboBoxEditor.setItem();
		editor.put(keyDate, dateComboBoxEditor);

		final NamedObject keyDateTime = new NamedObject("date_time", ""); 
		keyDateTime.setIcon(dateTimeIcon);
		final TimeComboBoxEditor dateTimeComboBoxEditor = new TimeComboBoxEditor(true);
		dateTimeComboBoxEditor.setItem();
		editor.put(keyDateTime, dateTimeComboBoxEditor);

		return editor;
    }

	
}
