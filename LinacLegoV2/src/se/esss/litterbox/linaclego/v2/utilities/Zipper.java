package se.esss.litterbox.linaclego.v2.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import se.esss.litterbox.linaclego.v2.LinacLegoException;

public class Zipper 
{
	private List<String> fileList;
	private String sourcePath; 
	public Zipper(String sourcePath) throws IOException
	{
        this.sourcePath = sourcePath;
        fileList = new ArrayList<String>();
        generateFileList(new File(sourcePath));
	}
	public void zipIt(String zipFile) throws LinacLegoException
	{
		byte[] buffer = new byte[1024];
		try
		{
			FileOutputStream fos = new FileOutputStream(zipFile);
	    	ZipOutputStream zos = new ZipOutputStream(fos);
	    	for(String file : this.fileList)
	    	{
	    		ZipEntry ze= new ZipEntry(file);
	        	zos.putNextEntry(ze);
	        	FileInputStream in = new FileInputStream(sourcePath + File.separator + file);
	        	int len;
	        	while ((len = in.read(buffer)) > 0) zos.write(buffer, 0, len);
	        	in.close();
	    	}
	    	zos.closeEntry();
	    	//remember close it
	    	zos.close();
	    } catch(IOException e){throw new LinacLegoException(e);}
	}
	    
	    /**
	     * Traverse a directory and get all files,
	     * and add the file into fileList  
	     * @param node file or directory
	     */
	    public void generateFileList(File node){

	    	//add file only
		if(node.isFile()){
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
		}
			
		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				generateFileList(new File(node, filename));
			}
		}
	 
	    }

	    /**
	     * Format the file path for zip
	     * @param file file path
	     * @return Formatted file path
	     */
	    private String generateZipEntry(String file)
	    {
	    	String zipeEntry = file.substring(sourcePath.length() + 1, file.length());
	    	return zipeEntry;
	    }

}
