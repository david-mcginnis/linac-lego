package se.esss.litterbox.linaclego.structures.beam;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamPositionMonitor extends LegoBeam 
{
	String data = "";
	public LegoBeamPositionMonitor() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamPositionMonitor(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	public LegoBeamPositionMonitor(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	@Override
	protected double[] getLocalTranslationVector() throws LinacLegoException 
	{
		double[] localInputVec = {0.0, 0.0, 0.0};
		return localInputVec;
	}
	@Override
	protected double[][] getLocalRotationMatrix() throws LinacLegoException 
	{
		double[][] localRotMat = { {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
		return localRotMat;
	}
	@Override
	public void addDataElements() throws LinacLegoException 
	{
		addDataElement("data", "", "string", "unit");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		data = getDataValue("data");
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		String latticeCommand = "";
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			latticeCommand = "DIAG_POSITION";
			latticeCommand = latticeCommand + Lego.space + getDataValue("data");
		}
		return latticeCommand;
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException {return 0;}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException {return 0;}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {return 0;}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return 0;}
	@Override
	protected void setType() {type = "beamPosition";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return "DIAG_POSITION";
		return null;
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			data = "";
			for (int ii = 0; ii < sdata.length; ++ii) data = data + " " + sdata[ii];
			setDataValue("data", data.trim());
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "BPM-";}
	@Override
	public String getPreferredDiscipline() {return "PBI";}

}
