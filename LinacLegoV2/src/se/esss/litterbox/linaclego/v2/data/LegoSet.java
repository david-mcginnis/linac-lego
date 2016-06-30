package se.esss.litterbox.linaclego.v2.data;

import java.io.Serializable;

import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoLinac;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeam;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoSet  implements Serializable
{
	private static final long serialVersionUID = 6384013469086118513L;
	private String dataId = null;
	private String devName = null;
	private String unit;
	private String type;
	private String sectionId = null;
	private String cellId = null;
	private String slotId = null;
	private String beamId = null;
	private String setting;
	private final static int numTransFuncCoeff = 5;
	double[] transFuncCoeff;

	public String getDataId() {return dataId;}
	public String getDevName() {return devName;}
	public String getUnit() {return unit;}
	public String getType() {return type;}
	public String getSectionId() {return sectionId;}
	public String getCellId() {return cellId;}
	public String getSlotId() {return slotId;}
	public String getBeamId() {return beamId;}
	public String getSetting() {return setting;}
	
	public LegoSet(SimpleXmlReader legoSetTag) throws LinacLegoException
	{
		
		try {devName = legoSetTag.attribute("devName");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No deviceName for legoSet.");}
		try {unit = legoSetTag.attribute("unit");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No unit for legoSet for device " + devName);}
		try {type = legoSetTag.attribute("type");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No type for legoSet for device " + devName);}
		try {dataId = legoSetTag.attribute("dataId");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No dataId for legoSet for device " + devName);}
		setting = legoSetTag.getCharacterData();
		
		sectionId = null;
		cellId = null;
		slotId = null;
		beamId = null;
		try {sectionId = legoSetTag.attribute("section");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No section for legoSet for device " + devName);}
		try {cellId = legoSetTag.attribute("cell");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No cell for legoSet for device " + devName);}
		try {slotId = legoSetTag.attribute("slot");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No slot for legoSet for device " + devName);}
		try {beamId = legoSetTag.attribute("beam");} 
		catch (SimpleXmlException e) {beamId = null;}
			
		transFuncCoeff = new double[numTransFuncCoeff];
		for (int itf = 0; itf < numTransFuncCoeff; ++itf)
		{
			try 
			{
				String coefString = legoSetTag.attribute("tf" + Integer.toString(itf));
				try {transFuncCoeff[itf] = Double.parseDouble(coefString);}
				catch (NumberFormatException nfe) {throw new LinacLegoException("Transfer function coeff not a number " + devName);} 
			} catch (SimpleXmlException e) {transFuncCoeff[itf] = 0.0;}
		}
	}
	public void setSettingFromLattice(LegoLinac legoLinac) throws LinacLegoException
	{
		LegoSlot legoSlot = legoLinac.getLegoSectionById(sectionId).getLegoCellById(cellId).getLegoSlotById(slotId);
		LegoData  legoData = null;
		if (beamId == null)
		{
			legoData = LegoData.findLegoDataById(legoSlot.getLegoDataList(), dataId);
		}
		else
		{
			LegoBeam legoBeam = legoSlot.getLegoBeamById(beamId);
			legoData = LegoData.findLegoDataById(legoBeam.getLegoDataList(), dataId);
		}       
		try {
			setting = Double.toString(invertTransferFunction(Double.parseDouble(legoData.getValue()), 5, .01));
			legoLinac.getLego().writeStatus("Setting " + devName + " to " + setting + " from " + dataId);
        } catch (NullPointerException e) {throw new LinacLegoException(e);}

	}
	public void setLatticeFromSetting(LegoLinac legoLinac) throws LinacLegoException
	{
		LegoSlot legoSlot = legoLinac.getLegoSectionById(sectionId).getLegoCellById(cellId).getLegoSlotById(slotId);
		LegoData  legoData = null;
		if (beamId == null)
		{
			legoData = LegoData.findLegoDataById(legoSlot.getLegoDataList(), dataId);
		}
		else
		{
			LegoBeam legoBeam = legoSlot.getLegoBeamById(beamId);
			legoData = LegoData.findLegoDataById(legoBeam.getLegoDataList(), dataId);
		}
		String latticeValue = Double.toString(getTransferFunction(Double.parseDouble(setting)));
		legoData.setValue(latticeValue);
		legoLinac.getLego().writeStatus("Setting " + dataId + " to " + latticeValue + " from " + devName);
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("legoSet");
			xw.setAttribute("section", sectionId);
			xw.setAttribute("cell", cellId);
			xw.setAttribute("slot", slotId);xw.setAttribute("unit", unit);
			if (beamId != null) xw.setAttribute("beam", beamId);
			xw.setAttribute("devName", devName);
			xw.setAttribute("dataId", dataId);
			xw.setAttribute("type", type);
			xw.setAttribute("unit", unit);xw.setAttribute("unit", unit);
			for (int itf = 0; itf < numTransFuncCoeff; ++itf)
			{
				if (transFuncCoeff[itf] != 0.0) xw.setAttribute("tf" + Integer.toString(itf), Double.toString(transFuncCoeff[itf]));
			}
			xw.writeCharacterData(setting);
			xw.closeXmlTag("legoSet");

		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		
	}

	private double getTransferFunction(double devSetting) 
	{
		double tfPow = 1.0;
		double tf = 0.0;
		for (int itf = 0; itf < numTransFuncCoeff; ++itf)
		{
			tf = tf + transFuncCoeff[itf] * tfPow;
			tfPow = tfPow * devSetting;
		}
		return tf;
	}
	private double invertTransferFunction(double tfDesired, int ntry, double tol)
	{
		double xguess = 0.0;
		if (Math.abs(transFuncCoeff[1]) > 0.0)
		{
			xguess = (tfDesired - transFuncCoeff[0]) / transFuncCoeff[1];
		}
		double tfDeriv = getTransferFunctionDerivative(xguess);
		double tf = getTransferFunction(xguess);
		double tferror = 1.0 + tol;
		int itry = 0;
		while ((itry < ntry) && (Math.abs(tfDeriv) > 0) && (tferror > tol))
		{		
			xguess = xguess + 1.0 * (tfDesired - tf) / tfDeriv;
			tfDeriv = getTransferFunctionDerivative(xguess);
			tf = getTransferFunction(xguess);
			tferror = (tf - tfDesired);
			if (tfDesired != 0) tferror = tferror / tfDesired;
			tferror = Math.abs(tferror);
			itry = itry + 1;
		}
		return xguess;
	}
	private double getTransferFunctionDerivative(double devSetting)
	{
		double tfPow = 1.0;
		double tf = 0.0;
		for (int itf = 1; itf < numTransFuncCoeff; ++itf)
		{
			tf = tf + ((double) itf) * transFuncCoeff[itf] * tfPow;
			tfPow = tfPow * devSetting;
		}
		return tf;
		
	}
	public String getTfCsvData()
	{
		String csvData = "";
		for (int itf = 0; itf < numTransFuncCoeff - 1; ++itf)
		{
			csvData = csvData + Double.toString(transFuncCoeff[itf]) + ",";
		}
		csvData = csvData + Double.toString(transFuncCoeff[numTransFuncCoeff - 1]);
		return csvData;
	}
	public static String getTfCsvDataHeader()
	{
		String csvData = "";
		for (int itf = 0; itf < numTransFuncCoeff - 1; ++itf)
		{
			csvData = csvData + "TF " + itf + ",";
		}
		csvData = csvData + "TF "+ (numTransFuncCoeff - 1);
		return csvData;
	}

}
