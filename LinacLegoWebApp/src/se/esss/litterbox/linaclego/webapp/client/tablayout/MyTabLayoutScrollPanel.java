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
package se.esss.litterbox.linaclego.webapp.client.tablayout;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ScrollPanel;

public class MyTabLayoutScrollPanel extends ScrollPanel
{
	private int panelWidth = 0;
	private int panelHeight = 0;
	private MyTabLayoutPanel myTabLayoutPanel;

	public MyTabLayoutPanel getMyTabLayoutPanel() {return myTabLayoutPanel;}
	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}

	public MyTabLayoutScrollPanel(MyTabLayoutPanel myTabLayoutPanel)
	{
		super();
		this.myTabLayoutPanel = myTabLayoutPanel;

		setAlwaysShowScrollBars(true);
		reSize();
		Window.addResizeHandler(new MyResizeHandler());
	}
	public void reSize()
	{
		panelWidth = myTabLayoutPanel.getLinacLegoWebApp().myTabLayoutPanelWidth() - 15;
		panelHeight = myTabLayoutPanel.getLinacLegoWebApp().myTabLayoutPanelHeight() 
				- myTabLayoutPanel.getLinacLegoWebApp().getMyTabLayoutPanelHeightBarHeightPx() - 15;
		setSize(panelWidth + "px", panelHeight + "px");
	}
	public class MyResizeHandler implements ResizeHandler
	{
		@Override
		public void onResize(ResizeEvent event) 
		{
			reSize();
		}
	}
}
