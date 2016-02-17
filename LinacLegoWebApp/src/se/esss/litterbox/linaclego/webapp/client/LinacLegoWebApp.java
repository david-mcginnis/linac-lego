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
package se.esss.litterbox.linaclego.webapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.webapp.client.panels.CsvFilePanel;
import se.esss.litterbox.linaclego.webapp.client.panels.DrawingListPanel;
import se.esss.litterbox.linaclego.webapp.client.panels.InfoPanel;
import se.esss.litterbox.linaclego.webapp.client.panels.PartsFilePanel;
import se.esss.litterbox.linaclego.webapp.client.panels.PbsLayoutPanel;
import se.esss.litterbox.linaclego.webapp.client.panels.TreeViewPanel;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutPanel;
import se.esss.litterbox.linaclego.webapp.shared.CsvFile;
import se.esss.litterbox.linaclego.webapp.shared.HtmlTextTree;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LinacLegoWebApp implements EntryPoint 
{
	String version = "v1.14";
	String versionDate = "July 28, 2015";

	private int statusTextAreaHeight = 150;
	private int myTabLayoutPanelHeightBarHeightPx = 30;
	private int logoPanelWidth = 200;

	public LinacLegoServiceAsync getLinacLegoService() {return linacLegoService;}
	public String getVersion() {return version;}
	public String getVersionDate() {return versionDate;}

	private final LinacLegoServiceAsync linacLegoService = GWT.create(LinacLegoService.class);
	public final String aigWeb = "https://aig.esss.lu.se:8443/LatticeRepository/data";
	public final String linacLegoMasterLink      = aigWeb + "/productionXml";
	public final String linacLegoDevelopmentLink = aigWeb + "/developmentXml";
	private String linacLegoLink;
	private String linacLegoXmlLink;
	private String parsedLinacLegoXmlLink;

	private StatusTextArea statusTextArea;
	private OptionDialog optionDialog;
	private MyTabLayoutPanel myTabLayoutPanel;
	private InfoPanel infoPanel;
	private TreeViewPanel pbsViewPanel;
	private TreeViewPanel xmlViewPanel;
	private CsvFilePanel cellDataPanel;
	private CsvFilePanel slotDataPanel;
	private CsvFilePanel bleDataPanel;
	private CsvFilePanel monDataPanel;
	private PartsFilePanel cellPartsFilePanel;
	private PartsFilePanel slotPartsFilePanel;
	private PartsFilePanel blePartsFilePanel;
	private PartsFilePanel lmonPartsFilePanel;
	private CsvFilePanel legoSetsCsvFilePanel;
	private DrawingListPanel drawingListPanel;
	private PbsLayoutPanel pbsLayoutPanel;
	private MessageDialog messageDialog;
	
	public StatusTextArea getStatusTextArea() {return statusTextArea;}
	public int getMyTabLayoutPanelHeightBarHeightPx() {return myTabLayoutPanelHeightBarHeightPx;}
	public String getLinacLegoLink() {return linacLegoLink;}
	public MessageDialog getMessageDialog() {return messageDialog;}
	public DrawingListPanel getDrawingListPanel() {return drawingListPanel;}
	public InfoPanel getInfoPanel() {return infoPanel;}
	public OptionDialog getOptionDialog() {return optionDialog;}

	public void setLinacLegoLink(String linacLegoLink) {this.linacLegoLink = linacLegoLink;}

	public void onModuleLoad() 
	{
		statusTextArea = new StatusTextArea(Window.getClientWidth() - 10, statusTextAreaHeight);
	    statusTextArea.setMaxBufferSize(100);
	    statusTextArea.addStatus("Welcome!");
	    statusTextArea.addStatus("Getting data from server..");
        optionDialog =  new OptionDialog();

        myTabLayoutPanel = new MyTabLayoutPanel(myTabLayoutPanelHeightBarHeightPx, this, myTabLayoutPanelWidth(), myTabLayoutPanelHeight());
		infoPanel  = new InfoPanel(myTabLayoutPanel);
		pbsLayoutPanel = new PbsLayoutPanel(myTabLayoutPanel, "PBS Layout", "PBS");
		pbsViewPanel = new TreeViewPanel(myTabLayoutPanel, "PBS Tree", "PBS");
		xmlViewPanel = new TreeViewPanel(myTabLayoutPanel, "XML Tree", "XML");
		cellDataPanel = new CsvFilePanel(myTabLayoutPanel, "Cell data", "Cell data", 2);
		slotDataPanel = new CsvFilePanel(myTabLayoutPanel, "Slot data", "Slot data", 2);
		bleDataPanel = new CsvFilePanel(myTabLayoutPanel, "BLE data", "BLE data", 2);
		monDataPanel = new CsvFilePanel(myTabLayoutPanel, "Monitor data", "BLE Monitor", 2);
		cellPartsFilePanel = new PartsFilePanel(myTabLayoutPanel, "Cell Parts", "Cell Parts");
		slotPartsFilePanel = new PartsFilePanel(myTabLayoutPanel, "Slot Parts", "Slot Parts");
		blePartsFilePanel = new PartsFilePanel(myTabLayoutPanel, "Beam-line Parts", "BLE Parts");
		lmonPartsFilePanel = new PartsFilePanel(myTabLayoutPanel, "Monitor Parts", "Monitor Parts");
		legoSetsCsvFilePanel = new CsvFilePanel(myTabLayoutPanel, "Lego Sets", "Lego Sets", 1);
		drawingListPanel = new DrawingListPanel(this);

		myTabLayoutPanel.getTabWidget(4).setStyleName("csvFilePanelHeader");
		myTabLayoutPanel.getTabWidget(5).setStyleName("csvFilePanelHeader");
		myTabLayoutPanel.getTabWidget(6).setStyleName("csvFilePanelHeader");
		myTabLayoutPanel.getTabWidget(7).setStyleName("csvFilePanelHeader");
		myTabLayoutPanel.getTabWidget(8).setStyleName("partsFilePanelHeader");
		myTabLayoutPanel.getTabWidget(9).setStyleName("partsFilePanelHeader");
		myTabLayoutPanel.getTabWidget(10).setStyleName("partsFilePanelHeader");
		myTabLayoutPanel.getTabWidget(11).setStyleName("partsFilePanelHeader");
		
		HorizontalPanel hp1 = new HorizontalPanel();
		VerticalPanel logoPanel = new VerticalPanel();
		logoPanel.setWidth(logoPanelWidth + "px");
		Image image = new Image("images/essLogo.png");
		logoPanel.add(image);
	    Label titleLabel = new Label("ESS Linac Parameter Book");
	    titleLabel.setStyleName("titleLabel");
	    logoPanel.add(titleLabel);
	    
		hp1.add(logoPanel);
		hp1.add(statusTextArea);
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(myTabLayoutPanel);
	    vp1.add(hp1);
		RootLayoutPanel.get().add(vp1);
		
		Window.addResizeHandler(new MyResizeHandler());
		setLinks(linacLegoMasterLink);
		messageDialog = new MessageDialog("Message");
		
		loadDataPanels();
	}
	public void setLinks(String linacLegoLink)
	{
		this.linacLegoLink = linacLegoLink;
		String helpLink = aigWeb + "/doc/LinacLegoManual.pdf";
		String linacLegoAppLink = aigWeb + "/dist/LinacLegoApp.jar";

		String linacLegoWebSitePartsDirectoryLink = linacLegoLink + "/linacLegoOutput";
		String downloadXmlLink = linacLegoLink + "/linacLego.zip";
		
		linacLegoXmlLink = linacLegoLink + "/linacLego.xml";
		parsedLinacLegoXmlLink = linacLegoLink + "/linacLegoParsed.xml";
		cellDataPanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoCellData.csv");
		slotDataPanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoSlotData.csv");
		bleDataPanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoBleData.csv");
		monDataPanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoMonitorData.csv");
		cellPartsFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoCellParts.csv");
		slotPartsFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoSlotParts.csv");
		blePartsFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoBleParts.csv");
		lmonPartsFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoMonitorParts.csv");
		legoSetsCsvFilePanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoLegoSet.csv");
		drawingListPanel.setSourceFileLink(linacLegoWebSitePartsDirectoryLink + "/linacLegoInfoLinks.csv");
		infoPanel.setLinks(downloadXmlLink, helpLink, linacLegoAppLink, linacLegoLink);
	}
	public void loadDataPanels()
	{
		messageDialog.getMessageImage().setUrl("images/Scarecrow.jpg");
		messageDialog.getMessageLabel().setText("Loading data from the server...");
		messageDialog.showMe(true);
		infoPanel.getChangeSourceSelectButtonl().setVisible(false);
		infoPanel.getLatticeVersionCaptionPanel().setVisible(false);
		getLinacLegoService().getXmlViewHtmlTextTree(parsedLinacLegoXmlLink, new LoadXmlViewPanelsCallback(this));
		getLinacLegoService().getPbsViewHtmlTextTree(linacLegoXmlLink, new LoadPbsViewPanelsCallback(this));	
		getLinacLegoService().getCsvFile(cellDataPanel.getSourceFileLink(), new LoadCsvFileCallback(this, cellDataPanel));
		getLinacLegoService().getCsvFile(slotDataPanel.getSourceFileLink(), new LoadCsvFileCallback(this, slotDataPanel));
		getLinacLegoService().getCsvFile(bleDataPanel.getSourceFileLink(), new LoadCsvFileCallback(this, bleDataPanel));
		getLinacLegoService().getCsvFile(monDataPanel.getSourceFileLink(), new LoadCsvFileCallback(this, monDataPanel));
		getLinacLegoService().getCsvFile(cellPartsFilePanel.getSourceFileLink(), new LoadPartsFileCallback(this, cellPartsFilePanel));
		getLinacLegoService().getCsvFile(slotPartsFilePanel.getSourceFileLink(), new LoadPartsFileCallback(this, slotPartsFilePanel));
		getLinacLegoService().getCsvFile(blePartsFilePanel.getSourceFileLink(), new LoadPartsFileCallback(this, blePartsFilePanel));
		getLinacLegoService().getCsvFile(lmonPartsFilePanel.getSourceFileLink(), new LoadPartsFileCallback(this, lmonPartsFilePanel));
		getLinacLegoService().getCsvFile(legoSetsCsvFilePanel.getSourceFileLink(), new LoadCsvFileCallback(this, legoSetsCsvFilePanel));
		getLinacLegoService().getCsvFile(drawingListPanel.getSourceFileLink(), new LoadDrawingListFileCallback(this, drawingListPanel));
	}
	public int myTabLayoutPanelWidth()
	{
		return Window.getClientWidth() + 10 - 15;
	}
	public int myTabLayoutPanelHeight()
	{
		return Window.getClientHeight() - statusTextAreaHeight - 15;
	}
	public class MyResizeHandler implements ResizeHandler
	{
		@Override
		public void onResize(ResizeEvent event) 
		{
			statusTextArea.setSize(Window.getClientWidth() - 10 - logoPanelWidth, statusTextAreaHeight);
			myTabLayoutPanel.setSize(myTabLayoutPanelWidth(), myTabLayoutPanelHeight());			
			if (messageDialog.getIsShowing()) messageDialog.center();
		}
	}
	public static class LoadXmlViewPanelsCallback implements AsyncCallback<HtmlTextTree>
	{
		LinacLegoWebApp linacLegoWebApp;
		LoadXmlViewPanelsCallback(LinacLegoWebApp linacLegoWebApp)
		{
			this.linacLegoWebApp = linacLegoWebApp;
		}
		@Override
		public void onFailure(Throwable caught) 
		{			
			linacLegoWebApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			linacLegoWebApp.getMessageDialog().getMessageImage().setUrl("images/dagnabbit.jpg");
			linacLegoWebApp.getMessageDialog().getMessageLabel().setText("Failed to load Xml View data from server.");
			linacLegoWebApp.getMessageDialog().showMe(true);
			linacLegoWebApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
			linacLegoWebApp.infoPanel.setCurrentSource(linacLegoWebApp.infoPanel.getPreviousSource());
			linacLegoWebApp.infoPanel.getSourceViewLabel().setText("Currently viewing " + linacLegoWebApp.infoPanel.getCurrentSource() + " Source");
		}
		@Override
		public void onSuccess(HtmlTextTree result) 
		{
			linacLegoWebApp.infoPanel.getLatticeVersionInlineHTML().setHTML(result.getInlineHtmlString(false, true));
			linacLegoWebApp.infoPanel.getLatticeVersionCaptionPanel().setVisible(true);
			linacLegoWebApp.xmlViewPanel.addTree(result);
			linacLegoWebApp.xmlViewPanel.getRootTreeItem().expand();
			linacLegoWebApp.xmlViewPanel.getRootTreeItem().getMyTreeItemChildrenList().get(0).expand();
			linacLegoWebApp.xmlViewPanel.getRootTreeItem().getMyTreeItemChildrenList().get(1).expand();
//			linacLegoWebApp.infoPanel.getMessagePanel().setVisible(false);
			linacLegoWebApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
			linacLegoWebApp.infoPanel.getSourceViewLabel().setText("Currently viewing " + linacLegoWebApp.infoPanel.getCurrentSource() + " Source");
		}
	}
	public static class LoadPbsViewPanelsCallback implements AsyncCallback<HtmlTextTree>
	{
		LinacLegoWebApp linacLegoWebApp;
		LoadPbsViewPanelsCallback(LinacLegoWebApp linacLegoWebApp)
		{
			this.linacLegoWebApp = linacLegoWebApp;
		}
		@Override
		public void onFailure(Throwable caught) 
		{			
			linacLegoWebApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			linacLegoWebApp.getMessageDialog().getMessageImage().setUrl("images/dagnabbit.jpg");
			linacLegoWebApp.getMessageDialog().getMessageLabel().setText("Failed to load Xml View data from server.");
			linacLegoWebApp.getMessageDialog().showMe(true);
			linacLegoWebApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
		}
		@Override
		public void onSuccess(HtmlTextTree result) 
		{
			linacLegoWebApp.pbsViewPanel.addTree(result);
			linacLegoWebApp.pbsViewPanel.getRootTreeItem().expand();
			linacLegoWebApp.pbsViewPanel.getRootTreeItem().getMyTreeItemChildrenList().get(0).expand();
			linacLegoWebApp.pbsLayoutPanel.addTree(result);
			linacLegoWebApp.myTabLayoutPanel.selectTab(0);
			linacLegoWebApp.getMessageDialog().showMe(false);
		}
	}
	public static class LoadCsvFileCallback implements AsyncCallback<CsvFile>
	{
		LinacLegoWebApp linacLegoWebApp;
		CsvFilePanel csvFilePanel;
		LoadCsvFileCallback(LinacLegoWebApp linacLegoWebApp, CsvFilePanel csvFilePanel)
		{
			this.linacLegoWebApp = linacLegoWebApp;
			this.csvFilePanel = csvFilePanel;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			linacLegoWebApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			linacLegoWebApp.getMessageDialog().getMessageImage().setUrl("images/dagnabbit.jpg");
			linacLegoWebApp.getMessageDialog().getMessageLabel().setText("Failed to load Xml View data from server.");
			linacLegoWebApp.getMessageDialog().showMe(true);
			linacLegoWebApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
		}
		@Override
		public void onSuccess(CsvFile result) 
		{
			csvFilePanel.setCsvFile(result);
		}
	}
	public static class LoadPartsFileCallback implements AsyncCallback<CsvFile>
	{
		LinacLegoWebApp linacLegoWebApp;
		PartsFilePanel partsFilePanel;
		LoadPartsFileCallback(LinacLegoWebApp linacLegoWebApp, PartsFilePanel partsFilePanel)
		{
			this.linacLegoWebApp = linacLegoWebApp;
			this.partsFilePanel = partsFilePanel;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			linacLegoWebApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			linacLegoWebApp.getMessageDialog().getMessageImage().setUrl("images/dagnabbit.jpg");
			linacLegoWebApp.getMessageDialog().getMessageLabel().setText("Failed to load Xml View data from server.");
			linacLegoWebApp.getMessageDialog().showMe(true);
			linacLegoWebApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
		}
		@Override
		public void onSuccess(CsvFile result) 
		{
			partsFilePanel.setPartsFile(result);
		}
	}
	public static class LoadDrawingListFileCallback implements AsyncCallback<CsvFile>
	{
		LinacLegoWebApp linacLegoWebApp;
		DrawingListPanel drawingListPanel;
		LoadDrawingListFileCallback(LinacLegoWebApp linacLegoWebApp, DrawingListPanel drawingListPanel)
		{
			this.linacLegoWebApp = linacLegoWebApp;
			this.drawingListPanel = drawingListPanel;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			linacLegoWebApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			linacLegoWebApp.getMessageDialog().getMessageImage().setUrl("images/dagnabbit.jpg");
			linacLegoWebApp.getMessageDialog().getMessageLabel().setText("Failed to load Xml View data from server.");
			linacLegoWebApp.getMessageDialog().showMe(true);
			linacLegoWebApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
		}
		@Override
		public void onSuccess(CsvFile result) 
		{
			drawingListPanel.setDrawingListFile(result);
		}
	}

}
