package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamAperture extends LegoBeam
{
	private static final long serialVersionUID = 5963629376297469167L;
	double dx;
	double dy;
	int apertype;

	double lenUp = 0.0;
	double lenDn = 0.0;
	
	public LegoBeamAperture() throws LinacLegoException   
	{
		super();
	}
	public LegoBeamAperture(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamAperture(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	@Override
	protected double[] getLocalTranslationVector() throws LinacLegoException 
	{
		double[] localInputVec = {0.0, 0.0, 0.0};
		localInputVec[2] = (lenUp + lenDn) * 0.001;
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
		addDataElement("dx", "0.0", "double", "mm");
		addDataElement("dy", "0.0", "double", "mm");
		addDataElement("apertype", "0", "int", "unit");

		addDataElement("lenUp", "0.0", "double", "mm");
		addDataElement("lenDn", "0.0", "double", "mm");
		addDataElement("r", "0.0", "double", "mm");
		addDataElement("ry", "0.0", "double", "mm");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		dx = Double.parseDouble(getDataValue("dx"));
		dy = Double.parseDouble(getDataValue("dy"));
		apertype = Integer.parseInt(getDataValue("apertype"));

		lenUp = Double.parseDouble(getDataValue("lenUp"));
		lenDn = Double.parseDouble(getDataValue("lenDn"));
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return defaultLatticeCommand();
		return defaultLatticeCommand();
	}
	@Override
	protected String defaultLatticeCommand() throws LinacLegoException 
	{
		String latticeCommand = "";
		if (lenUp > 0.00000001)
		{
			latticeCommand = latticeCommand + "DRIFT";
			latticeCommand = latticeCommand + Lego.space + getDataValue("lenUp");
			latticeCommand = latticeCommand + Lego.space + getDataValue("r");
			latticeCommand = latticeCommand + Lego.space + getDataValue("ry");
			latticeCommand = latticeCommand + "\n                  ";
		}
		
		latticeCommand = latticeCommand + getDefaultLatticeFileKeyWord();
		latticeCommand = latticeCommand + Lego.space + Double.toString(dx);
		latticeCommand = latticeCommand + Lego.space + Double.toString(dy);
		latticeCommand = latticeCommand + Lego.space + Integer.toString(apertype);

		if (lenDn > 0.00000001)
		{
			latticeCommand = latticeCommand + "\n                  ";
			latticeCommand = latticeCommand + "DRIFT";
			latticeCommand = latticeCommand + Lego.space + getDataValue("lenDn");
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
	protected void setType() {type="aperture";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return  getDefaultLatticeFileKeyWord();
		return getDefaultLatticeFileKeyWord();
	}
	@Override
	public String getDefaultLatticeFileKeyWord() {return "APERTURE";}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("dx", sdata[0]);
			setDataValue("dy", sdata[1]);
			setDataValue("apertype", sdata[2]);
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "APR-";}
	@Override
	public String getPreferredDiscipline() {return "VAC";}
	@Override
	public double characteristicValue() {return Math.sqrt(dx * dy);}
	@Override
	public String characteristicValueUnit() {return "mm^2";}

}
