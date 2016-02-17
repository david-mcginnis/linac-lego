/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.linaclego.structures.beamlineelement;


import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.simplexml.SimpleXmlReader;


public class Ncells extends BeamLineElement 
{
	private int mode = 1;
	private int ncells;
	private double	betag = 1.0;
	private double	e0tRef;
	private double	radius;
	private double	phiTWdeg;
	private int	phaseFlag = 0;
	private double[] ke0t = {0.0, 0.0, 0.0};
	private double[]	dz = {0.0, 0.0, 0.0};
	private double	betaRef = 1.0;
	private double[] ttRef = {1.0, 1.0, 1.0};
	private double[] kttRef = {0.0, 0.0, 0.0};
	private double[] k2ttRef = {0.0, 0.0, 0.0};
	private boolean ttInfo = false;
	private double phiSynchCalc;
	private double intNpt = 1001;
	private double kstep = 0.05;
	
	private double kref;
	private double kg;
	private double cellLength;
	private double thetaIntStart;
	private double deltatThetaInt;

	public Ncells(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		addDataElement("mode", "0", "int", "unit");
		addDataElement("ncells", "0", "int", "unit");
		addDataElement("betag", "0.0", "double","unit");
		addDataElement("e0t", "0.0", "double","Volt/m");
		addDataElement("theta", "0.0", "double","deg");
		addDataElement("radius", "0.0", "double","mm");
		addDataElement("p", "0", "int", "unit");
		addDataElement("ke0ti", "0.0", "double", "unit");
		addDataElement("ke0to", "0.0", "double", "unit");
		addDataElement("dzi", "0.0", "double","mm");
		addDataElement("dzo", "0.0", "double","mm");
		addDataElement("betas", "0.0", "double","unit");
		addDataElement("ts", "0.0", "double", "unit");
		addDataElement("kts", "0.0", "double", "unit");
		addDataElement("k2ts", "0.0", "double", "unit");
		addDataElement("ti", "0.0", "double", "unit");
		addDataElement("kti", "0.0", "double", "unit");
		addDataElement("k2ti", "0.0", "double", "unit");
		addDataElement("to", "0.0", "double", "unit");
		addDataElement("kto", "0.0", "double", "unit");
		addDataElement("k2to", "0.0", "double", "unit");
	}
	@Override
	public void readDataElements() throws LinacLegoException
	{
		setMode(Integer.parseInt(getDataElement("mode").getValue()));
		setNcells(Integer.parseInt(getDataElement("ncells").getValue()));
		setBetag(Double.parseDouble(getDataElement("betag").getValue()));
		e0tRef = Double.parseDouble(getDataElement("e0t").getValue());
		phiTWdeg = Double.parseDouble(getDataElement("theta").getValue());
		setRadius(Double.parseDouble(getDataElement("radius").getValue()));
		setPhaseFlag(Integer.parseInt(getDataElement("p").getValue()));
		ke0t[0] = Double.parseDouble(getDataElement("ke0ti").getValue());
		ke0t[2] = Double.parseDouble(getDataElement("ke0to").getValue());
		dz[0] = Double.parseDouble(getDataElement("dzi").getValue()) * 0.001;
		dz[2] = Double.parseDouble(getDataElement("dzo").getValue()) * 0.001;
		String betasString = getDataElement("betas").getValue();
		if (betasString != null)
		{
			ttInfo = true;
			betaRef = Double.parseDouble(getDataElement("betasString").getValue());
			ttRef[1] = Double.parseDouble(getDataElement("ts").getValue());
			kttRef[1] = -Double.parseDouble(getDataElement("kts").getValue());
			k2ttRef[1] = -Double.parseDouble(getDataElement("k2ts").getValue());
			ttRef[0] = Double.parseDouble(getDataElement("ti").getValue());
			kttRef[0] = -Double.parseDouble(getDataElement("kti").getValue());
			k2ttRef[0] = -Double.parseDouble(getDataElement("k2ti").getValue());
			ttRef[2] = Double.parseDouble(getDataElement("to").getValue());
			kttRef[2] = -Double.parseDouble(getDataElement("kto").getValue());
			k2ttRef[2] = -Double.parseDouble(getDataElement("k2to").getValue());
		}
		else
		{
			kref = TWOPI / getLamda();
		}
		double cavLength = ((double) ncells) * betag * getLamda() / 2.0;
		if (mode == 0) cavLength  = cavLength * 2.0;
		setLength(cavLength);
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		calcEnergyGain();
		setSynchronousPhaseDegrees(phiSynchCalc);

	}
	private double eTfit(double k, double kref, int index)
	{
		double et = e0tRef * (1.0 + ke0t[index]) * transitTimeFit(k, kref, index) / transitTimeFit(kref, kref, 1);
		return et;
	}
	private double transitTimeFit(double k, double kref, int index)
	{
		double arg = (k - kref) / kref;
		double tfit = ttRef[index] + kttRef[index] * arg + 0.5 * k2ttRef[index] * arg * arg;
		return tfit;
	}
	private void  calcEnergyGain()
	{
		// TODO need to throw exception if ncells = 1
		double wCcell;
		double wScell;
		double phicell;
		double betaCell;
		double evCell;
		double modePhase = 180.0;
		double kg = waveNum(betag);
		double cellLength = PI / kg;
		
		
		if (mode == 0) modePhase = 360.0;
		
		
		betaCell = beta(geteVout());
		phicell = phiTWdeg;
		wCcell = 0.0;
		wScell = 0.0;
		wCcell = wCcell + eTfit(waveNum(betaCell), waveNum(betaRef), 0) * cellLength * Math.cos(phicell * degToRad);
		wScell = wScell + eTfit(waveNum(betaCell), waveNum(betaRef), 0) * cellLength * Math.sin(phicell * degToRad);
		evCell = geteVin() + wCcell;
		
// Use - sign because dz[0] is suppose to be negative number of field spills out into beam pipe
		phicell = phicell - (betag / betaCell) * kg * dz[0] * radToDeg;
		for (int ii = 1; ii < (ncells - 1); ++ii)
		{
			betaCell = beta(evCell);
			phicell = phicell - modePhase * (1.0 - betag / betaCell);
			wCcell = wCcell + eTfit(waveNum(betaCell), waveNum(betaRef), 1) * cellLength * Math.cos(phicell * degToRad);
			wScell = wScell + eTfit(waveNum(betaCell), waveNum(betaRef), 1) * cellLength * Math.sin(phicell * degToRad);
			evCell = geteVin() + wCcell;;
		}
		betaCell = beta(evCell);
// Use + sign because dz[1] is suppose to be positive number of field spills out into beam pipe
		phicell = phicell + (betag / betaCell) * kg * dz[2] * radToDeg;
		phicell = phicell - modePhase * (1.0 - betag / betaCell);
		wCcell = wCcell + eTfit(waveNum(betaCell), waveNum(betaRef), 2) * cellLength * Math.cos(phicell * degToRad);
		wScell = wScell + eTfit(waveNum(betaCell), waveNum(betaRef), 2) * cellLength * Math.sin(phicell * degToRad);
		evCell = geteVin() + wCcell;;
		seteVout(evCell);

		phiSynchCalc = radToDeg * Math.atan2(wScell, wCcell);
		
		return;
	}
	@Override
	public String makeTraceWinCommand()
	{
		String command = "NCELLS";
		command = command + space + Integer.toString(mode);
		command = command + space + Integer.toString(ncells);
		command = command + space + fourPlaces.format(betag);
		command = command + space + zeroPlaces.format(e0tRef);
		command = command + space + twoPlaces.format(phiTWdeg);
		command = command + space + twoPlaces.format(radius);
		command = command + space + Integer.toString(phaseFlag);
		command = command + space + fourPlaces.format(ke0t[0]);
		command = command + space + fourPlaces.format(ke0t[2]);
		command = command + space + fourPlaces.format( dz[0] * 1000.0);
		command = command + space + fourPlaces.format( dz[2] * 1000.0);
		if (ttInfo)
		{
			command = command + space + fourPlaces.format(betaRef);
			command = command + space + fourPlaces.format(ttRef[1]);
			command = command + space + fourPlaces.format(-kttRef[1]);
			command = command + space + fourPlaces.format(-k2ttRef[1]);
			command = command + space + fourPlaces.format(ttRef[0]);
			command = command + space + fourPlaces.format(-kttRef[0]);
			command = command + space + fourPlaces.format(-k2ttRef[0]);
			command = command + space + fourPlaces.format(ttRef[2]);
			command = command + space + fourPlaces.format(-kttRef[2]);
			command = command + space + fourPlaces.format(-k2ttRef[2]);
		}
		return command;
	}
	@Override
	public String makeDynacCommand() throws LinacLegoException 
	{
		throw new LinacLegoException("Ncells not implemented in DYNAC");
	}
	protected double waveNum(double beta) {return (TWOPI / (beta* getLamda() ) );}
	protected double centerCellFieldProfile(double kgz) {return squareWaveEfield(kgz);}
	protected double inputCellFieldProfile(double kgz) {return squareWaveEfield(kgz);}
	protected double outputCellFieldProfile(double kgz) {return squareWaveEfield(kgz);}
	private double squareWaveEfield(double kgz)
	{
		double maxTheta = PI / 2.0;
		if (mode == 0)  maxTheta = PI;
		if (Math.abs(kgz) > maxTheta) return 0.0;
		double arg = kref * cellLength / 2.0;
		double tt = Math.sin(arg + 10e-10) / Math.sin(arg + 10e-10);
		return e0tRef / tt;
	}
	public void setIntegrationRange(double intNpt)
	{
		this.intNpt = intNpt;
// Will integrate from -PI to 2PI - assume field profile is zero outside this range
// for mode =0 (2PI mode) will double integration range;
		thetaIntStart = -2.0 *PI;
		deltatThetaInt = 4.0 * PI / ((double) (intNpt - 1));
		if (mode == 0) 
		{
			thetaIntStart = 2.0 * thetaIntStart;
			deltatThetaInt = 2.0 * deltatThetaInt;
		}
	}
	private double centerCellTransitTimeFactor(double k)
	{
		double enumSum = 0;
		double edenomSum = 0;
		double theta = thetaIntStart;
		for (int ii = 0; ii < intNpt; ++ii)
		{
			enumSum = enumSum + centerCellFieldProfile(theta) * Math.cos(k * theta / getKg());
			edenomSum = edenomSum + centerCellFieldProfile(theta);
			theta = theta + deltatThetaInt; 
		}
		return (enumSum / edenomSum);
	}
	protected double inputCellTransitTimeFactor(double k)
	{
		double enumSum = 0;
		double edenomSum = 0;
		double theta = thetaIntStart;
		double thetaOffset = kg * dz[0];
		for (int ii = 0; ii < intNpt; ++ii)
		{
			enumSum = enumSum + inputCellFieldProfile(theta) * Math.cos(k * (theta -  thetaOffset)/ getKg());
			edenomSum = edenomSum + inputCellFieldProfile(theta);
			theta = theta + deltatThetaInt; 
		}
		return (enumSum / edenomSum);
	}
	protected double outputCellTransitTimeFactor(double k)
	{
		double enumSum = 0;
		double edenomSum = 0;
		double theta = thetaIntStart;
		double thetaOffset = kg * dz[2];
		for (int ii = 0; ii < intNpt; ++ii)
		{
			enumSum = enumSum + outputCellFieldProfile(theta) * Math.cos(k * (theta -  thetaOffset)/ getKg());
			edenomSum = edenomSum + outputCellFieldProfile(theta);
			theta = theta + deltatThetaInt; 
		}
		return (enumSum / edenomSum);
	}
	protected void updateEndCellOffsets() throws LinacLegoException
	{
		double[] ecosSum = {0, 0};
		double[] esinSum = {0, 0};
		double[] kedge = {0, 0};
		kedge[0] = waveNum(beta(geteVin()));
		kedge[1] = waveNum(beta(geteVout()));
		double theta = thetaIntStart;
		for (int ii = 0; ii < intNpt; ++ii)
		{
			ecosSum[0] = ecosSum[0] +  inputCellFieldProfile(theta) * Math.cos(kedge[0] * theta / getKg());
			esinSum[0] = esinSum[0] +  inputCellFieldProfile(theta) * Math.sin(kedge[0] * theta / getKg());
			ecosSum[1] = ecosSum[1] + outputCellFieldProfile(theta) * Math.cos(kedge[1] * theta / getKg());
			esinSum[1] = esinSum[1] + outputCellFieldProfile(theta) * Math.sin(kedge[1] * theta / getKg());
			theta = theta + deltatThetaInt; 
		}
		dz[0] = Math.atan(esinSum[0] / ecosSum[0]) / kedge[0];
		dz[2] = Math.atan(esinSum[1] / ecosSum[1]) / kedge[1];
		getDataElement("dzi").setValue(Double.toString(dz[0] * 1000.0));
		getDataElement("dzo").setValue(Double.toString(dz[2] * 1000.0));
//System.exit(0);
	}
	protected void updateTransitTimeFactors() throws LinacLegoException
	{
		ttRef[0]   =      inputCellTransitTimeFactor(kref);
		kttRef[0]  =     (inputCellTransitTimeFactor(kref * (1.0 + kstep))
					    - inputCellTransitTimeFactor(kref * (1.0 - kstep))) / (2.0 * kstep);
		k2ttRef[0] =     (inputCellTransitTimeFactor(kref * (1.0 + kstep))
				        + inputCellTransitTimeFactor(kref * (1.0 - kstep))
				   -2.0 * inputCellTransitTimeFactor(kref)) / (kstep * kstep);
		ttRef[1]   =      centerCellTransitTimeFactor(kref);
		kttRef[1]  =     (centerCellTransitTimeFactor(kref * (1.0 + kstep))
					    - centerCellTransitTimeFactor(kref * (1.0 - kstep))) / (2.0 * kstep);
		k2ttRef[1] =     (centerCellTransitTimeFactor(kref * (1.0 + kstep))
				        + centerCellTransitTimeFactor(kref * (1.0 - kstep))
				   -2.0 * centerCellTransitTimeFactor(kref)) / (kstep * kstep);
		ttRef[2]   =      outputCellTransitTimeFactor(kref);
		kttRef[2]  =     (outputCellTransitTimeFactor(kref * (1.0 + kstep))
					    - outputCellTransitTimeFactor(kref * (1.0 - kstep))) / (2.0 * kstep);
		k2ttRef[2] =     (outputCellTransitTimeFactor(kref * (1.0 + kstep))
				        + outputCellTransitTimeFactor(kref * (1.0 - kstep))
				   -2.0 * outputCellTransitTimeFactor(kref)) / (kstep * kstep);

		getDataElement("ti").setValue(Double.toString(ttRef[0]));
		getDataElement("ts").setValue(Double.toString(ttRef[1]));
		getDataElement("to").setValue(Double.toString(ttRef[2]));
		getDataElement("kti").setValue(Double.toString(-kttRef[0]));
		getDataElement("kts").setValue(Double.toString(-kttRef[1]));
		getDataElement("kto").setValue(Double.toString(-kttRef[2]));
		getDataElement("k2ti").setValue(Double.toString(-k2ttRef[0]));
		getDataElement("k2ts").setValue(Double.toString(-k2ttRef[1]));
		getDataElement("k2to").setValue(Double.toString(-k2ttRef[2]));
	
	}
	protected void updateEndCellsFieldCorrection() throws LinacLegoException
	{
		double[] enumSum = {0, 0, 0};
		double edenomSum = 0;
		double theta = thetaIntStart;
		for (int ii = 0; ii < intNpt; ++ii)
		{
			enumSum[0] = enumSum[0] + inputCellFieldProfile(theta);
			enumSum[2] = enumSum[2] + outputCellFieldProfile(theta);
			edenomSum = edenomSum + centerCellFieldProfile(theta);
			theta = theta + deltatThetaInt; 
		}
		ke0t[0] =  ((enumSum[0] / edenomSum) - 1);
		ke0t[2] =  ((enumSum[2] / edenomSum) - 1);
		getDataElement("ke0ti").setValue(Double.toString(ke0t[0]));
		getDataElement("ke0to").setValue(Double.toString(ke0t[2]));
	}
	protected void  updateCenterCellETref() throws LinacLegoException
	{
		double enumSum = 0;
		double theta = thetaIntStart;
		for (int ii = 0; ii < intNpt; ++ii)
		{
			enumSum = enumSum + centerCellFieldProfile(theta) * Math.cos(kref * theta / kg);
			theta = theta + deltatThetaInt; 
		}
		e0tRef = enumSum * deltatThetaInt / (cellLength * kg);
		getDataElement("e0t").setValue(Double.toString(e0tRef));
	}
	protected void matchSynchPhase(double desiredSynchPhaseDeg, int ntry) throws LinacLegoException 
	{
		upDateTraceWinParams();
		double phasePerCell = 180.0;
		if (mode == 0) phasePerCell = 360.0;
		phiTWdeg = desiredSynchPhaseDeg + phasePerCell *  (((double) (ncells - 1)) / 2.0) * (betaRef - betag) / betaRef;
		// Use + sign because dz[0] is suppose to be negative number of field spills out into beam pipe
		phiTWdeg = phiTWdeg + radToDeg * (betag / betaRef) * kg * dz[0];

		for (int itry = 0; itry < ntry; ++itry)
		{
			upDateTraceWinParams();
			calcEnergyGain();
			phiTWdeg = phiTWdeg + (desiredSynchPhaseDeg - phiSynchCalc);
		}
	}
	protected void upDateTraceWinParams() throws LinacLegoException 
	{
		updateBetaRef();
		updateEndCellOffsets();
		updateTransitTimeFactors();
		updateCenterCellETref();
		updateEndCellsFieldCorrection();
	}
	@Override
	public void calcLocation() 
	{
		BeamLineElement previousBeamLineElement = getPreviousBeamLineElement();
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)
			{
				if (previousBeamLineElement != null)
					getEndRotMat()[ir][ic] = previousBeamLineElement.getEndRotMat()[ir][ic];
			}
			if (previousBeamLineElement != null)
				getEndPosVec()[ir] = previousBeamLineElement.getEndPosVec()[ir];
		}
	
		double[] localInputVec = {0.0, 0.0, getLength()};
		double[] localOutputVec = {0.0, 0.0, 0.0};
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
				localOutputVec[ir] = localOutputVec[ir] + getEndRotMat()[ir][ic] * localInputVec[ic];
			getEndPosVec()[ir] = getEndPosVec()[ir] + localOutputVec[ir];
		}
	}

	
	public int getMode() {return mode;}
	public int getNcells() {return ncells;}
	public double getPhiSynchCalc() {return phiSynchCalc;}
	public double getBetag() {return betag;}
	public double getRadius() {return radius;}
	public int getPhaseFlag() {return phaseFlag;}
	public double getBetaRef() {return betaRef;}
	public double getIntNpt() {return intNpt;}
	public double getKstep() {return kstep;}
	public double getKg() {return kg;}
	public double getPhiTWdeg() {return phiTWdeg;}
	public boolean isTtInfo() {return ttInfo;}

	public void setMode(int mode) throws LinacLegoException 
	{
		this.mode = mode;
		getDataElement("mode").setValue(Integer.toString(mode));
		setIntegrationRange(intNpt);
		cellLength = PI / getKg();
		if (mode == 0) cellLength = 2.0 * cellLength;
		setLength((double) ncells * cellLength);
	}
	public void setNcells(int ncells) throws LinacLegoException 
	{
		this.ncells = ncells;
		setLength((double) ncells * cellLength);
		getDataElement("ncells").setValue(Integer.toString(ncells));
	}
	public void setBetag(double betag) throws LinacLegoException 
	{
		this.betag = betag;
		kg = waveNum(betag);
		cellLength = PI / kg;
		if (mode == 0) cellLength = 2.0 * cellLength;
		setLength((double) ncells * cellLength);
		getDataElement("betag").setValue(Double.toString(betag));
	}
	public void setRadius(double radius) throws LinacLegoException 
	{
		this.radius = radius;
		getDataElement("radius").setValue(Double.toString(radius));
	}
	public void setPhaseFlag(int phaseFlag) throws LinacLegoException 
	{
		this.phaseFlag = phaseFlag;
		getDataElement("p").setValue(Integer.toString(phaseFlag));
	}
	public void setBetaRef(double betaRef) throws LinacLegoException 
	{
		this.betaRef = betaRef;
		kref = waveNum(betaRef);
		getDataElement("betas").setValue(Double.toString(betaRef));
	}
	public void updateBetaRef() throws LinacLegoException
	{
		double betaRefCalc = (beta(geteVin()) + beta(geteVout())) / 2.0;
		setBetaRef(betaRefCalc) ;
	}
	public void setKstep(double kstep) {this.kstep = kstep;}
	public void setPhiTWdeg(double phiTWdeg) throws LinacLegoException 
	{
		this.phiTWdeg = phiTWdeg;
		getDataElement("theta").setValue(Double.toString(phiTWdeg));
	}
	public void setTtInfo(boolean ttInfo) {this.ttInfo = ttInfo;}
	@Override
	public double characteristicValue() {return Math.abs(getVoltage());}
	@Override
	public String characteristicValueUnit() {return "MV";}

}
