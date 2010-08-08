/*
 *  The MIT License
 * 
 *  Copyright 2010 Vidar Wahlberg <canidae@exent.net>.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package net.exent.riker.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import net.exent.riker.metadata.Album;
import net.exent.riker.metadata.Group;
import net.exent.riker.metadata.Metafile;
import net.exent.riker.metadata.Track;

/**
 * Our tree cell renderer.
 */
public class MatchTreeCellRenderer implements TreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object node, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		String icon = "unknown_icon.png";
		Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
		if (userObject instanceof Group) {
			icon = "group.png";
		} else if (userObject instanceof Album) {
			icon = "album_55.png";
		} else if (userObject instanceof Track) {
			icon = "track_none.png";
		} else if (userObject instanceof Metafile) {
			Metafile file = (Metafile) userObject;
			if (file.matchScore() < 0.4)
				icon = "file_25.png";
			else if (file.matchScore() < 0.55)
				icon = "file_40.png";
			else if (file.matchScore() < 0.7)
				icon = "file_55.png";
			else if (file.matchScore() < 0.85)
				icon = "file_70.png";
			else
				icon = "file_85.png";
		}
		JLabel label = new JLabel(node.toString(), new ImageIcon(getClass().getResource("/net/exent/riker/gui/icons/" + icon)), JLabel.LEFT);
		label.setOpaque(true);
		if (selected) {
			label.setBackground(new Color(200, 200, 255));
		} else {
			label.setBackground(new Color(255, 255, 255));
		}
		return label;
	}
}
