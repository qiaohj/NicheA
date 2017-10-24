package org.ku.nicheanalyst.common;

import java.io.IOException;

public class HtmlBuilder {
	private StringBuilder content;
	public HtmlBuilder(String title){
		this.content = new StringBuilder();
		this.content.append("<h1>" + title + "</h1>");
	}
	public void append(String content){
		this.content.append(content + Const.LineBreak);
	}
	
	public void save(String filename) throws IOException{
		StringBuilder html = new StringBuilder();
		html.append(Message.getString("html_head") + Const.LineBreak);
		html.append(this.content.toString() + Const.LineBreak);
		html.append(Message.getString("html_tail") + Const.LineBreak);
		CommonFun.writeFile(html.toString(), filename);
	}
}
