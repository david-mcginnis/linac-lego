package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.v2.webapp.client.EntryPointAppService;
import se.esss.litterbox.linaclego.v2.webapp.client.EntryPointAppServiceAsync;


public class GskelSetupApp 
{
	private final EntryPointAppServiceAsync entryPointAppService = GWT.create(EntryPointAppService.class);
	private boolean debug = false;
	private String versionDate = "July 31, 2015 17:18";
	private String version = "v1.9";
	private String author = "Dave McGinnis david.mcginnis@esss.se";
	private GskelStatusTextArea statusTextArea;
	private GskelTabLayoutPanel gskelTabLayoutPanel;
	private GskelOptionDialog optionDialog;
	private GskelMessageDialog messageDialog;

	private int statusTextAreaHeight = 150;
	private int gskelTabLayoutPanelHeightBarHeightPx = 30;
	private int logoPanelWidth = 200;
	private Image logoImage = new Image("images/essLogo.png");
	private Label titleLabel = new Label("ESS Linac Parameter Book");


// Getters
	public boolean isDebug() {return debug;}
	public String getVersion() {return version;}
	public String getVersionDate() {return versionDate;}
	public String getAuthor() {return author;}
	public GskelStatusTextArea getStatusTextArea() {return statusTextArea;}
	public GskelOptionDialog getOptionDialog() {return optionDialog;}
	public GskelMessageDialog getMessageDialog() {return messageDialog;}
	public GskelTabLayoutPanel getGskelTabLayoutPanel() {return gskelTabLayoutPanel;}
	public int getGskelTabLayoutPanelHeightBarHeightPx() {return gskelTabLayoutPanelHeightBarHeightPx;}
	public int getStatusTextAreaHeight() {return statusTextAreaHeight;}
	public int getLogoPanelWidth() {return logoPanelWidth;}
	public EntryPointAppServiceAsync getEntryPointAppService() {return entryPointAppService;}
// Setters
	public void setDebug(boolean debug) {this.debug = debug;}
	public void setVersionDate(String versionDate) {this.versionDate = versionDate;}
	public void setVersion(String version) {this.version = version;}
	public void setAuthor(String author) {this.author = author;}
	
	public GskelSetupApp()
	{
		gskelTabLayoutPanel = new GskelTabLayoutPanel(gskelTabLayoutPanelHeightBarHeightPx, this, getGskelTabLayoutPanelWidth(), getGskelTabLayoutPanelHeight());
		statusTextArea = new GskelStatusTextArea(Window.getClientWidth() - 10, statusTextAreaHeight);
	    statusTextArea.setMaxBufferSize(100);
	    statusTextArea.addStatus("Welcome! Version: " + version + " Last updated on: " + versionDate + " by " + author);

        optionDialog =  new GskelOptionDialog();
        messageDialog =  new GskelMessageDialog();
	    VerticalPanel logoPanel = new VerticalPanel();
		logoPanel.setWidth(logoPanelWidth + "px");
		logoPanel.add(logoImage);
	    titleLabel.setStyleName("titleLabel");
	    logoPanel.add(titleLabel);
		
		HorizontalPanel hp1 = new HorizontalPanel();
	    
		hp1.add(logoPanel);
		hp1.add(statusTextArea);
		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(gskelTabLayoutPanel);
	    vp1.add(hp1);
		RootLayoutPanel.get().add(vp1);
		Window.addResizeHandler(new GskelResizeHandler(this));

	}
	public void setLogoImage(String logoImageUrl)
	{
		logoImage.setUrl(logoImageUrl);
		messageDialog.getLogoImage().setUrl(logoImageUrl);
		optionDialog.getLogoImage().setUrl(logoImageUrl);
	}
	public void setLogoTitle(String logoTitle)
	{
		titleLabel.setText(logoTitle);
	}
	public int getGskelTabLayoutPanelWidth()
	{
		return Window.getClientWidth() + 10 - 15;
	}
	public int getGskelTabLayoutPanelHeight()
	{
		return Window.getClientHeight() - statusTextAreaHeight - 15;
	}

}
