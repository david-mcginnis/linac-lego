package se.esss.litterbox.linaclego.v2.data.legosets;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.data.LegoData;
import se.esss.litterbox.linaclego.v2.structures.LegoLinac;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoSet  implements Serializable
{
	private static final long serialVersionUID = 6384013469086118513L;
	
	private final static int numTransFuncCoeff = 5;
	private LegoData latticeParameterData = null;
	private LegoData settingData = null;
	private LegoData addressData = null;
	private LegoData devNameData = null;	
	private LegoData[] transFuncCoeffData = new LegoData[numTransFuncCoeff];

	
	public LegoData getLatticeParameterData() {return latticeParameterData;}
	public LegoData getDevNameData() {return devNameData;}
	public LegoData getAddressData() {return addressData;}
	public LegoData getSetting() {return settingData;}

	
	public LegoSet(SimpleXmlReader legoSetTag) throws LinacLegoException 
	{
		SimpleXmlReader dataTags = legoSetTag.tagsByName("d");
		ArrayList<LegoData> legoDataList = new ArrayList<LegoData>();

		if (dataTags.numChildTags() > 0)
		{
			for (int ii = 0; ii < dataTags.numChildTags(); ++ii)
			{
				try {legoDataList.add(new LegoData(dataTags.tag(ii)));} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
			}
		}
		latticeParameterData = LegoData.findLegoDataById(legoDataList, "latticeParameter");
		if (latticeParameterData == null) throw new LinacLegoException("No latticeParameter defined");
		devNameData = LegoData.findLegoDataById(legoDataList, "devName");
		if (devNameData == null) throw new LinacLegoException("No devName defined");
		addressData = LegoData.findLegoDataById(legoDataList, "address");
		if (addressData == null) throw new LinacLegoException("No address defined");
		settingData = LegoData.findLegoDataById(legoDataList, "setting");
		if (settingData == null) throw new LinacLegoException("No setting defined");
		
		SimpleXmlReader tfTag;
		transFuncCoeffData = new LegoData[numTransFuncCoeff];
		for (int ii = 0; ii < numTransFuncCoeff; ++ii)
		{
			transFuncCoeffData[ii] = new LegoData("tf" + ii, "0.0", "double", "unit");
		}
		try 
		{
			tfTag = legoSetTag.tagsByName("tf").tag(0);
			dataTags = tfTag.tagsByName("d");
			if (dataTags.numChildTags() > 0)
			{
				for (int ii = 0; ii < dataTags.numChildTags(); ++ii)
				{
					LegoData ld = new LegoData(dataTags.tag(ii));
					transFuncCoeffData[Integer.parseInt(ld.getId().substring(2))] = ld;
				}
			}
		} catch (SimpleXmlException e1) {throw new LinacLegoException(e1);}
			
	}
	public String getSectionId() throws LinacLegoException
	{
		String id = null;
		try {id = addressData.getValue().split("[-]+")[0];} catch(java.lang.NullPointerException e){throw new LinacLegoException("Lego Section address too short.");}
		return id;
	}
	public String getCellId() throws LinacLegoException
	{
		String id = null;
		try {id = addressData.getValue().split("[-]+")[1];} catch(java.lang.NullPointerException e){throw new LinacLegoException("Lego Cell address too short.");}
		return id;
	}
	public String getSlotId() throws LinacLegoException
	{
		String id = null;
		try {id = addressData.getValue().split("[-]+")[2];} catch(java.lang.NullPointerException e){throw new LinacLegoException("Lego Slot address too short.");}
		return id;
	}
	public void setSettingFromLattice(LegoLinac legoLinac) throws LinacLegoException
	{
		LegoSlot legoSlot = legoLinac.getLegoSectionById(getSectionId()).getLegoCellById(getCellId()).getLegoSlotById(getSlotId());
		LegoData  legoData = null;
		legoData = LegoData.findLegoDataById(legoSlot.getLegoDataList(), latticeParameterData.getValue());
		if (legoData == null) throw new LinacLegoException("Could not find " + latticeParameterData.getValue() + " in " + addressData.getValue());
		try {
			settingData.setValue(Double.toString(invertTransferFunction(Double.parseDouble(legoData.getValue()), 5, .01)));
			legoLinac.getLego().writeStatus("Setting " + devNameData.getValue() + " to " + settingData.getValue() + " from " + latticeParameterData.getValue());
        } catch (NullPointerException e) {throw new LinacLegoException(e);}

	}
	public void setLatticeFromSetting(LegoLinac legoLinac) throws LinacLegoException
	{
		LegoSlot legoSlot = legoLinac.getLegoSectionById(getSectionId()).getLegoCellById(getCellId()).getLegoSlotById(getSlotId());
		LegoData  legoData = null;
		legoData = LegoData.findLegoDataById(legoSlot.getLegoDataList(), latticeParameterData.getValue());
		String latticeValue = Double.toString(getTransferFunction(Double.parseDouble(settingData.getValue())));
		legoData.setValue(latticeValue);
		legoLinac.getLego().writeStatus("Setting " + latticeParameterData.getValue() + " to " + latticeValue + " from " + devNameData.getValue());
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("legoSet");
			latticeParameterData.writeXml(xw);
			settingData.writeXml(xw);
			addressData.writeXml(xw);
			devNameData.writeXml(xw);


			xw.openXmlTag("tf");

			for (int itf = 0; itf < numTransFuncCoeff; ++itf)
			{
				if (Double.parseDouble(transFuncCoeffData[itf].getValue()) != 0.0) 
				{
					transFuncCoeffData[itf].writeXml(xw);
				}
			}
			xw.closeXmlTag("tf");
			xw.closeXmlTag("legoSet");

		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		
	}

	private double getTransferFunction(double devSetting) throws LinacLegoException 
	{
		try
		{
			double tfPow = 1.0;
			double tf = 0.0;
			for (int itf = 0; itf < numTransFuncCoeff; ++itf)
			{
				tf = tf + Double.parseDouble(transFuncCoeffData[itf].getValue()) * tfPow;
				tfPow = tfPow * devSetting;
			}
			return tf;
		}
		catch (NumberFormatException e) {throw new LinacLegoException(e);}
	}
	private double invertTransferFunction(double tfDesired, int ntry, double tol) throws LinacLegoException
	{
		try
		{
			double xguess = 0.0;
			if (Math.abs(Double.parseDouble(transFuncCoeffData[1].getValue())) > 0.0)
			{
				xguess = (tfDesired - Double.parseDouble(transFuncCoeffData[0].getValue())) / Double.parseDouble(transFuncCoeffData[1].getValue());
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
		catch (NumberFormatException e) {throw new LinacLegoException(e);}
	}
	private double getTransferFunctionDerivative(double devSetting) throws LinacLegoException
	{
		try
		{
			double tfPow = 1.0;
			double tf = 0.0;
			for (int itf = 1; itf < numTransFuncCoeff; ++itf)
			{
				tf = tf + ((double) itf) * Double.parseDouble(transFuncCoeffData[itf].getValue()) * tfPow;
				tfPow = tfPow * devSetting;
			}
			return tf;
		}
		catch (NumberFormatException e) {throw new LinacLegoException(e);}
		
	}
	public void printLegoSetsCsvFile(PrintWriter pw) throws LinacLegoException 
	{
		pw.print(getSectionId());
		pw.print("," + getCellId());
		pw.print("," + getSlotId());
		pw.print("," + latticeParameterData.getValue());
		pw.print("," + Double.toString(getTransferFunction(Double.parseDouble(settingData.getValue()))));
		pw.print("," + latticeParameterData.getUnit());
		pw.print("," + devNameData.getValue());
		pw.print("," + settingData.getValue());
		pw.print("," + settingData.getUnit());
		pw.println("," + getTfCsvData());
	}
	public String getTfCsvData() throws LinacLegoException
	{
		String csvData = "";
		for (int itf = 0; itf < numTransFuncCoeff - 1; ++itf)
		{
			csvData = csvData + transFuncCoeffData[itf].getValue() + ",";
		}
		csvData = csvData + transFuncCoeffData[numTransFuncCoeff - 1].getValue();
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
