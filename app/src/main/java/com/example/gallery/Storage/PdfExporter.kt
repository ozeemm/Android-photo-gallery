package com.example.gallery.Storage

import android.os.Environment
import com.example.gallery.Model.Photo
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.HorizontalAlignment
import java.io.File

object PdfExporter {
    private val fileDirPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS + "/Android-photo-gallery").toString()

    private val fileName = "Android-photo-gallery.pdf"

    private val COURIER = "src/res/fonts/couriercyrps.ttf"

    public fun export(photos: ArrayList<Photo>){
        val pdfDir = File(fileDirPath)
        val pdfFile = File(fileDirPath, fileName)

        if(!pdfDir.exists())
            pdfDir.mkdir()

        if(!pdfFile.exists())
            pdfFile.createNewFile()

        val font = PdfFontFactory.createFont(StandardFonts.COURIER)
        val pdfWriter = PdfWriter(pdfFile)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize(600F, 550F))

        document.setFont(font)

        photos.forEachIndexed{ i, photo ->
            document.add(Paragraph("Name: ${photo.name}\n"))
            document.add(Paragraph("Album: ${photo.album}\n"))
            document.add(Paragraph("Date: ${photo.date}"))
            // document.add(Paragraph("URI: ${photo.uri}"))

            val imageData = ImageDataFactory.create(photo.uri)
            val image = Image(imageData)
            image.setAutoScale(true)
            image.setHorizontalAlignment(HorizontalAlignment.CENTER)
            document.add(image)

            if(i != photos.size-1)
                document.add(AreaBreak())
        }
        document.close()
        pdfDocument.close()
        pdfWriter.close()
    }
}