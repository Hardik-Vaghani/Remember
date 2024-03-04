package com.hardik.remember.util

import com.hardik.remember.models.SpellingResponseItem

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter

class CsvWriterUtil {

    fun writeDataToCsv(data: List<SpellingResponseItem>, csvFilePath: String) {
        val csvFile = File(csvFilePath)
        csvFile.parentFile?.mkdirs()

        CSVPrinter(
            FileWriter(csvFile),
            CSVFormat.DEFAULT.withHeader("id", "word", "meaning", "pronounce", "type", "isLike")
        ).use { csvPrinter ->
            data.forEach { item ->
                csvPrinter.printRecord(
                    item.id,
                    item.word,
                    item.meaning,
                    item.pronounce,
                    item.type,
                    item.is_like
                )
            }
        }
    }
}

