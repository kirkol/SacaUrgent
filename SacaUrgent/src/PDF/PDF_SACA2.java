package PDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


public class PDF_SACA2 {

	public static Font ffont = FontFactory.getFont("times", BaseFont.CP1250, BaseFont.EMBEDDED, 10); 
	public static Font ffont2 = FontFactory.getFont("times", BaseFont.CP1250, BaseFont.EMBEDDED, 8); 
	public static Font ffont3 = FontFactory.getFont("times", BaseFont.CP1250, BaseFont.EMBEDDED, 6); 

	public static void createDoc(){
		Connection myConn = WB.RCPdatabaseConnection.dbConnector("listy", "listy1234");
		Document ListMech = new Document(PageSize.A4.rotate());
		Document ListK = new Document(PageSize.A4.rotate());
		Document List = new Document(PageSize.A4.rotate());
		SimpleDateFormat doNazwy = new SimpleDateFormat("yyyy.MM.dd");
		SimpleDateFormat doNazwy2 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat godz = new SimpleDateFormat("HH;mm");
		Calendar date = Calendar.getInstance();
		String dzis = doNazwy2.format(date.getTime());
		PdfWriter writerM;
		try{
			String path = PDF.Parameters.getPathToSave()+"/"+doNazwy.format(date.getTime())+"/";
			String nameMech = "SACA mechaniczne.pdf";
			File fMech = new File(path+nameMech);
			if(fMech.exists() && !fMech.isDirectory()) {
				if(!fMech.delete()) {nameMech = godz.format(date.getTime())+" "+nameMech;}
			}
			writerM = PdfWriter.getInstance(ListMech, new FileOutputStream(path+nameMech));
			ListMech.open();
			ListK.open();
			List.open();
			writerM.setPageEvent(new PDF_MyFooter());
			PdfPTable tableM = new PdfPTable(11);
			float widths[] = new float[] {20, 20, 20, 20, 40, 40, 10, 10, 10, 20, 4};
			Paragraph M = new Paragraph("Typ: M - mechaniczne \n\n", ffont2);
			ListMech.add(M);
			
			String sqlwazne = "Select count(*) from saca where dataDodania = '"+dzis+"' and wazne = 1";
			Statement stwazne = myConn.createStatement();
			ResultSet rsWazne = stwazne.executeQuery(sqlwazne);
			int p = 0;
			while(rsWazne.next()) {
				p = rsWazne.getInt(1);
			}
			rsWazne.close();stwazne.close();
			
			//jeœli s¹ jakieœ pilne detale z SACA
			if(p!=0) {
				//pierwsza strona = tabelka pilnych z saca (saca mechaniczne)
				Paragraph wazne = new Paragraph("Zapotrzebowanie na produkcjê FAT: \n \n",ffont);
				
				ListMech.add(wazne);
				PdfPTable wazneSaca = new PdfPTable(9);
				addHeaderW(wazneSaca);
				float widthsWazne[] = new float[] {10, 10, 12, 14, 8, 8, 8, 10, 5};
				String sqlwazne2 = "Select projekt, NrZamowienia2, kodartykulu, nazwaartykulu, sum(pozostalodoprojektu) as pozostalodoprojektu, pozostalonazamowieniu, jednostka, cel, typ from " + 
						" (Select projekt, NrZamowienia2, kodartykulu, nazwaartykulu, pozostalodoprojektu, pozostalonazamowieniu, jednostka, cel, saca.typ, calendar.dataprodukcji from saca "
						+ "join calendar on saca.projekt = calendar.nrMaszyny "
						+ "where datadodania = '"+dzis+"' and wazne = 1 "
								+ "group by NrZamowienia2, kodartykulu "
								+ "order by calendar.dataProdukcji asc, projekt asc, saca.typ desc, saca.kodartykulu) T group by KodArtykulu order by T.dataprodukcji";
				Statement Parts = myConn.createStatement();
				ResultSet rsParts = Parts.executeQuery(sqlwazne2);
				while(rsParts.next()) {
					addCell(wazneSaca, rsParts.getString("projekt"), ffont);
					addCell(wazneSaca, rsParts.getString("NrZamowienia2"), ffont);
					addCell(wazneSaca, rsParts.getString("kodartykulu"), ffont);
					addCell(wazneSaca, rsParts.getString("nazwaartykulu"), ffont);
					addCell(wazneSaca, rsParts.getString("pozostalodoprojektu"), ffont);
					addCell(wazneSaca, rsParts.getString("pozostalonazamowieniu"), ffont);
					addCell(wazneSaca, rsParts.getString("jednostka"), ffont);
					addCell(wazneSaca, rsParts.getString("cel"), ffont);
					addCell(wazneSaca, rsParts.getString("typ"), ffont);
				}
				rsParts.close();Parts.close();
				wazneSaca.setWidthPercentage(100);
				wazneSaca.setWidths(widthsWazne);
				wazneSaca.setHeaderRows(1);
				wazneSaca.setHorizontalAlignment(Element.ALIGN_CENTER);
				wazneSaca.setHorizontalAlignment(Element.ALIGN_CENTER);	
				
				ListMech.add(wazneSaca);
				ListMech.newPage();
			}
			
			addHeader(tableM);
			Statement pobierzProjectSchedule = myConn.createStatement();
			String sql = "Select nrMaszyny, opis, klient, dataprodukcji, dataKoniecMontazu, komentarz from calendar where Zakonczone = 0 order by dataProdukcji";
			ResultSet ProjectSchedule = pobierzProjectSchedule.executeQuery(sql);
			
			while(ProjectSchedule.next()){
				
				String calyNumer = ProjectSchedule.getString("nrMaszyny");
				String ProductionDate = ProjectSchedule.getString("dataProdukcji");
				String MontageFinishDate = ProjectSchedule.getString("dataKoniecMontazu");
				String ProjectName = ProjectSchedule.getString("Opis");
				String klient = ProjectSchedule.getString("klient");
				
				System.out.println("SACA "+calyNumer);
				boolean headerM = false;
				boolean headerK = false;
				boolean header = false;
				Statement takeParts = myConn.createStatement();
				ResultSet parts = takeParts.executeQuery("SELECT * from saca where datadodania = '"+dzis+"' and projekt = '"+calyNumer+"' order by cel, kodartykulu"); 
				while(parts.next()){
					String ArticleNo = parts.getString("KodArtykulu");
					String ArticleName = parts.getString("NazwaArtykulu");
					String nrZam = parts.getString("NrZamowienia2");
					String ileDoProj = parts.getString("PozostaloDoProjektu");
					String nrZamowienia=parts.getString("nrZamowienia");
					String ileWZamowieniu = parts.getString("PozostaloNaZamowieniu");
					String jednostka=parts.getString("Jednostka");
					String dataDost = parts.getString("DataDostarczenia"); 
					String dataZam = parts.getString("DataZlozeniaZamowienia");
					String typArtykuluSACA = parts.getString("typ");
					String bon = parts.getString("cel");
					int b = parts.getInt("zmiana"); 
					System.out.println(calyNumer+"   "+ArticleNo+"   "+b);
					
					
					if(typArtykuluSACA.equals("M")){
						if(!headerM){
							addProjectHeader(calyNumer, ProjectName, ProductionDate, MontageFinishDate, klient, tableM);
							headerM=true;
						}
						if(dataDost==null) dataDost = "";
						if(!dataDost.equals(""))
						{
							addCell(tableM, nrZamowienia, b);
							if(dataZam.length()>10)
								addCell(tableM, dataZam.substring(0, 11), b);
							else
								addCell(tableM, dataZam, b);
							if(dataDost.length()>10)
								addCell(tableM, dataDost.substring(0, 11), b);
							else
								addCell(tableM, dataDost, b);
							
							addCell(tableM, nrZam, b);
							addCell(tableM, ArticleNo, b);
							addCell(tableM, ArticleName, b);
							addCell(tableM, ileDoProj, b);
							addCell(tableM, ileWZamowieniu, b);
							addCell(tableM, jednostka, b);
							addCell(tableM, bon, b);
							addCell(tableM,typArtykuluSACA, b);
						}
					}
				}
				takeParts.close();
				if(headerM)
					addRow(tableM);
				System.out.println();
			}
			tableM.setWidthPercentage(100);
			tableM.setWidths(widths);
			tableM.setHeaderRows(1);
			tableM.setHorizontalAlignment(Element.ALIGN_CENTER);
			tableM.setHorizontalAlignment(Element.ALIGN_CENTER);	
			if(tableM.size()==0 ){
				Paragraph a1 = new Paragraph("Document is empty", ffont2);
				ListMech.add(a1);
			}
			else
				ListMech.add(tableM);
			ListMech.close();
			
			ListK.close();
			
			List.close();
			
			myConn.close();
		}
		catch (FileNotFoundException | DocumentException | SQLException e) {
				e.printStackTrace();
		}
	}
	
	
	private static void addHeaderW(PdfPTable t){
		String [] nagl = new String[] {"Projekt", "Nr zamowienia", "Kod artykulu", "Nazwa artykulu", "Ile pilnych", "W zamowieniu", "Jednostka", "Dok¹d", "Typ"};
		for(int i = 0; i<nagl.length; i++) {
			PdfPCell cell1 = new PdfPCell(new Phrase(nagl[i], ffont));
			cell1.setMinimumHeight(30);
			cell1.setBackgroundColor(new BaseColor(255,121,0));
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			t.addCell(cell1);
		}
	}
	
	
	private static void addHeader(PdfPTable t){
		
		//adding header to our file
		PdfPCell cell1 = new PdfPCell(new Phrase("Nr zamowienia", ffont));
		cell1.setMinimumHeight(30);
		cell1.setBackgroundColor(BaseColor.ORANGE);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell1);
		
		PdfPCell cell12 = new PdfPCell(new Phrase("Data zlozenia zamowienia", ffont));
		cell12.setMinimumHeight(30);
		cell12.setBackgroundColor(BaseColor.ORANGE);
		cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell12.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell12);
		
		PdfPCell cell13 = new PdfPCell(new Phrase("Data dostarczenia zamowienia", ffont));
		cell13.setMinimumHeight(30);
		cell13.setBackgroundColor(BaseColor.ORANGE);
		cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell13.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell13);
		
		PdfPCell cell10 = new PdfPCell(new Phrase("Nr zamowienia2", ffont));
		cell10.setMinimumHeight(30);
		cell10.setBackgroundColor(BaseColor.ORANGE);
		cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell10);
		
		PdfPCell cell2 = new PdfPCell(new Phrase("Kod artykulu", ffont));
		cell2.setMinimumHeight(30);
		cell2.setBackgroundColor(BaseColor.ORANGE);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell2);
		
		PdfPCell cell3 = new PdfPCell(new Phrase("Nazwa artykulu", ffont));
		cell3.setMinimumHeight(30);
		cell3.setBackgroundColor(BaseColor.ORANGE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell3);
		
		PdfPCell cell8 = new PdfPCell(new Phrase("Do projektu", ffont));
		cell8.setMinimumHeight(30);
		cell8.setBackgroundColor(BaseColor.ORANGE);
		cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell8);
		
		PdfPCell cell6 = new PdfPCell(new Phrase("W zamówieniu", ffont));
		cell6.setMinimumHeight(30);
		cell6.setBackgroundColor(BaseColor.ORANGE);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell6);

		PdfPCell cell7 = new PdfPCell(new Phrase("Jednostka", ffont));
		cell7.setMinimumHeight(30);
		cell7.setBackgroundColor(BaseColor.ORANGE);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell7);
		
		PdfPCell cell9 = new PdfPCell(new Phrase("Dok¹d", ffont));
		cell9.setMinimumHeight(30);
		cell9.setBackgroundColor(BaseColor.ORANGE);
		cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell9);
		
		PdfPCell cell91 = new PdfPCell(new Phrase("Typ", ffont));
		cell91.setMinimumHeight(30);
		cell91.setBackgroundColor(BaseColor.ORANGE);
		cell91.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell91.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell91);
	}
	
	private static void addProjectHeader(String project, String name, String ProductionDate, String MontageFinishDate, String client, PdfPTable t){
		PdfPCell cell1 = new PdfPCell(new Phrase("Numer projektu", ffont));
		cell1.setFixedHeight(15f);
		cell1.setColspan(2);
		cell1.setBackgroundColor(BaseColor.ORANGE);
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell1);
		
		PdfPCell cell2 = new PdfPCell(new Phrase("Nazwa projektu", ffont));
		cell2.setFixedHeight(15f);
		cell2.setColspan(3);
		cell2.setBackgroundColor(BaseColor.ORANGE);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell2);
		
		PdfPCell cell3 = new PdfPCell(new Phrase("Data produkcji czêœci", ffont));
		cell3.setFixedHeight(15f);
		cell3.setColspan(2);
		cell3.setBackgroundColor(BaseColor.ORANGE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell3);
		
		PdfPCell cell35 = new PdfPCell(new Phrase("Data koñca monta¿u", ffont));
		cell35.setFixedHeight(15f);
		cell35.setColspan(2);
		cell35.setBackgroundColor(BaseColor.ORANGE);
		cell35.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell35.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell35);
		
		PdfPCell cell4 = new PdfPCell(new Phrase("Klient", ffont));
		cell4.setFixedHeight(15f);
		cell4.setColspan(2);
		cell4.setBackgroundColor(BaseColor.ORANGE);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell4);
		
		PdfPCell cell04 = new PdfPCell(new Phrase(project, ffont));
		cell04.setFixedHeight(15f);
		cell04.setColspan(2);
		cell04.setBackgroundColor(BaseColor.YELLOW);
		cell04.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell04.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell04);
		
		PdfPCell cell5 = new PdfPCell(new Phrase(name, ffont));
		cell5.setFixedHeight(15f);
		cell5.setColspan(3);
		cell5.setBackgroundColor(BaseColor.ORANGE);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell5);
		
		PdfPCell cell6 = new PdfPCell(new Phrase(ProductionDate, ffont));
		cell6.setFixedHeight(15f);
		cell6.setColspan(2);
		cell6.setBackgroundColor(BaseColor.ORANGE);
		cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell6);
		
		PdfPCell cell65 = new PdfPCell(new Phrase(MontageFinishDate, ffont));
		cell65.setFixedHeight(15f);
		cell65.setColspan(2);
		cell65.setBackgroundColor(BaseColor.ORANGE);
		cell65.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell65.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell65);
		
		PdfPCell cell7 = new PdfPCell(new Phrase(client, ffont));
		cell7.setFixedHeight(15f);
		cell7.setColspan(4);
		cell7.setBackgroundColor(BaseColor.ORANGE);
		cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
		t.addCell(cell7);
	}

	private static void addRow(PdfPTable t){
		PdfPCell cell = new PdfPCell(new Phrase(" ",ffont2));
		cell.setNoWrap(true);
		cell.setColspan(11);
		t.addCell(cell);
		
	}
	
	public static void addCell(PdfPTable t, String z, int a){
		PdfPCell cell = new PdfPCell(new Phrase(z, ffont3));
		if(a!=0) {
			int green = Math.round((float)a*255/7); 
			cell.setBackgroundColor(new BaseColor(255, (int) green, 0));
		}
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setMinimumHeight(15f);
		t.addCell(cell);
	}
	public static void addCell(PdfPTable t, String z, Font f){
		PdfPCell cell = new PdfPCell(new Phrase(z, f));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setMinimumHeight(15f);
		t.addCell(cell);
	}
	
}
