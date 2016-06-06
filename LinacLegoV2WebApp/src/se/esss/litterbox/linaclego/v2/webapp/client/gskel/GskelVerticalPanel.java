package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.v2.webapp.client.EntryPointAppServiceAsync;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelOptionDialog.GskelOptionDialogInterface;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelTabLayoutPanel.GSkelTabLayoutPanelInterface;

public abstract class GskelVerticalPanel extends VerticalPanel implements GSkelTabLayoutPanelInterface, GskelOptionDialogInterface
{
	private GskelSetupApp setupApp;
	private GskelTabLayoutScrollPanel gskelTabLayoutScrollPanel;
	private int tabIndex;

	public int getTabIndex() {return tabIndex;}
	public GskelSetupApp getSetupApp() {return setupApp;}
	public GskelTabLayoutScrollPanel getGskelTabLayoutScrollPanel() {return gskelTabLayoutScrollPanel;}

	public void setTabIndex(int tabIndex) {this.tabIndex = tabIndex;}

	public GskelVerticalPanel(String tabTitle, GskelSetupApp setupApp)
	{
		super();
		this.setupApp = setupApp;
		setWidth("100%");
		setHeight("100%");

		
		gskelTabLayoutScrollPanel = new GskelTabLayoutScrollPanel(tabTitle, this,  setupApp);
		getStatusTextArea().addStatus("Adding " + tabTitle);
	}
	public GskelMessageDialog getMessageDialog() {return getSetupApp().getMessageDialog();}
	public GskelStatusTextArea getStatusTextArea() {return getSetupApp().getStatusTextArea();}
	public GskelOptionDialog getOptionDialog() {return getSetupApp().getOptionDialog();}
	public boolean isDebug() {return getSetupApp().isDebug();}
	public EntryPointAppServiceAsync getEntryPointAppService() {return getSetupApp().getEntryPointAppService();}
}
