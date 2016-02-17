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

public class DtlDriftTube extends BeamLineElement 
{
	private double noseConeUpLen;
	private double noseConeDnLen;
	private double radius;
	private double quadLen;
	private double quadGrad;
	
	public double getNoseConeUpLen() {return noseConeUpLen;}
	public double getNoseConeDnLen() {return noseConeDnLen;}
	public double getRadius() {return radius;}
	public double getQuadLen() {return quadLen;}
	public double getQuadGrad() {return quadGrad;}

	public DtlDriftTube(SimpleXmlReader elementTag, Slot slot, int beamLineElementIndex) throws LinacLegoException
	{
		super(elementTag, slot, beamLineElementIndex);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("noseConeUpLen", "0.0", "double", "mm");
		addDataElement("noseConeDnLen", "0.0", "double", "mm");
		addDataElement("radius", "0.0", "double", "mm");
		addDataElement("quadLen", "0.0", "double", "mm");
		addDataElement("quadGrad", "0.0", "double", "T/m");
	}
	@Override
	public void readDataElements() throws LinacLegoException
	{
		noseConeUpLen = Double.parseDouble(getDataElement("noseConeUpLen").getValue());
		noseConeDnLen = Double.parseDouble(getDataElement("noseConeDnLen").getValue());
		quadLen = Double.parseDouble(getDataElement("quadLen").getValue());
		radius = Double.parseDouble(getDataElement("radius").getValue());
		quadGrad = Double.parseDouble(getDataElement("quadGrad").getValue());
	}
	@Override
	public void calcParameters() 
	{
		setLength(0.001 * (noseConeUpLen + noseConeDnLen + quadLen));
		setQuadGradientTpm(quadGrad);
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
		command = command + "DRIFT";
		command = command + space + fourPlaces.format(noseConeUpLen);
		command = command + space + fourPlaces.format(radius);
		command = command + space + "0.0";
		command = command + "\n";
		command = command + "QUAD";
		command = command + space + Double.toString(quadLen);
		command = command + space + Double.toString(quadGrad);
		command = command + space + Double.toString(radius);
		command = command + "\n";
		command = command + "DRIFT";
		command = command + space + fourPlaces.format(noseConeDnLen);
		command = command + space + fourPlaces.format(radius);
		command = command + space + "0.0";
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		command = command + "DRIFT\n";
		command = command + space + fourPlaces.format(noseConeUpLen / 10.0);
		command = command + "\n";
		command = command + "QUADRUPO\n";
		command = command + space + fourPlaces.format(quadLen / 10.0);
		command = command + space + sixPlaces.format(quadGrad * radius * .001 * 10);
		command = command + space + fourPlaces.format(radius / 10.0);
		command = command + "\n";
		command = command + "DRIFT\n";
		command = command + space + fourPlaces.format(noseConeDnLen / 10.0);
		return command;
	}
	@Override
	public double characteristicValue() {return Math.abs(quadGrad);}
	@Override
	public String characteristicValueUnit() {return "T/m";}
}
