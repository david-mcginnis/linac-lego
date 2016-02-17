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
package se.esss.litterbox.linaclego.webapp.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.ui.TextArea;

public class StatusTextArea extends TextArea
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	private ArrayList<String> statusList = new ArrayList<String>();
	private int maxBufferSize = 100;
	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}

	public StatusTextArea(int panelWidth, int panelHeight)
	{
		super();
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");
	}
	public void setSize(int panelWidth, int panelHeight)
	{
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");
	}
	public void addStatus(String status)
	{
		statusList.add(0, new Date().toString() + ": " + status);
		int statusListSize = statusList.size();
		while (statusListSize > maxBufferSize)
		{
			statusList.remove(statusListSize - 1);
			statusListSize = statusList.size();
		}
		String text = "";
		for (int ii = 0; ii < statusListSize; ++ii)
		{
			text = text + statusList.get(ii) + "\n";
		}
		setText(text);
	}
	public int getBufferMaxSize() {return maxBufferSize;}
	public void setMaxBufferSize(int maxBufferSize) {this.maxBufferSize = maxBufferSize;}
}
