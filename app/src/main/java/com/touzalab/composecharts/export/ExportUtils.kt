package com.touzalab.composecharts.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.view.View
import androidx.compose.ui.platform.ComposeView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utilitaires pour exporter des graphiques
 */
object ExportUtils {
    /**
     * Exporte un graphique au format PNG
     *
     * @param context Contexte Android
     * @param view Vue du graphique
     * @param fileName Nom du fichier
     * @param width Largeur de l'image
     * @param height Hauteur de l'image
     * @return Chemin du fichier exporté ou null en cas d'erreur
     */
    fun exportToPng(
        context: Context,
        view: View,
        fileName: String,
        width: Int = 800,
        height: Int = 600
    ): String? {
        try {
            // Créer le bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Dessiner la vue sur le canvas
            view.draw(canvas)

            // Créer le fichier de sortie
            val file = File(context.getExternalFilesDir(null), "$fileName.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Exporte un graphique au format PDF
     *
     * @param context Contexte Android
     * @param view Vue du graphique
     * @param fileName Nom du fichier
     * @param width Largeur de la page
     * @param height Hauteur de la page
     * @return Chemin du fichier exporté ou null en cas d'erreur
     */
    fun exportToPdf(
        context: Context,
        view: View,
        fileName: String,
        width: Int = 800,
        height: Int = 600
    ): String? {
        try {
            // Créer le document PDF
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
            val page = document.startPage(pageInfo)

            // Dessiner la vue sur le canvas
            view.draw(page.canvas)

            // Finaliser la page et le document
            document.finishPage(page)

            // Créer le fichier de sortie
            val file = File(context.getExternalFilesDir(null), "$fileName.pdf")
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }

            document.close()

            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Exporte les données d'un graphique au format CSV
     *
     * @param context Contexte Android
     * @param data Données à exporter
     * @param fileName Nom du fichier
     * @return Chemin du fichier exporté ou null en cas d'erreur
     */
    fun exportToCsv(
        context: Context,
        data: List<List<String>>,
        fileName: String
    ): String? {
        try {
            // Créer le fichier de sortie
            val file = File(context.getExternalFilesDir(null), "$fileName.csv")

            FileOutputStream(file).use { out ->
                for (row in data) {
                    val line = row.joinToString(",") {
                        // Échapper les guillemets et ajouter des guillemets si nécessaire
                        if (it.contains(",") || it.contains("\"") || it.contains("\n")) {
                            "\"${it.replace("\"", "\"\"")}\""
                        } else {
                            it
                        }
                    }
                    out.write("$line\n".toByteArray())
                }
            }

            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}