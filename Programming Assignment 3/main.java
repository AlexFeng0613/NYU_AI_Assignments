import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;


public class main {
	
	public static double Prob[][];
	public static String Play[][];
	public static double LTarget;
	public static double UTarget;
	public static int NCards;
	
	public static void init(int Ncards, double Ltarget, double Utarget){
		NCards = Ncards;
		LTarget = Ltarget;
		UTarget = Utarget;
		Prob = new double[(int) LTarget][(int) LTarget];
		Play = new String[(int) LTarget][(int) LTarget];
	}
	
	public static double draws(int XT, int YT){
		double ProbWinning = 0.0;
		for(int Card = 1;Card<=NCards;Card++){
			double ProbYWins = 0;
			if(XT+Card > UTarget) ProbYWins = 1;
			else if(XT+Card >= LTarget) ProbYWins = 0;
			else ProbYWins = Prob[YT][XT+Card];		
			ProbWinning+=(1-ProbYWins)/NCards;
		}
		return ProbWinning;
	}
	
	public static double pass(int XT, int YT){
		if(XT < YT)
			return 0;
		else
			return 1-draws(YT,XT);
	}
	
	public static void printMatrix(double m[][]){
		for(int i=0;i<m.length;i++){
			for(int j=0;j<m[i].length;j++){
				DecimalFormat   Formator   =   new   DecimalFormat( "#.## ");
				System.out.print(Formator.format(m[i][j])+"  ");
			}
			System.out.println();
		}
	}
	
	public static void fill(int XT, int YT){
		double drawProb = draws(XT,YT);
		double passProb = pass(XT,YT);
		if(YT>XT){
			Play[XT][YT] = "DRAW";
			Prob[XT][YT] = draws(XT,YT);
		}
		
		else{
			if(drawProb>=passProb){
				Play[XT][YT] = "DRAW";
				Prob[XT][YT] = drawProb;
			}
			else{
				Play[XT][YT] = "PASS";
				Prob[XT][YT] = passProb;
			}
		}
		//System.out.println(XT+"  "+YT+"  "+drawProb+"  "+passProb+"  "+Play[XT][YT]);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(final String[] args) throws NumberFormatException, IOException {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please input the Ncards");
		int a = Integer.parseInt(reader.readLine());
		System.out.println("Please input the LTarget");
		int b = Integer.parseInt(reader.readLine());
		System.out.println("Please input the UTarget");
		int c = Integer.parseInt(reader.readLine());
		
		int NCards = a;
		double LTarget = (double) b;
		double UTarget = (double) c;
		
		init(NCards,LTarget,UTarget);
		for (int row = (int) (LTarget - 1); row >= 0; row--) {
			int i = row;
			int j = (int) (LTarget - 1);
			while (i <= LTarget - 1) {
				fill(i, j);
				i++;
				j--;
			}
		}

		for (int col = (int) (LTarget - 2); col >= 0; col--) {
			int j = col;
			int i = 0;
			while (j >= 0) {
				fill(i, j);
				i++;
				j--;
			}
		}

		System.out.println("Prob Matrix:");
		printMatrix(Prob);
		
		System.out.println("Play Matrix:");
		printMatrixString(Play);
	}

	private static void printMatrixString(String[][] m) {
		for(int i=0;i<m.length;i++){
			for(int j=0;j<m[i].length;j++){
				System.out.print((m[i][j])+"  ");
			}
			System.out.println();
		}
	}

}
