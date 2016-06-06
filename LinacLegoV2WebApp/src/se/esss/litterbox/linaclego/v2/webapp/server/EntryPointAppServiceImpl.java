package se.esss.litterbox.linaclego.v2.webapp.server;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import se.esss.litterbox.linaclego.v2.webapp.client.EntryPointAppService;
import se.esss.litterbox.linaclego.v2.webapp.shared.GskelException;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EntryPointAppServiceImpl extends RemoteServiceServlet implements EntryPointAppService 
{
	@Override
	public String[] gskelServerTest(String name, boolean debug, String[] debugResponse) throws GskelException 
	{
		System.out.println(name);
		if (debug)
		{
			try {Thread.sleep(3000);} catch (InterruptedException e) {}
			return debugResponse;
		}
		try {Thread.sleep(3000);} catch (InterruptedException e) {}
		String[] answer = {"high", "low"};
		return answer;
	}

}
