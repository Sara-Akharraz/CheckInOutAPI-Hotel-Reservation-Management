package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enums.FactureType;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

public class FacturePDF {
    public static byte[] gerercheckinFacturePDF(Reservation reservation) {
        List<Facture> factures = reservation.getFactureList();

        if (factures == null || factures.isEmpty()) {
            throw new RuntimeException("Aucune facture trouvée pour cette réservation !");
        }

        Optional<Facture> factureCheckin = factures.stream()
                .filter(f -> f.getType() == FactureType.Check_In)
                .findFirst();

        if (!factureCheckin.isPresent()) {
            throw new RuntimeException("Aucune facture de type Check_in trouvée pour cette réservation !");
        }

        Facture facture = factureCheckin.get();

        Document document = new Document(new Rectangle(205, 300), 10, 10, 10, 10); // Marges ajustées
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();


            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 6);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 4);


            Paragraph title = new Paragraph("Facture check-In", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // user
            Paragraph userInfo = new Paragraph();
            userInfo.setAlignment(Element.ALIGN_LEFT);
            userInfo.setFont(bodyFont);
            userInfo.add("Client :             " + reservation.getUser().getNom() + " " + reservation.getUser().getPrenom() + "\n");
            userInfo.add("Email :              " + reservation.getUser().getEmail() + "\n");
            document.add(userInfo);
            document.add(Chunk.NEWLINE);


            Paragraph factureInfo = new Paragraph();
            factureInfo.setFont(bodyFont);
            factureInfo.setAlignment(Element.ALIGN_LEFT);
            factureInfo.add("Date de la facture : " + reservation.getCheckIn().getDateCheckIn() + "\n");
            factureInfo.add("Tax :                 " + facture.getTax()+ " MAD\n");
            factureInfo.add("Montant total :       " + facture.getCheckInMontant() + " MAD\n");


            document.add(factureInfo);
            document.add(Chunk.NEWLINE);

            // Réservation
            Paragraph resInfo = new Paragraph();
            resInfo.setFont(bodyFont);
            resInfo.setAlignment(Element.ALIGN_LEFT);
            resInfo.add("Numéro de réservation : " + reservation.getId() + "\n");
            resInfo.add("Chambres réservées :\n");
            document.add(resInfo);

            // chambres
            PdfPTable chambreTable = new PdfPTable(2);
            chambreTable.setWidthPercentage(100);
            chambreTable.setSpacingBefore(5f);
            chambreTable.setWidths(new int[]{3, 1});

            chambreTable.addCell(new Phrase("Chambre", bodyFont));
            chambreTable.addCell(new Phrase("Prix (MAD)", bodyFont));

            reservation.getChambreReservations().forEach(chambre -> {
                chambreTable.addCell(new Phrase("  - " + chambre.getChambre().getNom(), bodyFont));
                chambreTable.addCell(new Phrase(String.valueOf(chambre.getChambre().getPrix()), bodyFont));
            });

            document.add(chambreTable);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }

        return out.toByteArray();
    }


}
