package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamMagneticSteerer extends LegoBeam
{
	private static final long serialVersionUID = -4648529559991157161L;
	private double Bx;
	private double By;
	private double Bmax;
	private double thetaXBy;
	private double thetaYBx;
	
	public LegoBeamMagneticSteerer() throws LinacLegoException   
	{
		super();
	}
	public LegoBeamMagneticSteerer(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamMagneticSteerer(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
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
		addDataElement("Bx", "0.0", "double", "T");
		addDataElement("By", "0.0", "double", "T");
		addDataElement("Bmax", "0.0", "double", "T");
		addDataElement("thetaXBy", "0.0", "double", "perTm");
		addDataElement("thetaYBx", "0.0", "double", "perTm");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		Bx = Double.parseDouble(getDataValue("Bx"));
		By = Double.parseDouble(getDataValue("By"));
		Bmax = Double.parseDouble(getDataValue("Bmax"));
		thetaXBy = Double.parseDouble(getDataValue("thetaXBy"));
		thetaYBx = Double.parseDouble(getDataValue("thetaYBx"));
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
		latticeCommand = latticeCommand + getDefaultLatticeFileKeyWord();
		latticeCommand = latticeCommand + Lego.space + Double.toString(Bx);
		latticeCommand = latticeCommand + Lego.space + Double.toString(By);
		latticeCommand = latticeCommand + Lego.space + Double.toString(Bmax);
		latticeCommand = latticeCommand + Lego.space + "0";
		latticeCommand = latticeCommand + Lego.space + Double.toString(thetaXBy);
		latticeCommand = latticeCommand + Lego.space + Double.toString(thetaYBx);
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
	protected void setType() {type = "magSteer";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return  getDefaultLatticeFileKeyWord();
		return getDefaultLatticeFileKeyWord();
	}
	@Override
	public String getDefaultLatticeFileKeyWord() {return "STEERER";}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("Bx", sdata[0]);
			setDataValue("By", sdata[1]);
			setDataValue("Bmax", sdata[2]);
			if (sdata.length > 3)
			{
				setDataValue("thetaXBy", sdata[4]);
				setDataValue("thetaYBx", sdata[5]);
			}
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "COR-";}
	@Override
	public String getPreferredDiscipline() {return "BMD";}
	@Override
	public double characteristicValue() {return Math.sqrt(Bx * Bx + By * By);}
	@Override
	public String characteristicValueUnit() {return "T";}

}
