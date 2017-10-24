package org.ku.nicheanalyst.ui.display.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdom.JDOMException;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.GompertzCurveParameters;
import org.ku.nicheanalyst.ui.display.Displayer;

public class NicheBreadthVariablePanel extends JPanel {
	private JTextField folderTextField;
	private Displayer theApp;
	private GompertzCurveParameters parameters;
	
	public NicheBreadthVariablePanel(Displayer theApp, String labelstr){
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.theApp = theApp;
		JLabel label = new JLabel(labelstr);
		add(label);
		this.folderTextField = new JTextField("");
		this.folderTextField.setEditable(false);
		add(this.folderTextField);
		JButton select_folder = new JButton("...");
		select_folder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder();
			}
		});
		add(select_folder);
		
	}
	public void setFolder(String folder){
		theApp.setLastFolder(folder);
		this.folderTextField.setText(folder);
		parameters = null;
		try {
			parameters = new GompertzCurveParameters(this.folderTextField.getText() + "/parameters.xml");
		} catch (Exception e) {
			theApp.ShowAlert(Message.getString("error_load_parameters"));
			e.printStackTrace();
		}
		if (parameters!=null){
			
		}
	}
	public String getFolder(){
		return this.folderTextField.getText();
	}
	public GompertzCurveParameters getParameters(){
		return this.parameters;
	}
	protected void selectFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_folder"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = (fc.getSelectedFile().exists())?
					fc.getSelectedFile().getAbsolutePath():fc.getSelectedFile().getParent();
			setFolder(target);
		}
		
	}
}

