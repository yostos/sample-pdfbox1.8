/**
 * @author yostos
 * @version 0.0.1
 */

package org.yostos.pdfboxtest;

import java.io.IOException;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptorDictionary;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * PDFBoxのテスト.
 * PDFBoxの古いバージョン (v1.8)で強引に日本語フォントを設定した書き出すための
 * サンプルプログラム。
 */
public class FontTest {
    public static void main(String [] args) {

        try {
            //PDFドキュメントを作成
            PDDocument document = new PDDocument();

            PDPage page = new PDPage();
            document.addPage(page);

            //書き込む用のストリームを準備
            PDPageContentStream stream = new PDPageContentStream(document, page);

            //日本語を設定する場合にはいくつかのフォント情報を指定する
            COSDictionary systeminfo = new COSDictionary();
            systeminfo.setString(COSName.REGISTRY, "Adobe");
            systeminfo.setString(COSName.ORDERING, "Japan1");
            systeminfo.setInt(COSName.SUPPLEMENT, 6);

            //フォントディスクリプタ設定
            PDFontDescriptorDictionary fd = new PDFontDescriptorDictionary();
            
            //Adobe Reader側で用意されている小塚フォントを指定
            fd.setFontName("KozGoPr6N-Medium");
            fd.setFlags(4);
            fd.setFontBoundingBox(new PDRectangle(new BoundingBox(-500, -300, 1200, 1400)));
            fd.setItalicAngle(0);
            fd.setAscent(1400);
            fd.setDescent(-300);
            fd.setCapHeight(700);
            fd.setStemV(100);

            //CIDフォント設定 (小塚ゴシック)
            COSDictionary cid = new COSDictionary();
            cid.setItem(COSName.TYPE, COSName.FONT);
            cid.setItem(COSName.SUBTYPE, COSName.CID_FONT_TYPE0);
            cid.setItem(COSName.BASE_FONT, COSName.getPDFName("KozGoPr6N-Medium"));
            cid.setItem(COSName.CIDSYSTEMINFO, systeminfo);
            cid.setItem(COSName.FONT_DESC, fd);

            //フォント設定 
            COSDictionary font = new COSDictionary();
            font.setItem(COSName.TYPE, COSName.FONT);
            font.setItem(COSName.SUBTYPE, COSName.TYPE0);
            font.setItem(COSName.BASE_FONT, COSName.getPDFName("KozGoPr6N-Medium"));
            font.setItem(COSName.ENCODING, COSName.ENCODING_90MS_RKSJ_H);

            COSArray array = new COSArray();
            array.add(cid);
            font.setItem(COSName.DESCENDANT_FONTS, array);

            //フォンと作成
            PDFont pdFont = new PDType0Font(font);

            //テキスト出力開始
            stream.beginText();


            //フォントとフォントサイズを設定
            stream.setFont(pdFont, 11);

            //文字の配置設定
            stream.moveTextPositionByAmount(10, 400);

            //文字列をMS932のバイト列にしつつ、ASCIIコード(1byte文字列)として出力
            stream.drawString(new String("MS932-淫餌葛僅煎詮遡遜捗溺賭謎箸遮餅嘲".getBytes("MS932"), "ISO8859-1"));


            //テキスト出力終了
            stream.endText();

            //書き込む用のストリームを閉じる
            stream.close();

            document.save("hogehoge.pdf");
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
    }
}
