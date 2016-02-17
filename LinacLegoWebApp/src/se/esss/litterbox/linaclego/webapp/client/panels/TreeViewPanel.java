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


import java.util.ArrayList;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.webapp.client.LinacLegoWebApp;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutPanel;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutScrollPanel;
import se.esss.litterbox.linaclego.webapp.shared.HtmlTextTree;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.InlineHTML;

public class TreeViewPanel extends VerticalPanel
{
    private Tree rootTree;
    private MyTreeItem rootTreeItem;
	private MyTabLayoutScrollPanel myTabLayoutScrollPanel;
	private String treeType = "";
	private HtmlTextTree textTree;
	
	public HtmlTextTree getTextTree() {return textTree;}
	public MyTreeItem getRootTreeItem() {return rootTreeItem;}
	public String getTreeType() {return treeType;}
	public MyTabLayoutScrollPanel getMyTabLayoutScrollPanel() {return myTabLayoutScrollPanel;}
	
	public TreeViewPanel(MyTabLayoutPanel myTabLayoutPanel, String tabTitle, String treeType) 
	{
		super();
		myTabLayoutScrollPanel = new MyTabLayoutScrollPanel(myTabLayoutPanel);
		myTabLayoutPanel.add(myTabLayoutScrollPanel, tabTitle);
		myTabLayoutScrollPanel.add(this);
		rootTree = null;
		this.treeType = treeType;
	}
	public LinacLegoWebApp getLinacLegoWebApp()
	{
		return getMyTabLayoutScrollPanel().getMyTabLayoutPanel().getLinacLegoWebApp();
	}
	public void addTree(HtmlTextTree textTree)
	{
		if (getWidgetCount() > 0) clear();
		this.textTree = textTree;
		rootTree = new Tree();
		rootTree.addSelectionHandler(new TreeSelectionHandler());
//	      rootTreeItem = buildTree(pbsViewTextTree);
		rootTreeItem = new MyTreeItem(textTree,  32, 32);
		rootTree.addItem(rootTreeItem);
		myTabLayoutScrollPanel.getMyTabLayoutPanel().getLinacLegoWebApp().getStatusTextArea().addStatus("Finished building " + treeType + " tree view.");
		add(rootTree);
	}
	public class MyTreeItem extends TreeItem
	{
		HtmlTextTree textTree;
		boolean beenExpanded = false;
		boolean open = false;
		InlineHTML inlineHTML;
		String origHtml;
		int iconWidthPx;
		int iconHeightPx;
		ArrayList<MyTreeItem> myTreeItemChildrenList = new ArrayList<MyTreeItem>();
		
		public ArrayList<MyTreeItem> getMyTreeItemChildrenList() {return myTreeItemChildrenList;} 
		
		MyTreeItem(HtmlTextTree textTree, int iconWidthPx, int iconHeightPx)
		{
			this(new InlineHTML(textTree.getInlineHtmlString(iconWidthPx, iconHeightPx, true, false)));
			this.textTree = textTree;
			this.iconWidthPx = iconWidthPx;
			this.iconHeightPx = iconHeightPx;
			beenExpanded = false;
			if (textTree.hasChildren() || textTree.hasDataFolder()) 
			{
				inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FF0000\">* </font>" + inlineHTML.getHTML());
			}
			else
			{
				inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FFFFFF\">* </font>" + inlineHTML.getHTML());
			}
		}
		MyTreeItem(InlineHTML inlineHTML)
		{
			super(inlineHTML);
			this.inlineHTML = inlineHTML;
			beenExpanded = false;
		}
		public void expand()
		{
			if (beenExpanded) return;
			if (textTree.hasDataFolder())
			{
				MyTreeItem myTreeItem = new MyTreeItem(textTree.getDataFolder(), iconWidthPx, iconHeightPx);
				addItem(myTreeItem);
				myTreeItemChildrenList.add(myTreeItem);
			}
			if (textTree.hasChildren())
			{
				for (int ichild = 0; ichild < textTree.getTextTreeArrayList().size(); ++ichild)
				{
					MyTreeItem myTreeItem = new MyTreeItem(textTree.getTextTreeArrayList().get(ichild), iconWidthPx, iconHeightPx);
					addItem(myTreeItem);
					myTreeItemChildrenList.add(myTreeItem);
				}
			}
			inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FFFFFF\">* </font>" + textTree.getInlineHtmlString(iconWidthPx, iconHeightPx, true, false));
			beenExpanded = true;
			setState(true);
		}
	}
	class TreeSelectionHandler implements SelectionHandler<TreeItem>
	{
		@Override
		public void onSelection(SelectionEvent<TreeItem> event) 
		{
			MyTreeItem myTreeItem = (MyTreeItem) event.getSelectedItem();
			myTreeItem.expand();
		}
	}

}
