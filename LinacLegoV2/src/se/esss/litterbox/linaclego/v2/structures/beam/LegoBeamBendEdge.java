package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamBendEdge extends LegoBeam
{
	private static final long serialVersionUID = 6292647273858756752L;
	private double poleFaceAngleDeg = 0.0;
	private double radOfCurvmm = -1.0;
	private double gapmm = -1.0;
	private double K1 = -1.0;
	private double K2 = -1.0;
	private double aperRadmm = -1.0;
	private int HVflag = 0;
	public LegoBeamBendEdge() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamBendEdge(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamBendEdge(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
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
		addDataElement("poleFaceAngleDeg", "0.0", "double","deg");
		addDataElement("radOfCurvmm", "0.0", "double","mm");
		addDataElement("gapmm", "0.0", "double","mm");
		addDataElement("K1", "0.0", "double","unit");
		addDataElement("K2", "0.0", "double","unit");
		addDataElement("aperRadmm", "0.0", "double","mm");
		addDataElement("HVflag", "0", "int", "unit");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		poleFaceAngleDeg = Double.parseDouble(getDataValue("poleFaceAngleDeg"));
		radOfCurvmm = Double.parseDouble(getDataValue("radOfCurvmm"));
		gapmm = Double.parseDouble(getDataValue("gapmm"));
		K1 = Double.parseDouble(getDataValue("K1"));
		K2 = Double.parseDouble(getDataValue("K2"));
		aperRadmm = Double.parseDouble(getDataValue("aperRadmm"));
		HVflag = Integer.parseInt(getDataValue("HVflag"));
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException {
		String latticeCommand = "";
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			latticeCommand = "EDGE";
			latticeCommand = latticeCommand + Lego.space + Double.toString(poleFaceAngleDeg);
			latticeCommand = latticeCommand + Lego.space + Double.toString(radOfCurvmm);
			latticeCommand = latticeCommand + Lego.space + Double.toString(gapmm);
			latticeCommand = latticeCommand + Lego.space + Double.toString(K1);
			latticeCommand = latticeCommand + Lego.space + Double.toString(K2);
			latticeCommand = latticeCommand + Lego.space + Double.toString(aperRadmm);
			latticeCommand = latticeCommand + Lego.space + Integer.toString(HVflag);
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
	protected void setType() {type = "edge";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return "EDGE";
		return null;
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("poleFaceAngleDeg", sdata[0]);
			setDataValue("radOfCurvmm", sdata[1]);
			setDataValue("gapmm", sdata[2]);
			setDataValue("K1", sdata[3]);
			setDataValue("K2", sdata[4]);
			setDataValue("aperRadmm", sdata[5]);
			setDataValue("HVflag", sdata[6]);
		}
	}
	@Override
	public String getPreferredIdLabelHeader() 
	{
		String labelHeader = "EDG-";
		if (HVflag == 0) labelHeader = "EDGH-";
		if (HVflag >  0) labelHeader = "EDGV-";
		return labelHeader;
	}
	@Override
	public String getPreferredDiscipline() {return "BMD";}
	@Override
	public double characteristicValue() {return Math.abs(K1);}
	@Override
	public String characteristicValueUnit() {return "unit";}

}
