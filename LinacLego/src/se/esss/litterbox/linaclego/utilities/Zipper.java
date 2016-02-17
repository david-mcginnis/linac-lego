package se.esss.litterbox.linaclego.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper 
{
	private static final String delim = System.getProperty("file.separator");
	ZipOutputStream zipOutputStream;
	public Zipper(String compressedFilePath) throws IOException
	{
        zipOutputStream = new ZipOutputStream(new FileOutputStream(compressedFilePath));
	}
	public void addFile(String filePath) throws IOException
	{
		FileInputStream fileInputStream = new FileInputStream(filePath);
		zipOutputStream.putNextEntry(new ZipEntry(new File(filePath).getName())); 
        byte[] b = new byte[1024];
        int count = 0;
        while ((count = fileInputStream.read(b)) > 0)  zipOutputStream.write(b, 0, count);
        fileInputStream.close();
	}
	public void addFilesInDir(String dirPath, String extension) throws IOException
	{
		String[] fileListPath = getDirFilePathList(dirPath, extension);
		if (fileListPath != null)
		{
			for (int ii = 0; ii < fileListPath.length; ++ii)
			{
				addFile(fileListPath[ii]);
			}
		}
	}
	public static String[] getDirFilePathList(String dirPath, String extension)
	{
		File dir = new File(dirPath);
		String[] fileListName = dir.list(new MyFileNameFilter(extension));
		String[] fileListPath = null;
		if (fileListName.length > 0)
		{
			fileListPath = new String[fileListName.length];
			for (int ii = 0; ii < fileListName.length; ++ii)
			{
				fileListPath[ii] = dirPath + delim + fileListName[ii];
			}
		}
		return fileListPath;
	}
	private static class MyFileNameFilter implements FilenameFilter
	{
		String extension;
		public MyFileNameFilter(String extension)
		{
			this.extension = extension;
		}
		@Override
		public boolean accept(File directory, String fileName) 
		{
			return fileName.endsWith("." + extension);
		}
		
	}
	public void close() throws IOException
	{
		zipOutputStream.close();
	}

	public static void main(String[] args) 
	{
	}

}
