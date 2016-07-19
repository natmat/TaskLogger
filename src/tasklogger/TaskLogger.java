package tasklogger;

import java.io.IOException;

import javax.swing.SwingUtilities;

public class TaskLogger {
	private static TLController controller;
	private static TLView view;
	private static TLModel model;

	public static void main(String[] args) {    
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	protected static void createAndShowGUI() {
		model = TLModel.getInstance();
		controller = TLController.getInstance();
		view = TLView.getInstance();
		
		controller.setView(view);
		controller.setModel(model);
		
		view.setTitle("Task logger");   
		view.setVisible(true);
		
		try {
			TLModel.importCSVFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TLModel.addModelToView();
	}
}
