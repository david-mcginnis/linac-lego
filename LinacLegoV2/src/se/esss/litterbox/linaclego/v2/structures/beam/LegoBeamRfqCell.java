package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamRfqCell extends LegoBeam
{
	private static final long serialVersionUID = -6691768413042987364L;
	private double cellLength;
	private double voltage;
	private double A10;
	private double phiSynch;
	private double energyGain = 0.0;
	public LegoBeamRfqCell() throws LinacLegoException   
	{
		super();
	}
	public LegoBeamRfqCell(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamRfqCell(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	@Override
	protected double[] getLocalTranslationVector() throws LinacLegoException 
	{
		double[] localInputVec = {0.0, 0.0, 0.0};
		localInputVec[2] = cellLength * 0.001;
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
		addDataElement("voltage", "0.0", "double", "Volts");
		addDataElement("R0", "0.0", "double", "mm");
		addDataElement("A10", "0.0", "double", "unit");
		addDataElement("m", "0.0", "double", "unit");
		addDataElement("cellLength", "0.0", "double", "mm");
		addDataElement("phiSynch", "0.0", "double", "deg");
		addDataElement("cellType", "0.0", "int", "unit");
		addDataElement("transCurve", "0.0", "double", "mm");
		addDataElement("recenterPhi", "0.0", "double", "deg");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		cellLength = Double.parseDouble(getDataValue("cellLength"));
		voltage = Double.parseDouble(getDataValue("voltage"));
		A10 = Double.parseDouble(getDataValue("A10"));
		phiSynch = Double.parseDouble(getDataValue("phiSynch"));
		
		energyGain = Lego.PI * voltage * A10 * Math.cos(Lego.degToRad * phiSynch) / 4.0;

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
		latticeCommand = getDefaultLatticeFileKeyWord();
		latticeCommand = latticeCommand + Lego.space + getDataValue("voltage");
		latticeCommand = latticeCommand + Lego.space + getDataValue("R0");
		latticeCommand = latticeCommand + Lego.space + getDataValue("A10");
		latticeCommand = latticeCommand + Lego.space + getDataValue("m");
		latticeCommand = latticeCommand + Lego.space + getDataValue("cellLength");
		latticeCommand = latticeCommand + Lego.space + getDataValue("phiSynch");
		latticeCommand = latticeCommand + Lego.space + getDataValue("cellType");
		latticeCommand = latticeCommand + Lego.space + getDataValue("transCurve");
		latticeCommand = latticeCommand + Lego.space + getDataValue("recenterPhi");
		return latticeCommand;
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException {return energyGain;}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException {return phiSynch;}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {return 0;}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return 0;}
	@Override
	protected void setType() {type = "rfqCell";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return  getDefaultLatticeFileKeyWord();
		return getDefaultLatticeFileKeyWord();
	}
	@Override
	protected String getDefaultLatticeFileKeyWord() {return "RFQ_CELL";}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("voltage", sdata[0]);
			setDataValue("R0", sdata[1]);
			setDataValue("A10", sdata[2]);
			setDataValue("m", sdata[3]);
			setDataValue("cellLength", sdata[4]);
			setDataValue("phiSynch", sdata[5]);
			setDataValue("cellType", sdata[6]);
			setDataValue("transCurve", sdata[7]);
			setDataValue("recenterPhi", sdata[8]);
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "CELL-";}
	@Override
	public String getPreferredDiscipline() {return "EMR";}
	@Override
	public double characteristicValue() {return energyGain * 0.001;}
	@Override
	public String characteristicValueUnit() {return "kV";}

}
