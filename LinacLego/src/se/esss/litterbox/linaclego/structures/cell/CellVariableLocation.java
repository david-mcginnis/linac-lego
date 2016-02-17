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
package se.esss.litterbox.linaclego.structures.cell;

public class CellVariableLocation 
{
	private int nslot;
	private String dataId;
	private CellVariable cellVariable;
	
	public int getNslot() {return nslot;}
	public String getDataId() {return dataId;}
	public CellVariable getCellVariable() {return cellVariable;}

	public CellVariableLocation(int nslot, String dataId, CellVariable cellVariable)
	{
		this.nslot = nslot;
		this.dataId = dataId;
		this.cellVariable = cellVariable;
	}

}
