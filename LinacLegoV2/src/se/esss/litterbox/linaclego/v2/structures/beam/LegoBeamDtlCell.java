package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamDtlCell extends LegoBeam
{
	private static final long serialVersionUID = 696111837581365089L;

	private double cellLenmm;
	private double q1Lenmm;
	private double q2Lenmm;
	private double cellCentermm;
	private double grad1Tpm;
	private double grad2Tpm;
	private double voltsT;
	private double voltMult;
	private double rfPhaseDeg;
	private double phaseAdd;
	private double radApermm;
	private int phaseFlag;
	private double betaS;
	private double tts;
	private double ktts;
	private double k2tts;
	private double energyGain = 0.0;
	private double synchPhase = 0.0;

	public LegoBeamDtlCell() throws LinacLegoException   
	{
		super();
	}
	public LegoBeamDtlCell(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamDtlCell(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	@Override
	protected double[] getLocalTranslationVector() throws LinacLegoException 
	{
		double[] localInputVec = {0.0, 0.0, 0.0};
		localInputVec[2] = cellLenmm * 0.001;
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
		addDataElement("cellLenmm", "0.0", "double", "mm");
		addDataElement("q1Lenmm", "0.0", "double", "mm");
		addDataElement("q2Lenmm", "0.0", "double", "mm");
		addDataElement("cellCentermm", "0.0", "double", "mm");
		addDataElement("grad1Tpm", "0.0", "double","T/m");
		addDataElement("grad2Tpm", "0.0", "double","T/m");
		addDataElement("voltsT", "0.0", "double","Volt");
		addDataElement("voltMult", "1.0", "double","unit");
		addDataElement("rfPhaseDeg", "0.0", "double","deg");
		addDataElement("phaseAdd", "0.0", "double","deg");
		addDataElement("radApermm", "0.0", "double", "mm");
		addDataElement("phaseFlag", "0", "int","unit");
		addDataElement("betaS", "0.0", "double","unit");
		addDataElement("tts", "0.0", "double","unit");
		addDataElement("ktts", "0.0", "double","unit");
		addDataElement("k2tts", "0.0", "double","unit");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		cellLenmm = Double.parseDouble(getDataValue("cellLenmm"));
		q1Lenmm = Double.parseDouble(getDataValue("q1Lenmm"));
		q2Lenmm = Double.parseDouble(getDataValue("q2Lenmm"));
		cellCentermm = Double.parseDouble(getDataValue("cellCentermm"));
		grad1Tpm = Double.parseDouble(getDataValue("grad1Tpm"));
		grad2Tpm = Double.parseDouble(getDataValue("grad2Tpm"));
		voltsT = Double.parseDouble(getDataValue("voltsT"));
		voltMult = Double.parseDouble(getDataValue("voltMult"));
		rfPhaseDeg = Double.parseDouble(getDataValue("rfPhaseDeg"));
		phaseAdd = Double.parseDouble(getDataValue("phaseAdd"));
		radApermm = Double.parseDouble(getDataValue("radApermm"));
		phaseFlag = Integer.parseInt(getDataValue("phaseFlag"));
		betaS = Double.parseDouble(getDataValue("betaS"));
		tts = Double.parseDouble(getDataValue("tts"));
		ktts = Double.parseDouble(getDataValue("ktts"));
		k2tts = Double.parseDouble(getDataValue("k2tts"));
		
		double beta;
		double volts;
		double ttratio = 1.0;
		beta = beta(geteVin());
		ttratio = 1.0;
		if (betaS > 0.001) ttratio = transitTimeFactor(beta) / transitTimeFactor(betaS);
		volts = voltsT * voltMult * ttratio;
		energyGain = volts * Math.cos((rfPhaseDeg + phaseAdd) * degToRad);
		synchPhase = rfPhaseDeg + phaseAdd;

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
	protected String defaultLatticeCommand() throws LinacLegoException 
	{
		String latticeCommand = "";
		latticeCommand = getDefaultLatticeFileKeyWord();
		latticeCommand = latticeCommand + Lego.space + Double.toString(cellLenmm);
		latticeCommand = latticeCommand + Lego.space + Double.toString(q1Lenmm);
		latticeCommand = latticeCommand + Lego.space + Double.toString(q2Lenmm);
		latticeCommand = latticeCommand + Lego.space + Double.toString(cellCentermm);
		latticeCommand = latticeCommand + Lego.space + Double.toString(grad1Tpm);
		latticeCommand = latticeCommand + Lego.space + Double.toString(grad2Tpm);
		latticeCommand = latticeCommand + Lego.space + Double.toString(voltsT * voltMult);
		latticeCommand = latticeCommand + Lego.space + Double.toString(rfPhaseDeg + phaseAdd);
		latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
		latticeCommand = latticeCommand + Lego.space + Integer.toString(phaseFlag);
		latticeCommand = latticeCommand + Lego.space + Double.toString(betaS);
		latticeCommand = latticeCommand + Lego.space + Double.toString(tts);
		latticeCommand = latticeCommand + Lego.space + Double.toString(ktts);
		latticeCommand = latticeCommand + Lego.space + Double.toString(k2tts);
		return latticeCommand;
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return defaultLatticeCommand();
		if (latticeType.equalsIgnoreCase("tracewinnodtlcell"))
		{
			double noseConeLength = radApermm;
			double halfGapLength = 0.5 * (cellLenmm - q1Lenmm - q2Lenmm - 2.0 * noseConeLength);
			String latticeCommand = "";
			if (Math.abs(grad1Tpm) > 0.00000001)
			{
				latticeCommand = "QUAD";
				latticeCommand = latticeCommand + Lego.space + Double.toString(q1Lenmm);
				latticeCommand = latticeCommand + Lego.space + Double.toString(grad1Tpm);
				latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
				latticeCommand = latticeCommand + "\n                  ";
			}
			else
			{
				latticeCommand = "DRIFT";
				latticeCommand = latticeCommand + Lego.space + Double.toString(q1Lenmm);
				latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
				latticeCommand = latticeCommand + Lego.space + "0";
				latticeCommand = latticeCommand + "\n                  ";
			}
			latticeCommand = latticeCommand + "DRIFT";
			latticeCommand = latticeCommand + Lego.space + Double.toString(noseConeLength);;
			latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + "\n";

			latticeCommand = latticeCommand + ";lego          </slot>\n";
			latticeCommand = latticeCommand + ";lego          <slot id=\"GAP\">\n                  ";

			latticeCommand = latticeCommand + "DRIFT";
			latticeCommand = latticeCommand + Lego.space + Lego.fourPlaces.format(halfGapLength);;
			latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + "\n                  ";

			latticeCommand = latticeCommand + "GAP";
			latticeCommand = latticeCommand + Lego.space + Double.toString(voltsT * voltMult);
			latticeCommand = latticeCommand + Lego.space + Double.toString(rfPhaseDeg + phaseAdd);
			latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
			latticeCommand = latticeCommand + Lego.space + Integer.toString(phaseFlag);
			latticeCommand = latticeCommand + Lego.space + Double.toString(betaS);
			latticeCommand = latticeCommand + Lego.space + Double.toString(tts);
			latticeCommand = latticeCommand + Lego.space + Double.toString(ktts);
			latticeCommand = latticeCommand + Lego.space + Double.toString(k2tts);
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + "\n                  ";
			
			latticeCommand = latticeCommand + "DRIFT";
			latticeCommand = latticeCommand + Lego.space + Lego.fourPlaces.format(halfGapLength);;
			latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + "\n";

			latticeCommand = latticeCommand + ";lego          </slot>\n";
			latticeCommand = latticeCommand + ";lego          <slot id=\"DRT\">\n                  ";

			latticeCommand = latticeCommand + "DRIFT";
			latticeCommand = latticeCommand + Lego.space + Double.toString(noseConeLength);;
			latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + "\n                  ";
			if (Math.abs(grad2Tpm) > 0.00000001)
			{
				latticeCommand = latticeCommand + "QUAD";
				latticeCommand = latticeCommand + Lego.space + Double.toString(q2Lenmm);
				latticeCommand = latticeCommand + Lego.space + Double.toString(grad2Tpm);
				latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
			}
			else
			{
				latticeCommand = latticeCommand + "DRIFT";
				latticeCommand = latticeCommand + Lego.space + Double.toString(q2Lenmm);
				latticeCommand = latticeCommand + Lego.space + Double.toString(radApermm);
				latticeCommand = latticeCommand + Lego.space + "0";
			}
			
			return latticeCommand;

		}
		return defaultLatticeCommand();
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException {return energyGain;}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException {return synchPhase;}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {
		double qgrad = 0.0;
		double qlength = 0.0;
		if (Math.abs(grad1Tpm) > 0.0000001)
		{
			qgrad = grad1Tpm * q1Lenmm;
			qlength = q1Lenmm;
		}
		if (Math.abs(grad2Tpm) > 0.0000001)
		{
			qgrad = qgrad + grad2Tpm * q2Lenmm;
			qlength = qlength + q2Lenmm;
		}
		if (qlength > 0.0) return qgrad / qlength;
		return 0.0;
	}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return 0;}
	@Override
	protected void setType() {type = "dtlcell";}
	@Override
	protected String getDefaultLatticeFileKeyWord() {return "DTL_CEL";}
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
			setDataValue("cellLenmm", sdata[0]);
			setDataValue("q1Lenmm", sdata[1]);
			setDataValue("q2Lenmm", sdata[2]);
			setDataValue("cellCentermm", sdata[3]);
			setDataValue("grad1Tpm", sdata[4]);
			setDataValue("grad2Tpm", sdata[5]);
			setDataValue("voltsT", sdata[6]);
			setDataValue("rfPhaseDeg", sdata[7]);
			setDataValue("radApermm", sdata[8]);
			setDataValue("phaseFlag", sdata[9]);
			setDataValue("betaS", sdata[10]);
			setDataValue("tts", sdata[11]);
			setDataValue("ktts", sdata[12]);
			setDataValue("k2tts", sdata[13]);
			setDataValue("voltMult", "1.0");
			setDataValue("phaseAdd", "0.0");
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "DCL-";}
	@Override
	public String getPreferredDiscipline() {return "EMR";}
	@Override
	public double characteristicValue() {return energyGain * 1.0e-3;}
	@Override
	public String characteristicValueUnit() {return "kV";}

}
