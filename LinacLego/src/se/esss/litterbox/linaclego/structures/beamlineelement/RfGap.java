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

public class RfGap extends BeamLineElement
{
	double voltsT = 0.0;
	double rfPhaseDeg = 0.0;
	double radApermm = 0.0;
	int phaseFlag = 1;
	double betaS = 0.0;
	double tts = 0.0;
	double ktts = 0.0;
	double k2tts = 0.0;
	double ks = 0.0;
	double k2s = 0.0;
	double voltMult = 1.0;
	double phaseOffDeg = 0.0;

	public RfGap(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("voltsT", "0.0", "double", "Volt");
		addDataElement("rfPhaseDeg", "0.0", "double", "deg");
		addDataElement("radApermm", "0.0", "double", "mm");
		addDataElement("phaseFlag", "0", "int", "unit");
		addDataElement("betaS", "0.0", "double", "unit");
		addDataElement("tts", "0.0", "double", "unit");
		addDataElement("ktts", "0.0", "double", "unit");
		addDataElement("k2tts", "0.0", "double", "unit");
		addDataElement("ks", "0.0", "double", "unit");
		addDataElement("k2s", "0.0", "double", "unit");
		addDataElement("voltMult", "1.0", "double", "unit");
		addDataElement("phaseOffDeg", "0.0", "double", "deg");
	}
	@Override
	public void readDataElements() throws NumberFormatException, LinacLegoException  
	{
		voltsT = Double.parseDouble(getDataElement("voltsT").getValue());
		rfPhaseDeg = Double.parseDouble(getDataElement("rfPhaseDeg").getValue());
		radApermm = Double.parseDouble(getDataElement("radApermm").getValue());
		phaseFlag = Integer.parseInt(getDataElement("phaseFlag").getValue());
		betaS = Double.parseDouble(getDataElement("betaS").getValue());
		tts = Double.parseDouble(getDataElement("tts").getValue());
		ktts = Double.parseDouble(getDataElement("ktts").getValue());
		k2tts = Double.parseDouble(getDataElement("k2tts").getValue());
		ks = Double.parseDouble(getDataElement("ks").getValue());
		k2s = Double.parseDouble(getDataElement("k2s").getValue());
		voltMult = Double.parseDouble(getDataElement("voltMult").getValue());
		phaseOffDeg = Double.parseDouble(getDataElement("phaseOffDeg").getValue());
	}
	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "GAP";
		command = command + space + Double.toString(voltsT * voltMult);
		command = command + space + Double.toString(rfPhaseDeg + phaseOffDeg);
		command = command + space + Double.toString(radApermm);
		command = command + space + Integer.toString(phaseFlag);
		command = command + space + Double.toString(betaS);
		command = command + space + Double.toString(tts);
		command = command + space + Double.toString(ktts);
		command = command + space + Double.toString(k2tts);
		command = command + space + Double.toString(ks);
		command = command + space + Double.toString(k2s);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		command = "BUNCHER\n";
		command = command + space + Double.toString(voltsT * voltMult / 1.0e6);
		command = command + space + Double.toString(rfPhaseDeg + phaseOffDeg);
		command = command + space + Integer.toString(getSlot().getCell().getSection().getRfHarmonic());
		command = command + space + Double.toString(radApermm / 10.0);
		return command;
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		setLength(0.00);
		seteVout(geteVin() + voltsT * voltMult * Math.cos((rfPhaseDeg + phaseOffDeg) * degToRad));
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
	public double characteristicValue() {return Math.abs(voltsT * voltMult);}
	@Override
	public String characteristicValueUnit() {return "Volts";}
}
