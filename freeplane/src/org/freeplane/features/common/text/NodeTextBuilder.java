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
package org.freeplane.features.common.text;

import java.io.IOException;

import org.freeplane.core.io.IAttributeHandler;
import org.freeplane.core.io.IAttributeWriter;
import org.freeplane.core.io.IElementContentHandler;
import org.freeplane.core.io.IElementWriter;
import org.freeplane.core.io.ITreeWriter;
import org.freeplane.core.io.ReadManager;
import org.freeplane.core.io.WriteManager;
import org.freeplane.core.resources.NamedObject;
import org.freeplane.core.util.HtmlUtils;
import org.freeplane.features.common.map.NodeBuilder;
import org.freeplane.features.common.map.NodeModel;
import org.freeplane.features.common.styles.StyleFactory;
import org.freeplane.features.common.styles.StyleNamedObject;
import org.freeplane.features.common.styles.StyleString;
import org.freeplane.n3.nanoxml.XMLElement;

public class NodeTextBuilder implements IElementContentHandler, IElementWriter, IAttributeWriter {
	public static final String XML_NODE_TEXT = "TEXT";
	public static final String XML_NODE_LOCALIZED_TEXT = "LOCALIZED_TEXT";
	public static final String XML_NODE_XHTML_CONTENT_TAG = "richcontent";
	public static final String XML_NODE_XHTML_TYPE_NODE = "NODE";
	public static final String XML_NODE_XHTML_TYPE_NOTE = "NOTE";
	public static final String XML_NODE_XHTML_TYPE_DETAILS = "DETAILS";
	public static final String XML_NODE_XHTML_TYPE_TAG = "TYPE";

	public Object createElement(final Object parent, final String tag, final XMLElement attributes) {
		if (attributes == null) {
			return null;
		}
		final Object typeAttribute = attributes.getAttribute(NodeTextBuilder.XML_NODE_XHTML_TYPE_TAG, null);
		if (typeAttribute != null && !NodeTextBuilder.XML_NODE_XHTML_TYPE_NODE.equals(typeAttribute)) {
			return null;
		}
		return parent;
	}

	public void endElement(final Object parent, final String tag, final Object node, final XMLElement attributes,
	                       final String content) {
		assert tag.equals("richcontent");
		final String xmlText = content;
		((NodeModel) node).setXmlText(xmlText);
	}

	private void registerAttributeHandlers(final ReadManager reader) {
		reader.addAttributeHandler(NodeBuilder.XML_NODE, NodeTextBuilder.XML_NODE_TEXT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				node.setText(value);
			}
		});
		reader.addAttributeHandler(NodeBuilder.XML_STYLENODE, NodeTextBuilder.XML_NODE_TEXT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				node.setUserObject(StyleFactory.create(value));
			}
		});
		reader.addAttributeHandler(NodeBuilder.XML_NODE, NodeTextBuilder.XML_NODE_LOCALIZED_TEXT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				node.setUserObject(StyleFactory.create(NamedObject.formatText(value)));
			}
		});
		reader.addAttributeHandler(NodeBuilder.XML_STYLENODE, NodeTextBuilder.XML_NODE_LOCALIZED_TEXT, new IAttributeHandler() {
			public void setAttribute(final Object userObject, final String value) {
				final NodeModel node = ((NodeModel) userObject);
				node.setUserObject(StyleFactory.create(NamedObject.formatText(value)));
			}
		});
	}

	/**
	 * @param writeManager 
	 */
	public void registerBy(final ReadManager reader, final WriteManager writeManager) {
		registerAttributeHandlers(reader);
		reader.addElementHandler("richcontent", this);
		writeManager.addElementWriter(NodeBuilder.XML_NODE, this);
		writeManager.addElementWriter(NodeBuilder.XML_STYLENODE, this);
		writeManager.addAttributeWriter(NodeBuilder.XML_NODE, this);
		writeManager.addAttributeWriter(NodeBuilder.XML_STYLENODE, this);
	}

	public void writeAttributes(final ITreeWriter writer, final Object userObject, final String tag) {
		final Object data = ((NodeModel) userObject).getUserObject();
		final Class<? extends Object> dataClass = data.getClass();
		if (dataClass.equals(StyleNamedObject.class)) {
			writer.addAttribute(NodeTextBuilder.XML_NODE_LOCALIZED_TEXT, ((StyleNamedObject) data).getObject().toString());
			return;
		}
		if (dataClass.equals(NamedObject.class)) {
			writer.addAttribute(NodeTextBuilder.XML_NODE_LOCALIZED_TEXT, ((NamedObject) data).getObject().toString());
			return;
		}
		if (!(dataClass.equals(StyleString.class) || dataClass.equals(String.class))) {
			return;
		}
		final String text =  data.toString();
		if (!HtmlUtils.isHtmlNode(text)) {
			writer.addAttribute(NodeTextBuilder.XML_NODE_TEXT, text.replace('\0', ' '));
		}
	}

	public void writeContent(final ITreeWriter writer, final Object element, final String tag) throws IOException {
		final NodeModel node = (NodeModel) element;
		if (HtmlUtils.isHtmlNode(node.toString())) {
			final XMLElement htmlElement = new XMLElement();
			htmlElement.setName(NodeTextBuilder.XML_NODE_XHTML_CONTENT_TAG);
			htmlElement.setAttribute(NodeTextBuilder.XML_NODE_XHTML_TYPE_TAG, NodeTextBuilder.XML_NODE_XHTML_TYPE_NODE);
			final String xmlText = node.getXmlText();
			final String content = xmlText.replace('\0', ' ');
			writer.addElement(content, htmlElement);
		}
	}
}
