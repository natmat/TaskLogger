package tasklogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class TLMenu extends JMenuBar {
	private static final long serialVersionUID = 1342845686975967732L;
	private JMenu fileMenu;
	private MenuActionListener menuListener;
	
	public TLMenu() {
		super();
		
		menuListener = new MenuActionListener();
		
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("sAD");
		add(fileMenu);
		
		//a group of JMenuItems
		JMenuItem saveMenuItem = new JMenuItem("Save to file", KeyEvent.VK_S);
		saveMenuItem.setActionCommand("Save");
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.addActionListener(menuListener);
		
		JMenuItem newTaskMenuItem = new JMenuItem("Add new task", KeyEvent.VK_N);
		newTaskMenuItem.setActionCommand("New");
		newTaskMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newTaskMenuItem.addActionListener(menuListener);
				
		fileMenu.add(saveMenuItem);
		fileMenu.add(newTaskMenuItem);
	
		// Left-align File menu
		add(Box.createHorizontalGlue());
	}
	
	class MenuActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch(e.getActionCommand()) {
			case "Save":
				try {
					TLModel.exportCVSFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			case "New":
				TLView.newTaskButtonPressed();
				break;
			}
		}
	}
}
