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
