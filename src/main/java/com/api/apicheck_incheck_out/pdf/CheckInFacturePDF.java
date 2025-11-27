package com.api.apicheck_incheck_out.pdf;

import com.api.apicheck_incheck_out.entity.Facture;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.FactureType;
import com.api.apicheck_incheck_out.enums.PhaseAjoutService;
import com.api.apicheck_incheck_out.exceptionhandling.FactureNotFoundException;
import com.api.apicheck_incheck_out.exceptionhandling.PDFGenerationException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

public class CheckInFacturePDF {
    private CheckInFacturePDF(){
        throw new UnsupportedOperationException("Cette classe ne doit pas étre instanciée");
    }

    public static byte[] gerercheckinFacturePDF(Reservation reservation) {
        List<Facture> factures = reservation.getFactureList();

        if (factures == null || factures.isEmpty()) {
            throw new FactureNotFoundException("Aucune facture trouvée pour cette réservation !");
        }

        Optional<Facture> factureCheckin = factures.stream()
                .filter(f -> f.getType() == FactureType.CHECK_IN)
                .findFirst();

        if (!factureCheckin.isPresent()) {
            throw new FactureNotFoundException("Aucune facture de type Check_in trouvée pour cette réservation !");
        }

        Facture facture = factureCheckin.get();


        Document document = new Document(new Rectangle(205, 400), 10, 10, 10, 10);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            FacturePDF.generateDocumentHead("Pré-Facture",document,reservation);

            // Infos facture
            Paragraph factureInfo = new Paragraph();
            factureInfo.setFont(FacturePDF.bodyFont);
            factureInfo.setLeading(8f);
            factureInfo.setAlignment(Element.ALIGN_LEFT);
            factureInfo.add("Date de la facture : " + reservation.getCheckIn().getDateCheckIn() + "\n");
            factureInfo.add("Tax :                 " + facture.getTax() +FacturePDF.MAD+ " \n");
            factureInfo.add("Montant total :       " + facture.getCheckInMontant() + FacturePDF.MAD+"\n");
            document.add(factureInfo);
            document.add(Chunk.NEWLINE);


            // Tableau des chambres
            PdfPTable chambreTable = new PdfPTable(2);
            chambreTable.setWidthPercentage(100);
            chambreTable.setSpacingBefore(5f);
            chambreTable.setWidths(new int[]{3, 1});
            chambreTable.addCell(new Phrase("Chambre", FacturePDF.bodyFont));
            chambreTable.addCell(new Phrase(FacturePDF.PRIX +"("+FacturePDF.MAD+")", FacturePDF.bodyFont));

            reservation.getChambreReservations().forEach(chambre -> {
                chambreTable.addCell(new Phrase("  - " + chambre.getChambre().getNom(), FacturePDF.bodyFont));
                chambreTable.addCell(new Phrase(String.valueOf(chambre.getChambre().getPrix()), FacturePDF.bodyFont));
            });

            document.add(chambreTable);

            // Tableau des services
            PdfPTable serviceTable = new PdfPTable(2);
            serviceTable.setWidthPercentage(100);
            serviceTable.setSpacingBefore(5f);
            serviceTable.setWidths(new int[]{3, 1});
            serviceTable.addCell(new Phrase("Service", FacturePDF.bodyFont));
            serviceTable.addCell(new Phrase(FacturePDF.PRIX+"("+FacturePDF.MAD+")", FacturePDF.bodyFont));

            reservation.getServiceList().forEach(service -> {
                if (service.getPhaseAjoutService().equals(PhaseAjoutService.CHECK_IN)) {
                    serviceTable.addCell(new Phrase("  - " + service.getService().getNom(), FacturePDF.bodyFont));
                    serviceTable.addCell(new Phrase(String.valueOf(service.getService().getPrix()), FacturePDF.bodyFont));
                }
            });

            document.add(serviceTable);
            document.close();

        } catch (Exception e) {
            throw new PDFGenerationException("Erreur lors de la génération du PDF", e);
        }

        return out.toByteArray();
    }


}
