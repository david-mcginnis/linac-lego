package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamBend extends LegoBeam
{
	private static final long serialVersionUID = -2510775864144660681L;
	private double twBendAngleDeg;
	private double radOfCurvmm;
	@SuppressWarnings("unused")
	private int fieldIndex;
	@SuppressWarnings("unused")
	private double aperRadmm;
	private int HVflag;
	@SuppressWarnings("unused")
	private double k1in;
	@SuppressWarnings("unused")
	private double k2in;
	@SuppressWarnings("unused")
	private double k1out;
	@SuppressWarnings("unused")
	private double k2out;

	public LegoBeamBend() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamBend(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamBend(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	@Override
	protected double[] getLocalTranslationVector() throws LinacLegoException 
	{
		// Because of TraceWin convention of +bend = right turn in H plane
		double bendAngleDeg = -twBendAngleDeg;
		double cosTheta = Math.cos(bendAngleDeg * degToRad);
		double sinTheta = Math.sin(bendAngleDeg * degToRad);
		double dz = radOfCurvmm * 0.001 * Math.abs(sinTheta);
		double dv = radOfCurvmm * 0.001 * (1.0 - cosTheta);
		if (bendAngleDeg < 0) dv = -dv;
		double[] localHorzInputVec = {dv, 0.0, dz};
		double[] localVertInputVec = {0.0, dv, dz};
		double[] localInputVec = localHorzInputVec;
		if (HVflag == 1) localInputVec = localVertInputVec;
		return localInputVec;
	}
	@Override
	protected double[][] getLocalRotationMatrix() throws LinacLegoException 
	{
		// Because of TraceWin convention of +bend = right turn in H plane
		double bendAngleDeg = -twBendAngleDeg;
		double cosTheta = Math.cos(bendAngleDeg * degToRad);
		double sinTheta = Math.sin(bendAngleDeg * degToRad);
		double[][] localVertRotMat = { {1.0, 0.0, 0.0}, {0.0, cosTheta, sinTheta}, {0.0, -sinTheta, cosTheta}};
		double[][] localHorzRotMat = { {cosTheta, 0.0, sinTheta}, {0.0, 1.0, 0.0}, {-sinTheta, 0.0, cosTheta}};
		double[][] localRotMat = localHorzRotMat;
		if (HVflag == 1) localRotMat = localVertRotMat;
		return localRotMat;
	}
	@Override
	public void addDataElements() throws LinacLegoException 
	{
		addDataElement("bendAngleDeg", "0.0", "double", "deg");
		addDataElement("radOfCurvmm", "0.0", "double", "mm");
		addDataElement("fieldIndex", "0", "int", "unit");
		addDataElement("aperRadmm", "0.0", "double", "mm");
		addDataElement("HVflag", "0", "int", "unit");
		addDataElement("K1in", "0.0", "double", "unit");
		addDataElement("K2in", "0.0", "double", "unit");
		addDataElement("K1out", "0.0", "double", "unit");
		addDataElement("K2out", "0.0", "double", "unit");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		twBendAngleDeg = Double.parseDouble(getDataValue("bendAngleDeg"));
		radOfCurvmm = Double.parseDouble(getDataValue("radOfCurvmm"));
		aperRadmm = Double.parseDouble(getDataValue("aperRadmm"));
		fieldIndex = Integer.parseInt(getDataValue("fieldIndex"));
		HVflag = Integer.parseInt(getDataValue("HVflag"));
		k1in = Double.parseDouble(getDataValue("K1in"));
		k2in = Double.parseDouble(getDataValue("K2in"));
		k1out = Double.parseDouble(getDataValue("K1out"));
		k2out = Double.parseDouble(getDataValue("K2out"));
	}
	@Override
	protected String defaultLatticeCommand() throws LinacLegoException 
	{
		String latticeCommand = "";
		latticeCommand = getDefaultLatticeFileKeyWord();
		latticeCommand = latticeCommand + Lego.space + getDataValue("bendAngleDeg");
		latticeCommand = latticeCommand + Lego.space + getDataValue("radOfCurvmm");
		latticeCommand = latticeCommand + Lego.space + getDataValue("aperRadmm");
		latticeCommand = latticeCommand + Lego.space + getDataValue("fieldIndex");
		latticeCommand = latticeCommand + Lego.space + getDataValue("HVflag");
		return latticeCommand;
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return defaultLatticeCommand();
		return defaultLatticeCommand();
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException  {return 0;}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException {return 0;}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {return 0;}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return twBendAngleDeg;}
	@Override
	protected void setType() {type = "bend";}
	@Override
	public String getDefaultLatticeFileKeyWord() { return "BEND";}
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
			setDataValue("bendAngleDeg", sdata[0]);
			setDataValue("radOfCurvmm", sdata[1]);
			setDataValue("fieldIndex", sdata[2]);
			setDataValue("aperRadmm", sdata[3]);
			setDataValue("HVflag", sdata[4]);
			setDataValue("K1in", "0.0");
			setDataValue("K2in", "0.0");
			setDataValue("K1out", "0.0");
			setDataValue("K2out", "0.0");
		}
	}
	@Override
	public String getPreferredIdLabelHeader() 
	{
		String labelHeader = "BND-";
		if (HVflag == 0) labelHeader = "BNDH-";
		if (HVflag >  0) labelHeader = "BNDV-";
		return labelHeader;
	}
	@Override
	public String getPreferredDiscipline() {return "BMD";}
	@Override
	public double characteristicValue() {return Math.abs(twBendAngleDeg);}
	@Override
	public String characteristicValueUnit() {return "deg";}
}
