package com.api.apicheck_incheck_out.pdf;

import com.api.apicheck_incheck_out.entity.Facture;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.FactureType;
import com.api.apicheck_incheck_out.exceptionhandling.FactureNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.PDFGenerationException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

public class CheckoutFacturePDF {
    private CheckoutFacturePDF(){
        throw new UnsupportedOperationException("Cette classe ne doit pas étre instanciée");
    }
    public static byte[] gerercheckOutFacturePDF(Reservation reservation, List<Services> extras, double total) {
        List<Facture> factures = reservation.getFactureList();

        if (factures == null || factures.isEmpty()) {
            throw new FactureNotFoundException("Aucune facture trouvée pour cette réservation !");
        }

        Optional<Facture> factureCheckOut = factures.stream()
                .filter(f -> f.getType() == FactureType.CHECK_OUT)
                .findFirst();

        if (!factureCheckOut.isPresent()) {
            throw new FactureNotFoundException("Aucune facture de type Check-out trouvée pour cette réservation !");
        }


        Document document = new Document(new Rectangle(205, 400), 10, 10, 10, 10);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            FacturePDF.generateDocumentHead("Facture",document,reservation);

            // facture
            Paragraph factureInfo = new Paragraph();
            factureInfo.setFont(FacturePDF.bodyFont);
            factureInfo.setLeading(8f);
            factureInfo.setAlignment(Element.ALIGN_LEFT);
            factureInfo.add("Date de la facture : " + reservation.getCheckOut().getDateCheckOut() + "\n");
            factureInfo.add("Montant total :       " + total +FacturePDF.MAD+"\n");
            document.add(factureInfo);
            document.add(Chunk.NEWLINE);


            // services
            PdfPTable extrasTable = new PdfPTable(2);
            extrasTable.setWidthPercentage(100);
            extrasTable.setSpacingBefore(5f);
            extrasTable.setWidths(new int[]{3, 1});

            extrasTable.addCell(new Phrase("Service", FacturePDF.bodyFont));
            extrasTable.addCell(new Phrase(FacturePDF.PRIX+"("+FacturePDF.MAD+")", FacturePDF.bodyFont));

            if (extras == null || extras.isEmpty()) {
                PdfPCell noExtrasCell = new PdfPCell(new Phrase("Aucun service ajouté au séjour", FacturePDF.bodyFont));
                noExtrasCell.setColspan(2);
                noExtrasCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                extrasTable.addCell(noExtrasCell);
            } else {
                extras.forEach(extra -> {
                    extrasTable.addCell(new Phrase(extra.getNom(), FacturePDF.bodyFont));
                    extrasTable.addCell(new Phrase(String.valueOf(extra.getPrix()), FacturePDF.bodyFont));
                });
            }

            document.add(extrasTable);

            document.close();

        } catch (DocumentException e) {
            throw new PDFGenerationException("Erreur lors de la génération du PDF", e);
        }

        return out.toByteArray();
    }
}
