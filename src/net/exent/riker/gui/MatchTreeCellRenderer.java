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
import net.exent.riker.metadata.MetaFile;
import net.exent.riker.metadata.Track;

public class MatchTreeCellRenderer implements TreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object node, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		return null;
		/*
		String icon = "unknown_icon.png";
		Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
		int status = MetaFile.NONE;
		if (userObject instanceof Group) {
		} else if (userObject instanceof Album) {
			Album album = (Album) userObject;
			if (album.getMinScore() < 0.4)
				icon = "album_25.png";
			else if (album.getMinScore() < 0.55)
				icon = "album_40.png";
			else if (album.getMinScore() < 0.7)
				icon = "album_55.png";
			else if (album.getMinScore() < 0.85)
				icon = "album_70.png";
			else
				icon = "album_85.png";
		} else if (userObject instanceof Track) {
			Track track = (Track) userObject;
			if (track.hasGotMatch())
				icon = "track_matched.png";
			else if (!track.hasGotFiles())
				icon = "track_none.png";
			else if (track.getScore() < 0.4)
				icon = "track_25.png";
			else if (track.getScore() < 0.55)
				icon = "track_40.png";
			else if (track.getScore() < 0.7)
				icon = "track_55.png";
			else if (track.getScore() < 0.85)
				icon = "track_70.png";
			else
				icon = "track_85.png";
		} else if (userObject instanceof MetaFile) {
			MetaFile file = (MetaFile) userObject;
			if (file.getTrackID() > 0)
				icon = "file_matched.png";
			else if (file.getScore() < 0.4)
				icon = "file_25.png";
			else if (file.getScore() < 0.55)
				icon = "file_40.png";
			else if (file.getScore() < 0.7)
				icon = "file_55.png";
			else if (file.getScore() < 0.85)
				icon = "file_70.png";
			else
				icon = "file_85.png";
			status = file.getStatus();
		}
		JLabel label = new JLabel(node.toString(), new ImageIcon(getClass().getResource("/net/exent/riker/gui/icons/" + icon)), JLabel.LEFT);
		label.setOpaque(true);
		if (selected) {
			label.setBackground(new Color(200, 200, 255));
		} else {
			label.setBackground(new Color(255, 255, 255));
		}
		if (status == MetaFile.SAVE)
			label.setForeground(new Color(0, 150, 0));
		else if (status == MetaFile.DELETE)
			label.setForeground(new Color(150, 0, 0));
		else if (status == MetaFile.SAVE_METADATA)
			label.setForeground(new Color(150, 0, 150));
		return label;
		*/
	}
}
