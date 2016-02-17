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

public class Bend  extends BeamLineElement
{
	private double twBendAngleDeg;
	private double radOfCurvmm;
	private int fieldIndex;
	private double aperRadmm;
	private int HVflag;
	private double k1in;
	private double k2in;
	private double k1out;
	private double k2out;
	
	public Bend(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("bendAngleDeg", "0.0", "double", "deg");
		addDataElement("radOfCurvmm", "0.0", "double", "mm");
		addDataElement("fieldIndex", "0", "int", "unit");
		addDataElement("aperRadmm", "0.0", "double", "mm");
		addDataElement("HVflag", "0", "int", "unit");
		addDataElement("K1in", "0.0", "double", "unit");
		addDataElement("K2in", "0.0", "double", "unit");
		addDataElement("K1out", "0.0", "double", "unit");
		addDataElement("K2out", "0.0", "double", "unit");
	}
	@Override
	public void readDataElements() throws LinacLegoException
	{
		twBendAngleDeg = Double.parseDouble(getDataElement("bendAngleDeg").getValue());
		radOfCurvmm = Double.parseDouble(getDataElement("radOfCurvmm").getValue());
		aperRadmm = Double.parseDouble(getDataElement("aperRadmm").getValue());
		fieldIndex = Integer.parseInt(getDataElement("fieldIndex").getValue());
		HVflag = Integer.parseInt(getDataElement("HVflag").getValue());
		k1in = Double.parseDouble(getDataElement("K1in").getValue());
		k2in = Double.parseDouble(getDataElement("K2in").getValue());
		k1out = Double.parseDouble(getDataElement("K1out").getValue());
		k2out = Double.parseDouble(getDataElement("K2out").getValue());
	}
	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "BEND";
		command = command + space + Double.toString(twBendAngleDeg);
		command = command + space + Double.toString(radOfCurvmm);
		command = command + space + Integer.toString(fieldIndex);
		command = command + space + Double.toString(aperRadmm);
		command = command + space + Integer.toString(HVflag);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		// TODO Need to fix edge fields
		String command = "";
		command = "BMAGNET\n";
		command = command + space + Integer.toString(1) + newline; //NSEC
		command = command + space + Double.toString(twBendAngleDeg);
		command = command + space + Double.toString(radOfCurvmm / 10.0);
		command = command + space + Double.toString(0.0); //BAIM in kG
		command = command + space + Double.toString(0.0); //XN
		command = command + space + Double.toString(0.0) + newline; //XB
		command = command + space + Double.toString(0.0); //PENT1 in deg
		command = command + space + Double.toString(0.0); //RAB1 in cm
		command = command + space + Double.toString(k1in); //EK1
		command = command + space + Double.toString(k2in); //EK2
		command = command + space + Double.toString(aperRadmm / 10.0) + newline; //APB(1)
		command = command + space + Double.toString(0.0); //PENT2 in deg
		command = command + space + Double.toString(0.0); //RAB2 in cm
		command = command + space + Double.toString(k1out); //SK1
		command = command + space + Double.toString(k2out); //SK2
		command = command + space + Double.toString(aperRadmm / 10.0); //APB(2)
		return command;
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		setLength(Math.abs(twBendAngleDeg) * degToRad * radOfCurvmm * 0.001);
	}

	@Override
	public void calcLocation() 
	{
		BeamLineElement previousBeamLineElement = getPreviousBeamLineElement();
		double[][] prevEndRotMat = { {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
		if (previousBeamLineElement != null)
			prevEndRotMat = previousBeamLineElement.getEndRotMat();
		for (int ir = 0; ir  < 3; ++ir)
		{
			if (previousBeamLineElement != null)
				getEndPosVec()[ir] = previousBeamLineElement.getEndPosVec()[ir];
		}
// Because of TraceWin convention of +bend = right turn in H plane
		double bendAngleDeg = -twBendAngleDeg;
		double cosTheta = Math.cos(bendAngleDeg * degToRad);
		double sinTheta = Math.sin(bendAngleDeg * degToRad);
		double[][] localVertRotMat = { {1.0, 0.0, 0.0}, {0.0, cosTheta, sinTheta}, {0.0, -sinTheta, cosTheta}};
		double[][] localHorzRotMat = { {cosTheta, 0.0, sinTheta}, {0.0, 1.0, 0.0}, {-sinTheta, 0.0, cosTheta}};
		double[][] localRotMat = localHorzRotMat;
		if (HVflag == 1) localRotMat = localVertRotMat;
		double dz = radOfCurvmm * 0.001 * Math.abs(sinTheta);
		double dv = radOfCurvmm * 0.001 * (1.0 - cosTheta);
		if (bendAngleDeg < 0) dv = -dv;
		double[] localHorzInputVec = {dv, 0.0, dz};
		double[] localVertInputVec = {0.0, dv, dz};
		double[] localInputVec = localHorzInputVec;
		if (HVflag == 1) localInputVec = localVertInputVec;
		
		double[] localOutputVec = {0.0, 0.0, 0.0};
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
				localOutputVec[ir] = localOutputVec[ir] + prevEndRotMat[ir][ic] * localInputVec[ic];
			getEndPosVec()[ir] = getEndPosVec()[ir] + localOutputVec[ir];
		}
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
			{
				getEndRotMat()[ir][ic] = 0.0;
				for (int ik = 0; ik < 3; ++ik)	
				{
					getEndRotMat()[ir][ic] = getEndRotMat()[ir][ic] + localRotMat[ir][ik] * prevEndRotMat[ik][ic];
				}
				
			}
		}
	}
	public double getBfieldTesla()
	{
		return pc(geteVin()) / (radOfCurvmm * 0.001 * cvel);
	}
	@Override
	public double characteristicValue() {return Math.abs(getBfieldTesla());}
	@Override
	public String characteristicValueUnit() {return "Tesla";}
}
