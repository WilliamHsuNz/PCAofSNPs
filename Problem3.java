/*
 * Author: William Hsu
 * UPI: whsu014
 * 6/4/2015
 * 
 * This programme reads the data matrix from 
 * https://www.cs.auckland.ac.nz/courses/compsci369s1c/resources/for-assig1/snps.txt
 * which is a 100 (trials) x 2734 (measures) matrix, the programme centres the matrix,
 * finds the principal components and prints the first 5 entries of the first 5 principal 
 * component vectors as well as the 100 singular values from the analysis.  The programme 
 * then projects the data vectors along the first 2 principal components, generating a new 
 * 100 x 2 matrix L.  Matrix L is then written onto a file called locations.txt.
 *  
 * The programme finds and print the row number of individuals belonging to the largest 
 * subpopulation identified using the first 2 principal component.
 * 
 * Instructions for using this programme:
 * Make sure the location (line 95) of where to write the location.txt file is adjusted
 * to suit your system.  
 * 
 */


import java.net.*;
import Jama.*;

import java.text.NumberFormat;
import java.util.Date;
import java.io.*;

public class Problem3{
	public Problem3(double[][] array2DD){
		Matrix problem3 = new Matrix(array2DD);
	
		double [] mean = new double [problem3.getColumnDimension()];
		double total = 0;
		//find mean for each column
		for(int c = 0; c < problem3.getColumnDimension();c++){
			for(int r = 0; r < problem3.getRowDimension(); r++){
				double current = problem3.get(r, c);
				total = total + current;
			}
		mean[c] = total/problem3.getRowDimension();	
		total = 0;	
		}
		//centre the matrix
		for(int c = 0; c < problem3.getColumnDimension();c++){
			for(int r = 0; r < problem3.getRowDimension(); r++){
				double currentMean = mean[c];
				double current = problem3.get(r, c); 
				double currentCentred = current - currentMean;
				problem3.set(r, c, currentCentred);
			}
		}
		
		Matrix problem3t = problem3.transpose();
		
		//find e-vectors
		SingularValueDecomposition svd = new SingularValueDecomposition(problem3t);
		Matrix U = svd.getU();
		Matrix subU = U.getMatrix(0,4,0,4);
		subU.print(6, 4);
		//find singular values
		double[]singularValues = svd.getSingularValues();
		for(int i = 0; i < singularValues.length; i++){
			System.out.println("Singular Value "+ (i+1) + ":  "+ singularValues[i]);		
		}
		//get the first 2 eigenvectors/principal components
		Matrix eVector1 = U.getMatrix(0,U.getRowDimension()-1, 0,0);
		Matrix eVector2 = U.getMatrix(0,U.getRowDimension()-1, 1,1);
		Matrix projection = new Matrix(100, 2);	
		//project data vector along the first 2 principal components
		for (int i = 0 ; i < problem3.getRowDimension(); i ++){
			Matrix individual = problem3.getMatrix(i,  i, 0, 2733);
			Matrix current = individual.times(eVector1);
			projection.set(i, 0, current.get(0, 0));
			Matrix current1 = individual.times(eVector2);
			projection.set(i,  1,  current1.get(0,0));
		}
		System.out.println("Projection");
		projection.print(6, 4);
		//find individuals in the largest subpopulation
		int count = 0;
		for(int i = 0; i<projection.getRowDimension(); i++){
			for(int j = 0; j < projection.getColumnDimension(); j++){
				if(j == 0 && projection.get(i, j)>2){
					System.out.println("Individual (row number) "+ (i+1));
					count++;
				}
			}
		}
		System.out.println(count);
		
		try{
			PrintWriter writer = new PrintWriter("/Users/williamhsu/Documents/2015 Computer Science/Compsci 369/Assignment1/Problem3/locations.txt");
			for(int i = 0; i<projection.getRowDimension(); i++){
				writer.print(projection.get(i, 0));
				writer.print(" ");
				writer.print(projection.get(i, 1));		
				writer.println();
			}
		writer.close();	
		}catch(IOException e){
			System.out.print(e);
		}
	}

	public static void main(String[]args){
		String [][] array2D = new String[100][2734];
		double [][] array2DD = new double [100][2734];
		try{
			URL p3data = new URL("https://www.cs.auckland.ac.nz/courses/compsci369s1c/resources/for-assig1/snps.txt");
			BufferedReader in = new BufferedReader( new InputStreamReader(p3data.openStream()));
			//BufferedReader in = new BufferedReader( new FileReader("Problem3.txt"));
			String inputLine;
			int row = 0;
			int counter = 0;
			while((inputLine = in.readLine())!=null){
				counter++;
				array2D[row]= inputLine.split(" ");
				row++;
			}
			in.close();
		}catch(IOException e){
			System.out.println(e);	
		}
		for(int i =0; i < array2D.length; i++){
			for(int j = 0; j< array2D[0].length; j++){
				array2DD[i][j] = Double.parseDouble(array2D[i][j]);
			}	
		}
		Problem3 p3 = new Problem3(array2DD); 
	}
	
}


