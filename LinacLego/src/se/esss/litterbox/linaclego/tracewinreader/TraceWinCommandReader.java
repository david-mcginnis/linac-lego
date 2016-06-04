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
package se.esss.litterbox.linaclego.tracewinreader;

import java.util.Scanner;

import se.esss.litterbox.linaclego.tracewinreader.tracewinbledata.*;

public class TraceWinCommandReader 
{
	private String traceWinType = "";
	private String[] traceWinData = null;
	private TraceWinBleData traceWinBleData = null;
	private TraceWinReader traceWinReader;
	private int traceWinCommandListIndex;
	
	public int getTraceWinCommandListIndex() {return traceWinCommandListIndex;}
	public String getTraceWinType() {return traceWinType;}
	public String[] getTraceWinData() {return traceWinData;}
	public TraceWinBleData getTraceWinBleData() {return traceWinBleData;}
	public TraceWinReader getTraceWinReader() {return traceWinReader;}

	public void setTraceWinCommandListIndex(int traceWinCommandListIndex) {this.traceWinCommandListIndex = traceWinCommandListIndex;}

	TraceWinCommandReader(int iline, String inputString, TraceWinReader traceWinReader)
	{
		this.traceWinReader = traceWinReader;
		String comment = null;
		inputString = inputString.trim();
		int isc = inputString.indexOf(";");
		if (isc == 0 ) 
		{
			traceWinType = "";
			traceWinData = null;
			return;
		}
		if (isc > 0)
		{
			comment = inputString.substring(isc).trim();
			if (comment.length() > 0)
			{
				comment = comment.substring(1).trim();
			}
			else
			{
				comment = null;
			}
			inputString = inputString.substring(0, isc);
		}
// Get rid of leading spaces and delimators
		Scanner inputScanner = new Scanner(inputString);
		inputScanner.useDelimiter("[, \t]");
		try{inputString = inputString.substring(inputString.indexOf(inputScanner.next()));}
		catch (java.util.NoSuchElementException e) 
		{
			inputScanner.close();
			return;
		}
		inputScanner.close();
// Get rid of leader description
		if (inputString.indexOf(":") >= 0)
		{
			inputScanner = new Scanner(inputString.substring(inputString.indexOf(":") + 1));
			inputScanner.useDelimiter("[, \t]");
			inputString = inputString.substring(inputString.indexOf(inputScanner.next()));
			inputScanner.close();
		}

		String delims = "[ ,\t]+";
		String[] splitResponse = null;
		
		splitResponse = inputString.split(delims);
		if (splitResponse.length > 0)
		{
			if (splitResponse[0].length() > 0)
			{
				if (splitResponse[0].indexOf(";") != 0)
				{
					traceWinReader.writeStatus("     Processing TraceWin Line " + Integer.toString(iline + 1) + " Command = " + splitResponse[0]);
					traceWinData = new String[splitResponse.length - 1];
					for (int idata = 0; idata < (splitResponse.length - 1); ++idata)
					{
						traceWinData[idata] = splitResponse[idata + 1];
					}
					String bleType = "";
					traceWinBleData = null;
					boolean traceWinTypeFound = false;
					if (splitResponse[0].toUpperCase().equals("BEND")) 				bleType = "BEND";
					if (splitResponse[0].toUpperCase().equals("DRIFT")) 			bleType = "DRIFT";
					if (splitResponse[0].toUpperCase().equals("DTL_CEL")) 			bleType = "DTL_CEL";
					if (splitResponse[0].toUpperCase().equals("EDGE")) 				bleType = "EDGE";
					if (splitResponse[0].toUpperCase().equals("FIELD_MAP")) 		bleType = "FIELD_MAP";
					if (splitResponse[0].toUpperCase().equals("NCELLS")) 			bleType = "NCELLS";
					if (splitResponse[0].toUpperCase().equals("QUAD")) 				bleType = "QUAD";
					if (splitResponse[0].toUpperCase().equals("GAP")) 				bleType = "GAP";
					if (splitResponse[0].toUpperCase().equals("THIN_STEERING"))		bleType = "THIN_STEERING";
					if (splitResponse[0].toUpperCase().equals("DIAG_POSITION"))		bleType = "DIAG_POSITION";

					if (splitResponse[0].toUpperCase().equals("END")) traceWinTypeFound = true;
					if (splitResponse[0].toUpperCase().equals("FREQ")) traceWinTypeFound = true;
					if (splitResponse[0].toUpperCase().equals("LATTICE")) traceWinTypeFound = true;
					if (splitResponse[0].toUpperCase().equals("LATTICE_END")) traceWinTypeFound = true;
					if (bleType.length() > 0) 
					{
						traceWinTypeFound = true;
						if (bleType.equals("BEND")) 			traceWinBleData = new TraceWinBendData(traceWinData, comment, this);
						if (bleType.equals("DRIFT")) 			traceWinBleData = new TraceWinDriftData(traceWinData, comment, this);
						if (bleType.equals("DTL_CEL"))			traceWinBleData = new TraceWinDtlCellDataV3(traceWinData, comment, this);
						if (bleType.equals("EDGE"))				traceWinBleData = new TraceWinEdgeData(traceWinData, comment, this);
						if (bleType.equals("FIELD_MAP"))		traceWinBleData = new TraceWinFieldMapData(traceWinData, comment, this);
						if (bleType.equals("NCELLS"))			traceWinBleData = new TraceWinNcellsData(traceWinData, comment, this);
						if (bleType.equals("QUAD"))				traceWinBleData = new TraceWinQuadData(traceWinData, comment, this);
						if (bleType.equals("GAP"))				traceWinBleData = new TraceWinRFGapData(traceWinData, comment, this);
						if (bleType.equals("THIN_STEERING"))	traceWinBleData = new TraceWinThinSteerData(traceWinData, comment, this);
						if (bleType.equals("DIAG_POSITION"))	traceWinBleData = new TraceWinDiagPosData(traceWinData, comment, this);
					}
					if (traceWinTypeFound)
					{
						traceWinType = splitResponse[0].toUpperCase();
					}
					else
					{
						traceWinReader.writeStatus("          TraceWin Command " + splitResponse[0] + " not understood");
						traceWinType = "";
						traceWinData = null;
					}
				}
			}
		}
	}
}
