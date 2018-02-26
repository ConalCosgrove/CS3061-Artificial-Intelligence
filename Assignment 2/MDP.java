
public class MDP{
	//Declaring constant values for better readability
	public static final int FIT = 0;
	public static final int UNFIT = 1;
	public static final int EXERCISE = 0;
	public static final int RELAX = 1;

	final double[][] 	exerciseProbabilities 	= {{0.99,0.01},{0.2,0.8}};	
	final double[][] 	relaxProbabilities 		= {{0.7,0.3},{0,1}};
	final int[][] 		exerciseRewards 		= {{8,8},{0,0}};
	final int[][]		relaxRewards 			= {{10,10},{5,5}};

	//Arrays to store previously calculated q values, saves time in recursive call
	public static double[][][] q;
	public static boolean[][][] qAvailable;

	//Declaring Input Variables
	private State 	start_state;	
	private int 	n;					
	private double 	gamma;			

	//Declaring states fit and unfit
	private State fit;		
	private State unfit;

	//Constructor(positive int,start state,gamma)
	public MDP(int n,String s, double g){
		fit = 	new State(exerciseProbabilities[0],exerciseRewards[0],relaxProbabilities[0],relaxRewards[0],"fit");
		unfit = new State(exerciseProbabilities[1],exerciseRewards[1],relaxProbabilities[1],relaxRewards[1],"unfit");

		//Instantiating variables
		this.start_state = (s.equals("fit")?(fit):(unfit));
		this.n = n;
		this.gamma = g;

		//Setting up previously calculated values array
		q = new double[2][2][n+1];
		qAvailable = new boolean[2][2][n+1];
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 2; j++){
				for(int k = 0; k < n; k++){
					qAvailable[i][j][k] = false;
				}
			}
		}
		double qExercise = q(start_state,EXERCISE,n);
		double qRelax 	 = q(start_state,RELAX,n);
		System.out.println("Q" + n + "(" + start_state.getName() + ", exercise) = " + String.format("%.5g",qExercise));
		System.out.println("Q" + n + "(" + start_state.getName() + ", relax) = " + String.format("%.5g",qRelax));
		System.out.println("𝛑 = " + ((qExercise > qRelax)?("exercise"):("relax")));
	}

	//Function p(s,a,s')
	//Returns the probability that, given a start state of s and an action a, the state s' will be reached
	public double p(State s,int a,int sNext){
		return (a == EXERCISE)?(s.exercise(sNext)[0]):(s.relax(sNext)[0]);
	}

	//Function r(s,a,s')
	//Returns the reward gained if, given a start state of s and an action a, the state s' is reached
	public double r(State s,int a,int sNext){
		return(a == EXERCISE)?(s.exercise(sNext)[1]):(s.relax(sNext)[1]);
	}

	//Calculates Vn(s) given a state and n value
	public double v(State s,int i){
		//Vn(s) := max(qn(s, exercise), qn(s,relax))
		return     max(q(s,EXERCISE,i),q(s,RELAX,i));
	}

	//Returns the greater of 2 rewards 
	double max(double exerciseR,double relaxR){
		return (exerciseR >= relaxR)?(exerciseR):(relaxR);
	}


	public double q(State s, int a, int n){
		
		double qValue = 0;
		double qZero = 0;

		//Check if this q value has been previously calculated
		if(qAvailable[s.getStateInt()][a][n]){
			
			return q[s.getStateInt()][a][n];
		}
		//q0(s, a) := p(s, a, fit)r(s, a, fit) + p(s, a, unfit)r(s, a, unfit)
		else if(n == 0){
			qValue = p(s,a,FIT) * r(s,a,FIT) + p(s,a,UNFIT) * r(s,a,FIT);
		}else{
			//qn+1(s, a) := q0(s, a) + γ( p(s, a, fit)Vn(fit) + p(s, a, unfit)Vn(unfit) )
			qZero = p(s,a,FIT) * r(s,a,FIT) + p(s,a,UNFIT) * r(s,a,FIT);
			qValue = qZero + gamma * ( ( p(s,a,FIT) * v(fit,n-1)) + p(s,a,UNFIT) * v(unfit,(n-1)));

		}

		q[s.getStateInt()][a][n] = qValue;
		qAvailable[s.getStateInt()][a][n] = true;
		//System.out.println("Q" + n + "(" + s.getName() + ", " + ((a==EXERCISE)?("exercise"):("relax")) + ") = " + qValue);
		return qValue;
	}





	private class State{
		private int[] exerciseRewards;			//exerciseReward[0] = reward when finishing state is 'fit' after exercising
												//exerciseReward[1] = reward when finishing state is 'unfit' after exercising

		private double[] exerciseProbabilities;	//exerciseProbabilities[0] = probability finishing state is 'fit' after exercising
												//exerciseProbabilities[1] = probability finishing state is 'unfit'	after exercising

		private int[] relaxRewards;				//relaxReward[0] = reward when finishing state is 'fit' after relaxing
												//relaxReward[1] = reward when finishing state is 'unfit' after relaxing

		private double[] relaxProbabilities;	//relaxProbabilities[0] = probability finishing state is 'fit' after relaxing
												//relaxProbabilities[1] = probability finishing state is 'unfit' after relaxing

		private String name; 
		private int stateInt;

		//Constructor(exerciseProbabilities, exerciseRewards, relaxProbabilities, relaxRewards, name)
		//Sets the probability and reward matricies of the State
		public State(double[] eP,int[] eR,double[] rP,int[] rR,String n){
			this.exerciseRewards = eR;	
			this.exerciseProbabilities = eP;
			this.relaxRewards = rR;
			this.relaxProbabilities = rP;
			this.name = n;
			this.stateInt = (name.equals("fit"))?(0):(1);
		}

		//exercise(s')
		//returns tuple of probability and reward of finishing in state s' after relaxing
		public double[] exercise(int finishingState){
			double[] returnable = new double[2];
			if(finishingState == FIT){
				returnable[0] = exerciseProbabilities[0];
				returnable[1] = exerciseRewards[0];
			}else{
				returnable[0] = exerciseProbabilities[1];
				returnable[1] = exerciseRewards[1];
			}
			return returnable;
		}

		//relax(s')
		//returns tuple of probability and reward of finishing in state s' after relaxing
		public double[] relax(int finishingState){
			double[] returnable = new double[2];
			if(finishingState == FIT){
				returnable[0] = relaxProbabilities[0];
				returnable[1] = relaxRewards[0];
			}else{
				returnable[0] = relaxProbabilities[1];
				returnable[1] = relaxRewards[1];
			}
			return returnable;
		}

		//Returns name of state, used for print functions
		public String getName(){
			return name;
		}

		public int getStateInt(){
			return stateInt;
		}
	}

	//Main function, defines probability and reward matricies and creates 
	//instance of MDP with user input parameters 
	public static void main(String[] args){


		if(args.length < 3 || args.length > 3){
			System.out.println("Please enter three parameters:\nn: a positive integer\ns: starting state, fit or unfit\ng: a gamma setting between 0 and 1");
			System.exit(1);
		}else{
			int n 		 = Integer.parseInt(args[0]);
			String start = args[1].toLowerCase();
			float g 	 = Float.parseFloat(args[2]);
			
			MDP test = new MDP(n,start,g);
		}
	}

}
