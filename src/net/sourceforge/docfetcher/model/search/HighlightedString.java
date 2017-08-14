/*******************************************************************************
 * Copyright (c) 2011 Tran Nam Quang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tran Nam Quang - initial API and implementation
 *******************************************************************************/

package net.sourceforge.docfetcher.model.search;

import java.awt.Desktop;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sourceforge.docfetcher.util.Util;
import net.sourceforge.docfetcher.util.annotations.Immutable;
import net.sourceforge.docfetcher.util.annotations.NotNull;

/**
 * @author Tran Nam Quang
 */
public final class HighlightedString {
	private final List<String> strings = new ArrayList<String>(1);
	private final List<Range> ranges;
	private int length;

	HighlightedString(@NotNull String string, @NotNull List<Range> ranges) {
		Util.checkNotNull(string, ranges);
		strings.add(string);
		length = string.length();
		this.ranges = ranges;
	}

	@NotNull
	public String getString() {
		StringBuilder sb = new StringBuilder(length);
		for (String string : strings)
			sb.append(string);
		return sb.toString();
	}

	@Immutable
	@NotNull
	public List<Range> getRanges() {
		return Collections.unmodifiableList(ranges);
	}

	public int length() {
		return length;
	}

	public boolean isEmpty() {
		return length == 0;
	}

	public String hasEmail() {
		String[] splitStrings = strings.get(0).split("\n| ");
		System.out.println("ORIGINAL STRING:  " + strings.get(0));
		for (String string : splitStrings) {
			System.out.println("STRING: " + string);
			Pattern p = Pattern.compile(
					"^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
			Matcher m = p.matcher(string);

			if (m.find()) {
				String email = m.group();
				
				strings.add(m.group());
			}

		}
		if(strings.size() > 0){
			JFrame allEmails = new JFrame();
			allEmails.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			allEmails.getContentPane().setLayout(new GridLayout(strings.size(), 1));
			for(int i = 1; i < strings.size(); i++) {
				JButton btn = new JButton();
				String email = strings.get(i);
				btn.setText("Send Email to: " + email );
				allEmails.add(btn);
				
				btn.addActionListener(new java.awt.event.ActionListener() {
		            @Override
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
//		                String name = JOptionPane.showInputDialog(parent,
//		                        "NEW EMAIL!!! " + email, null);
		            	try {
				            Desktop.getDesktop().mail(new URI("mailto:" + email + "?subject=Hello"));
				        } catch (URISyntaxException | IOException ex) {
				        }
		            }
		        });
			}
			JButton btn = new JButton();
			btn.setText("Send Email to All" );
			allEmails.add(btn);
			
			btn.addActionListener(new java.awt.event.ActionListener() {
	            @Override
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
//	                String name = JOptionPane.showInputDialog(parent,
//	                        "NEW EMAIL!!! " + email, null);
	            	String emailList = "";
	            	for(int i = 1; i < strings.size(); i++){
		            	emailList = emailList + strings.get(i)+ ",";
	            	}
	            	emailList = emailList.substring(0, emailList.length()-1);
	            	try {
			            Desktop.getDesktop().mail(new URI("mailto:" + emailList + "?subject=Hello"));
			        } catch (URISyntaxException | IOException ex) {
			        }
	            }
	        });
			allEmails.pack();
	        allEmails.setVisible(true);
			return strings.get(0);
		}
		else{
			return null;
		}
	}

	public int getRangeCount() {
		return ranges.size();
	}

	// The receiver is modified, the given highlighted string is not
	public void add(@NotNull HighlightedString otherString) {
		Util.checkNotNull(otherString);
		strings.addAll(otherString.strings);

		// We must create new Range objects here, otherwise we'll modify the
		// given highlighted string
		for (Range range : otherString.ranges)
			ranges.add(new Range(range.start + length, range.length));

		length += otherString.length;
	}

}
