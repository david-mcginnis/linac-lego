package se.esss.litterbox.linaclego.v2.webapp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import se.esss.litterbox.linaclego.v2.webapp.shared.GskelException;


/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("entrypointapp")
public interface EntryPointAppService extends RemoteService 
{
	String[] gskelServerTest(String name, boolean debug, String[] debugResponse) throws GskelException;
}
