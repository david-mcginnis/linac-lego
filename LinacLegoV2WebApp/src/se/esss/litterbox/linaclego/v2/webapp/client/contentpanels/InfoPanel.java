package se.esss.litterbox.linaclego.v2.webapp.client.contentpanels;

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

import se.esss.litterbox.linaclego.v2.webapp.client.EntryPointApp;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelSetupApp;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelVerticalPanel;


public class InfoPanel extends GskelVerticalPanel 
{
	private String currentSource = "Master";
	private String previousSource = "Master";
	private Label sourceViewLabel = new Label("Loading Master Source...");
	CaptionPanel latticeVersionCaptionPanel;
	InlineHTML latticeVersionInlineHTML;
	CaptionPanel sourceTypeCaptionPanel;
	private EntryPointApp entryPointApp;
	
	DownLoadClickHandler downloadXmlClickHandler;
	DownLoadClickHandler helpClickHandler;
	DownLoadClickHandler linacLegoAppClickHandler;
	
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

	public InfoPanel(String tabTitle, String tabStyle, GskelSetupApp setupApp, EntryPointApp entryPointApp)
	{
		super(tabTitle, tabStyle, setupApp);
		this.entryPointApp = entryPointApp;
		
		CaptionPanel versionCaptionPanel = new CaptionPanel("ESS Linac Parameter Book " + setupApp.getVersion());
		VerticalPanel versionVerticalPanel = new VerticalPanel();
		versionCaptionPanel.setContentWidget(versionVerticalPanel);
		versionVerticalPanel.add(new Label("Last Updated " + setupApp.getVersionDate()));

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

		downloadXmlClickHandler = new DownLoadClickHandler();
		helpClickHandler = new DownLoadClickHandler();
		linacLegoAppClickHandler = new DownLoadClickHandler();

		downloadXmlAnchor.addClickHandler(downloadXmlClickHandler);
		helpAnchor.addClickHandler(helpClickHandler);
		linacLegoAppAnchor.addClickHandler(linacLegoAppClickHandler);

		downloadsVerticalPanel.add(downloadXmlAnchor);
		downloadsVerticalPanel.add(helpAnchor);
		downloadsVerticalPanel.add(linacLegoAppAnchor);

		
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
		add(hp1);
		
	}
	public void setLinks(String downloadXmlLink, String helpLink, String linacLegoAppLink)
	{
		downloadXmlClickHandler.setLink(downloadXmlLink);
		helpClickHandler.setLink(helpLink);
		linacLegoAppClickHandler.setLink(linacLegoAppLink);
	}
	@Override
	public void optionDialogInterfaceAction(String choiceButtonText) 
	{
		previousSource = currentSource;
		currentSource = choiceButtonText;
		getSourceViewLabel().setText("Loading " + choiceButtonText + " source...");
		if (choiceButtonText.equals("Production"))
		{
			getSetupApp().getStatusTextArea().addStatus("Reloading Production Source...");
			entryPointApp.setLinks(entryPointApp.linacLegoMasterLink);
			entryPointApp.loadDataPanels();
		}
		if (choiceButtonText.equals("Development"))
		{
			previousSource = currentSource;
			getSetupApp().getStatusTextArea().addStatus("Reloading Development Source...");
			entryPointApp.setLinks(entryPointApp.linacLegoDevelopmentLink);
			entryPointApp.loadDataPanels();
		}
	}
	@Override
	public void tabLayoutPanelInterfaceAction(String message) {
		// TODO Auto-generated method stub
		
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
			infoPanel.getOptionDialog().setOption("Source Change", "Change Source", "Production", "Development", infoPanel);
			infoPanel.getOptionDialog().coverOverWidget(infoPanel.sourceTypeCaptionPanel);;
		}
		
	}
}
