package se.esss.litterbox.linaclego.v2.webapp.client;

import com.google.gwt.core.client.EntryPoint;

import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelSetupApp;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.TestGskelVerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EntryPointApp implements EntryPoint 
{
	private GskelSetupApp setupApp;

	public void onModuleLoad() 
	{
		setupApp = new GskelSetupApp();
		setupApp.setDebug(false);
		setupApp.setVersionDate("July 31, 2015 17:18");
		setupApp.setVersion("v1.0");
		setupApp.setAuthor("Dave McGinnis david.mcginnis@esss.se");
		setupApp.setLogoImage("images/essLogo.png");
		setupApp.setLogoTitle("GWT Skeleton");
		
		new TestGskelVerticalPanel("tab 1", setupApp);
		new TestGskelVerticalPanel("tab 2", setupApp);
		
		
	}
}
