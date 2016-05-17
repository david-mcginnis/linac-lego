package se.esss.litterbox.linaclego.app;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Date;
import java.util.List;

public class LegoAppWatchKeyRunnable implements Runnable
{
	LegoApp legoApp;
	LegoAppWatchKeyRunnable(LegoApp legoApp)
	{
		this.legoApp = legoApp;
	}
	@Override
	public void run() 
	{
		Date oldDate = new Date();
		while(true)
		{
			try 
			{
				WatchKey key = legoApp.getWatchService().take();
				List<WatchEvent<?>> event = key.pollEvents();
				for (int ii = 0; ii < event.size(); ++ii)
				{
					if (event.get(ii).kind().name().equals("ENTRY_MODIFY") && event.get(ii).context().toString().equals(legoApp.getOpenedXmlFile().getName()))
					{
						Date newDate = new Date();
						if ((newDate.getTime() - oldDate.getTime()) > 5000)
						{
							int ichoice = legoApp.optionDialog("File Modified!", legoApp.getOpenedXmlFile().getName() + " modified! Reload?", 
									"Reload", "Cancel",1);
							if (ichoice == 1) legoApp.loadLinacLegoFile();
							oldDate.setTime(newDate.getTime());
						}
					}
				}
				key.reset();
			} 
			catch (InterruptedException e) {}	// retrieve the watchkey
		}
	}
}
