package com.caixaapp.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.caixaapp.model.Transaction
import com.caixaapp.model.TransactionType
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExportService {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toEmptyJson(transactions: List<Transaction>): String {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .create()
        return gson.toJson(transactions)
    }

    fun toXml(transactions: List<Transaction>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<transactions>\n")
        for (t in transactions) {
            sb.append("  <transaction>\n")
            sb.append("    <id>${t.id}</id>\n")
            sb.append("    <date>${t.data.format(dateFormatter)}</date>\n")
            sb.append("    <description>${t.descricao}</description>\n")
            sb.append("    <value>${t.valor}</value>\n")
            sb.append("    <type>${t.tipo}</type>\n")
            sb.append("    <personId>${t.pessoaId}</personId>\n")
            sb.append("  </transaction>\n")
        }
        sb.append("</transactions>")
        return sb.toString()
    }

    fun createPdf(context: Context, transactions: List<Transaction>): PdfDocument {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint()
        
        var y = 40f
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Relatório de Lançamentos - CaixaAPP", 40f, y, paint)
        
        y += 30f
        paint.textSize = 12f
        paint.isFakeBoldText = false
        
        // Headers
        canvas.drawText("Data", 40f, y, paint)
        canvas.drawText("Descrição", 120f, y, paint)
        canvas.drawText("Tipo", 400f, y, paint)
        canvas.drawText("Valor", 480f, y, paint)
        
        y += 5f
        canvas.drawLine(40f, y, 550f, y, paint)
        y += 20f

        for (t in transactions) {
            if (y > 800) {
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
            }
            
            canvas.drawText(t.data.format(dateFormatter), 40f, y, paint)
            
            val desc = if (t.descricao.length > 40) t.descricao.substring(0, 37) + "..." else t.descricao
            canvas.drawText(desc, 120f, y, paint)
            
            canvas.drawText(if (t.tipo == TransactionType.CREDITO) "C" else "D", 400f, y, paint)
            canvas.drawText(String.format("%.2f", t.valor), 480f, y, paint)
            
            y += 20f
        }

        document.finishPage(page)
        return document
    }
}
