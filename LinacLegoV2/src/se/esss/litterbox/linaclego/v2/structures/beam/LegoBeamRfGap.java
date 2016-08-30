package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamRfGap extends LegoBeam
{
	private static final long serialVersionUID = -1829792305283242129L;
	private double voltsT = 0.0;
	private double rfPhaseDeg = 0.0;
	private double radApermm = 0.0;
	private int phaseFlag = 0;
	private double betaS = 0.0;
	private double tts = 0.0;
	private double ktts = 0.0;
	private double k2tts = 0.0;
	private double voltMult = 1.0;
	private double phaseOffDeg = 0.0;
	private double energyGain = 0.0;
	private double synchPhase = 0.0;

	public LegoBeamRfGap() throws LinacLegoException   
	{
		super();
	}
	public LegoBeamRfGap(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamRfGap(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
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
		addDataElement("voltsT", "0.0", "double", "Volt");
		addDataElement("rfPhaseDeg", "0.0", "double", "deg");
		addDataElement("radApermm", "0.0", "double", "mm");
		addDataElement("phaseFlag", "0", "int", "unit");
		addDataElement("betaS", "0.0", "double", "unit");
		addDataElement("tts", "0.0", "double", "unit");
		addDataElement("ktts", "0.0", "double", "unit");
		addDataElement("k2tts", "0.0", "double", "unit");
		addDataElement("voltMult", "1.0", "double", "unit");
		addDataElement("phaseOffDeg", "0.0", "double", "deg");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		voltsT = Double.parseDouble(getDataValue("voltsT"));
		rfPhaseDeg = Double.parseDouble(getDataValue("rfPhaseDeg"));
		radApermm = Double.parseDouble(getDataValue("radApermm"));
		phaseFlag = Integer.parseInt(getDataValue("phaseFlag"));
		betaS = Double.parseDouble(getDataValue("betaS"));
		tts = Double.parseDouble(getDataValue("tts"));
		ktts = Double.parseDouble(getDataValue("ktts"));
		k2tts = Double.parseDouble(getDataValue("k2tts"));
		voltMult = Double.parseDouble(getDataValue("voltMult"));
		phaseOffDeg = Double.parseDouble(getDataValue("phaseOffDeg"));

		double beta;
		double volts;
		double ttratio = 1.0;
		beta = beta(geteVin());
		ttratio = 1.0;
		if (betaS > 0.001) ttratio = transitTimeFactor(beta) / transitTimeFactor(betaS);
		volts = voltsT * voltMult * ttratio;
		energyGain = volts * Math.cos((rfPhaseDeg + phaseOffDeg) * degToRad);
		synchPhase = rfPhaseDeg + phaseOffDeg;
	}
	private double transitTimeFactor(double beta)
	{
		if (betaS == 0.0) return 1.0;
		if (tts == 0.0) return 1.0;
		double kappa = betaS / beta;
		double transitTime = tts;
		transitTime = transitTime + ktts * (kappa - 1);
		transitTime = transitTime + 0.5 * k2tts * (kappa - 1) * (kappa - 1);
		return transitTime;
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
		latticeCommand = latticeCommand + "GAP";
		latticeCommand = latticeCommand + Lego.space + Double.toString(voltsT * voltMult);
		latticeCommand = latticeCommand + Lego.space + Double.toString(rfPhaseDeg + phaseOffDeg);
		latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
		latticeCommand = latticeCommand + Lego.space + Integer.toString(phaseFlag);
		latticeCommand = latticeCommand + Lego.space + Double.toString(betaS);
		latticeCommand = latticeCommand + Lego.space + Double.toString(tts);
		latticeCommand = latticeCommand + Lego.space + Double.toString(ktts);
		latticeCommand = latticeCommand + Lego.space + Double.toString(k2tts);
		latticeCommand = latticeCommand + Lego.space + "0";
		latticeCommand = latticeCommand + Lego.space + "0";
		return latticeCommand;
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException {return energyGain;}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException {return synchPhase;}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {return 0;}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return 0;}
	@Override
	protected void setType() {type="rfgap";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return  getDefaultLatticeFileKeyWord();
		return getDefaultLatticeFileKeyWord();
	}
	@Override
	public String getDefaultLatticeFileKeyWord() {return "GAP";}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("voltsT", sdata[0]);
			setDataValue("rfPhaseDeg", sdata[1]);
			setDataValue("radApermm", sdata[2]);
			setDataValue("phaseFlag", sdata[3]);
			setDataValue("betaS", sdata[4]);
			setDataValue("tts", sdata[5]);
			setDataValue("ktts", sdata[6]);
			setDataValue("k2tts", sdata[7]);
			setDataValue("voltMult", "1");
			setDataValue("phaseOffDeg", "0");
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "GAP-";}
	@Override
	public String getPreferredDiscipline() {return "EMR";}
	@Override
	public double characteristicValue() {return energyGain * 0.001;}
	@Override
	public String characteristicValueUnit() {return "kV";}

}
