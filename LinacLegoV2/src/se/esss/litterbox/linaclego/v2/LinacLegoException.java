package se.esss.litterbox.linaclego.v2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("serial")
public class LinacLegoException extends Exception
{
	public LinacLegoException() {}
	public LinacLegoException(Exception e) {super(e);}
	public LinacLegoException(String message) {super(message);}
	public String getRootCause()
	{
		Throwable chain = getCause();
		if (chain == null) return getMessage();
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

}
