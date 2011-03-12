/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2011 dimitry
 *
 *  This file author is dimitry
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
package org.freeplane.core.ui.components;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import org.freeplane.core.resources.NamedObject;

/**
 * @author Dimitry Polivaev
 * Mar 12, 2011
 */
public class ContainerComboBoxEditor implements ComboBoxEditor {
	final private Map<NamedObject, ComboBoxEditor> editors;
	private ComboBoxEditor editor;
	final private JComboBox editorSelector;
	final private JPanel editorPanel;
	private Box editorComponent;
	private final JButton selectorButton;
	private Popup popup;

	final private List<ActionListener> actionListeners;

	public ContainerComboBoxEditor() {
		editors = new HashMap<NamedObject, ComboBoxEditor>();
		editorComponent = Box.createHorizontalBox();
		editorSelector = new JComboBox();
		editorSelector.setEditable(false);
		editorSelector.setRenderer(NamedObject.getIconRenderer());
		selectorButton = new JButton();
		selectorButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if(popup != null)
					return;
				final Point location = selectorButton.getLocationOnScreen();
				popup = PopupFactory.getSharedInstance().getPopup(selectorButton, editorSelector, location.x, location.y + selectorButton.getHeight());
				popup.show();
			}
		});
		editorSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(popup == null)
					return;
				final NamedObject key = (NamedObject) editorSelector.getSelectedItem();
				editor = editors.get(key);
				popup.hide();
				popup = null;
				setButtonContent(key);
				final CardLayout layout = (CardLayout) editorPanel.getLayout();				
				layout.show(editorPanel,  key.getObject().toString());
				editor.getEditorComponent().requestFocusInWindow();
			    final ActionEvent actionEvent = new ActionEvent(editor, 0, null);
			    for (final ActionListener l : actionListeners) {
			    	l.actionPerformed(actionEvent);
			    }
			}
		});
		editorComponent.add(selectorButton);
		editorPanel = new JPanel(new CardLayout(0, 0));
		editorComponent.add(editorPanel);
		actionListeners = new LinkedList<ActionListener>();
    }
	
	public boolean put(NamedObject key, ComboBoxEditor editor){
		final ComboBoxEditor oldEditor = editors.put(key, editor);
		if(oldEditor != null){
			editors.put(key, oldEditor);
			return false;
		}
		final DefaultComboBoxModel model = (DefaultComboBoxModel) editorSelector.getModel();
		model.addElement(key);
		if(this.editor == null){
			this.editor = editor;
			setButtonContent(key);
		}
		editorPanel.add(editor.getEditorComponent(), key.getObject().toString());
		return true;
	}

	public Component getEditorComponent() {
		return editorComponent;
	}

	public void setItem(Object anObject) {
		editor.setItem(anObject);
	}

	public Object getItem() {
		return editor.getItem();
	}

	public void selectAll() {
		editor.selectAll();
	}

	public void addActionListener(ActionListener l) {
		actionListeners.add(l);
		for(ComboBoxEditor e : editors.values())
			e.addActionListener(l);
	}

	public void removeActionListener(ActionListener l) {
		actionListeners.remove(l);
		for(ComboBoxEditor e : editors.values())
			e.removeActionListener(l);
	}

	void setButtonContent(final NamedObject key) {
		final Icon icon = key.getIcon();
	    selectorButton.setIcon(icon);
		if(icon == null){
		    selectorButton.setText(key.toString());
		}
		else{
		    selectorButton.setText(null);
		}
    }
}
