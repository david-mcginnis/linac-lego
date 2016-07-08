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
package se.esss.litterbox.linaclego.v2.webapp.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class CsvLine implements Serializable
{
	private ArrayList<String> cellList = null;
	public CsvLine()
	{
		cellList = new ArrayList<String>();
	}
	public CsvLine(String line)
	{
		this();
		addLine(line);
	}
	public void addLine(String line)
	{
        String delims = "[,]+";
        String[] splitResponse = null;
        splitResponse = line.split(delims);
        if (splitResponse.length > 0)
        {
        	for (int icell = 0; icell < splitResponse.length; ++ icell)
        		cellList.add(splitResponse[icell]);
        }
	}
	public int numCells()
	{
		return cellList.size();
	}
	public String getCell(int icell)
	{
		if (icell >= numCells()) return "";
		if (icell < 0) return "";
		return cellList.get(icell);
	}
	public void removeLastCell()
	{
		cellList.remove(numCells() - 1);
	}

}
