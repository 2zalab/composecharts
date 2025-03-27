package com.touzalab.composecharts.data

import android.content.Context
import com.touzalab.composecharts.theme.ColorPalettes
import java.io.InputStream

/**
 * Interface pour les adaptateurs de données
 */
interface DataAdapter<T> {
    /**
     * Charge les données à partir d'une source
     *
     * @param context Contexte Android
     * @param source Source de données (fichier, URL, etc.)
     * @return Données chargées
     */
    fun loadData(context: Context, source: String): T

    /**
     * Charge les données à partir d'un flux d'entrée
     *
     * @param inputStream Flux d'entrée
     * @return Données chargées
     */
    fun loadData(inputStream: InputStream): T
}

/**
 * Adaptateur pour les données CSV
 */
class CsvDataAdapter : DataAdapter<List<DataSeries>> {
    override fun loadData(context: Context, source: String): List<DataSeries> {
        val inputStream = when {
            source.startsWith("http") -> {
                // Charger depuis une URL
                java.net.URL(source).openStream()
            }
            else -> {
                // Charger depuis un fichier
                context.assets.open(source)
            }
        }

        return loadData(inputStream)
    }

    override fun loadData(inputStream: InputStream): List<DataSeries> {
        // Lire le fichier CSV
        val reader = inputStream.bufferedReader()
        val lines = reader.readLines()

        if (lines.isEmpty()) {
            return emptyList()
        }

        // Extraire les en-têtes
        val headers = lines[0].split(",").map { it.trim() }

        // Extraire les données
        val data = mutableListOf<MutableMap<String, Float>>()
        for (i in 1 until lines.size) {
            val values = lines[i].split(",").map { it.trim() }
            if (values.size < headers.size) continue

            val row = mutableMapOf<String, Float>()
            for (j in 0 until headers.size) {
                try {
                    row[headers[j]] = values[j].toFloat()
                } catch (e: NumberFormatException) {
                    // Ignorer les valeurs non numériques
                }
            }

            data.add(row)
        }

        // Créer les séries de données
        val result = mutableListOf<DataSeries>()

        // Supposer que la première colonne est l'axe X
        val xColumn = headers.firstOrNull() ?: return emptyList()

        // Créer une série pour chaque colonne restante
        for (i in 1 until headers.size) {
            val yColumn = headers[i]
            val points = data.mapNotNull { row ->
                val x = row[xColumn] ?: return@mapNotNull null
                val y = row[yColumn] ?: return@mapNotNull null

                DataPoint(
                    x = x,
                    y = y,
                    label = x.toString()
                )
            }

            result.add(
                DataSeries(
                    name = yColumn,
                    color = ColorPalettes.Default[i % ColorPalettes.Default.size],
                    points = points
                )
            )
        }

        return result
    }
}