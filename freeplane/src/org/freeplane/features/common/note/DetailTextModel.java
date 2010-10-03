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
package org.freeplane.features.common.note;

import org.freeplane.core.extension.IExtension;
import org.freeplane.features.common.map.NodeModel;

/**
 * @author Dimitry Polivaev
 */
public class DetailTextModel extends RichTextModel implements IExtension {
	private boolean hidden = false;
	public DetailTextModel(boolean hidden) {
	    this.hidden = hidden;
    }

	public boolean isHidden() {
    	return hidden;
    }

	public void setHidden(boolean hidden) {
    	this.hidden = hidden;
    }

	public static DetailTextModel createDetailText(final NodeModel node) {
		DetailTextModel note = DetailTextModel.getDetailText(node);
		if (note == null) {
			note = new DetailTextModel(false);
			node.addExtension(note);
		}
		return note;
	}

	public static DetailTextModel getDetailText(final NodeModel node) {
		final DetailTextModel extension = (DetailTextModel) node.getExtension(DetailTextModel.class);
		return extension;
	}

	public static String getDetailTextText(final NodeModel node) {
		final DetailTextModel extension = DetailTextModel.getDetailText(node);
		return extension != null ? extension.getHtml() : null;
	}

	public static String getXmlDetailTextText(final NodeModel node) {
		final DetailTextModel extension = DetailTextModel.getDetailText(node);
		return extension != null ? extension.getHtml() : null;
	}

}
