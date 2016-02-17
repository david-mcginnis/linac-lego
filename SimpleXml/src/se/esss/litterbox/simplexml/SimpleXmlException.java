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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author mcginnis
 *
 */
public class SimpleXmlException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5197848508573008856L;

	public SimpleXmlException(Exception e)
	{
		super(e);
	}
	public SimpleXmlException(String smessage)
	{
		super(smessage);
	}
	public SimpleXmlException(String smessage, Throwable cause)
	{
		super(smessage, cause);
	}
	public String getRootCause()
	{
		Throwable chain = getCause();
		String errorMessage  = chain.toString();
		while (chain != null)
		{
			errorMessage  = chain.toString();
			chain = chain.getCause();
		}
		return errorMessage;
	}
	public String getStackTraceString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		printStackTrace(pw);
		pw.close();
		String st = sw.toString();
		try {sw.close();} catch (IOException e) {}
		return st;
	}
	public void printErrorMessage()
	{
		System.out.println("SimpleXmlTagException: " + " " + getMessage());
	}

}
