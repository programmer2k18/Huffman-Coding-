package HuffmanPackage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Compression {
	
	double array[]=new double[128];
	String data="";
	int index;
	Vector<WordProb> Probability=new Vector<WordProb>();
	HashMap<String,Double> map = new HashMap<String,Double>();
	HashMap<String,String> mapOfCodes = new HashMap<String,String>();
	File WriteToFile;
    PrintWriter Output;
    File ReadFromFile; 
    Scanner Input;
    static String path;
	public void CalculatProbability() {
		try 
    	{
    		//For Read From the file 
            ReadFromFile=new File(path);
            Input =new Scanner(ReadFromFile);
    	} 
    	catch(FileNotFoundException e) {
    		System.out.println("Error to open files or something went wrong");
    	}
		while(Input.hasNextLine()){
    		data+=Input.nextLine();
    		for(int i=index;i<data.length();i++)
    			++array[data.charAt(i)];
    		index=data.length();
    	}
		WordProb obj;
		for(int i=0;i<array.length;i++) {
			if(array[i]!=0) {
				obj=new WordProb();
				obj.prob=(double)(array[i]/data.length());
				obj.word=(char)i+"";
				System.out.println(obj.word + " "+ array[i]);
				Probability.add(obj);
				map.put(obj.word, obj.prob);
			}
		}
	}
	public void SortProbablity(Vector<WordProb>myData) {
		WordProb value=new WordProb();
		int hole;
		for(int i=1;i<myData.size();i++) {
			value=myData.get(i);
			hole=i;
			while(hole>0 &&myData.get(hole-1).prob<value.prob) {
				myData.set(hole, myData.get(hole-1));
				hole--;
			}
			myData.set(hole,value);
		}
	}
	public void CombineSymbols() {
		WordProb LastOne=new WordProb();
		WordProb BeforeLast=new WordProb();
		SortProbablity(Probability);
		
		for(int i=0;i<Probability.size();i++) {
			System.out.println(Probability.get(i).word + " " + Probability.get(i).prob);
		}
		while(Probability.size()>2) {
			
			LastOne=Probability.get(Probability.size()-1);
			BeforeLast=Probability.get(Probability.size()-2);
			Probability.removeElementAt(Probability.size()-1);
			Probability.removeElementAt(Probability.size()-1);
			WordProb CombineObj=new WordProb();
			if(LastOne.word.length()>BeforeLast.word.length()) {
				CombineObj.word=LastOne.word+BeforeLast.word;
				CombineObj.prob=LastOne.prob+BeforeLast.prob;
			}
			else {
				CombineObj.word=BeforeLast.word+LastOne.word;
				CombineObj.prob=BeforeLast.prob+LastOne.prob;
			}
			Probability.add(CombineObj);
			SortProbablity(Probability);	
		}
	}
	public void CalcSymbolesCodes() {
		try 
    	{
            //For write to the file 
    		WriteToFile=new File("symbolesCodes.txt");
            Output =new PrintWriter(WriteToFile);
    	} 
    	catch(FileNotFoundException e) {
    		System.out.println("Error to open files or something went wrong");
    	}
		String code="0";
		double probOfLastSymbole;
		char symbole;
		for(int i=0;i<Probability.size();i++) {
			for(int j=Probability.get(i).word.length()-1;j>0;j--) {
				symbole=Probability.get(i).word.charAt(j);
				probOfLastSymbole=map.get(Character.toString(symbole));
				Probability.get(i).word=Probability.get(i).word.substring(0,j);
				Probability.get(i).prob-=probOfLastSymbole;
				
				if(Probability.get(i).prob>=probOfLastSymbole) {
					Output.println(symbole + " " + (code+"1"));
					mapOfCodes.put(Character.toString(symbole), code+"1");
					code+="0";
				}
				else {
					Output.println(symbole + " " + (code+"0"));
					mapOfCodes.put(Character.toString(symbole), code+"0");
					code+="1";
				}
			}
			Output.println(Probability.get(i).word + " " + code);
			mapOfCodes.put(Probability.get(i).word, code);
			code="1";	
		}
		Output.close();
	}
    public void compressData() {
    	try 
    	{ 
    		WriteToFile=new File("compressedData.txt");
            Output =new PrintWriter(WriteToFile);
    	} 
    	catch(FileNotFoundException e) {
    		System.out.println("Error to open files or something went wrong");
    	}
    	for(int i=0;i<data.length();i++) {
    		Output.print(mapOfCodes.get(Character.toString(data.charAt(i))));
    	}
    	Output.close();
    	mapOfCodes.clear();
    }
    public void Compress() {
    	this.CalculatProbability();
		this.CombineSymbols();
		this.CalcSymbolesCodes();
		this.compressData();
    }
    public void Decompress() {
    	try 
    	{   
            ReadFromFile=new File("symbolesCodes.txt");
            Input =new Scanner(ReadFromFile);
    	} 
    	catch(FileNotFoundException e) {
    		System.out.println("Error to open files or something went wrong");
    	}
    	while(Input.hasNextLine()) {
    		String line=Input.nextLine();
    		String []arr=line.split(" ",2);
    		mapOfCodes.put(arr[1], arr[0]);
    	}
    	Input.close();
    	//**********************************
    	try 
    	{   
            ReadFromFile=new File("compressedData.txt");
            Input =new Scanner(ReadFromFile);
            WriteToFile=new File("decompressedData.txt");
            Output =new PrintWriter(WriteToFile);
    	} 
    	catch(FileNotFoundException e) {
    		System.out.println("Error to open files or something went wrong");
    	}
    	while(Input.hasNextLine()) {
    		String line=Input.nextLine();
    		String code="";
    		for(int i=0;i<line.length();i++) {
    			code+=line.charAt(i);
    			if(mapOfCodes.get(code)!=null) {
    				Output.print(mapOfCodes.get(code));
    				code="";
    			}
    		}
    	}
    	Output.close();
    }
    public void GUI()
	 {
	 	JButton btnToComp ;
	 	JButton btnToDeComp ;
	 	JButton btnToBrowse ;
	 	JFrame frame ;
	 	JPanel panl ;
	 	JLabel lab ;

	 	frame = new JFrame(" Compress And Decompress ") ;
	 	
	 	panl = new JPanel() ;
	 	panl.setLayout(new BoxLayout(panl , BoxLayout.LINE_AXIS));
	 	
	 	btnToBrowse = new JButton(" Browse ") ;
	 	btnToComp = new JButton(" Compress ") ;
	 	btnToDeComp = new JButton (" DeCompress ") ;
	 	
	 	lab = new JLabel("******************************** Plesea Choose An Action ******************************** ") ;
	 	panl.add(btnToBrowse  ) ;
	 	panl.add(Box.createHorizontalGlue()) ;
	 	panl.add(btnToComp) ;
	 	panl.add(Box.createHorizontalGlue()) ;
	 	panl.add(btnToDeComp) ;
	 	
	 	
	 	/************************************** FRAM *********************************************/
	 	frame.add(lab ,BorderLayout.CENTER ) ;
	 	frame.add(panl , BorderLayout.SOUTH) ;
	 	frame.pack();
	 	frame.setSize(500 , 250);
	 	frame.setVisible(true);

	 	/************** BUTTON TO PROWSE THE DATA FILE ******************************/ 
	 	btnToBrowse.addMouseListener(new MouseAdapter(){
	 		public void mouseClicked(MouseEvent arg0) {
	 			// TODO Auto-generated method stub
	 			super.mouseClicked(arg0);
	 			
	 			final JFileChooser gh = new JFileChooser() ;
	 			int val = gh.showOpenDialog(null) ;
	 			if(val == JFileChooser.APPROVE_OPTION )
	 			{
	 				path = gh.getSelectedFile().getAbsolutePath(); 	
	 				JOptionPane.showMessageDialog(null, " Done :D ");
	 			}
	 			else
	 			{
	 				JOptionPane.showMessageDialog(null, " Process Canceled ");
	 			}
	 		}
	 	});
	 	
	 	/*********************************** BUTTON TO COMPRESS *********************************/
	 	btnToComp.addMouseListener(new MouseAdapter(){
	 		@Override
	 		public void mouseClicked(MouseEvent arg0) {
	 			// TODO Auto-generated method stub
	 			super.mouseClicked(arg0);
	 			if(path!="")
	 			{
	 				Compress();
	 			}
	 			else
	 				JOptionPane.showMessageDialog(null, " You Should Choose A File First If You Please ");			
	 		}
	 		
	 	}) ;
	 	
	 	/***************************** BUTTON TO DECOMPRESS **************************/
	 	btnToDeComp.addMouseListener(new MouseAdapter(){
	 		@Override
	 		public void mouseClicked(MouseEvent arg0) {
	 			// TODO Auto-generated method stub
	 			super.mouseClicked(arg0);
					Decompress();
	 		}
	 	});
	 	}
}
