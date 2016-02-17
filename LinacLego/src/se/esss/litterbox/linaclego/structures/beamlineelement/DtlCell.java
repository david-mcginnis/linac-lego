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

public class DtlCell  extends BeamLineElement
{
	double cellLenmm;
	double q1Lenmm;
	double q2Lenmm;
	double cellCentermm;
	double grad1Tpm;
	double grad2Tpm;
	double voltsT;
	double voltMult;
	double rfPhaseDeg;
	double phaseAdd;
	double radApermm;
	int phaseFlag;
	double betaS;
	double tts;
	double ktts;
	double k2tts;
	public DtlCell(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("cellLenmm", "0.0", "double", "mm");
		addDataElement("q1Lenmm", "0.0", "double", "mm");
		addDataElement("q2Lenmm", "0.0", "double", "mm");
		addDataElement("cellCentermm", "0.0", "double", "mm");
		addDataElement("grad1Tpm", "0.0", "double","T/m");
		addDataElement("grad2Tpm", "0.0", "double","T/m");
		addDataElement("voltsT", "0.0", "double","Volt");
		addDataElement("voltMult", "1.0", "double","unit");
		addDataElement("rfPhaseDeg", "0.0", "double","deg");
		addDataElement("phaseAdd", "0.0", "double","deg");
		addDataElement("radApermm", "0.0", "double", "mm");
		addDataElement("phaseFlag", "0", "int","unit");
		addDataElement("betaS", "0.0", "double","unit");
		addDataElement("tts", "0.0", "double","unit");
		addDataElement("ktts", "0.0", "double","unit");
		addDataElement("k2tts", "0.0", "double","unit");
	}
	@Override
	public void readDataElements() throws LinacLegoException
	{
		cellLenmm = Double.parseDouble(getDataElement("cellLenmm").getValue());
		q1Lenmm = Double.parseDouble(getDataElement("q1Lenmm").getValue());
		q2Lenmm = Double.parseDouble(getDataElement("q2Lenmm").getValue());
		cellCentermm = Double.parseDouble(getDataElement("cellCentermm").getValue());
		grad1Tpm = Double.parseDouble(getDataElement("grad1Tpm").getValue());
		grad2Tpm = Double.parseDouble(getDataElement("grad2Tpm").getValue());
		voltsT = Double.parseDouble(getDataElement("voltsT").getValue());
		voltMult = Double.parseDouble(getDataElement("voltMult").getValue());
		rfPhaseDeg = Double.parseDouble(getDataElement("rfPhaseDeg").getValue());
		phaseAdd = Double.parseDouble(getDataElement("phaseAdd").getValue());
		radApermm = Double.parseDouble(getDataElement("radApermm").getValue());
		phaseFlag = Integer.parseInt(getDataElement("phaseFlag").getValue());
		betaS = Double.parseDouble(getDataElement("betaS").getValue());
		tts = Double.parseDouble(getDataElement("tts").getValue());
		ktts = Double.parseDouble(getDataElement("ktts").getValue());
		k2tts = Double.parseDouble(getDataElement("k2tts").getValue());
	}
	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "DTL_CEL";
		command = command + space + Double.toString(cellLenmm);
		command = command + space + Double.toString(q1Lenmm);
		command = command + space + Double.toString(q2Lenmm);
		command = command + space + Double.toString(cellCentermm);
		command = command + space + Double.toString(grad1Tpm);
		command = command + space + Double.toString(grad2Tpm);
		command = command + space + Double.toString(voltsT * voltMult);
		command = command + space + Double.toString(rfPhaseDeg + phaseAdd);
		command = command + space + Double.toString(radApermm);
		command = command + space + Integer.toString(phaseFlag);
		command = command + space + Double.toString(betaS);
		command = command + space + Double.toString(tts);
		command = command + space + Double.toString(ktts);
		command = command + space + Double.toString(k2tts);
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		double cl = cellLenmm / 10.0;
		double ql1 = q1Lenmm / 10.0;
		double ql2 = q2Lenmm / 10.0;
		double shift = cellCentermm / 10.0;
		double qb1 = grad1Tpm;
		double qb2 = grad2Tpm;
		double ef = voltsT * voltMult;
		double phis = rfPhaseDeg + phaseAdd;
		double apt = radApermm / 10.0;
		double br = betaS;
		double  ttf = tts;
		double ttfp = -1.0 * ktts / TWOPI;
		double ttfpp = -1.0 * k2tts / (TWOPI * TWOPI);
		if (Math.abs(ef) > 0.1)
		{
			ef = ef / (ttf * cl * 10000.0);
			double fk = getRfFreqMHz() * TWOPI * 1.0e+06 / (br * cvel);
			ttfp = ttfp / fk;
			ttfpp = ttfpp / fk;
			if (Math.abs(qb1) > 0.000001)
			{
				command = command + "QUADRUPO\n";
				qb1 = 0.1 * qb1 * apt;
				command = command + space + fourPlaces.format(ql1);
				command = command + space + Double.toString(qb1);
				command = command + space + fourPlaces.format(apt);
				command = command + "\nDRIFT\n";
				command = command + space + Double.toString(-ql1 + shift);
			}
			if (command.length() > 0) command = command + "\n";
			command = command + "CAVSC\n";
			command = command + space + Integer.toString(0);
			command = command + space + Double.toString(0.0);
			command = command + space + Double.toString(br);
			command = command + space + fourPlaces.format(cl);
			command = command + space + Double.toString(ttf);
			command = command + space + Double.toString(ttfp);
			command = command + space + Double.toString(0.0);
			command = command + space + Double.toString(0.0);
			command = command + space + Double.toString(0.0);
			command = command + space + Double.toString(0.0);
			command = command + space + Double.toString(ef);
			command = command + space + fourPlaces.format(phis);
			command = command + space + Double.toString(0.0);
			command = command + space + Double.toString(ttfpp);
			command = command + space + Double.toString(getRfFreqMHz());
			command = command + space + Double.toString(1.0);
			if (Math.abs(qb2) > 0.000001)
			{
				command = command + "\nDRIFT\n";
				command = command + space + Double.toString(-ql2 - shift);
				command = command + "\nQUADRUPO\n";
				qb2 = 0.1 * qb2 * apt;
				command = command + space + fourPlaces.format(ql2);
				command = command + space + Double.toString(qb2);
				command = command + space + fourPlaces.format(apt);
			}
			else
			{
				if (Math.abs(qb1) > 0.000001)
				{
					command = command + "QUADRUPO\n";
					qb1 = 0.1 * qb1 * apt;
					command = command + space + fourPlaces.format(ql1);
					command = command + space + Double.toString(qb1);
					command = command + space + fourPlaces.format(apt);
					command = command + "\nDRIFT\n";
					command = command + space + fourPlaces.format(-ql1 + shift);
				}
				if (Math.abs(qb2) > 0.000001)
				{
					if (command.length() > 0) command = command + "\n";
					command = command + "DRIFT\n";
					command = command + space + fourPlaces.format(-ql2 - shift);
					command = command + "\nQUADRUPO\n";
					qb2 = 0.1 * qb2 * apt;
					command = command + space + fourPlaces.format(ql2);
					command = command + space + Double.toString(qb2);
					command = command + space + fourPlaces.format(apt);
				}
			}
		}
		return command;
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		setLength(0.001 * cellLenmm);
		setQuadGradientTpm(grad1Tpm);
		if (Math.abs(grad1Tpm) < Math.abs(grad2Tpm)) setQuadGradientTpm(grad2Tpm);

		double avgE;
		double beta;
		double volts;
		double eGain;
		double ttratio = 1.0;
		for (int ii = 0; ii < 3; ++ii)
		{
			avgE = 0.5 *(geteVin() + geteVout());
			beta = beta(avgE);
			ttratio = 1.0;
			if (betaS > 0.001) ttratio = transitTimeFactor(beta) / transitTimeFactor(betaS);
			volts = voltsT * voltMult * ttratio;
			eGain = volts * Math.cos((rfPhaseDeg + phaseAdd) * degToRad);
			seteVout(geteVin() + eGain);
		}
		setSynchronousPhaseDegrees((rfPhaseDeg + phaseAdd));
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
	private double transitTimeFactor(double beta)
	{
		double kappa = betaS / beta;
		double transitTime = tts;
		transitTime = transitTime + ktts * (kappa - 1);
		transitTime = transitTime + 0.5 * k2tts * (kappa - 1) * (kappa - 1);
		return transitTime;
	}
	@Override
	public double characteristicValue() {return getVoltage();}
	@Override
	public String characteristicValueUnit() {return "MV";}
}
