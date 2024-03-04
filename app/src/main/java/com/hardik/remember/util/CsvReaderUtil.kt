package com.hardik.remember.util

import com.hardik.remember.models.SpellingResponseItem

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.FileReader

class CsvReaderUtil {

    fun readDataFromCsv(csvFilePath: String): List<SpellingResponseItem> {
        val csvFile = File(csvFilePath)

        CSVParser(
            FileReader(csvFile),
            CSVFormat.DEFAULT.withHeader("id", "word", "meaning", "pronounce", "type", "isLike")
        ).use { csvParser ->
            return csvParser.records.map { record ->
                SpellingResponseItem(
                    id = record["id"]?.toLong() ?: 0,
                    word = record["word"] ?: "",
                    meaning = record["meaning"] ?: "",
                    pronounce = record["pronounce"] ?: "",
                    type = record["type"] ?: "",
                    is_like = record["isLike"]?.toBoolean() ?: false
                )
            }
        }
    }
}
