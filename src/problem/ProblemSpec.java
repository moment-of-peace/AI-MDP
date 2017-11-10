package problem;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class is used for file I/O
 */
public class ProblemSpec {
	
	/** True iff user stochastic model is currently loaded */
	private boolean modelLoaded = false;
	/** The number of fortnights the venture manager will be evaluated */
	private int numFortnights;
	/** Discount factor */
	private double discountFactor;
	/** The venture manager type */
	private VentureManager ventureManager;
	/** The probabilities for the order demand for each venture */
	private List<Matrix> probabilities;
    /** The price received per sale for each venture type (x$10 000) */
    private List<Double> salePrices;
    /** Initial amount allocated to each venture's manufacturing fund */
    private List<Integer> initialFunds;
	
	public ProblemSpec() {
	}
	
	public ProblemSpec(String specFileName) throws IOException {
	    this();
	    loadInputFile(specFileName);
	}
	
	/**
	 * Loads the stochastic model from file
	 * @param filename the path of the text file to load.
	 * @throws IOException
	 * 		if the text file doesn't exist or doesn't meet the assignment
	 *      specifications.
	 */
	public void loadInputFile(String filename) throws IOException {
		modelLoaded = false;
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;
		int lineNo = 0;
		Scanner s;
		try {
		    // read venture manager type (line 1)
            line = input.readLine();
            lineNo++;
            ventureManager = new VentureManager(line.trim().toLowerCase());

            // read discount factor (line 2)
		    line = input.readLine();
            lineNo++;
            s = new Scanner(line);
            s.useLocale(Locale.US); // For decimal point handling for computers set up for foreign locale
            discountFactor = s.nextDouble();
            s.close();

            // read number of fortnights over which the venture manager is tested (line 3)
			line = input.readLine();
			lineNo++;
			s = new Scanner(line);
			numFortnights = s.nextInt();
			s.close();
			
			// read item sale prices (line 4)
            line = input.readLine();
            lineNo++;
            s = new Scanner(line);
            s.useLocale(Locale.US);
            salePrices = new ArrayList<Double>(ventureManager.getNumVentures());
            for(int i = 0; i < ventureManager.getNumVentures(); i++) {
                if(s.hasNextDouble()) {
                    salePrices.add(s.nextDouble());
                } else {
                    throw new IOException("Not enough sale prices for the venture manager type.");
                }

            }

            // read initial manufacturing funding allocation (line 5)
            line = input.readLine();
            lineNo++;
            s = new Scanner(line);
            initialFunds = new ArrayList<Integer>(ventureManager.getNumVentures());
            for(int i = 0; i < ventureManager.getNumVentures(); i++) {
                if(s.hasNextInt()) {
                    initialFunds.add(s.nextInt());
                } else {
                    throw new IOException("Not enough initial funding amounts for the venture manager type.");
                }
            }
			
			// read order demand model (line 6+)
			probabilities = new ArrayList<Matrix>();
			for (int k = 0; k < ventureManager.getNumVentures(); k++) {
				double[][] data = new double[ventureManager.getMaxManufacturingFunds() + 1]
						[ventureManager.getMaxManufacturingFunds() + 1];
				for (int i = 0; i <= ventureManager.getMaxManufacturingFunds(); i++) {
					line = input.readLine();
					lineNo++;
					double rowSum = 0;
					s = new Scanner(line);
                    s.useLocale(Locale.US);
					for (int j = 0; j <= ventureManager.getMaxManufacturingFunds(); j++) {
						data[i][j] = s.nextDouble();
						rowSum += data[i][j];
					}
					s.close();
					if (Math.round(rowSum*100000) != 100000) {
						throw new InputMismatchException(
								"Row probabilities do not sum to 1.");
					}
				}
				probabilities.add(new Matrix(data));
			}
			modelLoaded = true;
		} catch (InputMismatchException e) {
			throw new IOException(String.format(
					"Invalid number format on line %d: %s", lineNo,
					e.getMessage()));
		} catch (NoSuchElementException e) {
			throw new IOException(String.format("Not enough tokens on line %d",
					lineNo));
		} catch (NullPointerException e) {
			throw new IOException(String.format(
					"Line %d expected, but file ended.", lineNo));
		} finally {
			input.close();
		}
	}
	
	/**
	 * Save output to file
	 * @param filename The file path to save to
	 * @param orderHistory List of all shopping orders starting at week 0
	 * @throws IOException
	 */
	public void saveOutput(String filename, List<List<Integer>> requestHistory,
                           List<List<Integer>> orderHistory) throws IOException {
		String ls = System.getProperty("line.separator");
		FileWriter output = new FileWriter(filename);

        // write number of fortnights
        output.write(String.format("%d %s", numFortnights, ls));

        // write initial funds allocation
        for(int item : initialFunds) {
            output.write(item + " ");
        }
        output.write(ls);

        for(int fortnight = 0; fortnight < numFortnights; fortnight++) {

            // write number of customer orders
            for(int item : requestHistory.get(fortnight)) {
                output.write(item + " ");
            }
            output.write(ls);

			/*
            // do not write additional funding for fortnight N
            if(fortnight == numFortnights) {
                break;
            }
			*/

            // write additional funding
            for(int item : orderHistory.get(fortnight)) {
                output.write(item + " ");
            }
            output.write(ls);

        }

		//output.write(String.format("%f", totalProfit));
		output.close();
	}

	public boolean isModelLoaded() {
		return modelLoaded;
	}

	public int getNumFortnights() {
		return numFortnights;
	}

	public double getDiscountFactor() {
		return discountFactor;
	}

	public VentureManager getVentureManager() {
		return ventureManager;
	}

	public List<Matrix> getProbabilities() {
		return new ArrayList<Matrix>(probabilities);
	}

	public List<Double> getSalePrices() {
	    return new ArrayList<Double>(salePrices);
	}

	public List<Integer> getInitialFunds() {
	    return new ArrayList<Integer>(initialFunds);
    }
}
