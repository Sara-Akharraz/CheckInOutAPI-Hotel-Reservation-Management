package com.api.apicheck_incheck_out.pdf;

import com.api.apicheck_incheck_out.entity.Reservation;
import com.itextpdf.text.*;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;


@Slf4j
public  class FacturePDF {
    protected static final String MAD = "MAD";
    protected static final String PRIX = "Prix";
    private FacturePDF(){
        throw new UnsupportedOperationException("Cette classe ne doit pas étre instanciée");
    }
   protected static final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 6);
   protected static final Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 4);

    private static Image loadLogo() throws IOException, BadElementException {
        return Image.getInstance("src/main/resources/logo.png");
    }

    public static void addLogo(Document document) {
        try {
            Image logo = loadLogo();

            logo.scaleAbsolute(30f, 30f);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {

            log.error("Erreur lors du chargement de l'image du logo: {}", e.getMessage());
        }
    }
    private static void addHotelInfo(Document document) throws DocumentException{
        Paragraph hotelInfo = new Paragraph();
        hotelInfo.setAlignment(Element.ALIGN_CENTER);
        hotelInfo.setFont(bodyFont);
        hotelInfo.setLeading(8f);
        hotelInfo.add("Hôtel Nature\n");
        hotelInfo.add("123 Chemin de la Forêt, Natureville, Maroc\n");
        hotelInfo.add("Tél: +212 6 23 45 67 89\n");
        hotelInfo.add("Email: contact@hotelnature.fr\n");
        hotelInfo.add("Réception ouverte 24h/24, 7j/7\n");
        document.add(hotelInfo);
        document.add(Chunk.NEWLINE);
    }
    private static void addClientInfo(Document document, Reservation reservation) throws DocumentException{
        Paragraph userInfo = new Paragraph();
        userInfo.setAlignment(Element.ALIGN_LEFT);
        userInfo.setFont(bodyFont);
        userInfo.setLeading(8f);
        userInfo.add("Client :             " + reservation.getUser().getNom() + " " + reservation.getUser().getPrenom() + "\n");
        userInfo.add("Email :              " + reservation.getUser().getEmail() + "\n");
        document.add(userInfo);
        document.add(Chunk.NEWLINE);
    }
    private static void addReservationInfo(Document document,Reservation reservation) throws DocumentException{
        Paragraph resInfo = new Paragraph();
        resInfo.setFont(bodyFont);
        resInfo.setLeading(8f);
        resInfo.setAlignment(Element.ALIGN_LEFT);
        resInfo.add("Numéro de réservation : " + reservation.getId() + "\n");
        resInfo.add("Date de debut réservation : " + reservation.getDateDebut() + "\n");
        resInfo.add("Date de fin réservation : " + reservation.getDateFin() + "\n");
        resInfo.add("Chambres réservées :\n");
        document.add(resInfo);
    }
    static void generateDocumentHead(String factureTitle,Document document,Reservation reservation) throws DocumentException {

            Paragraph title = new Paragraph(factureTitle, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            addLogo(document);
            addHotelInfo(document);
            addClientInfo(document,reservation);
            addReservationInfo(document,reservation);
    }


}
