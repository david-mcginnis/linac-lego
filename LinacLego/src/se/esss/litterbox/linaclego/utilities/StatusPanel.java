/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.linaclego.utilities;

import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StatusPanel 
{
	private JTextArea textArea;
	private JScrollPane scrollPane;
	public StatusPanel(int numLines, String title)
	{
		textArea = new JTextArea();
		textArea.setRows(numLines);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
				BorderFactory.createEmptyBorder(5,5,5,5)));
	}
	public void setText(String text)
	{
		String info = new Date().toString() + " : " + text;
		textArea.insert(info + "\n", 0);
		scrollToTop();
//		System.out.println(info);
	}
	public JScrollPane getScrollPane() 
	{
		return scrollPane;
	}
	public void scrollToTop()
	{
		textArea.setCaretPosition(0);
	}
}
