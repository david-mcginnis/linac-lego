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
package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;

public class GskelTabLayoutScrollPanel extends ScrollPanel
{
	private int panelWidth = 0;
	private int panelHeight = 0;
	private GskelTabLayoutPanel gskelTabLayoutPanel;
	private GskelVerticalPanel gskelVerticalPanel;
	private GskelSetupApp setupApp;
	private int tabValue = -1;

	public GskelTabLayoutPanel getGskelTabLayoutPanel() {return gskelTabLayoutPanel;}
	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}
	public GskelVerticalPanel getGskelVerticalPanel() {return gskelVerticalPanel;}
	public GskelSetupApp getSetupApp() {return setupApp;}
	public int getTabValue() {return tabValue;}

	public GskelTabLayoutScrollPanel(String tabTitle, GskelVerticalPanel gskelVerticalPanel, GskelSetupApp setupApp)
	{
		super();
		this.gskelVerticalPanel = gskelVerticalPanel;
		this.setupApp = setupApp;
		setAlwaysShowScrollBars(true);
		add(gskelVerticalPanel);
		tabValue = getSetupApp().getGskelTabLayoutPanel().addGskelTabLayoutScrollPanel(this, tabTitle);
		reSize();
		Window.addResizeHandler(new GskelTabLayoutScrollPanelResizeHandler());
	}
	public void reSize()
	{
		panelWidth = setupApp.getGskelTabLayoutPanelWidth() - 15;
		panelHeight = setupApp.getGskelTabLayoutPanelHeight() 
				- setupApp.getGskelTabLayoutPanelHeightBarHeightPx() - 15;
		setSize(panelWidth + "px", panelHeight + "px");
	}
	
	public class GskelTabLayoutScrollPanelResizeHandler implements ResizeHandler
	{
		@Override
		public void onResize(ResizeEvent event) 
		{
			reSize();
		}
	}
}
