package com.api.apicheck_incheck_out.pdftest;

import com.api.apicheck_incheck_out.entity.Facture;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.enums.FactureType;
import com.api.apicheck_incheck_out.exceptionhandling.FactureNotFoundException;
import com.api.apicheck_incheck_out.pdf.CheckoutFacturePDF;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
 class CheckoutFacturePDFTest {
    @Test
    void testCheckOutFacturePDF_PrivateConstructor(){
        InvocationTargetException ex=assertThrows(InvocationTargetException.class,()->{
            Constructor<CheckoutFacturePDF> constructor= CheckoutFacturePDF.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        assertEquals("Cette classe ne doit pas étre instanciée",ex.getCause().getMessage());
    }
    @Test
    void testGerercheckoutFacturePDF_noFactures() {
        Reservation res = new Reservation();
        Services service1=new Services();
        service1.setId(1L);
        List<Services> list=List.of(service1);
        res.setFactureList(Collections.emptyList());

        FactureNotFoundException exception = assertThrows(FactureNotFoundException.class, () ->
                CheckoutFacturePDF.gerercheckOutFacturePDF(res,list,500.0)
        );

        assertEquals("Aucune facture trouvée pour cette réservation !", exception.getMessage());
    }

    @Test
    void testGerercheckoutFacturePDF_noCheckOutFacture() {
        Reservation res = new Reservation();
        res.setChambreReservations(new ArrayList<>());
        res.setServiceList(new ArrayList<>());
        Services service1=new Services();
        service1.setId(1L);
        List<Services> list=List.of(service1);
        Facture f1 = new Facture();
        f1.setType(FactureType.CHECK_IN);
        res.setFactureList(List.of(f1));

        FactureNotFoundException exception = assertThrows(FactureNotFoundException.class, () ->
                CheckoutFacturePDF.gerercheckOutFacturePDF(res,list,500.0)
        );

        assertEquals("Aucune facture de type Check-out trouvée pour cette réservation !", exception.getMessage());
    }


 }
