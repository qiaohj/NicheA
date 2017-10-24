package org.ku.nicheanalyst.ui.display.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.filefilter.FileFileFilter;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.GompertzCurveParameters;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;

public class SelectFilePanal extends JPanel {
	private LabeledTextField textFiled;
	private Displayer theApp; 
	private JButton select_folder;
	
	public SelectFilePanal (Displayer theApp, String label, String defaultValue, 
			final boolean isFolder, final FileFilter filter, final boolean isSave){
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.theApp = theApp;
		textFiled = new LabeledTextField(label, defaultValue);
		
		add(textFiled);
		select_folder = new JButton("...");
		select_folder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder(isFolder, filter, isSave);
			}
		});
		add(select_folder);
	}
	public void setEnabled(boolean enabled){
		this.textFiled.setEnabled(enabled);
		this.select_folder.setEnabled(enabled);
	}
	protected void selectFolder(boolean isFolder, FileFilter filter, boolean isSave) {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("");
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		if (isFolder){
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}else{
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (filter!=null){
				fc.setFileFilter(filter);
			}
		}
		
		int returnVal = 0;
		if (isSave){
			returnVal = fc.showSaveDialog(this);
		}else{
			returnVal = fc.showOpenDialog(this);
		}
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = (fc.getSelectedFile().exists())?
					fc.getSelectedFile().getAbsolutePath():fc.getSelectedFile().getParent();
			theApp.setLastFolder(target);
			this.textFiled.setText(target);
			
		}
		
	}
	public void requestFocus(){
		this.textFiled.requestFocus();
	}
	public String getText(){
		return this.textFiled.getText();
	}
	
	public void setText(String text){
		this.textFiled.setText(text);
	}
}
