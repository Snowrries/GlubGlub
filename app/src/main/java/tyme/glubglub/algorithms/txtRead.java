package tyme.glubglub.algorithms;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.*;
import java.util.Scanner;

public class txtRead{
	public static double[][] readFile(Context context, int size_x, int size_y) throws IOException {
		AssetManager am = context.getAssets();
		InputStream is = am.open("simWorld.txt");
		Scanner scan = new Scanner(is);
		double[][] simWorld = new double[size_x][size_y]; 
		try{
			String line = "";
			int rowNumber = 0;
			while(scan.hasNextLine()) {
			  line = scan.nextLine();
			  String[] elements = line.split(",");
			  int elementCount = 0;
			  for(String element : elements) {
			    double elementValue = Double.parseDouble(element);
			    simWorld[rowNumber][elementCount] = elementValue;
			    elementCount++;
			  } 
			    rowNumber++;
			}
		}
		catch(Exception e){
			System.err.println("Error");
		}
		scan.close();
		return simWorld;
	}
	
}