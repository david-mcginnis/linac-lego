/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.linaclego.webapp.client.panels;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.webapp.client.LinacLegoWebApp;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutPanel;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutScrollPanel;
import se.esss.litterbox.linaclego.webapp.shared.HtmlTextTree;

public class PbsLayoutPanel extends VerticalPanel
{
	private MyTabLayoutScrollPanel myTabLayoutScrollPanel;
	private String treeType = "";
	HtmlTextTree textTree;
	PbsLevelPanel pbsLevelPanel;
	
	public String getTreeType() {return treeType;}
	public MyTabLayoutScrollPanel getMyTabLayoutScrollPanel() {return myTabLayoutScrollPanel;}


	public PbsLayoutPanel(MyTabLayoutPanel myTabLayoutPanel, String tabTitle, String treeType) 
	{
		super();
		setWidth("100%");
		setHeight("100%");
		myTabLayoutScrollPanel = new MyTabLayoutScrollPanel(myTabLayoutPanel);
		myTabLayoutPanel.add(myTabLayoutScrollPanel, tabTitle);
		myTabLayoutScrollPanel.add(this);
		myTabLayoutScrollPanel.getPanelWidth();
		this.treeType = treeType;
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

	}
	public LinacLegoWebApp getLinacLegoWebApp()
	{
		return getMyTabLayoutScrollPanel().getMyTabLayoutPanel().getLinacLegoWebApp();
	}
	public void addTree(HtmlTextTree textTree)
	{
		if (getWidgetCount() > 0) clear();
	      myTabLayoutScrollPanel.getMyTabLayoutPanel().getLinacLegoWebApp().getStatusTextArea().addStatus("Finished building " + treeType + " layout view.");
	      pbsLevelPanel = new PbsLevelPanel(0, textTree.getTextTreeArrayList().get(0), true, null, myTabLayoutScrollPanel);
	      VerticalPanel vertWrapperPanel = new VerticalPanel();
	      vertWrapperPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

	      vertWrapperPanel.add(new InlineHTML(textTree.getInlineHtmlString(false, false)));
	      vertWrapperPanel.add(pbsLevelPanel);
	      add(vertWrapperPanel);
//	      pbsLevelPanel.focusPanel.setFocus(false);
	      pbsLevelPanel.expandPanel();
	}

}
