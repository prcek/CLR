package com.prcek.clr.server;

import java.awt.Color;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Element;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Font;
import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;
import com.prcek.clr.client.data.InitDataBundle;
import com.prcek.clr.client.data.LessonMember;
import com.prcek.clr.client.data.MassageItem;
import com.prcek.clr.client.data.MassageType;


public class PrintService {
    private Font font;
    private Font font_big;
    private BaseFont baseFont;
	
	public PrintService(byte[] font_data) {
		try {
			baseFont = BaseFont.createFont("dummy.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED,false,font_data,null);
			font = new Font(baseFont, 8, Font.BOLD);
			font_big = new Font(baseFont, 14, Font.BOLD);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	private PdfPTable make_lecture_member_line(LessonMember m) {
        PdfPTable table = new PdfPTable(4);
        table.getDefaultCell().setBorder(0);
        table.addCell(new Phrase(m.getOrderAsString(),font));
        table.addCell(new Phrase(m.getSurname(),font));
        table.addCell(new Phrase(m.getName(),font));
        table.addCell(new Phrase(m.getPhone(),font));
        return table;
    }

	private PdfPTable make_massage_item_line(MassageItem m, List<MassageType> types) {
        PdfPTable table = new PdfPTable(4);
        table.getDefaultCell().setBorder(0);
        
        Date start = new Date(m.getStart());
        String start_s = String.format("%02d:%02d", start.getHours(),start.getMinutes());
        
        if (m.isEmpty()) {
            table.addCell(new Phrase(start_s,font));
            table.addCell(new Phrase("",font));
            table.addCell(new Phrase("",font));
            table.addCell(new Phrase("",font));
        } else {
        	
        	int typ = m.getType();
        	String typ_name = "?";
        	for(MassageType mt : types) {
        		if (mt.getId()==typ) {
        			typ_name = mt.getName();
        			break;
        		}
        	}
        	if (m.getDesc()!=null)  {
        		typ_name = typ_name+" "+m.getDesc();
        	}
        	
        	table.addCell(new Phrase(start_s,font));
        	if (m.isPrimary()) {
        		table.addCell(new Phrase(m.getSurname(),font));
        		table.addCell(new Phrase(m.getPhone(),font));
        	} else {
        		table.addCell(new Phrase("----",font));
        		table.addCell(new Phrase("----",font));
        	}
        	table.addCell(new Phrase(typ_name,font));
        }
        return table;
    }
	
	public Boolean print_lecture_members(OutputStream os, String lesson, ArrayList<LessonMember> members) {
		Document document = new Document(PageSize.A4,28,28,28,28);
		try {
	        PdfWriter.getInstance(document, os);
            document.open();
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(89);
            table.getDefaultCell().setBorderColor(Color.lightGray);

            table.addCell(new Phrase(lesson,font_big));
            //table.addCell(makeMoneyHeader());

            for(LessonMember m: members) {
                            table.addCell(make_lecture_member_line(m));
            }
            document.add(table);

		} catch (DocumentException ex) {
			System.err.println(ex.getMessage());
			return Boolean.FALSE;
		}
		document.close();
		return Boolean.TRUE;
	}
	public Boolean print_massage_day(OutputStream os, String desc, List<MassageItem> items, InitDataBundle data) {
		Document document = new Document(PageSize.A4,28,28,28,28);
		try {
	        PdfWriter.getInstance(document, os);
            document.open();
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(89);
            table.getDefaultCell().setBorderColor(Color.lightGray);
            table.addCell(new Phrase(desc,font_big));
            for(MassageItem i: items) {
                table.addCell(make_massage_item_line(i,data.getMassageTypes()));
            }
            document.add(table);
            
		} catch (DocumentException ex) {
			System.err.println(ex.getMessage());
			return Boolean.FALSE;
		}
		document.close();
		return Boolean.TRUE;
		
	}

}
