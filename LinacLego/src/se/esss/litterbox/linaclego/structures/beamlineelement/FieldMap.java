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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import se.esss.litterbox.linaclego.FieldProfileBuilder;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class FieldMap extends BeamLineElement
{
	double lengthmm = -1;
	double xelmax = 0.0;
	double radiusmm = 0.0;
	String fieldMapFileName;
	double phisdeg = -361.0;
	double rfpdeg = -361.0;
	FieldProfileBuilder fieldProfileBuilder = null;
	int betaInt = 5; //no of iterations to get average cavity beta
	double[] phiZprofile;
	double scaleFactor = 1.0;
	private boolean newFieldProfileEncountered = true;
	
	public FieldMap(SimpleXmlReader elementTag, Slot slot, int beamLineElementIndex) throws LinacLegoException 
	{
		super(elementTag, slot, beamLineElementIndex);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("rfpdeg", "0.0", "double", "deg");
		addDataElement("xelmax", "0.0", "double", "unit");
		addDataElement("radiusmm", "0.0", "double", "mm");
		addDataElement("lengthmm", "0.0", "double", "mm");
		addDataElement("file", null, "String", "unit");
		addDataElement("scaleFactor", "1.0", "double", "unit");
	}
	@Override
	public void readDataElements() throws LinacLegoException  
	{
		xelmax = Double.parseDouble(getDataElement("xelmax").getValue());
		radiusmm = Double.parseDouble(getDataElement("radiusmm").getValue());
		fieldMapFileName = getDataElement("file").getValue();
		lengthmm = Double.parseDouble(getDataElement("lengthmm").getValue());
		scaleFactor = Double.parseDouble(getDataElement("scaleFactor").getValue());
		rfpdeg = Double.parseDouble(getDataElement("rfpdeg").getValue());
	}
	private boolean lengthOk()
	{
		if (fieldProfileBuilder.getZmax() == lengthmm ) return true;
		return false;
	}

	@Override
	public String makeTraceWinCommand() 
	{
		String command = "";
		command = "FIELD_MAP 100";
		command = command + space + fourPlaces.format(lengthmm);
		command = command + space + fourPlaces.format(rfpdeg);
		command = command + space + fourPlaces.format(radiusmm);
		command = command + space + "0";
		command = command + space + Double.toString(xelmax);
		command = command + space + "0 0";
		command = command + space + fieldMapFileName.split("\\.")[0];
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException
	{
		String command = "";
		double fxelmax = (xelmax - 1.0) * 100.0;
		if (newFieldProfileEncountered)
		{
			command = command + "FIELD" + newline + fieldProfileBuilder.getTitle() + ".txt" + newline + scaleFactor + newline;
		}
		command = command + "CAVNUM" + newline + "1" + newline + "0.00" + space + twoPlaces.format(phisdeg) + space + eightPlaces.format(fxelmax);
		return command;
	}
	@Override
	public void calcParameters() throws LinacLegoException  
	{
		try 
		{
			URL fieldProfileBuilderUrl = new URL(getLinacLego().getSimpleXmlDoc().getXmlSourceParentUrl() + "/" + fieldMapFileName + ".xml");
//			getLinacLego().writeStatus(fieldProfileBuilderUrl.toString());
//			getLinacLego().writeStatus(getLinac().getFieldProfileBuilderUrl().toString());
			if (fieldProfileBuilderUrl.toString().equals(getLinac().getFieldProfileBuilderUrl().toString()))
			{
				if (scaleFactor != 1.0)
						throw new LinacLegoException(getAddress() + ": scaleFactor does not equal 1.0 ");
				newFieldProfileEncountered = false;
			}
			else
			{
				getLinac().setFieldProfileBuilder(FieldProfileBuilder.readXmlFile(fieldProfileBuilderUrl), fieldProfileBuilderUrl);
				newFieldProfileEncountered = true;
				if(getLinacLego().isReportDirectoryExists())
				{
					getLinacLego().writeStatus("     Writing field profile " + fieldMapFileName);
					getLinac().getFieldProfileBuilder().writeTraceWinFile(new File(getLinacLego().getReportDirectory().getPath() + File.separator +  fieldMapFileName + ".edz"));
					getLinac().getFieldProfileBuilder().writeDynacFile(new File(getLinacLego().getReportDirectory().getPath() + File.separator +  fieldMapFileName + ".txt"), getRfFreqMHz());
				}
			}
			fieldProfileBuilder = getLinac().getFieldProfileBuilder();
	
			if (!lengthOk()) 
			{
				throw new LinacLegoException(getAddress() + ": length does not match field profile Length");
			}
			setLength(0.001 * lengthmm);
			for (int ii = 0; ii < betaInt; ++ii) updateEvOut();
			updateSynchPhase();
			setSynchronousPhaseDegrees(phisdeg);
		} 
		catch (MalformedURLException e) {throw new LinacLegoException(e); } 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
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
	private double egain(double phiDeg, boolean cos)
	{
		double dW = 0.0;
		double dz =  lengthmm * 0.001 / ((double) (fieldProfileBuilder.getNpts() - 1));
		for (int ii = 0; ii < fieldProfileBuilder.getNpts(); ++ii)
		{
			if (cos)
			{
				dW = dW + xelmax * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] * Math.cos(phiDeg * degToRad + phiZprofile[ii]) * dz;
			}
			else
			{
				dW = dW +  xelmax * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] * Math.sin(phiDeg * degToRad + phiZprofile[ii]) * dz;
			}
		}
		dW = dW * 1.0e+06; //go from MV to V
		return dW;
	}
	public void updateSynchPhase()
	{
		phisdeg = Math.atan(egain(rfpdeg,false) / egain(rfpdeg,true) ) / degToRad;
	}
	public void updateRFPhase()
	{
		double ss = -egain(-phisdeg, false);
		double cc =  egain(-phisdeg, true);
		rfpdeg = Math.atan2(ss, cc) / degToRad;
		
	}
	public void updateEvOut()
	{
		phiZprofile = new double[fieldProfileBuilder.getNpts()];
		phiZprofile[0] = 0.0;
		double dW = 0.0;
		double dz =  lengthmm * 0.001 / ((double) (fieldProfileBuilder.getNpts() - 1));
		double k0 = TWOPI / getLamda();
		for (int ii = 0; ii < fieldProfileBuilder.getNpts(); ++ii)
		{
			dW = dW + 1.0e+06 * xelmax  * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] 
					* Math.cos(rfpdeg * degToRad + phiZprofile[ii]) * dz;
			seteVout(geteVin() + dW);
			if (ii < (fieldProfileBuilder.getNpts() - 1) )
			{
				phiZprofile[ii + 1] = phiZprofile[ii] + k0 * dz / beta(geteVout());
			}
		}
	}
	@Override
	public double characteristicValue() {return Math.abs(getVoltage());}
	@Override
	public String characteristicValueUnit() {return "MV";}
}
