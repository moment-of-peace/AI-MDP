package problem;

/**
 * COMP3702 A3 2017 Support Code
 * v1.0
 * last updated by Nicholas Collins 19/10/17
 */

public class VentureManager {
	
	/** Name of customer level for this Venture Manager */
	private String name;
	/** Number of ventures to be managed */
	private int numVentures;
	/** Maximum amount of manufacturing funding across all ventures (x$10 000) */
	private int maxManufacturingFunds;
	/** Maximum amount of funding which can be added to a venture in 1 fortnight (x$10 000) */
    private int maxAdditionalFunding;
	
	/**
	 * Constructor
	 * @param name
	 * @param maxManufacturingFunds
	 * @param numVentures
	 */
	public VentureManager(String name, int maxManufacturingFunds, int maxAdditionalFunding, int numVentures) {
		this.name = name;
		this.maxManufacturingFunds = maxManufacturingFunds;
		this.maxAdditionalFunding = maxAdditionalFunding;
		this.numVentures = numVentures;
	}
	
	/**
	 * Constructor
	 * @param name Takes values bronze, silver, gold or platinum
	 */
	public VentureManager(String name) {
		this.name = name;
		if (name.equals("bronze")) {
			numVentures = 2;
			maxManufacturingFunds = 3;
			maxAdditionalFunding = 3;
		} else if (name.equals("silver")) {
			numVentures = 2;
			maxManufacturingFunds = 5;
			maxAdditionalFunding = 4;
		} else if (name.equals("gold")) {
			numVentures = 3;
			maxManufacturingFunds = 6;
			maxAdditionalFunding = 4;
		} else if (name.equals("platinum")) {
			numVentures = 3;
			maxManufacturingFunds = 8;
			maxAdditionalFunding = 5;
		} else {
			throw new IllegalArgumentException("Invalid customer level.");
		}
	}

	public String getName() {
		return name;
	}

	public int getMaxManufacturingFunds() {
		return maxManufacturingFunds;
	}
	
	public int getMaxAdditionalFunding() {
	    return maxAdditionalFunding;
	}

	public int getNumVentures() {
		return numVentures;
	}
}
