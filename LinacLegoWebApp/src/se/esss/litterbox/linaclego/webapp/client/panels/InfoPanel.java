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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.webapp.client.LinacLegoWebApp;
import se.esss.litterbox.linaclego.webapp.client.OptionDialog.OptionDialogInterface;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutPanel;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutScrollPanel;

public class InfoPanel extends MyTabLayoutScrollPanel implements OptionDialogInterface
{
	private String currentSource = "Master";
	private String previousSource = "Master";
	private Label sourceViewLabel = new Label("Loading Master Source...");
	CaptionPanel latticeVersionCaptionPanel;
	private CaptionPanel drawingListCaptionPanel;
	InlineHTML latticeVersionInlineHTML;
	CaptionPanel sourceTypeCaptionPanel;
	
	DownLoadClickHandler downloadXmlClickHandler;
	DownLoadClickHandler helpClickHandler;
	DownLoadClickHandler linacLegoAppClickHandler;
	DownLoadClickHandler sourceWebFolderClickHandler;
	
	LinacLegoWebApp linacLegoWebApp;
	Button changeSourceSelectButton;
	Button masterSourceSelectButton;
	Button developmentSourceSelectButton;
	
	public CaptionPanel getLatticeVersionCaptionPanel() {return latticeVersionCaptionPanel;}
	public InlineHTML getLatticeVersionInlineHTML() {return latticeVersionInlineHTML;}
	public String getCurrentSource() {return currentSource;}
	public String getPreviousSource() {return previousSource;}
	public Label getSourceViewLabel() {return sourceViewLabel;}
	public Button getChangeSourceSelectButtonl() {return changeSourceSelectButton;}
	
	public void setCurrentSource(String currentSource) {this.currentSource = currentSource;}

	public InfoPanel(MyTabLayoutPanel myTabLayoutPanel)
	{
		super(myTabLayoutPanel);
		myTabLayoutPanel.add(this, "Info");
		linacLegoWebApp = myTabLayoutPanel.getLinacLegoWebApp();
		
		CaptionPanel versionCaptionPanel = new CaptionPanel("ESS Linac Parameter Book " + myTabLayoutPanel.getLinacLegoWebApp().getVersion());
		VerticalPanel versionVerticalPanel = new VerticalPanel();
		versionCaptionPanel.setContentWidget(versionVerticalPanel);
		versionVerticalPanel.add(new Label("Last Updated " + myTabLayoutPanel.getLinacLegoWebApp().getVersionDate()));

		CaptionPanel programmerCaptionPanel = new CaptionPanel("Maintained by");
		VerticalPanel programmerVerticalPanel = new VerticalPanel();
		programmerCaptionPanel.setContentWidget(programmerVerticalPanel);
		programmerVerticalPanel.add(new Label("Dave McGinnis"));
		programmerVerticalPanel.add(new Label("email: david.mcginnis@esss.se"));

		CaptionPanel downloadsCaptionPanel = new CaptionPanel("Downloads");
		VerticalPanel downloadsVerticalPanel = new VerticalPanel();
		downloadsCaptionPanel.setContentWidget(downloadsVerticalPanel);

		latticeVersionInlineHTML = new InlineHTML();
		latticeVersionCaptionPanel = new CaptionPanel("Lattice Version");
		latticeVersionCaptionPanel.setContentWidget(latticeVersionInlineHTML);
		latticeVersionCaptionPanel.setVisible(false);

		Anchor downloadXmlAnchor = new Anchor("Download XML files");
		Anchor helpAnchor = new Anchor("LinacLego Manual");
		Anchor linacLegoAppAnchor = new Anchor("LinacLego Application");
		Anchor sourceWebFolderAnchor = new Anchor("Source Web Folder");

		downloadXmlClickHandler = new DownLoadClickHandler();
		helpClickHandler = new DownLoadClickHandler();
		linacLegoAppClickHandler = new DownLoadClickHandler();
		sourceWebFolderClickHandler = new DownLoadClickHandler();

		downloadXmlAnchor.addClickHandler(downloadXmlClickHandler);
		helpAnchor.addClickHandler(helpClickHandler);
		linacLegoAppAnchor.addClickHandler(linacLegoAppClickHandler);
		sourceWebFolderAnchor.addClickHandler(sourceWebFolderClickHandler);

		downloadsVerticalPanel.add(downloadXmlAnchor);
		downloadsVerticalPanel.add(helpAnchor);
		downloadsVerticalPanel.add(linacLegoAppAnchor);
		downloadsVerticalPanel.add(sourceWebFolderAnchor);

		
		changeSourceSelectButton = new Button("Change");
		changeSourceSelectButton.addClickHandler(new SourceButtonClickHandler(this));

		sourceTypeCaptionPanel = new CaptionPanel("Current Source");
		VerticalPanel sourceTypeVPanel = new VerticalPanel();
		HorizontalPanel sourceTypeHPanel = new HorizontalPanel();
		sourceTypeHPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		sourceTypeHPanel.add(sourceViewLabel);
		sourceTypeHPanel.add(changeSourceSelectButton);
		sourceTypeVPanel.add(sourceTypeHPanel);
		
		sourceTypeCaptionPanel.add(sourceTypeVPanel);
		
		
		drawingListCaptionPanel = new CaptionPanel("Drawing Index");

		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(versionCaptionPanel);
		vp1.add(programmerCaptionPanel);
		vp1.add(downloadsCaptionPanel);

		VerticalPanel vp2 = new VerticalPanel();
		vp2.add(latticeVersionCaptionPanel);
		vp2.add(sourceTypeCaptionPanel);

		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(vp1);
		hp1.add(vp2);
		hp1.add(drawingListCaptionPanel);
		add(hp1);
		
	}
	public void setDrawingListCaptionPanelWidget(DrawingListPanel drawingListPanel )
	{
		drawingListCaptionPanel.setContentWidget(drawingListPanel);;
	}
	public void setLinks(String downloadXmlLink, String helpLink, String linacLegoAppLink, String linacLegoLink)
	{
		downloadXmlClickHandler.setLink(downloadXmlLink);
		helpClickHandler.setLink(helpLink);
		linacLegoAppClickHandler.setLink(linacLegoAppLink);
		sourceWebFolderClickHandler.setLink(linacLegoLink);
	}
	@Override
	public void optionDialogChoice(String choiceButtonText) 
	{
		previousSource = currentSource;
		currentSource = choiceButtonText;
		getSourceViewLabel().setText("Loading " + choiceButtonText + " source...");
		if (choiceButtonText.equals("Production"))
		{
			linacLegoWebApp.getStatusTextArea().addStatus("Reloading Production Source...");
			linacLegoWebApp.setLinks(linacLegoWebApp.linacLegoMasterLink);
			linacLegoWebApp.loadDataPanels();
		}
		if (choiceButtonText.equals("Development"))
		{
			previousSource = currentSource;
			linacLegoWebApp.getStatusTextArea().addStatus("Reloading Development Source...");
			linacLegoWebApp.setLinks(linacLegoWebApp.linacLegoDevelopmentLink);
			linacLegoWebApp.loadDataPanels();
		}
	}
	static class DownLoadClickHandler implements ClickHandler
	{
		private String link = "";

		public String getLink() {return link;}
		public void setLink(String link) {this.link = link;}

		DownLoadClickHandler()
		{
		}
		@Override
		public void onClick(ClickEvent event) {
			
//			Window.open(link, "_blank", "enabled");
			Window.open(link, "_blank", "");
		}
		
	}
	static class SourceButtonClickHandler implements ClickHandler
	{
		InfoPanel infoPanel;
		String clickType;
		SourceButtonClickHandler(InfoPanel infoPanel)
		{
			this.infoPanel = infoPanel;
		}
		@Override
		public void onClick(ClickEvent event) 
		{

			infoPanel.changeSourceSelectButton.setVisible(false);
			infoPanel.linacLegoWebApp.getOptionDialog().setOption("Source Change", "Change Source", "Production", "Development", infoPanel);
			infoPanel.linacLegoWebApp.getOptionDialog().coverOverWidget(infoPanel.sourceTypeCaptionPanel);;
		}
		
	}
}
