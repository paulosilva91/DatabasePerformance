package com.psilva.android.databaseperformance.databases.export.csv

import android.content.Context
import android.os.Environment
import com.psilva.android.databaseperformance.databases.export.csv.model.DatabaseResultModel
import java.io.FileWriter
import java.io.IOException
import java.util.*

class CSVFile {
    companion object {
        private const val CSV_HEADER = "name,databaseOperation,databaseOperationType,duration"

        fun export(databaseResultModels: Collection<DatabaseResultModel>) {
            var fileWriter: FileWriter? = null

            try {
                fileWriter = FileWriter("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/database_results_${Calendar.getInstance().time.time}.csv")

                fileWriter.append(CSV_HEADER)
                fileWriter.append('\n')

                for (item in databaseResultModels) {
                    fileWriter.append(item.databaseName.toString())
                    fileWriter.append(',')
                    fileWriter.append(item.databaseOperationEnum.toString())
                    fileWriter.append(',')
                    fileWriter.append(item.databaseOperationTypeEnum.toString())
                    fileWriter.append(',')
                    fileWriter.append(item.timeValue.toString())
                    fileWriter.append('\n')
                }

                println("Write CSV successfully!")
            } catch (e: Exception) {
                println("Writing CSV error!")
                e.printStackTrace()
            } finally {
                try {
                    fileWriter!!.flush()
                    fileWriter.close()
                } catch (e: IOException) {
                    println("Flushing/closing error!")
                    e.printStackTrace()
                }
            }
        }
    }
}