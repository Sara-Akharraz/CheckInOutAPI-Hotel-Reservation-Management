package com.api.apicheck_incheck_out.pdftest;

import com.api.apicheck_incheck_out.pdf.FacturePDF;
import com.itextpdf.text.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FacturePDFTest {
    @Test
    void testCheckOutFacturePDF_PrivateConstructor(){
        InvocationTargetException ex=assertThrows(InvocationTargetException.class,()->{
            Constructor<FacturePDF> constructor= FacturePDF.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        assertEquals("Cette classe ne doit pas étre instanciée",ex.getCause().getMessage());
    }
    @Test
    @DisplayName("Test loadLogo - chemin invalide")
    void testLoadLogo_InvalidPath() {
        // Arrange
        try (MockedStatic<Image> imageMock = mockStatic(Image.class)) {
            imageMock.when(() -> Image.getInstance(anyString()))
                    .thenThrow(new IOException("Fichier introuvable"));

            // Act - Utiliser la réflexion pour tester loadLogo
            Method loadLogoMethod = FacturePDF.class.getDeclaredMethod("loadLogo");
            loadLogoMethod.setAccessible(true);

            // Assert
            Exception exception = assertThrows(Exception.class, () -> {
                loadLogoMethod.invoke(null);
            });

            // Vérifier que l'exception racine est bien IOException
            Throwable cause = exception.getCause();
            assertNotNull(cause);
            assertTrue(cause instanceof IOException,
                    "La cause doit être IOException");
        } catch (Exception e) {
            fail("Erreur lors de la configuration du test: " + e.getMessage());
        }
    }


 }
