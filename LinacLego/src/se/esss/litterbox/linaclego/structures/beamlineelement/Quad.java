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


public class Quad extends BeamLineElement 
{
	private double lengthMM = 0.0;
	private double gradTpM = 0.0;
	private double radius = 0.0;
	
	public Quad(SimpleXmlReader elementTag, Slot slot, int beamLineElementIndex) throws LinacLegoException
	{
		super(elementTag, slot, beamLineElementIndex);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("l", "0.0", "double","mm");
		addDataElement("g", "0.0", "double","T/m");
		addDataElement("r", "0.0", "double","mm");
	}
	@Override
	public void readDataElements() throws NumberFormatException, LinacLegoException 
	{
		lengthMM = Double.parseDouble(getDataElement("l").getValue());
		gradTpM = Double.parseDouble(getDataElement("g").getValue());
		radius = Double.parseDouble(getDataElement("r").getValue());
	}
	@Override
	public void calcParameters() throws NumberFormatException, LinacLegoException 
	{
		setLength(0.001 * Double.parseDouble(getDataElement("l").getValue()));
		setQuadGradientTpm(gradTpM);
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
	@Override
	public String makeTraceWinCommand()  
	{
		String command = "";
		command = "QUAD";
		command = command + space + Double.toString(lengthMM);
		command = command + space + Double.toString(gradTpM);
		command = command + space + Double.toString(radius);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		command = "QUADRUPO\n";
		command = command + space + fourPlaces.format(lengthMM / 10.0);
		command = command + space + sixPlaces.format(gradTpM * radius * .001 * 10);
		command = command + space + fourPlaces.format(radius / 10.0);
		return command;
	}
	public double getLengthMM() {return lengthMM;}
	public double getGradTpM() {return gradTpM;}
	public double getRadius() {return radius;}
	@Override
	public double characteristicValue() {return Math.abs(gradTpM);}
	@Override
	public String characteristicValueUnit() {return "Tesla/m";}
}
