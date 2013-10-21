import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;



public class main
{
	// MACROS
	static final String INPUT_LAMDA			= "Mean arrival rate:";
	static final String INPUT_MEW			= "Mean service rate per server:";
	static final String INPUT_BUFFER		= "Maximum buffer size:";
	static final String INPUT_SERVER		= "Number of servers:";
	static final String INPUT_WINDOW		= "Window size:";
	static final String INPUT_FREQUENCY		= "Frequency:";
	static final String INPUT_END			= "Max number of departures:";
	
	// FCFS queue.
	static LinkedList<Double> _queue;
	
	// Input parameters.
	static double _lambda 		= -1;
	static double _mu			= -1;
	static int _bufferSize		= -1; // -1 = infinite
	static int _numServers		= -1;
	static int _windowSize  	= -1;
	static int _frequency		= -1;
	static int _maxDepartures	= -1;
	
	// Initialize other variables.
	static int _busyServers 				= 0;
	static int _totalArrivals 				= 0;
	static int _totalEvents 				= 0;
	static int _totalCustomersServed		= 0;
	static double _clock 					= 0.0;
	static double _timeToNextArrival 		= 0.0;
	static double _timeOfServerCompletion 	= 0.0;
	static double _timeToNextEvent 		= 0.0;
	static double _timeFree				= 0.0;
	static double _serverTimeFree			= 0.0;
	static double _totalWaitingTime		= 0.0;
	
	// Main code block.
	public static void main(String[] args)
	{
		// Initialize the queue.
		_queue = new LinkedList<Double>();
		
		// Initialize the random number generators
		// and their seeds.
		Random ra = new Random();
		Random rs = new Random();
		
		// Prompt user for input parameters.
		receiveInputParameters();
		
		// Simulation loop.
		while (true)
		{
			boolean arrivalEvent = false;
			boolean serviceEvent = false;
			double timeOfPreviousEvent = 0.0;
			
			_timeToNextEvent = Math.min(_timeToNextArrival, _timeOfServerCompletion);
			
			// Updated the next event type.
			if ((_timeToNextEvent == _timeToNextArrival) &&
				(_timeToNextArrival != _timeOfServerCompletion))
			{
				arrivalEvent = true;
				serviceEvent = false;
			}
			else if ((_timeToNextEvent == _timeOfServerCompletion) &&
					 (_timeToNextArrival != _timeOfServerCompletion))
			{
				serviceEvent = true;
				arrivalEvent = false;
			}
			else
			{
				arrivalEvent = true;
			}
			
			// Update the clock.
			if (_timeToNextEvent != Double.POSITIVE_INFINITY)
			{
				timeOfPreviousEvent = _clock;
				_clock = _timeToNextEvent;
			}
			
			// Check the stopping condition.
			if (shouldEndSimulation(_totalCustomersServed))
			{
				reportFinalStatistics();
				
				System.exit(0);
			}
			
			// Handle the next event.
			if (arrivalEvent)
			{
				arrivalEvent(ra, rs, timeOfPreviousEvent);
			}
			else
			{
				if (serviceEvent)
				{
					serviceEvent(rs);
				}
				else
				{
					_timeToNextArrival = Double.POSITIVE_INFINITY;
				}
			}
			
			_totalEvents++;
		}
	}
	
	// Function to check if the simulation
	// should be stopped.
	private static boolean shouldEndSimulation(int customers)
	{
		if (customers == _maxDepartures)
		{
			return true;
		}
		
		return false;
	}
	
	// Function for an arrival event.
	private static void arrivalEvent(Random ra, Random rs, double timeOfPreviousEvent)
	{
		_timeToNextArrival = _clock + getNextArrivalExponential(ra);
		
		_totalArrivals++;
		
		// Check if a server is free.
		if (_busyServers < _numServers)
		{
			_busyServers++;
			
			_timeFree = _clock - timeOfPreviousEvent;
			_serverTimeFree += _timeFree;
			
			_timeOfServerCompletion = _clock + getNextServiceExponential(rs);
		}
		// No servers are free, so add the
		// customer to the queue.
		else
		{
			_queue.add(_clock);
		}
		
		// A customer has departed.
		if (_queue.isEmpty())
		{
			_totalCustomersServed++;
		}
	}
	
	// Function for a service event.
	private static void serviceEvent(Random rs)
	{
		if (_queue.isEmpty())
		{
			_busyServers--;
			
			if (_busyServers == 0)
			{
				_timeOfServerCompletion = Double.POSITIVE_INFINITY;
			}
			
//			_busyServers = 0;
//			
//			_timeOfServerCompletion = Double.POSITIVE_INFINITY;
		}
		else
		{
			double time = _queue.pop();
			
//			_busyServers--;
			
			_totalCustomersServed++;
			
			_totalWaitingTime += (_clock - time);
			
			_timeOfServerCompletion = _clock + getNextServiceExponential(rs);
		}
	}
	
	// Function to report the final statistics.
	private static void reportFinalStatistics()
	{
		System.out.println("\nSimulation completed!\n\nSIMULATION REPORT\n-----------------");
		
//		System.out.println("_totalArrivals: " + _totalArrivals);
//		System.out.println("_totalCustomersServed: " + _totalCustomersServed);
//		System.out.println("_clock: " + _clock);
//		System.out.println("_totalWaitingTime: " + _totalWaitingTime);
//		System.out.println("_serverTimeFree: " + _serverTimeFree);
		
		
	}
	
	// Function to receive the input parameters.
	private static void receiveInputParameters()
	{
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(in);
		
		// Receive the input parameters
		// from the keyboard.
		try
		{
			System.out.println(INPUT_LAMDA);
			_lambda = Double.parseDouble(br.readLine());
			
			System.out.println(INPUT_MEW);
			_mu = Double.parseDouble(br.readLine());
			
			System.out.println(INPUT_BUFFER);
			_bufferSize = Integer.parseInt(br.readLine());
			
			System.out.println(INPUT_SERVER);
			_numServers = Integer.parseInt(br.readLine());
			
			System.out.println(INPUT_WINDOW);
			_windowSize = Integer.parseInt(br.readLine());
			
			System.out.println(INPUT_FREQUENCY);
			_frequency = Integer.parseInt(br.readLine());
			
			System.out.println(INPUT_END);
			_maxDepartures = Integer.parseInt(br.readLine());
			
			dumpInputParameters();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// Function used for testing purposes to
	// dump the input parameters.
	private static void dumpInputParameters()
	{
		System.out.println("\nINPUT PARAMETERS\n----------------");
		System.out.println("_lambda = " + _lambda);
		System.out.println("_mu = " + _mu);
		System.out.println("_bufferSize = " + _bufferSize);
		System.out.println("_numServers = " + _numServers);
		System.out.println("_windowSize = " + _windowSize);
		System.out.println("_frequency = " + _frequency);
		System.out.println("_maxDepartures = " + _maxDepartures);
	}
	
	// Function for the interarrival time distribution.
	private static double getNextArrivalExponential(Random r)
	{
		double num;
		double exp;
	
		// Generate a pseudorandom number with
		// exponential distribution.
		num = r.nextDouble();
		exp = ((Math.log(1 - num)) / (-_lambda));
		
		return exp;
	}
	
	// Function for the interarrival time distribution.
	private static double getNextServiceExponential(Random r)
	{
		double num;
		double exp;
	
		// Generate a pseudorandom number with
		// exponential distribution.
		num = r.nextDouble();
		exp = ((Math.log(1 - num)) / (-_lambda));
		
		return exp;
	}
	
	////////////////////
	// HELPER CLASSES //
	////////////////////
	
	// Job Class
	public class Job
	{
		// Default constructor.
		public Job()
		{
			
		}
	}
}
