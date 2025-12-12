package com.example.demo.util;

import com.example.demo.view.components.VenteForm;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FactureVentePDFGenerator {

    public static void genererFactureVente(
            int numeroVente,
            String client,
            LocalDate date,
            double totalGlobal,
            double montantPaye,
            double resteAPayer,
            List<VenteForm.ProductItem> produits,
            Stage parentStage) {

        try {
            System.out.println("🚀 Début génération facture vente #" + numeroVente);
            System.out.println("📊 Nombre de produits : " + (produits != null ? produits.size() : 0));

            // 1. Choisir l'emplacement
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer la facture de vente");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            fileChooser.setInitialFileName("facture_vente_" + numeroVente + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf");

            File file = fileChooser.showSaveDialog(parentStage);

            if (file == null) {
                System.out.println("❌ Annulé par l'utilisateur");
                return;
            }

            // 2. Créer le document
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // 3. Écrire le contenu
            PDPageContentStream content = new PDPageContentStream(document, page);

            // Position initiale
            float y = 780;
            float marge = 50;

            // TITRE PRINCIPAL
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 22);
            content.newLineAtOffset(marge, y);
            content.showText("FACTURE DE VENTE");
            content.endText();
            y -= 30;

            // Sous-titre
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 14);
            content.newLineAtOffset(marge, y);
            content.showText("Gestion de Stock - Facture Client");
            content.endText();
            y -= 40;

            // Ligne de séparation
            content.moveTo(marge, y);
            content.lineTo(550, y);
            content.stroke();
            y -= 30;

            // INFORMATIONS ENTÊTE
            float infoX = marge;
            float infoY = y;

            // Colonne gauche : Informations vente
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.newLineAtOffset(infoX, infoY);
            content.showText("N° FACTURE :");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(infoX + 90, infoY);
            content.showText("VEN-" + numeroVente);
            content.endText();
            infoY -= 20;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.newLineAtOffset(infoX, infoY);
            content.showText("DATE VENTE :");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(infoX + 90, infoY);
            content.showText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            content.endText();
            infoY -= 20;

            // Colonne droite : Informations client
            float infoXRight = 350;
            float infoYRight = y;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.newLineAtOffset(infoXRight, infoYRight);
            content.showText("CLIENT :");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(infoXRight + 60, infoYRight);
            content.showText(client);
            content.endText();
            infoYRight -= 20;

            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            content.newLineAtOffset(infoXRight, infoYRight);
            content.showText("GÉNÉRÉ LE : ");
            content.endText();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(infoXRight + 60, infoYRight);
            content.showText(LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            content.endText();

            y = Math.min(infoY, infoYRight) - 40;

            // Ligne de séparation
            content.moveTo(marge, y);
            content.lineTo(550, y);
            content.stroke();
            y -= 30;

            // TITRE TABLEAU
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.newLineAtOffset(marge, y);
            content.showText("DÉTAIL DES PRODUITS VENDUS");
            content.endText();
            y -= 30;

            // EN-TÊTES TABLEAU
            content.setFont(PDType1Font.HELVETICA_BOLD, 11);

            // Dessiner fond gris pour les en-têtes
            content.setNonStrokingColor(240, 240, 240);
            content.addRect(marge, y - 5, 500, 20);
            content.fill();
            content.setNonStrokingColor(0, 0, 0); // Réinitialiser la couleur

            // Position des colonnes
            float[] colPositions = {marge, marge + 150, marge + 230, marge + 350, marge + 430};
            String[] headers = {"PRODUIT", "QUANTITÉ", "PRIX UNITAIRE", "TOTAL", "NO."};

            for (int i = 0; i < headers.length; i++) {
                content.beginText();
                content.newLineAtOffset(colPositions[i], y);
                content.showText(headers[i]);
                content.endText();
            }
            y -= 25;

            // Ligne sous les en-têtes
            content.moveTo(marge, y);
            content.lineTo(550, y);
            content.stroke();
            y -= 10;

            // PRODUITS
            content.setFont(PDType1Font.HELVETICA, 10);

            if (produits != null && !produits.isEmpty()) {
                int index = 1;
                double totalProduits = 0;

                for (VenteForm.ProductItem produit : produits) {
                    // Numéro
                    content.beginText();
                    content.newLineAtOffset(colPositions[4], y);
                    content.showText(String.valueOf(index++));
                    content.endText();

                    // Produit
                    content.beginText();
                    content.newLineAtOffset(colPositions[0], y);
                    content.showText(produit.getProduct());
                    content.endText();

                    // Quantité
                    content.beginText();
                    content.newLineAtOffset(colPositions[1], y);
                    content.showText(String.valueOf(produit.getQuantity()));
                    content.endText();

                    // Prix unitaire
                    content.beginText();
                    content.newLineAtOffset(colPositions[2], y);
                    content.showText(String.format("%.2f DZD", produit.getSellingPrice()));
                    content.endText();

                    // Total
                    content.beginText();
                    content.newLineAtOffset(colPositions[3], y);
                    content.showText(String.format("%.2f DZD", produit.getTotalPrice()));
                    content.endText();

                    totalProduits += produit.getTotalPrice();
                    y -= 15;

                    // Ligne de séparation entre les produits
                    if (index <= produits.size()) {
                        content.moveTo(marge, y + 5);
                        content.lineTo(550, y + 5);
                        content.setLineWidth(0.2f);
                        content.stroke();
                        content.setLineWidth(1.0f);
                        y -= 5;
                    }

                    // Nouvelle page si besoin
                    if (y < 100) {
                        content.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        content = new PDPageContentStream(document, page);
                        y = 750;
                    }
                }

                // Ligne de séparation finale
                y -= 10;
                content.moveTo(marge, y);
                content.lineTo(550, y);
                content.stroke();
                y -= 20;

            } else {
                // Aucun produit
                content.beginText();
                content.newLineAtOffset(marge, y);
                content.showText("Aucun produit enregistré pour cette vente.");
                content.endText();
                y -= 30;
            }

            // TOTAUX
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // Ligne de total
            content.moveTo(350, y);
            content.lineTo(550, y);
            content.stroke();
            y -= 20;

            // Total Global
            content.beginText();
            content.newLineAtOffset(350, y);
            content.showText("TOTAL VENTE :");
            content.endText();

            content.beginText();
            content.newLineAtOffset(470, y);
            content.showText(String.format("%.2f DZD", totalGlobal));
            content.endText();
            y -= 20;

            // Montant Payé
            content.beginText();
            content.newLineAtOffset(350, y);
            content.showText("MONTANT PAYÉ :");
            content.endText();

            content.beginText();
            content.newLineAtOffset(470, y);
            content.showText(String.format("%.2f DZD", montantPaye));
            content.endText();
            y -= 20;

            // Reste à Payer
            content.beginText();
            content.newLineAtOffset(350, y);
            content.showText("RESTE À PAYER :");
            content.endText();

            // Changer la couleur selon le reste
            if (resteAPayer > 0) {
                content.setNonStrokingColor(231, 76, 60); // Rouge
            } else {
                content.setNonStrokingColor(39, 174, 96); // Vert
            }

            content.beginText();
            content.newLineAtOffset(470, y);
            content.showText(String.format("%.2f DZD", resteAPayer));
            content.endText();

            content.setNonStrokingColor(0, 0, 0); // Réinitialiser la couleur
            y -= 30;

            // Ligne de séparation double
            content.setLineWidth(2f);
            content.moveTo(marge, y);
            content.lineTo(550, y);
            content.stroke();
            content.setLineWidth(1f);
            y -= 40;

            // MENTIONS
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.beginText();
            content.newLineAtOffset(marge, y);
            content.showText("MODE DE PAIEMENT :");
            content.endText();

            content.setFont(PDType1Font.HELVETICA, 10);
            content.beginText();
            content.newLineAtOffset(marge + 110, y);
            content.showText(montantPaye == totalGlobal ? "COMPTANT" : "CRÉDIT");
            content.endText();
            y -= 15;

            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.beginText();
            content.newLineAtOffset(marge, y);
            content.showText("REMISE :");
            content.endText();

            content.setFont(PDType1Font.HELVETICA, 10);
            content.beginText();
            content.newLineAtOffset(marge + 60, y);
            content.showText("0.00 DZD (0%)");
            content.endText();
            y -= 30;

            // PIED DE PAGE
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            content.beginText();
            content.newLineAtOffset(marge, y);
            content.showText("Signature du client :");
            content.endText();

            y -= 40;

            // Espace pour signature
            content.moveTo(marge, y);
            content.lineTo(250, y);
            content.stroke();

            content.beginText();
            content.newLineAtOffset(marge, y - 15);
            content.showText("_____________________________");
            content.endText();

            content.beginText();
            content.newLineAtOffset(marge, y - 25);
            content.showText("Nom et signature");
            content.endText();

            // Informations de génération
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 9);
            content.newLineAtOffset(300, y);
            content.showText("Document généré par Gestion Stock App");
            content.endText();

            content.beginText();
            content.newLineAtOffset(300, y - 15);
            content.showText("Le " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));
            content.endText();

            // Mentions légales
            content.beginText();
            content.newLineAtOffset(marge, y - 40);
            content.showText("TVA non applicable, article 293 B du CGI");
            content.endText();

            // 4. Fermer et sauvegarder
            content.close();
            document.save(file);
            document.close();

            System.out.println("✅ PDF de vente généré avec succès: " + file.getAbsolutePath());

            // 5. Ouvrir le PDF
            ouvrirPDF(file);

        } catch (Exception e) {
            System.err.println("❌ ERREUR lors de la génération du PDF de vente:");
            e.printStackTrace();

            // Afficher une alerte JavaFX
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Erreur PDF");
                alert.setHeaderText("Impossible de générer la facture");
                alert.setContentText("Détails: " + e.getMessage());
                alert.showAndWait();
            });
        }
    }

    private static void ouvrirPDF(File file) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Impossible d'ouvrir le PDF: " + e.getMessage());
        }
    }
}