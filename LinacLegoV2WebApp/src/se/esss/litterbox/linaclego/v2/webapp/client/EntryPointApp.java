package se.esss.litterbox.linaclego.v2.webapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;

import se.esss.litterbox.linaclego.v2.webapp.client.contentpanels.CsvLinkFilePanel;
import se.esss.litterbox.linaclego.v2.webapp.client.contentpanels.InfoPanel;
import se.esss.litterbox.linaclego.v2.webapp.client.contentpanels.PbsLayoutPanel;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelCsvFilePanel;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelSetupApp;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelTreePanel;
import se.esss.litterbox.linaclego.v2.webapp.shared.CsvFile;
import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;


/**z
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryPointApp implements EntryPoint 
{
	private GskelSetupApp setupApp;
	
	public final String latticeDataWeb = "https://aig.esss.lu.se:8443/LinacLegoData/data";
	public final String linacLegoMasterLink      = latticeDataWeb + "/development/linacLegoOutput";
	public final String linacLegoDevelopmentLink = latticeDataWeb + "/development/linacLegoOutput";
	private String linacLegoDataLink = linacLegoMasterLink;
	private InfoPanel infoPanel;
	private PbsLayoutPanel pbsLayoutPanel;
	private GskelTreePanel pbsTreePanel;
	private GskelTreePanel xmlTreePanel;
	private GskelCsvFilePanel sectionCsvFilePanel;
	private GskelCsvFilePanel cellCsvFilePanel;
	private CsvLinkFilePanel slotCsvFilePanel;
	private GskelCsvFilePanel beamCsvFilePanel;
	private CsvLinkFilePanel slotPartsCsvFilePanel;
	private GskelCsvFilePanel beamPartsCsvFilePanel;
	private GskelCsvFilePanel legoSetsCsvFilePanel;

	public void onModuleLoad() 
	{
		System.out.println("test");
		setupApp = new GskelSetupApp();
		setupApp.setDebug(false);
		setupApp.setVersionDate("July 16, 2016 08:28");
		setupApp.setVersion("v2.2");
		setupApp.setAuthor("Dave McGinnis david.mcginnis@esss.se");
		setupApp.setLogoImage("images/essLogo.png");
		setupApp.setLogoTitle("LinacLego Parameter  Book");
		
		infoPanel = new InfoPanel("Info", null, setupApp, this);
		pbsLayoutPanel = new PbsLayoutPanel("PBS Layout", null, "PBS",  setupApp);
		pbsTreePanel = new GskelTreePanel("PBS Tree", null, setupApp);
		xmlTreePanel = new GskelTreePanel("XML Tree", null, setupApp);
		sectionCsvFilePanel = new GskelCsvFilePanel("Section Data", 2, "csvFilePanelHeader", setupApp);
		cellCsvFilePanel = new GskelCsvFilePanel("Cell Data", 2, "csvFilePanelHeader", setupApp);
		slotCsvFilePanel = new CsvLinkFilePanel("Slot Data", 2, "csvFilePanelHeader", setupApp);
		beamCsvFilePanel = new GskelCsvFilePanel("Beam Data", 2, "csvFilePanelHeader", setupApp);
		slotPartsCsvFilePanel = new CsvLinkFilePanel("Slot Parts", 1, "partsFilePanelHeader", setupApp);
		beamPartsCsvFilePanel = new GskelCsvFilePanel("Beam Parts", 1, "partsFilePanelHeader", setupApp);
		legoSetsCsvFilePanel = new GskelCsvFilePanel("LegoSets", 1, "csvFilePanelHeader", setupApp);
		setLinks(linacLegoMasterLink);
		loadDataPanels();
	}
	public void loadDataPanels()
	{
		setupApp.getMessageDialog().setImageUrl("images/Scarecrow.jpg");
		setupApp.getMessageDialog().setMessage("Wait", "Loading data from the server...", false);
		setupApp.getEntryPointAppService().getTextTrees(linacLegoDataLink, new TextTreesAsyncCallback(this));
		setupApp.getEntryPointAppService().getCsvFile(linacLegoDataLink + "/linacLegoSectionData.csv", new LoadCsvFileCallback(this, sectionCsvFilePanel));
		setupApp.getEntryPointAppService().getCsvFile(linacLegoDataLink + "/linacLegoCellData.csv", new LoadCsvFileCallback(this, cellCsvFilePanel));
		setupApp.getEntryPointAppService().getCsvFile(linacLegoDataLink + "/linacLegoSlotData.csv", new LoadCsvLinkFileCallback(this, slotCsvFilePanel));
		setupApp.getEntryPointAppService().getCsvFile(linacLegoDataLink + "/linacLegoBeamData.csv", new LoadCsvFileCallback(this, beamCsvFilePanel));
		setupApp.getEntryPointAppService().getCsvFile(linacLegoDataLink + "/linacLegoSlotParts.csv", new LoadCsvLinkFileCallback(this, slotPartsCsvFilePanel));
		setupApp.getEntryPointAppService().getCsvFile(linacLegoDataLink + "/linacLegoBleParts.csv", new LoadCsvFileCallback(this, beamPartsCsvFilePanel));
		setupApp.getEntryPointAppService().getCsvFile(linacLegoDataLink + "/linacLegoSets.csv", new LoadCsvFileCallback(this, legoSetsCsvFilePanel));
	}
	public void setLinks(String linacLegoDataLink)
	{
		this.linacLegoDataLink = linacLegoDataLink;
		infoPanel.setLinks(
				linacLegoDataLink + "/linacLegoOutput.zip", 
				latticeDataWeb + "/doc/LinacLegoManual.pdf", 
				latticeDataWeb + "/dist/se.esss.litterbox.linaclego.v2.legoapp.jar");
		sectionCsvFilePanel.setSourceFileLink(linacLegoDataLink + "/linacLegoSectionData.csv");
		cellCsvFilePanel.setSourceFileLink(linacLegoDataLink + "/linacLegoCellData.csv");
		slotCsvFilePanel.setSourceFileLink(linacLegoDataLink + "/linacLegoSlotData.csv");
		beamCsvFilePanel.setSourceFileLink(linacLegoDataLink + "/linacLegoBeamData.csv");
		slotPartsCsvFilePanel.setSourceFileLink(linacLegoDataLink + "/linacLegoSlotParts.csv");
		beamPartsCsvFilePanel.setSourceFileLink(linacLegoDataLink + "/linacLegoBleParts.csv");
		legoSetsCsvFilePanel.setSourceFileLink(linacLegoDataLink + "/linacLegoSets.csv");
	}
	public static class TextTreesAsyncCallback implements AsyncCallback<HtmlTextTree[]>
	{
		EntryPointApp entryPointApp;
		TextTreesAsyncCallback(EntryPointApp entryPointApp)
		{
			this.entryPointApp = entryPointApp;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			entryPointApp.setupApp.getStatusTextArea().setText("Server Failure: " + caught.getMessage());
			entryPointApp.setupApp.getMessageDialog().setImageUrl("images/dagnabbit.jpg");
			entryPointApp.setupApp.getMessageDialog().setMessage("Error", "Failed to load Xml View data from server.", true);
		}
		@Override
		public void onSuccess(HtmlTextTree[] result) 
		{
			entryPointApp.pbsLayoutPanel.addTree(result[0]);
			entryPointApp.pbsTreePanel.addTree(result[0]);
			entryPointApp.pbsTreePanel.getRootTreeItem().expand();
			entryPointApp.pbsTreePanel.getRootTreeItem().getMyTreeItemChildrenList().get(0).expand();
			entryPointApp.xmlTreePanel.addTree(result[1]);
			entryPointApp.xmlTreePanel.getRootTreeItem().expand();
			entryPointApp.xmlTreePanel.getRootTreeItem().getMyTreeItemChildrenList().get(0).expand();
			entryPointApp.infoPanel.getLatticeVersionInlineHTML().setHTML(result[1].getInlineHtmlString(false, true));
			entryPointApp.infoPanel.getLatticeVersionCaptionPanel().setVisible(true);
			entryPointApp.setupApp.getMessageDialog().hide();
			entryPointApp.setupApp.getGskelTabLayoutPanel().selectTab(0);
			entryPointApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
			entryPointApp.infoPanel.getSourceViewLabel().setText("Currently viewing " + entryPointApp.infoPanel.getCurrentSource() + " Source");
		}
	}
	public static class LoadCsvFileCallback implements AsyncCallback<CsvFile>
	{
		EntryPointApp entryPointApp;
		GskelCsvFilePanel gskelCsvFilePanel;
		LoadCsvFileCallback(EntryPointApp entryPointApp, GskelCsvFilePanel gskelCsvFilePanel)
		{
			this.entryPointApp = entryPointApp;
			this.gskelCsvFilePanel = gskelCsvFilePanel;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			entryPointApp.setupApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			entryPointApp.setupApp.getMessageDialog().setImageUrl("images/dagnabbit.jpg");
			entryPointApp.setupApp.getMessageDialog().setMessage("Error", "Failed to load Xml View data from server.", true);
			entryPointApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
		}
		@Override
		public void onSuccess(CsvFile result) 
		{
			gskelCsvFilePanel.setCsvFile(result);
		}
	}
	public static class LoadCsvLinkFileCallback implements AsyncCallback<CsvFile>
	{
		EntryPointApp entryPointApp;
		CsvLinkFilePanel csvLinkFilePanel;
		LoadCsvLinkFileCallback(EntryPointApp entryPointApp, CsvLinkFilePanel csvLinkFilePanel)
		{
			this.entryPointApp = entryPointApp;
			this.csvLinkFilePanel = csvLinkFilePanel;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			entryPointApp.setupApp.getStatusTextArea().addStatus("Server Failure: " + caught.getMessage());
			entryPointApp.setupApp.getMessageDialog().setImageUrl("images/dagnabbit.jpg");
			entryPointApp.setupApp.getMessageDialog().setMessage("Error", "Failed to load Xml View data from server.", true);
			entryPointApp.infoPanel.getChangeSourceSelectButtonl().setVisible(true);
		}
		@Override
		public void onSuccess(CsvFile result) 
		{
			csvLinkFilePanel.setLinacLegoDataLink(entryPointApp.linacLegoDataLink);
			csvLinkFilePanel.setCsvFile(result);
		}
	}

}
