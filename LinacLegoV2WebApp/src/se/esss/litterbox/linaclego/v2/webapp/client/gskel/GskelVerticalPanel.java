package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.v2.webapp.client.EntryPointAppServiceAsync;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelOptionDialog.GskelOptionDialogInterface;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelTabLayoutPanel.GSkelTabLayoutPanelInterface;

public abstract class GskelVerticalPanel extends VerticalPanel implements GSkelTabLayoutPanelInterface, GskelOptionDialogInterface
{
	private GskelSetupApp setupApp;
	private GskelTabLayoutScrollPanel gskelTabLayoutScrollPanel;
	private String tabStyle = null;

	public GskelSetupApp getSetupApp() {return setupApp;}
	public GskelTabLayoutScrollPanel getGskelTabLayoutScrollPanel() {return gskelTabLayoutScrollPanel;}
	public String getTabStyle() {return tabStyle;}
	public int getTabValue() {return getGskelTabLayoutScrollPanel().getTabValue();}

	public GskelVerticalPanel(String tabTitle, String tabStyle, GskelSetupApp setupApp)
	{
		super();
		this.setupApp = setupApp;
		this.tabStyle = tabStyle;
		setWidth("100%");
		setHeight("100%");

		
		gskelTabLayoutScrollPanel = new GskelTabLayoutScrollPanel(tabTitle, this,  setupApp);
		if (tabStyle != null)
			setupApp.getGskelTabLayoutPanel().getTabWidget(getTabValue()).setStyleName(tabStyle);
		getStatusTextArea().addStatus("Adding " + tabTitle);
	}
	public GskelMessageDialog getMessageDialog() {return getSetupApp().getMessageDialog();}
	public GskelStatusTextArea getStatusTextArea() {return getSetupApp().getStatusTextArea();}
	public GskelOptionDialog getOptionDialog() {return getSetupApp().getOptionDialog();}
	public boolean isDebug() {return getSetupApp().isDebug();}
	public EntryPointAppServiceAsync getEntryPointAppService() {return getSetupApp().getEntryPointAppService();}
}
