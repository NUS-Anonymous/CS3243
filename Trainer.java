import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Trainer {
	static int ROUNDS = 5;
	public static void main(String[] args) {
		
		//the linear weight for each feature, set others to 0 to test the correctness
		//numbers or rows cleared should have negative weights, cuz we want to award this
		double[] weights = {0.510066, -0.76066, 0.35663, 0.184483,  0.1, 100000};
		playAtWeight(weights);		
	}
	
	//call this function to play and store data to the database for a single Weights Vector
	private static void playAtWeight(double[] weights){
		double[] scoreArr = new double[ROUNDS];
		
		for (int i= 0; i< ROUNDS; i++){
			scoreArr[i] = play(i,weights);
		}
		
		//Find Mean for number of ROUNDS
		double sum = 0.0;
		for (double s : scoreArr) sum += s;
		double average = sum / ROUNDS;
		//Find Std Dev
		double variance = 0.0;
		for (double s : scoreArr) variance += Math.pow((s-average), 2);
		double sd = Math.sqrt((variance/ROUNDS));
		
		//Write the case to Database
		writeToDataBase(weights, "database.txt", average, sd);
	}
	
	private static double play(int round, double[] weights){
		State s= new State();
		TFrame TF= new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		p.setWeight(weights);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
		TF.dispose();
		return s.getRowsCleared();
	}
	
	private static synchronized void writeToDataBase(double[] weightsArr, String fileName, double ave, double sd){
		//Building String with " weight0 weight1 ... ave_score sd";
		//Formating the number
		
		StringBuilder sb = new StringBuilder();
		for (double w: weightsArr) {
			sb.append(w).append(" ");
		}
		
		DecimalFormat df2 = new DecimalFormat(".###");
		sb.append(df2.format(ave)).append(" ");
		sb.append(df2.format(sd));
		
		try(FileWriter fw = new FileWriter(fileName, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter write = new PrintWriter(bw))
			{
			    write.println(sb.toString());
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
	}
	
}