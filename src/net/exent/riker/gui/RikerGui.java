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

import java.util.Enumeration;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.exent.riker.Riker;
import net.exent.riker.RikerUi;
import net.exent.riker.metadata.Album;
import net.exent.riker.metadata.Group;
import net.exent.riker.metadata.Metafile;
import net.exent.riker.metadata.Track;
import net.exent.riker.util.Logger;

/**
 * Graphical User Interface for Riker.
 */
public class RikerGui extends JFrame implements RikerUi {
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = new Logger(RikerGui.class);

	/**
	 * Default constructor.
	 */
	public RikerGui() {
		initComponents();
		matchTree.setCellRenderer(new MatchTreeCellRenderer());
	}

	@Override
	public void fileLoaded(Metafile metafile) {
	}

	@Override
	public void groupMatched(Group group) {
		setGroupInTree(group);
	}

	@Override
	public void allFilesLoaded() {
		for (Map.Entry<String, Group> entry : Riker.groups().entrySet())
			setGroupInTree(entry.getValue());
		matchTree.expandPath(new TreePath(((DefaultMutableTreeNode) matchTree.getModel().getRoot()).getPath()));
	}

	@Override
	public void allFilesMatched() {
	}

	/**
	 * Add or update a group in the JTree.
	 * Tree layout:
	 * root
	 * - groups (sorted alphabetically)
	 *   - albums (sorted alphabetically)
	 *     - tracks (sorted by track number)
	 *       - matched files (sorted by best match)
	 *   - unmatched files (sorted alphabetically)
	 * @param group group to add/update in the tree
	 */
	private void setGroupInTree(Group group) {
		DefaultTreeModel model = (DefaultTreeModel) matchTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode groupNode = null;
		int groupNodeIndex = 0;
		Enumeration groupNodes = root.children();
		while (groupNodes.hasMoreElements()) {
			DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) groupNodes.nextElement();
			if (group.equals(tmpNode.getUserObject())) {
				/* update this group */
				groupNode = tmpNode;
				groupNode.removeAllChildren();
				break;
			}
			if (group.name().compareTo(((Group) tmpNode.getUserObject()).name()) < 0)
				break;
			++groupNodeIndex;
		}
		if (groupNode == null) {
			groupNode = new DefaultMutableTreeNode(group);
			model.insertNodeInto(groupNode, root, groupNodeIndex);
		}
		for (Metafile metafile : group.files()) {
			Track track = metafile.track();
			DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(metafile);
			if (track != null) {
				/* metafile is matched, place it beneath track in album */
				/* find album in tree, create it with tracks if missing */
				Album album = track.album();
				int albumNodeIndex = 0;
				DefaultMutableTreeNode albumNode = null;
				Enumeration entryNodes = groupNode.children();
				while (entryNodes.hasMoreElements()) {
					DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) entryNodes.nextElement();
					LOG.info(tmpNode);
					if (!(tmpNode.getUserObject() instanceof Album))
						break;
					if (album.toString().compareTo(tmpNode.toString()) < 0)
						break;
					if (album.equals(tmpNode.getUserObject())) {
						albumNode = tmpNode;
						break;
					}
					++albumNodeIndex;
				}
				if (albumNode == null) {
					albumNode = new DefaultMutableTreeNode(album);
					model.insertNodeInto(albumNode, groupNode, albumNodeIndex);
					/* create nodes for all tracks in album */
					DefaultMutableTreeNode[] trackNodes = new DefaultMutableTreeNode[album.tracks().size()];
					for (Track albumTrack : album.tracks())
						trackNodes[albumTrack.tracknumber() - 1] = new DefaultMutableTreeNode(albumTrack);
					for (DefaultMutableTreeNode trackNode : trackNodes)
						model.insertNodeInto(trackNode, albumNode, albumNode.getChildCount());
				} else {
					model.insertNodeInto(albumNode, groupNode, albumNodeIndex);
				}
				/* find track in tree */
				Enumeration trackNodes = albumNode.children();
				while (trackNodes.hasMoreElements()) {
					DefaultMutableTreeNode trackNode = (DefaultMutableTreeNode) trackNodes.nextElement();
					if (!(trackNode.getUserObject() instanceof Track))
						continue;
					if (!metafile.track().equals(trackNode.getUserObject()))
						continue;
					/* add file beneath track, ordered by score if more than 1 matching file */
					int fileNodeIndex = 0;
					Enumeration fileNodes = trackNode.children();
					while (fileNodes.hasMoreElements()) {
						DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) fileNodes.nextElement();
						if (tmpNode.getUserObject() instanceof Metafile && metafile.matchScore() > ((Metafile) tmpNode.getUserObject()).matchScore())
							break;
						++fileNodeIndex;
					}
					model.insertNodeInto(fileNode, trackNode, fileNodeIndex);
					break;
				}
			} else {
				/* metafile is not matched, place it directly in the group after any albums and alphabetically */
				int fileNodeIndex = 0;
				Enumeration entryNodes = groupNode.children();
				while (entryNodes.hasMoreElements()) {
					DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) entryNodes.nextElement();
					if (metafile.toString().compareTo(tmpNode.getUserObject().toString()) < 0)
						break;
					++fileNodeIndex;
				}
				model.insertNodeInto(fileNode, groupNode, fileNodeIndex);
			}
		}
		return;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                menuPanel = new javax.swing.JPanel();
                jButton1 = new javax.swing.JButton();
                matchScrollPane = new javax.swing.JScrollPane();
                matchTree = new javax.swing.JTree();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

                menuPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

                jButton1.setText("Import");
                menuPanel.add(jButton1);

                getContentPane().add(menuPanel, java.awt.BorderLayout.NORTH);

                javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
                matchTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
                matchTree.setRootVisible(false);
                matchScrollPane.setViewportView(matchTree);

                getContentPane().add(matchScrollPane, java.awt.BorderLayout.CENTER);

                pack();
        }// </editor-fold>//GEN-END:initComponents
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton jButton1;
        private javax.swing.JScrollPane matchScrollPane;
        private javax.swing.JTree matchTree;
        private javax.swing.JPanel menuPanel;
        // End of variables declaration//GEN-END:variables
}
