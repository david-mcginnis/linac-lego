package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamRfqGap extends LegoBeam
{
	private static final long serialVersionUID = 7038739635711784611L;
	public LegoBeamRfqGap() throws LinacLegoException   
	{
		super();
	}
	public LegoBeamRfqGap(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamRfqGap(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
	{
		super(legoSlot, beamListIndex, id, disc, model);
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
		addDataElement("longPos", "0.0", "double", "m");
		addDataElement("gapWidth", "0.0", "double", "m");
		addDataElement("ellipseLong", "0.0", "double", "m");
		addDataElement("ellipseTrans", "0.0", "double", "m");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
	}
	@Override
	protected String defaultLatticeCommand() throws LinacLegoException 
	{
		String latticeCommand = "";
		latticeCommand = getDefaultLatticeFileKeyWord();
		latticeCommand = latticeCommand + Lego.space + getDataValue("longPos");
		latticeCommand = latticeCommand + Lego.space + getDataValue("gapWidth");
		latticeCommand = latticeCommand + Lego.space + getDataValue("ellipseLong");
		latticeCommand = latticeCommand + Lego.space + getDataValue("ellipseTrans");
		return latticeCommand;
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return defaultLatticeCommand();
		return defaultLatticeCommand();
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
	protected void setType() {type = "rfqGap";}
	@Override
	public String getDefaultLatticeFileKeyWord() {return "RFQ_GAP";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return  getDefaultLatticeFileKeyWord();
		return getDefaultLatticeFileKeyWord();
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("longPos", sdata[0]);
			setDataValue("gapWidth", sdata[1]);
			setDataValue("ellipseLong", sdata[2]);
			setDataValue("ellipseTrans", sdata[3]);
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "RFQ-";}
	@Override
	public String getPreferredDiscipline() {return "EMR";}
	@Override
	public double characteristicValue() {return 0.0;}
	@Override
	public String characteristicValueUnit() {return "";}

}
