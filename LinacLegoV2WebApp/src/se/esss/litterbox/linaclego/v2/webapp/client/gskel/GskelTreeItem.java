package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TreeItem;

import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;


public class GskelTreeItem extends TreeItem 
{
	HtmlTextTree textTree;
	boolean beenExpanded = false;
	boolean open = false;
	InlineHTML inlineHTML;
	HorizontalPanel horizontalPanel;
	Anchor infoAnchor;
	String origHtml;
	int iconWidthPx;
	int iconHeightPx;
	ArrayList<GskelTreeItem> treeItemChildrenList = new ArrayList<GskelTreeItem>();
	GskelSetupApp setupApp;
	
	public ArrayList<GskelTreeItem> getGskelTreeItemChildrenList() {return treeItemChildrenList;} 
	
	GskelTreeItem(HtmlTextTree textTree, int iconWidthPx, int iconHeightPx, GskelSetupApp setupApp)
	{
		super(new HorizontalPanel());
		horizontalPanel = (HorizontalPanel) this.getWidget();
		inlineHTML = new InlineHTML(textTree.getInlineHtmlString(iconWidthPx, iconHeightPx, true, false));
		horizontalPanel.add(inlineHTML);
		this.setupApp = setupApp;
		this.textTree = textTree;
		this.iconWidthPx = iconWidthPx;
		this.iconHeightPx = iconHeightPx;
		this.setupApp = setupApp;
		beenExpanded = false;
		if (textTree.hasChildren() || textTree.hasDataFolder()) 
		{
			inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FF0000\">* </font>" + inlineHTML.getHTML());
		}
		else
		{
			inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FFFFFF\">* </font>" + inlineHTML.getHTML());
		}
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		if (textTree.getInfoLink() != null)
		{
			infoAnchor = new Anchor(textTree.getInfoLinkTitle());
			horizontalPanel.add(infoAnchor);
			infoAnchor.setStyleName("treeItemInfoAnchor");

		}
//		expand();
	}
	public void expand()
	{
		if (beenExpanded) return;
		if (textTree.getInfoLink() != null)
		{
			infoAnchor.addClickHandler(new InfoAnchorClickHandler(textTree.getInfoLink()));

		}
		if (textTree.hasDataFolder())
		{
			GskelTreeItem treeItem = new GskelTreeItem(textTree.getDataFolder(), iconWidthPx, iconHeightPx, setupApp);
			addItem(treeItem);
			treeItemChildrenList.add(treeItem);
		}
		if (textTree.hasChildren())
		{
			for (int ichild = 0; ichild < textTree.getTextTreeArrayList().size(); ++ichild)
			{
				GskelTreeItem treeItem = new GskelTreeItem(textTree.getTextTreeArrayList().get(ichild), iconWidthPx, iconHeightPx, setupApp);
				addItem(treeItem);
				treeItemChildrenList.add(treeItem);
			}
		}
		inlineHTML.setHTML("<font style=\"font-weight:bold;\" size=\"3px\" color=\"FFFFFF\">* </font>" + textTree.getInlineHtmlString(iconWidthPx, iconHeightPx, true, false));
		beenExpanded = true;
		setState(true);
	}
	class InfoAnchorClickHandler implements ClickHandler
	{
		private String link = "";
		InfoAnchorClickHandler(String link)
		{
			this.link = link;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
//			Window.open(link, "_blank", "");
			setupApp.getFrameDialog().setFrameUrl(link);
			setupApp.getFrameDialog().setMessage(link, "", true);
		}
		
	}

}
