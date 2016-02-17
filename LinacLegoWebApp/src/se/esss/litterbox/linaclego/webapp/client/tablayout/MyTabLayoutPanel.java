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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import se.esss.litterbox.linaclego.webapp.client.LinacLegoWebApp;

public class MyTabLayoutPanel extends TabLayoutPanel
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	
	private LinacLegoWebApp linacLegoWebApp;

	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}
	public LinacLegoWebApp getLinacLegoWebApp() {return linacLegoWebApp;}

	public MyTabLayoutPanel(int barHeightPx, LinacLegoWebApp linacLegoWebApp, int panelWidth, int panelHeight) 
	{
		super((double) barHeightPx, Unit.PX);
		this.linacLegoWebApp = linacLegoWebApp;
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight + "px");

	    addSelectionHandler(new MyTabLayoutPanelSelectionHandler(this));
	    	    
	}
	public void setSize(int panelWidth, int panelHeight)
	{
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");
	}
	public void showTab(int itab, boolean showTab)
	{
	    getTabWidget(itab).setVisible(showTab);
	    getTabWidget(itab).getParent().setVisible(showTab);
	}
	class MyTabLayoutPanelSelectionHandler implements SelectionHandler<Integer>
	{
		MyTabLayoutPanel myTabLayoutPanel;
		MyTabLayoutPanelSelectionHandler(MyTabLayoutPanel myTabLayoutPanel)
		{
			this.myTabLayoutPanel = myTabLayoutPanel;
		}
		@Override
		public void onSelection(SelectionEvent<Integer> event) 
		{
//			int tabId = event.getSelectedItem();
		}
	}

}
