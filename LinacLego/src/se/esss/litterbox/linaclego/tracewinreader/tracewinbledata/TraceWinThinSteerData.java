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
package se.esss.litterbox.linaclego.tracewinreader.tracewinbledata;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.tracewinreader.TraceWinCommandReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class TraceWinThinSteerData extends TraceWinBleData
{

	public TraceWinThinSteerData(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "thinSteering";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"xkick",	"ykick",	"r",		"kickType"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"Tm",		"Tm",		"mm",		"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double",	"double",	"double",	"int"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0],	twd[1],		twd[2],	"0"};
		if (twd.length == 4) value[3] = twd[3];
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "CHV-";}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}

}
