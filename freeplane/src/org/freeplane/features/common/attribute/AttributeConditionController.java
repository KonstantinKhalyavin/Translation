/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
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
package org.freeplane.features.common.attribute;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.ListModel;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.resources.NamedObject;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.components.ContainerComboBoxEditor;
import org.freeplane.core.util.TextUtils;
import org.freeplane.core.util.collection.ExtendedComboBoxModel;
import org.freeplane.features.common.filter.condition.ConditionFactory;
import org.freeplane.features.common.filter.condition.IElementaryConditionController;
import org.freeplane.features.common.filter.condition.ASelectableCondition;
import org.freeplane.features.common.time.TimeComboBoxEditor;
import org.freeplane.n3.nanoxml.XMLElement;

/**
 * @author Dimitry Polivaev
 * 21.12.2008
 */
class AttributeConditionController implements IElementaryConditionController {
// // 	final private Controller controller;
	private final ExtendedComboBoxModel values = new ExtendedComboBoxModel();

	public AttributeConditionController() {
		super();
//		this.controller = controller;
	}

	public boolean canEditValues(final Object selectedItem, final NamedObject simpleCond) {
		return true;
	}

	public boolean canHandle(final Object selectedItem) {
		return selectedItem.getClass().equals(String.class);
	}

	public boolean canSelectValues(final Object selectedItem, final NamedObject simpleCond) {
		return !simpleCond.objectEquals(ConditionFactory.FILTER_EXIST)
		        && !simpleCond.objectEquals(ConditionFactory.FILTER_DOES_NOT_EXIST);
	}

	public ASelectableCondition createCondition(final Object selectedItem, final NamedObject simpleCondition,
	                                            final Object value, final boolean matchCase) {
		final String attribute = (String) selectedItem;
		if (simpleCondition.objectEquals(ConditionFactory.FILTER_EXIST)) {
			return new AttributeExistsCondition(attribute);
		}
		if (simpleCondition.objectEquals(ConditionFactory.FILTER_DOES_NOT_EXIST)) {
			return new AttributeNotExistsCondition(attribute);
		}
		if (matchCase) {
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_IS_EQUAL_TO)) {
				return new AttributeCompareCondition(attribute, value, true, 0, true);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_IS_NOT_EQUAL_TO)) {
				return new AttributeCompareCondition(attribute, value, true, 0, false);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_GT)) {
				return new AttributeCompareCondition(attribute, value, true, 1, true);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_GE)) {
				return new AttributeCompareCondition(attribute, value, true, -1, false);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_LT)) {
				return new AttributeCompareCondition(attribute, value, true, -1, true);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_LE)) {
				return new AttributeCompareCondition(attribute, value, true, 1, false);
			}
		}
		else {
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_IS_EQUAL_TO)) {
				return new AttributeCompareCondition(attribute, value, false, 0, true);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_IS_NOT_EQUAL_TO)) {
				return new AttributeCompareCondition(attribute, value, false, 0, false);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_GT)) {
				return new AttributeCompareCondition(attribute, value, false, 1, true);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_GE)) {
				return new AttributeCompareCondition(attribute, value, false, -1, false);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_LT)) {
				return new AttributeCompareCondition(attribute, value, false, -1, true);
			}
			if (simpleCondition.objectEquals(ConditionFactory.FILTER_LE)) {
				return new AttributeCompareCondition(attribute, value, false, 1, false);
			}
		}
		return null;
	}

	public ComboBoxModel getConditionsForProperty(final Object selectedItem) {
		return new DefaultComboBoxModel(new NamedObject[] {
		        TextUtils.createTranslatedString(ConditionFactory.FILTER_EXIST),
		        TextUtils.createTranslatedString(ConditionFactory.FILTER_DOES_NOT_EXIST),
		        TextUtils.createTranslatedString(ConditionFactory.FILTER_IS_EQUAL_TO),
		        TextUtils.createTranslatedString(ConditionFactory.FILTER_IS_NOT_EQUAL_TO),
		        NamedObject.literal(ConditionFactory.FILTER_GT), NamedObject.literal(ConditionFactory.FILTER_GE),
		        NamedObject.literal(ConditionFactory.FILTER_LE), NamedObject.literal(ConditionFactory.FILTER_LT) });
	}

	public ListModel getFilteredProperties() {
		final AttributeRegistry registry = AttributeRegistry.getRegistry(Controller.getCurrentController().getMap());
		if (registry != null) {
			return registry.getListBoxModel();
		}
		return new DefaultListModel();
	}

	public ComboBoxEditor getValueEditor(Object selectedProperty, NamedObject selectedCondition) {
		return TimeComboBoxEditor.getTextDateTimeEditor();
	}

	public ComboBoxModel getValuesForProperty(final Object selectedItem, NamedObject simpleCond) {
		values.setExtensionList(AttributeRegistry.getRegistry(Controller.getCurrentController().getMap()).getElement(selectedItem.toString())
		    .getValues());
		return values;
	}

	public boolean isCaseDependent(final Object selectedItem, final NamedObject simpleCond) {
		return true;
	}

	public ASelectableCondition loadCondition(final XMLElement element) {
		if (element.getName().equalsIgnoreCase(AttributeCompareCondition.NAME)) {
			return AttributeCompareCondition.load(element);
		}
		if (element.getName().equalsIgnoreCase(AttributeExistsCondition.NAME)) {
			return AttributeExistsCondition.load(element);
		}
		if (element.getName().equalsIgnoreCase(AttributeNotExistsCondition.NAME)) {
			return AttributeNotExistsCondition.load(element);
		}
		return null;
	}
}
