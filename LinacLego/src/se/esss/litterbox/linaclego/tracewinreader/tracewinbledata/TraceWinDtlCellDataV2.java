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

import java.text.DecimalFormat;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.tracewinreader.TraceWinCommandReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class TraceWinDtlCellDataV2 extends TraceWinBleData
{

	public TraceWinDtlCellDataV2(String[] traceWinData, TraceWinCommandReader traceWinCommandReader) 
	{
		super(traceWinData, traceWinCommandReader);
	}
	@Override
	public String setLegoType() {return "dtlCell";}
	@Override
	public String[] setDataName() 
	{
		String[] name  = {"cellLenmm",	"q1Lenmm",	"q2Lenmm",	"cellCentermm",	"grad1Tpm",	"grad2Tpm",	"voltsT",	"voltMult",	"rfPhaseDeg",	"phaseAdd",	"radApermm",	"phaseFlag",	"betaS",	"tts",		"ktts",		"k2tts"};
		return name;
	}
	@Override
	public String[] setDataUnit() 
	{
		String[] unit  = {"mm",			"mm",		"mm",		"mm",			"T/m",		"T/m",		"Volt",		"unit",		"deg",			"deg",		"mm",			"unit",			"m",		"unit",		"unit",		"unit"};
		return unit;
	}
	@Override
	public String[] setDataType() 
	{
		String[] type  = {"double",		"double",	"double",	"double",		"double",	"double",	"double",	"double",	"double",		"double",	"double",		"int",			"double",	"double",	"double",	"double"};
		return type;
	}
	@Override
	public String[] setDataValue(String[] twd) 
	{
		String[] value = {twd[0], 		 twd[1],	twd[2],		twd[3],			twd[4],		twd[5],		twd[6],		"1.0",		twd[7],			"0.0",		twd[8],			twd[9],			twd[10],	twd[11],	twd[12],	twd[13]};
		return value;
	}
	@Override
	public String setLegoIdIndexLabel() {return "DTC-";}
	public void createBleTagEcho(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException 
	{
		createBleTag(xw, legoIdIndex, getLegoIdIndexLabel(), getLegoType(), getDataName(), getDataUnit(), getDataType(), getDataValue());
		
	}
	@Override
	public void createBleTag(SimpleXmlWriter xw, String legoIdIndex) throws LinacLegoException
	{
		TraceWinBleData prevTraceWinBleData = getPrevTraceWinBleData(getLegoType());
		double quadLen = getDoubleValue("q1Lenmm");
		if (prevTraceWinBleData != null ) quadLen = quadLen + prevTraceWinBleData.getDoubleValue("q2Lenmm");
		double totalGapLen = getDoubleValue("cellLenmm") - getDoubleValue("q1Lenmm") - getDoubleValue("q2Lenmm");
//		double driftLen1 = 0.5 * getDoubleValue("cellLenmm") - getDoubleValue("q1Lenmm") - getDoubleValue("cellCentermm");
		double driftLen1 = 0.5 * totalGapLen - getDoubleValue("cellCentermm");
		double driftLen2 = totalGapLen - driftLen1;
		double gapLen = 2.0 * getDoubleValue("radApermm");
		double noseConeDnLen = driftLen1 - gapLen / 2.0;
		double noseCone2 = driftLen2 - gapLen / 2.0;

		double prevTotalGapLen = 0.0;
		double prevDriftLen1 = 0.0;
		double prevDriftLen2 = 0.0;
		double prevGapLen = 0.0;
		double noseConeUpLen = 0.0;
		
		int legoIndex = Integer.parseInt(legoIdIndex);
		
		if (prevTraceWinBleData != null)
		{
			prevTotalGapLen = prevTraceWinBleData.getDoubleValue("cellLenmm") 
					- prevTraceWinBleData.getDoubleValue("q1Lenmm") - prevTraceWinBleData.getDoubleValue("q2Lenmm");
//			prevDriftLen1 = 0.5 * prevTraceWinBleData.getDoubleValue("cellLenmm") 
//					- prevTraceWinBleData.getDoubleValue("q1Lenmm") - prevTraceWinBleData.getDoubleValue("cellCentermm");
			prevDriftLen1 = 0.5 * prevTotalGapLen - prevTraceWinBleData.getDoubleValue("cellCentermm");
			prevDriftLen2 = prevTotalGapLen - prevDriftLen1;
			prevGapLen = 2.0 * prevTraceWinBleData.getDoubleValue("radApermm");
			noseConeUpLen = prevDriftLen2 - prevGapLen / 2.0;
		}
		DecimalFormat fourPlaces = new DecimalFormat("####.####");

		TraceWinDriftData traceWinDriftData;
		TraceWinQuadData traceWinQuadData;
		TraceWinRFGapData traceWinRFGapData;
		String[] driftTwd = new String[3];
		String[] quadTwd = new String[3];
		String[] rfGapTwd = new String[10];
		driftTwd[0] = fourPlaces.format(noseConeUpLen);
		driftTwd[1] = getValue("radApermm");
		driftTwd[2] = "0.0";
		traceWinDriftData = new TraceWinDriftData(driftTwd, traceWinCommandReader);
		createBleTag(xw, legoIdIndex, 
				traceWinDriftData.getLegoIdIndexLabel(), 
				traceWinDriftData.getLegoType(), 
				traceWinDriftData.getDataName(), 
				traceWinDriftData.getDataUnit(), 
				traceWinDriftData.getDataType(), 
				traceWinDriftData.getDataValue());
		quadTwd[0] = fourPlaces.format(quadLen);
		quadTwd[1] = getValue("grad1Tpm");
		quadTwd[2] = getValue("radApermm");
		traceWinQuadData = new TraceWinQuadData(quadTwd, traceWinCommandReader);
		createBleTag(xw, addLeadingZeros(legoIndex + 1, 4), 
				traceWinQuadData.getLegoIdIndexLabel(), 
				traceWinQuadData.getLegoType(), 
				traceWinQuadData.getDataName(), 
				traceWinQuadData.getDataUnit(), 
				traceWinQuadData.getDataType(), 
				traceWinQuadData.getDataValue());
		driftTwd[0] = fourPlaces.format(noseConeDnLen);
		traceWinDriftData = new TraceWinDriftData(driftTwd, traceWinCommandReader);
		createBleTag(xw, addLeadingZeros(legoIndex + 2, 4), 
				traceWinDriftData.getLegoIdIndexLabel(), 
				traceWinDriftData.getLegoType(), 
				traceWinDriftData.getDataName(), 
				traceWinDriftData.getDataUnit(), 
				traceWinDriftData.getDataType(), 
				traceWinDriftData.getDataValue());
		driftTwd[0] = fourPlaces.format(gapLen / 2.0);
		traceWinDriftData = new TraceWinDriftData(driftTwd, traceWinCommandReader);
		createBleTag(xw, addLeadingZeros(legoIndex + 3, 4), 
				traceWinDriftData.getLegoIdIndexLabel(), 
				traceWinDriftData.getLegoType(), 
				traceWinDriftData.getDataName(), 
				traceWinDriftData.getDataUnit(), 
				traceWinDriftData.getDataType(), 
				traceWinDriftData.getDataValue());
		rfGapTwd[0] = getValue("voltsT");
		rfGapTwd[1] = getValue("rfPhaseDeg");
		rfGapTwd[2] = getValue("radApermm");
		rfGapTwd[3] = getValue("phaseFlag");
		rfGapTwd[4] = getValue("betaS");
		rfGapTwd[5] = getValue("tts");
		rfGapTwd[6] = getValue("ktts");
		rfGapTwd[7] = getValue("k2tts");
		rfGapTwd[8] = "0.0";
		rfGapTwd[9] = "0.0";
		traceWinRFGapData = new TraceWinRFGapData(rfGapTwd, traceWinCommandReader);
		createBleTag(xw, addLeadingZeros(legoIndex + 4, 4), 
				traceWinRFGapData.getLegoIdIndexLabel(), 
				traceWinRFGapData.getLegoType(), 
				traceWinRFGapData.getDataName(), 
				traceWinRFGapData.getDataUnit(), 
				traceWinRFGapData.getDataType(), 
				traceWinRFGapData.getDataValue());
		createBleTag(xw, addLeadingZeros(legoIndex + 5, 4), 
				traceWinDriftData.getLegoIdIndexLabel(), 
				traceWinDriftData.getLegoType(), 
				traceWinDriftData.getDataName(), 
				traceWinDriftData.getDataUnit(), 
				traceWinDriftData.getDataType(), 
				traceWinDriftData.getDataValue());
		
		TraceWinBleData nextTraceWinBleData = getNextTraceWinBleData(getLegoType());
		if (nextTraceWinBleData == null)
		{
			driftTwd[0] = fourPlaces.format(noseCone2);
			driftTwd[1] = getValue("radApermm");
			driftTwd[2] = "0.0";
			traceWinDriftData = new TraceWinDriftData(driftTwd, traceWinCommandReader);
			createBleTag(xw, addLeadingZeros(legoIndex + 6, 4), 
					traceWinDriftData.getLegoIdIndexLabel(), 
					traceWinDriftData.getLegoType(), 
					traceWinDriftData.getDataName(), 
					traceWinDriftData.getDataUnit(), 
					traceWinDriftData.getDataType(), 
					traceWinDriftData.getDataValue());
			quadTwd[0] = getValue("q2Lenmm");
			quadTwd[1] = getValue("grad2Tpm");
			quadTwd[2] = getValue("radApermm");
			traceWinQuadData = new TraceWinQuadData(quadTwd, traceWinCommandReader);
			createBleTag(xw, addLeadingZeros(legoIndex + 7, 4), 
					traceWinQuadData.getLegoIdIndexLabel(), 
					traceWinQuadData.getLegoType(), 
					traceWinQuadData.getDataName(), 
					traceWinQuadData.getDataUnit(), 
					traceWinQuadData.getDataType(), 
					traceWinQuadData.getDataValue());
			driftTwd[0] = "0.00";
			traceWinDriftData = new TraceWinDriftData(driftTwd, traceWinCommandReader);
			createBleTag(xw, addLeadingZeros(legoIndex + 8, 4), 
					traceWinDriftData.getLegoIdIndexLabel(), 
					traceWinDriftData.getLegoType(), 
					traceWinDriftData.getDataName(), 
					traceWinDriftData.getDataUnit(), 
					traceWinDriftData.getDataType(), 
					traceWinDriftData.getDataValue());
		}

	}


}
