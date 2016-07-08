package se.esss.litterbox.linaclego.v2.webapp.client.contentpanels;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelTabLayoutScrollPanel;
import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;


public class PbsLevelPanel extends VerticalPanel
{
	HtmlTextTree textTree;
	ArrayList<PbsLevelPanel> childrenPbsLevelPanelList = new ArrayList<PbsLevelPanel>();
	VerticalPanel mainWrapperPanel;
	HorizontalPanel childrenPanel;
	boolean expanded = false;
	Grid dataPanel = null;
	HorizontalPanel tagAndButtonWrapperPanel;
	Button expandButton = new Button("+");
	Button collapseButton = new Button("-");
	Image iconImage;
	HorizontalPanel arrowLine1Panel;
	HorizontalPanel arrowLine2Panel;
	Image arrowLine1Image;
	boolean odd;
	int iconWidth = 50;
	int iconHeight = 50;
	int arrowHeight = 16;
	int arrowLine1Width = 64;
	int preExpansionWidth = -1;
	int oldWidth = 0;
	int ilevel = -1;
	PbsLevelPanel parentPbsLevelPanel;
	PbsLevelPanelTimer pbsLevelPanelTimer;
	PbsInfoDialogBox pbsInfoDialogBox;
	GskelTabLayoutScrollPanel gskelTabLayoutScrollPanel;
	
	public PbsLevelPanel(int ilevel, HtmlTextTree textTree, boolean odd, PbsLevelPanel parentPbsLevelPanel, GskelTabLayoutScrollPanel gskelTabLayoutScrollPanel)
	{
		super();
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.textTree = textTree;
		this.odd = odd;
		this.parentPbsLevelPanel = parentPbsLevelPanel;
		this.gskelTabLayoutScrollPanel = gskelTabLayoutScrollPanel;
		this.ilevel = ilevel;
		childrenPbsLevelPanelList = new ArrayList<PbsLevelPanel>();
		mainWrapperPanel = new VerticalPanel();

		mainWrapperPanel.add(setupElementPanel());

		pbsLevelPanelTimer = new PbsLevelPanelTimer(this);
		pbsLevelPanelTimer.scheduleRepeating(100);
		if (odd) setStyleName("pbsBorderOdd" + ilevel);
		if (!odd) setStyleName("pbsBorderEven" + ilevel);
		add(mainWrapperPanel);
	}
	private void addChildrenPanels()
	{
		childrenPanel = new HorizontalPanel();
		if (textTree.hasChildren())
		{
			boolean oddChild = true;
			for (int ichild = 0; ichild < textTree.getTextTreeArrayList().size(); ++ichild)
			{
				PbsLevelPanel childPanel = new PbsLevelPanel(ilevel + 1, textTree.getTextTreeArrayList().get(ichild), oddChild, this, gskelTabLayoutScrollPanel);
				childrenPbsLevelPanelList.add(childPanel);
				oddChild = !oddChild;
				childrenPanel.add(childPanel);
			}
			mainWrapperPanel.add(childrenPanel);
		}
	}
	private void expandAllIconPanel()
	{
		if (getOffsetWidth() < 1) return;
		arrowLine1Width = (getOffsetWidth() - iconWidth - arrowHeight) / 2;
		if (arrowLine1Width < 1) return;
		int arrowLine2Width = getOffsetWidth() - arrowLine1Width - iconWidth - arrowHeight;
		if (arrowLine2Width < 1) return;
		arrowLine1Panel.setWidth(arrowLine1Width + "px");
		arrowLine2Panel.setWidth(arrowLine2Width + "px");
		arrowLine1Image.setWidth(arrowLine1Width + "px");
	}
	private void removeChildren()
	{
		if (childrenPbsLevelPanelList.size() > 0)
		{
			for (int ichild = 0; ichild < childrenPbsLevelPanelList.size(); ++ichild)
			{
				childrenPbsLevelPanelList.get(ichild).pbsLevelPanelTimer.cancel();
				childrenPbsLevelPanelList.get(ichild).removeChildren();
				childrenPanel.remove(childrenPbsLevelPanelList.get(ichild));
			}
		}
		childrenPbsLevelPanelList.clear();
	}
	private void collapseAllIconPanel()
	{
		arrowLine1Panel.setWidth(1 + "px");
		arrowLine2Panel.setWidth(1 + "px");
		arrowLine1Image.setWidth(1 + "px");
		if (parentPbsLevelPanel != null ) parentPbsLevelPanel.collapseAllIconPanel();
	}
	private VerticalPanel setupElementPanel()
	{
		HorizontalPanel expandButtonPanel = new HorizontalPanel();
		HorizontalPanel collapseButtonPanel = new HorizontalPanel();
		expandButtonPanel.add(expandButton);
		collapseButtonPanel.add(collapseButton);
		
		arrowLine1Image  = new Image("images/blueLine.png");
		arrowLine1Image.setHeight(arrowHeight + "px");
		arrowLine1Image.setWidth(arrowLine1Width + "px");
		arrowLine1Panel = new HorizontalPanel();
		arrowLine1Panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		arrowLine1Panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		arrowLine1Panel.setWidth(arrowLine1Width + "px");
		arrowLine1Panel.setHeight("100%");
		arrowLine1Panel.add(arrowLine1Image);
		
		Image arrowLine2Image  = new Image("images/blueLine.png");
		arrowLine2Image.setHeight(arrowHeight + "px");
		arrowLine2Image.setWidth("100%");
		arrowLine2Panel = new HorizontalPanel();
		arrowLine2Panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		arrowLine2Panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		arrowLine2Panel.setWidth("100%");
		arrowLine2Panel.setHeight("100%");
		arrowLine2Panel.add(arrowLine2Image);
		
		iconImage = new Image(textTree.getIconImageLocation());
		iconImage.addClickHandler(new IconImageClickHandler(this));
		iconImage.setSize(iconWidth + "px", iconHeight + "px");
		Image arrowHeadImage = new Image("images/blueArrowHead.png");
		arrowHeadImage.setSize(arrowHeight + "px", arrowHeight + "px");

		HorizontalPanel iconAndArrowHeadPanel = new HorizontalPanel();
		iconAndArrowHeadPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		iconAndArrowHeadPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		iconAndArrowHeadPanel.setWidth(iconWidth + arrowHeight + "px");
		iconAndArrowHeadPanel.add(arrowHeadImage);
		iconAndArrowHeadPanel.add(iconImage);
		
		HorizontalPanel allIconPanel = new HorizontalPanel();
		allIconPanel.setWidth("100%");
		allIconPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LOCALE_START);
		allIconPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		allIconPanel.add(arrowLine1Panel);
		allIconPanel.add(iconAndArrowHeadPanel);
		allIconPanel.add(arrowLine2Panel);

		HorizontalPanel tagAndButtonPanel = new HorizontalPanel();
		tagAndButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		tagAndButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		tagAndButtonPanel.add(expandButtonPanel);
		if (textTree.getInfoLink() != null)
		{
			Anchor infoAnchor = new Anchor(textTree.getAttribute(0).getAttributeValue());
			tagAndButtonPanel.add(infoAnchor);
			infoAnchor.addClickHandler(new InfoAnchorClickHandler(textTree.getInfoLink()));
//			infoAnchor.setStyleName("treeItemInfoAnchor");

		}
		else
		{
			tagAndButtonPanel.add(new InlineHTML("<html>" + textTree.getAttribute(0).getAttributeValue() + "</html>"));
		}
		tagAndButtonPanel.add(collapseButtonPanel);
		tagAndButtonWrapperPanel = new HorizontalPanel();
		tagAndButtonWrapperPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		tagAndButtonWrapperPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		tagAndButtonWrapperPanel.setWidth("100%");
		tagAndButtonWrapperPanel.setHeight("40px");
		tagAndButtonWrapperPanel.add(tagAndButtonPanel);
		if (odd) tagAndButtonWrapperPanel.setStyleName("pbsBackGroundOdd" + ilevel);
		if (!odd) tagAndButtonWrapperPanel.setStyleName("pbsBackGroundEven" + ilevel);
		FocusPanel tagAndButtonWrapperFocusPanel = new FocusPanel();
		tagAndButtonWrapperFocusPanel.addClickHandler(new CenterClickHandler(this));
		tagAndButtonWrapperFocusPanel.add(tagAndButtonWrapperPanel);

		VerticalPanel iconAndTag = new VerticalPanel();
		iconAndTag.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		iconAndTag.setWidth("100%");
		iconAndTag.add(tagAndButtonWrapperFocusPanel);
		iconAndTag.add(allIconPanel);

		VerticalPanel wrapperPanel = new VerticalPanel();
		wrapperPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		wrapperPanel.add(iconAndTag);
		if (textTree.hasChildren())
		{
			expandButton.addClickHandler(new ExpandButtonClickHandler(this));
			expandButton.setVisible(true);
			collapseButton.addClickHandler(new CollapseButtonClickHandler(this));
			collapseButton.setVisible(false);
		}
		else
		{
			expandButton.setVisible(false);
			collapseButton.setVisible(false);
		}
		Grid attributePanel = new Grid(textTree.numAttributes(), 1);
		for (int ia = 0; ia < textTree.numAttributes(); ++ia)
		{
			attributePanel.setWidget(ia, 0, new InlineHTML(textTree.getAttribute(ia).getInlineHtmlString(false, false)));
		}
		
		Grid idPanel = new Grid(1, 1);
		idPanel.setWidget(0, 0, new Label(" "));
		wrapperPanel.add(idPanel);
		if (textTree.hasDataFolder())
		{
			dataPanel = new Grid(textTree.getDataFolder().numChildren(), 1);
			for (int ia = 0; ia < textTree.getDataFolder().numChildren(); ++ia)
			{
				String html = textTree.getDataFolder().getTextTreeArrayList().get(ia).getInlineHtmlString(false, false);
				dataPanel.setWidget(ia, 0, new InlineHTML(html));
			}
		}
		pbsInfoDialogBox = new PbsInfoDialogBox(textTree.getAttribute(1).getAttributeValue(), attributePanel, dataPanel);
		return wrapperPanel;
	}
	protected void expandPanel()
	{
		if (!textTree.hasChildren()) return;
		if (!expanded)
		{
			if (preExpansionWidth < 0) preExpansionWidth = getOffsetWidth();
			addChildrenPanels();
			expanded = true;
			expandButton.setVisible(true);
			expandAllIconPanel();
			collapseButton.setVisible(true);
		}
		else
		{
			if (childrenPbsLevelPanelList.size() > 0)
			{
				for (int ichild = 0; ichild < childrenPbsLevelPanelList.size(); ++ichild)
				{
					childrenPbsLevelPanelList.get(ichild).expandPanel();
				}
			}
			
		}
		
	}
	private void centerIconOnScreen()
	{
		int scrollPanelWidth;
		int offsetWidth;
		int newScrollHorzPos;

		scrollPanelWidth =  gskelTabLayoutScrollPanel.getPanelWidth();
		offsetWidth = mainWrapperPanel.getOffsetWidth();
		if (scrollPanelWidth < offsetWidth)
		{
			newScrollHorzPos =   (offsetWidth - scrollPanelWidth) / 2;
			if (newScrollHorzPos < 0 ) newScrollHorzPos = 0;
			gskelTabLayoutScrollPanel.setHorizontalScrollPosition(newScrollHorzPos);
		}
		
	}
	private void collapsePanel()
	{
		if (expanded)
		{
			gskelTabLayoutScrollPanel.setHorizontalScrollPosition(0);
			gskelTabLayoutScrollPanel.setVerticalScrollPosition(0);
			expandButton.setVisible(false);
			expandButton.setText("+");
			collapseAllIconPanel();
			removeChildren();
			expanded = false;
			expandButton.setVisible(true);
			int hscrollPos = getAbsoluteLeft();
			hscrollPos = hscrollPos + (getOffsetWidth() - gskelTabLayoutScrollPanel.getPanelWidth()) / 2;
			if (hscrollPos < 0) hscrollPos = 0;
			gskelTabLayoutScrollPanel.setHorizontalScrollPosition(hscrollPos);
			gskelTabLayoutScrollPanel.setVerticalScrollPosition(gskelTabLayoutScrollPanel.getMaximumVerticalScrollPosition());
			collapseButton.setVisible(false);
		}
		
	}
	static class CenterClickHandler implements ClickHandler
	{
		PbsLevelPanel parentPbsLevelPanel;
		
		CenterClickHandler(PbsLevelPanel parentPbsLevelPanel)
		{
			this.parentPbsLevelPanel = parentPbsLevelPanel;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			parentPbsLevelPanel.centerIconOnScreen();
		}
	}
	static class ExpandButtonClickHandler implements ClickHandler
	{
		PbsLevelPanel parentPbsLevelPanel;
		
		ExpandButtonClickHandler(PbsLevelPanel parentPbsLevelPanel)
		{
			this.parentPbsLevelPanel = parentPbsLevelPanel;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			parentPbsLevelPanel.expandPanel();
			parentPbsLevelPanel.centerIconOnScreen();
		}
	}
	static class CollapseButtonClickHandler implements ClickHandler
	{
		PbsLevelPanel parentPbsLevelPanel;
		
		CollapseButtonClickHandler(PbsLevelPanel parentPbsLevelPanel)
		{
			this.parentPbsLevelPanel = parentPbsLevelPanel;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			parentPbsLevelPanel.collapsePanel();
		}
	}
	class IconImageClickHandler implements ClickHandler
	{
		PbsLevelPanel parentPbsLevelPanel;
		
		IconImageClickHandler(PbsLevelPanel parentPbsLevelPanel)
		{
			this.parentPbsLevelPanel = parentPbsLevelPanel;
		}

		@Override
		public void onClick(ClickEvent event) 
		{
			parentPbsLevelPanel.pbsInfoDialogBox.setPopupPosition(
					parentPbsLevelPanel.iconImage.getAbsoluteLeft(), parentPbsLevelPanel.iconImage.getAbsoluteTop());
			parentPbsLevelPanel.pbsInfoDialogBox.show();	
		}
	}
	class PbsLevelPanelTimer extends Timer
	{
		PbsLevelPanel parentPbsLevelPanel;
		PbsLevelPanelTimer(PbsLevelPanel parentPbsLevelPanel)
		{
			this.parentPbsLevelPanel = parentPbsLevelPanel;
		}
		@Override
		public void run() 
		{
			if (parentPbsLevelPanel.getOffsetWidth() != parentPbsLevelPanel.oldWidth)
			{
				parentPbsLevelPanel.expandAllIconPanel();
				parentPbsLevelPanel.oldWidth = getOffsetWidth(); 

			}
			
		}
		
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
			gskelTabLayoutScrollPanel.getGskelVerticalPanel().getSetupApp().getFrameDialog().setFrameUrl(link);
			gskelTabLayoutScrollPanel.getGskelVerticalPanel().getSetupApp().getFrameDialog().setMessage(link, "", true);
		}
		
	}

}
