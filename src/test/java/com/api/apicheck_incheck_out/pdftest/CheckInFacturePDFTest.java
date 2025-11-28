package com.api.apicheck_incheck_out.pdftest;
import com.api.apicheck_incheck_out.entity.*;
import com.api.apicheck_incheck_out.enums.*;
import com.api.apicheck_incheck_out.exceptionhandling.*;
import com.api.apicheck_incheck_out.pdf.CheckInFacturePDF;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CheckInFacturePDFTest {

    @Test
    void testCheckInFacturePDF_PrivateConstructor(){
        InvocationTargetException ex=assertThrows(InvocationTargetException.class,()->{
            Constructor<CheckInFacturePDF> constructor=CheckInFacturePDF.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        assertEquals("Cette classe ne doit pas étre instanciée",ex.getCause().getMessage());
    }

    @Test
    void testGerercheckinFacturePDF_noFactures() {
        Reservation res = new Reservation();
        res.setFactureList(Collections.emptyList());

        FactureNotFoundException exception = assertThrows(FactureNotFoundException.class, () ->
                CheckInFacturePDF.gerercheckinFacturePDF(res)
        );

        assertEquals("Aucune facture trouvée pour cette réservation !", exception.getMessage());
    }

    @Test
    void testGerercheckinFacturePDF_noCheckInFacture() {
        Reservation res = new Reservation();
        Facture f1 = new Facture();
        f1.setType(FactureType.CHECK_OUT);
        res.setFactureList(List.of(f1));

        FactureNotFoundException exception = assertThrows(FactureNotFoundException.class, () ->
                CheckInFacturePDF.gerercheckinFacturePDF(res)
        );

        assertEquals("Aucune facture de type Check_in trouvée pour cette réservation !", exception.getMessage());
    }

    @Test
    void testGerercheckinFacturePDF_pdfGenerationException() {
        Reservation res = new Reservation();
        Facture f = new Facture();
        f.setType(FactureType.CHECK_IN);
        res.setFactureList(List.of(f));

        PDFGenerationException exception = assertThrows(PDFGenerationException.class, () ->
                CheckInFacturePDF.gerercheckinFacturePDF(res)
        );

        assertTrue(exception.getMessage().contains("Erreur lors de la génération du PDF"));
    }
}
