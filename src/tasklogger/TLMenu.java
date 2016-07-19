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
	private JMenuItem saveMenuItem;
	
	public TLMenu() {
		super();
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("sAD");
		add(fileMenu);
		
		//a group of JMenuItems
		saveMenuItem = new JMenuItem("Save to file", KeyEvent.VK_S);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TLModel.exportCVSFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});		
		fileMenu.add(saveMenuItem);
		
		add(Box.createHorizontalGlue());
	}

}
