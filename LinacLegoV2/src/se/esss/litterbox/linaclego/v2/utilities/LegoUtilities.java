package se.esss.litterbox.linaclego.v2.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.esss.litterbox.linaclego.v2.LinacLegoException;

public class LegoUtilities 
{
	public static void copyFolder(File src, File dest) throws LinacLegoException 
	{
		if(!src.exists()) throw new LinacLegoException(src.getName() + " does not exist");
		if(src.isDirectory())
	    {
	    		
	    		//if directory not exists, create it
			if(!dest.exists()) dest.mkdir();
	    		
	    		//list all the directory contents
			String files[] = src.list();
			for (String file : files) 
			{
    		   //construct the src and dest file structure
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   //recursive copy
    		   copyFolder(srcFile,destFile);
    		}
	    	   
    	}
		else
    	{
	    		//if file, then copy it
	    		//Use bytes stream to support all file types
			try {
				InputStream in = new FileInputStream(src);
		        OutputStream out = new FileOutputStream(dest); 
                
		        byte[] buffer = new byte[1024];
		    
		        int length;
	        //copy the file content in bytes 
		        while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
	 
		        in.close();
		        out.close();
			} catch (IOException e) {throw new LinacLegoException(e);}
	    }
	}

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
