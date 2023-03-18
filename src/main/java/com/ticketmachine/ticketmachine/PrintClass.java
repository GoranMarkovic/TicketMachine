package com.ticketmachine.ticketmachine;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PrintClass {
	
	public String usluga="Vrsta usluge";
	public String salter="Broj saltera";
	public String brojIspred= "13";
	public String redniBroj="47";
	
	public PrintClass(String usluga, String salter, String brojIspred, String redniBroj)
	{
		this.usluga=usluga;
		this.salter=salter;
		this.brojIspred=brojIspred;
		this.redniBroj=redniBroj;
	}
	
	public class QueueNumberPaper implements Printable{
		


		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			int result=NO_SUCH_PAGE;
			
			 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");  
			 LocalDateTime now = LocalDateTime.now();  
			
			if(pageIndex == 0)
			{
				Graphics2D g2d= (Graphics2D) graphics;
				//double width = pageFormat.getImageableWidth();
				double width=220;
				System.out.println(width);
				g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
				
				try
				{
					int y=35;
					int yShift=25;
					int widthOfString=0;
					int starting=0;
					//Image gradlogo=Toolkit.getDefaultToolkit().getImage("file:///BLsrednji.jpg");
					//g2d.drawImage(gradlogo, 22, 40, 40, 40, null);
					
					g2d.drawLine(10, y, 200, y);
					int height2=g2d.getFontMetrics(new Font("Times New Roman", Font.BOLD,56)).getHeight();
					System.out.println(height2);
					y+=43;
					g2d.setFont(new Font("Times New Roman", Font.BOLD,56));
					widthOfString=g2d.getFontMetrics(new Font("Times New Roman", Font.BOLD,56)).stringWidth(redniBroj.toString());
					starting=(int)(width-widthOfString)/2;
					g2d.drawString(redniBroj, starting, y);
					y+=5;
					g2d.drawLine(10, y, 200, y);
					y+=yShift;

					widthOfString=g2d.getFontMetrics(new Font("Times New Roman", Font.BOLD,12)).stringWidth("Vrijeme izdavanja: 22:30");
					starting=(int)(width-widthOfString)/2;
					g2d.setFont(new Font("Times New Roman", Font.BOLD,12));
					g2d.drawString("Vrijeme izdavanja: "+dtf.format(now), starting, y);
					
					/*g2d.setFont(new Font("Times New Roman", Font.BOLD,12));
					g2d.drawString("Vrijeme izdavanja: ", 22, y);
					g2d.setFont(new Font("Times New Roman", Font.PLAIN,12));
					g2d.drawString(dtf.format(now), 120, y);*/
					y+=yShift;
					
					g2d.setFont(new Font("Times New Roman", Font.BOLD,12));
					widthOfString=g2d.getFontMetrics(new Font("Times New Roman", Font.BOLD,12)).stringWidth("Usluga: "+usluga);
					starting=(int)(width-widthOfString)/2;
					g2d.drawString("Usluga: "+usluga, starting, y);
					y+=yShift;
					
					g2d.setFont(new Font("Times New Roman", Font.BOLD,12));
					widthOfString=g2d.getFontMetrics(new Font("Times New Roman", Font.BOLD,12)).stringWidth("Salter: "+salter);
					starting=(int)(width-widthOfString)/2;
					g2d.drawString("�alter: "+salter, starting, y);
					y+=yShift;
					
					g2d.setFont(new Font("Times New Roman", Font.BOLD,12));
					widthOfString=g2d.getFontMetrics(new Font("Times New Roman", Font.BOLD,12)).stringWidth("Broj klijenata ispred Vas: "+brojIspred.toString());
					starting=(int)(width-widthOfString)/2;
					g2d.drawString("Broj klijenata ispred Vas: "+brojIspred, starting, y);
					y+=yShift;

					

					
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
				}
				result=PAGE_EXISTS;
			}
			
			return result;
		}

	}
	
    public PageFormat getPageFormat(PrinterJob pj)
{
    
    PageFormat pf = pj.defaultPage();
    Paper paper = pf.getPaper();    
      
    double width = cm_to_pp(15); 
    double height = cm_to_pp(15); 
    paper.setSize(width, height);
    paper.setImageableArea(0,10,width,height - cm_to_pp(1));  
            
    pf.setOrientation(PageFormat.PORTRAIT);  
    pf.setPaper(paper);    

    return pf;
}
   
    
    
    protected static double cm_to_pp(double cm)
    {            
	        return toPPI(cm * 0.393600787);            
    }
 
protected static double toPPI(double inch)
    {            
	        return inch * 72d;            
    }
	
	
	public void printNumber()
	{
		PrinterJob pj=PrinterJob.getPrinterJob();
		PrintService[] printServices=PrintServiceLookup.lookupPrintServices(null, null);
		for(PrintService service : printServices)
		{
			//ispod unijeti ime stampaca
			//EPSON TM-T88VI Receipt
			//Microsoft Print to PDF
			if(service.getName().equals("EPSON TM-T88VI Receipt"))
			{
				try
				{
					pj.setPrintService(service);
					pj.setPrintable(new QueueNumberPaper(),getPageFormat(pj));
					pj.print();
					//Thread.sleep(1000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

}
