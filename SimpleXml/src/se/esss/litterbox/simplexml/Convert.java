/*
Copyright (c) 2014 European Spallation Source

This file is part of SimpleXml.
SimpleXml is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.simplexml;

/**
 * @author mcginnis
 *
 */
public class Convert 
{
	/**
	 * @param s
	 * @return int
	 */
	public static int StringToInt(String s)
	{
		return Integer.valueOf(s).intValue();
	}
	/**
	 * @param s
	 * @return double
	 */
	public static double StringToDouble(String s)
	{
		return Double.valueOf(s).doubleValue();
	}
	/**
	 * @param s
	 * @return float
	 */
	public static float StringToFloat(String s)
	{
		return Float.valueOf(s).floatValue();
	}
	/**
	 * @param s
	 * @return boolean
	 */
	public static boolean StringToBoolean(String s)
	{
		return Boolean.valueOf(s).booleanValue();
	}
	/**
	 * @param ii
	 * @return String
	 */
	public static String intToString(int ii)
	{
		return Integer.toString(ii);
	}
	/**
	 * @param d
	 * @return String
	 */
	public static String doubleToString(double d)
	{
		return Double.toString(d);
	}
	/**
	 * @param f
	 * @return String
	 */
	public static String floatToString(float f)
	{
		return Float.toString(f);
	}
	/**
	 * @param b
	 * @return String
	 */
	public static String booleanToString(boolean b)
	{
		return Boolean.toString(b);
	}
}
