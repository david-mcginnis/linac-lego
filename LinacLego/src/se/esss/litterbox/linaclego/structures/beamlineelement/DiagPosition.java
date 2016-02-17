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
package se.esss.litterbox.linaclego.structures.beamlineelement;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class DiagPosition  extends BeamLineElement
{
	private int diagNumber;
	private double xpos;
	private double ypos;
	private double resolution;
	public DiagPosition(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("N", "0.0", "int","unit");
		addDataElement("X", "0.0", "double","mm");
		addDataElement("Y", "0.0", "double", "mm");
		addDataElement("dm","0.0", "double", "mm");
	}
	@Override
	public void readDataElements() throws NumberFormatException, LinacLegoException 
	{
		diagNumber = Integer.parseInt(getDataElement("N").getValue());
		xpos = Double.parseDouble(getDataElement("X").getValue());
		ypos = Double.parseDouble(getDataElement("Y").getValue());
		resolution = Double.parseDouble(getDataElement("dm").getValue());
	}
	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "DIAG_POSITION";
		command = command + space +Integer.toString(diagNumber);
		command = command + space + Double.toString(xpos);
		command = command + space + Double.toString(ypos);
		command = command + space + Double.toString(resolution);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
//TODO implement DIAG_POSITION in DYNAC
		String command = ";DIAG_POSITION not in DYNAC";
		return command;
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		setLength(0.0);
	}
	@Override
	public void calcLocation() 
	{
		BeamLineElement previousBeamLineElement = getPreviousBeamLineElement();
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)
			{
				if (previousBeamLineElement != null)
					getEndRotMat()[ir][ic] = previousBeamLineElement.getEndRotMat()[ir][ic];
			}
			if (previousBeamLineElement != null)
				getEndPosVec()[ir] = previousBeamLineElement.getEndPosVec()[ir];
		}
	
		double[] localInputVec = {0.0, 0.0, getLength()};
		double[] localOutputVec = {0.0, 0.0, 0.0};
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
				localOutputVec[ir] = localOutputVec[ir] + getEndRotMat()[ir][ic] * localInputVec[ic];
			getEndPosVec()[ir] = getEndPosVec()[ir] + localOutputVec[ir];
		}
		
	}
	public double getXpos() {return xpos;}
	public double getYpos() {return ypos;}
	public double getRes() {return resolution;}
	public int getDiagNumber() {return diagNumber;}
	@Override
	public double characteristicValue() {return Math.abs(resolution);}
	@Override
	public String characteristicValueUnit() {return "mm";}
}
