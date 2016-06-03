package se.esss.litterbox.linaclego.structures.beam;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamDrift extends LegoBeam
{
	private static final long serialVersionUID = 7038739635711784611L;
	private double lengthmm;
	public LegoBeamDrift() throws LinacLegoException   
	{
		super();
	}
	public LegoBeamDrift(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamDrift(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	@Override
	protected double[] getLocalTranslationVector() throws LinacLegoException 
	{
		double[] localInputVec = {0.0, 0.0, 0.0};
		localInputVec[2] = lengthmm * 0.001;
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
		addDataElement("l", "0.0", "double", "mm");
		addDataElement("r", "0.0", "double", "mm");
		addDataElement("ry", "0.0", "double", "mm");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		lengthmm = Double.parseDouble(getDataValue("l"));
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		String latticeCommand = "";
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			latticeCommand = "DRIFT";
			latticeCommand = latticeCommand + Lego.space + getDataValue("l");
			latticeCommand = latticeCommand + Lego.space + getDataValue("r");
			latticeCommand = latticeCommand + Lego.space + getDataValue("ry");
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
	protected void setType() {type = "drift";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return "DRIFT";
		return null;
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("l", sdata[0]);
			setDataValue("r", sdata[1]);
			setDataValue("ry", sdata[2]);
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "DRF-";}
	@Override
	public String getPreferredDiscipline() {return "VAC";}
	@Override
	public double characteristicValue() {return lengthmm;}
	@Override
	public String characteristicValueUnit() {return "mm";}

}
