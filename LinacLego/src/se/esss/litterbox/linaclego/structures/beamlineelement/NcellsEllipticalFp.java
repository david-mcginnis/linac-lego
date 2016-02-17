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


public class NcellsEllipticalFp extends Ncells
{
	private double	elmax;
	private double	endfac;
	private double	phisdeg;
	private double	xelmax;
	private double 	x3harm = 0.0;
	
	public NcellsEllipticalFp(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		super.addDataElements();
		addDataElement("elmax", "0.0", "double","Volt/m");
		addDataElement("endfac", "0.0", "double","unit");
		addDataElement("phisdeg", "0.0", "double", "deg");
		addDataElement("x3harm", "0.0", "double","unit");
		addDataElement("xelmax", "0.0", "double","unit");
	}
	@Override
	public void readDataElements() throws LinacLegoException 
	{
		super.readDataElements();
		setMode(1);
		setNcells(Integer.parseInt(getDataElement("ncells").getValue()));
		setBetag(Double.parseDouble(getDataElement("betag").getValue()));
		setRadius(Double.parseDouble(getDataElement("radius").getValue()));
		elmax = Double.parseDouble(getDataElement("elmax").getValue());
		endfac = Double.parseDouble(getDataElement("endfac").getValue());
		phisdeg = Double.parseDouble(getDataElement("phisdeg").getValue());
		xelmax = Double.parseDouble(getDataElement("xelmax").getValue());
		x3harm = Double.parseDouble(getDataElement("x3harm").getValue());
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		setTtInfo(true);
		matchSynchPhase(phisdeg, 5);
	}
	@Override
	protected double centerCellFieldProfile(double kgz)
	{
		double ec = 0;
		if (Math.abs(kgz) <= (PI / 2.0) )
		{
			ec = (1.0 + x3harm) * Math.cos(kgz) - x3harm * Math.cos(3.0 * kgz);
		}
		ec = ec * xelmax * elmax;
		return ec;
	}
	@Override
	protected double outputCellFieldProfile(double kgz)
	{
		double ee = 0;
		if (kgz < 0) 
		{
			if (Math.abs(kgz) <= (PI / 2.0) )
			{
				ee = (1.0 + x3harm) * Math.cos(kgz) - x3harm * Math.cos(3.0 * kgz);
			}
		}
		else
		{
			ee = 2.0 * endfac * kgz / PI;
			ee = (1.0 + x3harm) * Math.exp(-ee * ee);
			if ( kgz <= (PI / 2.0))
			{
				ee = ee - x3harm * Math.cos(3.0 * kgz);
			}
		}
		ee = ee * xelmax * elmax;
		return ee;
	}
	@Override
	protected double inputCellFieldProfile(double kgz)
	{
		return outputCellFieldProfile(-kgz);
	}
	public double getElmax() {return elmax;}
	public double getEndfac() {return endfac;}
	public double getPhisdeg() {return phisdeg;}
	public double getXelmax() {return xelmax;}
	public double getX3harm() {return x3harm;}

	public void setElmax(double elmax) throws LinacLegoException 
	{
		this.elmax = elmax;
		getDataElement("elmax").setValue(Double.toString(elmax));
	}
	public void setEndfac(double endfac) throws LinacLegoException 
	{
		this.endfac = endfac;
		getDataElement("endfac").setValue(Double.toString(endfac));
	}
	public void setPhisdeg(double phisdeg) throws LinacLegoException 
	{
		this.phisdeg = phisdeg;
		getDataElement("phisdeg").setValue(Double.toString(phisdeg));
	}
	public void setXelmax(double xelmax) throws LinacLegoException
	{
		this.xelmax = xelmax;
		getDataElement("xelmax").setValue(Double.toString(xelmax));
	}
	public void setX3harm(double x3harm) throws LinacLegoException 
	{
		this.x3harm = x3harm;
		getDataElement("x3harm").setValue(Double.toString(x3harm));
	}
}
