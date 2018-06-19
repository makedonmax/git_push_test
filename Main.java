package com.company;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("PDF and/or printer is not specified!");
            return;
        }
        String pdf = args[0];
        String printer = args[1];
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(pdf));

            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(new Copies(1));
            aset.add(MediaSizeName.ISO_A4);
            aset.add(Sides.ONE_SIDED);
            aset.add(OrientationRequested.LANDSCAPE);

            PDFPrintable printable = new PDFPrintable(document, Scaling.SHRINK_TO_FIT);

            PrintService myPrintService = findPrintService(printer);

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));

            if (myPrintService != null) {
                job.setPrintService(myPrintService);
                job.setPrintable(printable);
                job.print(aset);
            }
            else {
                System.out.println("Printer not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (document != null) try {
                document.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }
}
