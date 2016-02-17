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

public class ThinSteering  extends BeamLineElement
{
	private double xkick;
	private double ykick;
	private double rmm;
	private int kickType;
	public ThinSteering(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("xkick", "0.0", "double","Tm");
		addDataElement("ykick", "0.0", "double","Tm");
		addDataElement("r", "0.0", "double", "mm");
		addDataElement("kickType", "0", "double", "unit");
	}
	@Override
	public void readDataElements() throws NumberFormatException, LinacLegoException 
	{
		xkick = Double.parseDouble(getDataElement("xkick").getValue());
		ykick = Double.parseDouble(getDataElement("ykick").getValue());
		rmm = Double.parseDouble(getDataElement("r").getValue());
		kickType = Integer.parseInt(getDataElement("kickType").getValue());
	}
	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "THIN_STEERING";
		command = command + space + Double.toString(xkick);
		command = command + space + Double.toString(ykick);
		command = command + space + Double.toString(rmm);
		command = command + space + Integer.toString(kickType);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
//TODO implement thing steering in DYNAC
		String command = ";Thin Steering not in DYNAC";
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
	public double getXkick() {return xkick;}
	public double getYkick() {return ykick;}
	public double getRmm() {return rmm;}
	public int getKickType() {return kickType;}
	@Override
	public double characteristicValue() {return Math.abs(xkick) + Math.abs(ykick);}
	@Override
	public String characteristicValueUnit() {return "Tesla-m";}
}
