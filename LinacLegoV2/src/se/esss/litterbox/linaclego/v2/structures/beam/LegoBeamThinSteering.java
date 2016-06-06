package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamThinSteering extends LegoBeam
{
	private static final long serialVersionUID = 7164618645216179173L;
	private double xkick;
	private double ykick;
	@SuppressWarnings("unused")
	private double radius;
	@SuppressWarnings("unused")
	private int kickType;

	public LegoBeamThinSteering() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamThinSteering(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamThinSteering(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
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
		addDataElement("xkick", "0.0", "double","Tm");
		addDataElement("ykick", "0.0", "double","Tm");
		addDataElement("r", "0.0", "double", "mm");
		addDataElement("kickType", "0", "int", "unit");
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		String latticeCommand = "";
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			latticeCommand = "THIN_STEERING";
			latticeCommand = latticeCommand + Lego.space + getDataValue("xkick");
			latticeCommand = latticeCommand + Lego.space + getDataValue("ykick");
			latticeCommand = latticeCommand + Lego.space + getDataValue("r");
			latticeCommand = latticeCommand + Lego.space + getDataValue("kickType");
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
	protected void calcParameters() throws LinacLegoException 
	{
		xkick = Double.parseDouble(getDataValue("xkick"));
		ykick = Double.parseDouble(getDataValue("ykick"));
		radius = Double.parseDouble(getDataValue("r"));
		kickType = Integer.parseInt(getDataValue("kickType"));
		
	}
	@Override
	protected void setType() {type = "thinSteering";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return "THIN_STEERING";
		return null;
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("xkick", sdata[0]);
			setDataValue("ykick", sdata[1]);
			setDataValue("r", sdata[2]);
			setDataValue("kickType", sdata[3]);
		}
		
	}
	@Override
	public String getPreferredIdLabelHeader() {return "COR-";}
	@Override
	public String getPreferredDiscipline() {return "BMD";}
	@Override
	public double characteristicValue() {return Math.sqrt(xkick * xkick + ykick * ykick);}
	@Override
	public String characteristicValueUnit() {return "Tm";}
}