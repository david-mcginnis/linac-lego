package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamSizeMonitor extends LegoBeam 
{
	private static final long serialVersionUID = 176550111628919801L;
	String data = "";
	double xpos;
	double xwidth;
	double ypos;
	double ywidth;
	public LegoBeamSizeMonitor() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamSizeMonitor(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	public LegoBeamSizeMonitor(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
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
		addDataElement("xpos", "0.0", "double", "mm");
		addDataElement("ypos", "0.0", "double", "mm");
		addDataElement("xwidth", "0.0", "double", "mm");
		addDataElement("ywidth", "0.0", "double", "mm");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		data = getDataValue("data");
		xpos = Double.parseDouble(getDataValue("xpos"));
		ypos = Double.parseDouble(getDataValue("ypos"));
		xwidth = Double.parseDouble(getDataValue("xwidth"));
		ywidth = Double.parseDouble(getDataValue("ywidth"));
	}
	@Override
	protected String defaultLatticeCommand() throws LinacLegoException 
	{
		String latticeCommand = "";
		latticeCommand = getDefaultLatticeFileKeyWord();
		latticeCommand = latticeCommand + Lego.space + getDataValue("data");
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
	protected void setType() {type = "beamSize";}
	@Override
	public String getDefaultLatticeFileKeyWord() {return "DIAG_SIZE";}
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
			data = "";
			for (int ii = 0; ii < sdata.length; ++ii) data = data + " " + sdata[ii];
			setDataValue("data", data.trim());
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "WSC-";}
	@Override
	public String getPreferredDiscipline() {return "PBI";}
	@Override
	public double characteristicValue() {return 0.0;}
	@Override
	public String characteristicValueUnit() {return "";}

}
