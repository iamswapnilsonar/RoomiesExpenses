package com.harshal.roomiesexpenses.reporting;

import android.graphics.Bitmap;

import com.harshal.roomiesexpenses.entities.Expense;
import com.harshal.roomiesexpenses.entities.GeneratedBill;
import com.harshal.roomiesexpenses.entities.Roommate;
import com.harshal.roomiesexpenses.utils.Util;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Harshal on 1/18/2015.
 */
public class PdfGenerator {
    public static final String ORI_LAND = "LANDSCAPE";
    public static final String ORI_PORT = "PORTRAIT";
    // private static final Font SHEET_HEAD_FONT = new
    // Font(FontFamily.HELVETICA, 9, Font.BOLD);
    // private static final Font TITLE_HEAD_FONT = new
    // Font(FontFamily.HELVETICA, 12, Font.BOLD);
//    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
//    private static final Font DATA_FONT_GRAYSmall = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY);
//    private static final Font TITLE_DATA_FONT = new Font(Font.FontFamily.HELVETICA, 10);

    /*private static final Font COLUMN_HEAD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font DATA_FONT = new Font(Font.FontFamily.HELVETICA, 8);
    private static final Font DATA_FONT_GRAYBIG = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, BaseColor.GRAY);*/

    private static Font COLUMN_HEAD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static Font DATA_FONT = new Font(Font.FontFamily.HELVETICA, 8);
    private static Font DATA_FONT_GRAYBIG = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, BaseColor.GRAY);

    private String monthName;
    private HashMap<Integer, GeneratedBill> billMap;
    private double memberNotFoundAmount;
    private ArrayList<Roommate> arrLstSharedMembers;
    private ArrayList<Expense> arrLstExpenses;
    private HashMap<Integer, String> mapIdName;

    public String generatePdfFormatBill(String monthName, HashMap<Integer, GeneratedBill> billMap, double memberNotFoundAmount, ArrayList<Roommate> arrLstSharedMembers, ArrayList<Expense> arrLstExpenses, HashMap<Integer, String> mapIdName)throws FileNotFoundException, DocumentException {
        createRequiredFonts();

        this.monthName = monthName;
        this.billMap = billMap;
        this.memberNotFoundAmount = memberNotFoundAmount;
        this.arrLstSharedMembers = arrLstSharedMembers;
        this.arrLstExpenses = arrLstExpenses;
        this.mapIdName = mapIdName;
        String fileName = (monthName+" "+Util.getCurrentDateTimeStampForFile()+".pdf");
        String outputPdfFile = Util.PDF_FOLDER_PATH+fileName;
        Document document = getDocument(outputPdfFile, ORI_PORT);
        createPdfContent(document);
        return outputPdfFile;
    }

    private void createRequiredFonts() {
        try {
            BaseFont baseFont = BaseFont.createFont("assets/fonts/Oswald_Regular.ttf", "UTF-8", BaseFont.EMBEDDED);

            COLUMN_HEAD_FONT = new Font(baseFont, 12, Font.BOLD);
            DATA_FONT = new Font(baseFont, 10);
            DATA_FONT_GRAYBIG = new Font(baseFont, 15, Font.NORMAL, BaseColor.GRAY);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPdfContent(Document document) throws DocumentException {
        if (document.isOpen() == false) {
            document.open();
        }
        document.add(new Phrase(new Chunk("* Generated Bills For Month : "+monthName, DATA_FONT_GRAYBIG)));
        addEmptyLine(document, 1);

        List<String> expenseSummaryHeaders = Arrays.asList("SrNo", "Picture", "Member", "Share", "Paid", "Remaining");

        PdfPTable expenseSummaryTable = new PdfPTable(expenseSummaryHeaders.size());
        expenseSummaryTable.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        expenseSummaryTable.setWidthPercentage(100);
        expenseSummaryTable.setWidths(new float[] { 2, 5, 10, 5, 5, 5});
        expenseSummaryTable.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        expenseSummaryTable.getDefaultCell().setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

        //Summary Table Headers
        for (Iterator<String> itr = expenseSummaryHeaders.iterator(); itr.hasNext();) {
            PdfPCell cellhd = new PdfPCell(new Phrase(new Chunk(itr.next(), COLUMN_HEAD_FONT)));
            cellhd.setBackgroundColor(new BaseColor(200, 200, 200));
            cellhd.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cellhd.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cellhd.setFixedHeight(25);
            expenseSummaryTable.addCell(cellhd);
        }

        int count = 1;
        for (Iterator<Roommate> roommateIterator = arrLstSharedMembers.iterator(); roommateIterator.hasNext();){
            Roommate member = roommateIterator.next();
            GeneratedBill tempBill = billMap.get(member.getId());

            Phrase srNoPhrase = new Phrase(new Chunk(String.valueOf(count), DATA_FONT));
            count++;
            Phrase imagePhrase = getImage(member);
            Phrase memberNamePhrase = new Phrase(new Chunk(member.getName(), DATA_FONT));
            Phrase sharePhrase = new Phrase(new Chunk(Util.formatAmount(tempBill.getSharedAmount()), DATA_FONT));
            Phrase paidPhrase = new Phrase(new Chunk(Util.formatAmount(tempBill.getPaidAmount()), DATA_FONT));

            double remainingAmt = tempBill.getSharedAmount() - tempBill.getPaidAmount();
            String strRemaining = Util.formatAmount(remainingAmt);
            if(memberNotFoundAmount > 0){
                double eachExtraPay = memberNotFoundAmount / arrLstSharedMembers.size();
                remainingAmt+=eachExtraPay;
                strRemaining = new StringBuilder().append(strRemaining)
                        .append("\n+\n").append(Util.formatAmount(eachExtraPay)).append(" *NoOne\n\n")
                        .append(Util.formatAmount(remainingAmt)).toString();

            }
            Phrase remainingPhrase = new Phrase(new Chunk(strRemaining, DATA_FONT));

            expenseSummaryTable.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            expenseSummaryTable.getDefaultCell().setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            expenseSummaryTable.getDefaultCell().setFixedHeight(70);
            expenseSummaryTable.addCell(srNoPhrase);
            expenseSummaryTable.addCell(imagePhrase);
            expenseSummaryTable.addCell(memberNamePhrase);
            expenseSummaryTable.addCell(sharePhrase);
            expenseSummaryTable.addCell(paidPhrase);
            expenseSummaryTable.addCell(remainingPhrase);
        }

        document.add(expenseSummaryTable);
        addEmptyLine(document, 1);

        if(memberNotFoundAmount > 0){
            Phrase memberNotFoundAmountPhrase = new Phrase(new Chunk("* Member Not Found Amount : "+Util.formatAmount(memberNotFoundAmount), COLUMN_HEAD_FONT));
            Phrase extraPayAmountPhrase = new Phrase(new Chunk("* So, Each Extra Pay Amount : "+Util.formatAmount(memberNotFoundAmount / arrLstSharedMembers.size()), COLUMN_HEAD_FONT));
            document.add(memberNotFoundAmountPhrase);
            document.add(extraPayAmountPhrase);
            addEmptyLine(document, 1);
        }

        document.add(new Phrase(new Chunk("* Expense Details -> ", DATA_FONT_GRAYBIG)));

        List<String> expenseDetailsHeaders = Arrays.asList("SrNo", "Expense", "Amount", "Paid By", "Shared By", "Created On");

        PdfPTable expenseDetailsTable = new PdfPTable(expenseDetailsHeaders.size());
        expenseDetailsTable.setHorizontalAlignment(PdfPTable.ALIGN_CENTER);
        expenseDetailsTable.setWidthPercentage(100);
        expenseDetailsTable.setWidths(new float[] { 2, 7, 5, 5, 10, 7});
        expenseDetailsTable.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        expenseDetailsTable.getDefaultCell().setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

        //Summary Table Headers
        for (Iterator<String> itr = expenseDetailsHeaders.iterator(); itr.hasNext();) {
            PdfPCell cellhd = new PdfPCell(new Phrase(new Chunk(itr.next(), COLUMN_HEAD_FONT)));
            cellhd.setBackgroundColor(new BaseColor(200, 200, 200));
            cellhd.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cellhd.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cellhd.setFixedHeight(25);
            expenseDetailsTable.addCell(cellhd);
        }

        count = 1;
        for (Iterator<Expense> expenseIterator = arrLstExpenses.iterator(); expenseIterator.hasNext();){
            Expense expense = expenseIterator.next();

            Phrase srNoPhrase = new Phrase(new Chunk(String.valueOf(count), DATA_FONT));
            count++;
            Phrase expensePhrase = new Phrase(new Chunk(expense.geteName(), DATA_FONT));
            Phrase amountPhrase = new Phrase(new Chunk(Util.formatAmount(expense.geteAmount()), DATA_FONT));
            String paidBy = mapIdName.get(expense.getePaidBy());
            Phrase paidByPhrase = new Phrase(new Chunk((paidBy==null?"*No One":paidBy), DATA_FONT));

            String[] arrSharedMembers = expense.geteSharedMembers().split(";");
            StringBuffer buffer = new StringBuffer();
            for (int j = 0; j < arrSharedMembers.length; j++) {
                String memberName = mapIdName.get(Integer.parseInt(arrSharedMembers[j]));
                buffer.append(memberName == null ? "*No One" : memberName);
                buffer.append(", ");
            }
            if (buffer.length() > 2) {
                buffer.delete(buffer.length() - 2, buffer.length() - 1);
            }
            Phrase sharedByPhrase = new Phrase(new Chunk(buffer.toString(), DATA_FONT));
            Phrase createdOnPhrase = new Phrase(new Chunk(expense.geteCreatedDate(), DATA_FONT));

            expenseDetailsTable.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            expenseDetailsTable.getDefaultCell().setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            expenseDetailsTable.getDefaultCell().setFixedHeight(30);
            expenseDetailsTable.addCell(srNoPhrase);
            expenseDetailsTable.addCell(expensePhrase);
            expenseDetailsTable.addCell(amountPhrase);
            expenseDetailsTable.addCell(paidByPhrase);
            expenseDetailsTable.addCell(sharedByPhrase);
            expenseDetailsTable.addCell(createdOnPhrase);
        }
        document.add(expenseDetailsTable);

        if (document.isOpen() == true)
            document.close();
    }


    private Document getDocument(String outputFile, String orientation) throws DocumentException, FileNotFoundException {
        File file = new File(outputFile);
        if (file.exists()) {
            file.delete();
        }
        Document document = null;
        if (orientation.equals(ORI_LAND)) {
            document = new Document(PageSize.A4.rotate());
        } else {
            document = new Document(PageSize.A4);// (PageSize.A4);
        }
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        if (orientation.equals(ORI_PORT)) {
            writer.setPageEvent(new Watermark());
        }
        return document;
    }

    private void addEmptyLine(Document document, int number) throws DocumentException {
        for (int i = 0; i < number; i++) {
            document.add(new Paragraph(" "));
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public class Watermark extends PdfPageEventHelper {
        Paragraph prefaceNextLine = new Paragraph("\n\n");

        @Override
        public void onStartPage(PdfWriter writer, Document document) {

        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(String.format("%d", writer.getPageNumber())), (document

                    .right() - document.left())

                    / 2 + document.leftMargin(), document.bottom(), 0);
        }
    }

    private Phrase getImage(Roommate member) {
        try {
            Bitmap logo = member.getPicture();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            logo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            Image itemImage = Image.getInstance(bitmapdata);
            itemImage.scaleToFit(60, 60);
            return new Phrase(new Chunk(itemImage, 0, 0, true));
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
