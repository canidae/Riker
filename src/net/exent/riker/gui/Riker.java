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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.exent.riker.metadata.Album;
import net.exent.riker.metadata.Artist;
import net.exent.riker.metadata.Group;
import net.exent.riker.metadata.Metafile;
import net.exent.riker.metadata.Track;
import net.exent.riker.util.FileHandler;
import net.exent.riker.util.Logger;
import net.exent.riker.util.MusicBrainz;
import org.jaudiotagger.audio.AudioFile;

/**
 * GUI class.
 */
public class Riker extends JFrame {
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = new Logger(FileHandler.class);
	/**
	 * Map of all loaded albums.
	 */
	private static Map<String, Album> albums = Collections.synchronizedMap(new HashMap<String, Album>());
	/**
	 * Map of all loaded artists.
	 */
	private static Map<String, Artist> artists = Collections.synchronizedMap(new HashMap<String, Artist>());
	/**
	 * Map of all loaded tracks.
	 */
	private static Map<String, Track> tracks = Collections.synchronizedMap(new HashMap<String, Track>());
	/**
	 * Map of all loaded groups.
	 */
	private static Map<String, Group> groups = Collections.synchronizedMap(new HashMap<String, Group>());
	/**
	 * Map of all loaded metafiles.
	 */
	private static Map<String, Metafile> metafiles = Collections.synchronizedMap(new HashMap<String, Metafile>());

	/**
	 * Default constructor.
	 */
	public Riker() {
		initComponents();
	}

	/**
	 * Called by FileHandler when it reads a new file.
	 * @param audioFile the file just read
	 */
	public static void fileLoaded(AudioFile audioFile) {
		Metafile metafile = new Metafile(audioFile);
		if (metafiles.size() == 0) {
			MusicBrainz.searchTrack(metafile);
		}
		LOG.info("Adding Metafile to Riker: ", metafile);
		/* add metafile to map */
		metafiles.put(metafile.filename(), metafile);
		/* add group to map */
		String groupName = metafile.createGroupName();
		Group group = groups.get(groupName);
		if (group == null) {
			group = new Group(groupName);
			groups.put(groupName, group);
		}
		/* add metafile to group */
		group.addFile(metafile);
		/* let metafile know which group it belongs to */
		metafile.group(group);
		/* update matchTree */
		DefaultTreeModel model = (DefaultTreeModel) matchTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(metafile);
		Enumeration groupNodes = root.children();
		int groupNodeIndex = 0;
		while (groupNodes.hasMoreElements()) {
			DefaultMutableTreeNode groupNode = (DefaultMutableTreeNode) groupNodes.nextElement();
			if (groupName.equals(groupNode.getUserObject().toString())) {
				/* add file to this group */
				Enumeration fileNodes = groupNode.children();
				int fileNodeIndex = 0;
				while (fileNodes.hasMoreElements()) {
					DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) fileNodes.nextElement();
					if (fileNode.toString().compareTo(tmpNode.toString()) < 0) {
						break;
					}
					++fileNodeIndex;
				}
				model.insertNodeInto(fileNode, groupNode, fileNodeIndex);
				return;
			}
			if (groupName.compareTo(groupNode.toString()) > 0)
				++groupNodeIndex;
		}
		/* group name did not match any existing groups, create a new one */
		DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group);
		model.insertNodeInto(groupNode, root, groupNodeIndex);
		model.insertNodeInto(fileNode, groupNode, 0);
		matchTree.expandPath(new TreePath(root.getPath()));
	}

	/**
	 * Called by FileHandler when all files in queue are loaded.
	 */
	public static void filesLoaded() {
		/* TODO: start up matchers for each group */
	}

	/**
	 * Called by Matchers when they're done matching files.
	 */
	public static void filesMatched(List<Metafile> files) {
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

	/**
	 * @param args the command line arguments
	 */
	public static void main(String... args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Riker().setVisible(true);
				FileHandler.start();
				FileHandler.load("/home/canidae/Music/tmp_un");
			}
		});
	}
        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton jButton1;
        private javax.swing.JScrollPane matchScrollPane;
        private static javax.swing.JTree matchTree;
        private javax.swing.JPanel menuPanel;
        // End of variables declaration//GEN-END:variables
}
