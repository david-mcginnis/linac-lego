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

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class GskelTabLayoutPanel extends TabLayoutPanel
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	
	private GskelSetupApp setupApp;
	private ArrayList<GskelVerticalPanel> gskelVerticalPanelList = new ArrayList<GskelVerticalPanel>();

	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}
	public GskelSetupApp getSetupApp() {return setupApp;}
	public ArrayList<GskelVerticalPanel> getGskelVerticalPanel() {return gskelVerticalPanelList;}

	public GskelTabLayoutPanel(int barHeightPx, GskelSetupApp setupApp, int panelWidth, int panelHeight) 
	{
		super((double) barHeightPx, Unit.PX);
		this.setupApp = setupApp;
		
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight + "px");

	    addSelectionHandler(new GskelTabLayoutPanelSelectionHandler(this));
	    	    
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
	public void addGskelTabLayoutScrollPanel(GskelTabLayoutScrollPanel gskelTabLayoutScrollPanel, String tabTitle)
	{
		gskelVerticalPanelList.add(gskelTabLayoutScrollPanel.getGskelVerticalPanel());
		gskelTabLayoutScrollPanel.getGskelVerticalPanel().setTabIndex(gskelVerticalPanelList.size() - 1);
		add(gskelTabLayoutScrollPanel,tabTitle);
	}
	class GskelTabLayoutPanelSelectionHandler implements SelectionHandler<Integer>
	{
		GskelTabLayoutPanel gskelTabLayoutPanel;
		GskelTabLayoutPanelSelectionHandler(GskelTabLayoutPanel gskelTabLayoutPanel)
		{
			this.gskelTabLayoutPanel = gskelTabLayoutPanel;
		}
		@Override
		public void onSelection(SelectionEvent<Integer> event) 
		{
			  gskelVerticalPanelList.get(event.getSelectedItem()).tabLayoutPanelInterfaceAction("selected!!!");
		}
	}
	public interface GSkelTabLayoutPanelInterface 
	{
		void tabLayoutPanelInterfaceAction(String message);

	}

}
