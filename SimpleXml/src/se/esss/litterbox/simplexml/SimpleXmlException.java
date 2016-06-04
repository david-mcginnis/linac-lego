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
