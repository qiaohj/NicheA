package org.ku.nicheanalyst.ui.display.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Message;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.parser.InputSourceImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class HTMLViewer extends JDialog{
	public HTMLViewer(final File htmlFile, 
			String title, int xsize, int ysize, boolean isopenfolder) throws SAXException, IOException{
		
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		HtmlPanel panel = null;
		if (!CommonFun.isLinux()){
			panel = new HtmlPanel();
			panel.setBorder(BorderFactory.createTitledBorder(title));
			
			SimpleUserAgentContext ucontext = new SimpleUserAgentContext();
			SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(panel, ucontext);
			// Note that document builder should receive both contexts.
			DocumentBuilderImpl dbi = new DocumentBuilderImpl(ucontext, rcontext);
			File openedHtml = null;
			if (isopenfolder){
				String temp = ConfigInfo.getInstance().getTemp();
				CommonFun.copyFolder(htmlFile.getParent(), temp, true);
				ArrayList<String> content = CommonFun.readFromFile(htmlFile.getAbsolutePath());
				StringBuilder sb = new StringBuilder();
				for (String f : content){
					sb.append(f.replace(htmlFile.getParent(), temp));
				}
				openedHtml = new File(temp + "/" + htmlFile.getName());
				CommonFun.writeFile(sb.toString(), openedHtml.getAbsolutePath());
				
			}else{
				openedHtml = htmlFile;
			}
			FileInputStream documentReader = new FileInputStream(openedHtml);
			// A documentURI should be provided to resolve relative URIs.
			Document document = dbi.parse(new InputSourceImpl(documentReader));
			// Now set document in panel. This is what causes the document to render.
			panel.setDocument(document, rcontext);
			
		    add(panel);
		}else{
			JPanel ppp = new JPanel();
			ppp.setLayout(new BoxLayout(ppp, BoxLayout.Y_AXIS));
			
			JPanel pp = new JPanel();
			pp.setBorder(new TitledBorder(Message.getString("message")));
			pp.setLayout(new BoxLayout(pp, BoxLayout.X_AXIS));
			JLabel l = new JLabel();
			l.setText(String.format(Message.getString("unsupport_functions_os"), xsize));
			pp.add(l);
			pp.add(new JLabel());
			ppp.add(pp);
			
			
			pp = new JPanel();
			pp.setLayout(new BoxLayout(pp, BoxLayout.X_AXIS));
			
			JButton open = new JButton(Message.getString("open_in_firefox"));
	    	 open.addActionListener(new ActionListener() {

	             public void actionPerformed(ActionEvent event) {
	                 show_in_firefox(htmlFile.getAbsolutePath());
	             }
	         });
	        pp.add(open);
	        pp.add(new JLabel());
	        
			ppp.add(pp);
			
			add(ppp);
			
		}
	    JPanel p = new JPanel();
	    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
	    if (isopenfolder){
	    	 JButton open = new JButton(Message.getString("open_in_finder"));
	    	 open.addActionListener(new ActionListener() {

	             public void actionPerformed(ActionEvent event) {
	                 show_in_finder(htmlFile.getParent());
	             }
	         });
	         p.add(open);
		    
	    }
	    JButton cancel = new JButton(Message.getString("ok"));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                close();
            }
        });
        p.add(cancel);
        add(p);
        if (!CommonFun.isLinux()){
        	panel.doLayout();
        }
        
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(xsize, ysize);
        setTitle(title);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	private void show_in_firefox(String folder) {
		CommonFun.show_in_firefox(folder);
	}

	private void show_in_finder(String folder) {
		CommonFun.show_in_filder(folder);
	}

	protected void close() {
		dispose();
		
	}
}
