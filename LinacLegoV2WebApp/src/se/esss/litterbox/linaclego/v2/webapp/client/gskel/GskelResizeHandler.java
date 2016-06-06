package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;


public class GskelResizeHandler implements ResizeHandler
{
	private GskelSetupApp setupApp;
	public GskelResizeHandler(GskelSetupApp setupApp)
	{
		this.setupApp = setupApp;
	}
	@Override
	public void onResize(ResizeEvent event) 
	{
		setupApp.getStatusTextArea().setSize(Window.getClientWidth() - 10 - setupApp.getLogoPanelWidth(), setupApp.getStatusTextAreaHeight());
		setupApp.getGskelTabLayoutPanel().setSize(setupApp.getGskelTabLayoutPanelWidth(), setupApp.getGskelTabLayoutPanelHeight());			
	}
}
