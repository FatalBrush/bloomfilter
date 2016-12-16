import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * Created by Seb on 13.12.2016.
 * functionality explained here: https://en.wikipedia.org/wiki/Bloom_filter
 */


public class BloomFilter {
    private int nrOfExpectedElements;
    private double errorProbability;
    private boolean[] elements;
    private int elementsSize;
    private int nrOfHashFunctions;
    private int nrofWordsInserted;

    /**
     * Creates the bloom-filter
     * @param n number of expected elements to be looked at
     * @param p the desired probability for false positives 0<p<1
     */
    public BloomFilter(int n, double p){
        nrOfExpectedElements = n;
        errorProbability = p;
        double lnp = Math.log(p);
        double ln2 = Math.log(2);
        elementsSize = (int)(-((n*lnp)/(Math.pow(ln2,2)))); // m
        elements = new boolean[elementsSize];
        nrOfHashFunctions = (int)(-(lnp/ln2)); // k
    }

    /***
     * Inserts a word into our bloom-filter by setting index values to 1
     * @param word word to be inserted
     */
    public void insertWord(String word){
        int seed = 0;
        while(seed < nrOfHashFunctions){
            elements[getIndex(seed, word)] = true;
            seed++;
        }
        nrofWordsInserted++;
    }

    /***
     * Checks if a word is in our bloom-filter
     * @param word word being checked
     * @return false = word not available (100% accuracy) and true = it is available (but maybe not!)
     */
    public boolean containsWord(String word){
        int seed = 0;
        while(seed < nrOfHashFunctions){
            if(!elements[getIndex(seed,word)]){
                return false;
            }
            seed++;
        }
        return true;
    }

    /**
     * Returns an index of our bloom-filter
     * @param seed integer used as seed for hashing
     * @param key key word that is being hashed
     * @return integer from range 0 to elementsSize-1
     */
    public int getIndex(int seed, String key){
        HashFunction hf = Hashing.murmur3_128(seed);
        HashCode hc = hf.hashString(key, Charsets.UTF_16); // Java uses UTF 16 for Strings so let's stick to it.
        return Math.abs(hc.asInt()%elementsSize);
    }

    // Getter for test purposes
    public int getNrOfExpectedElements() {
        return nrOfExpectedElements;
    }

    public double getErrorProbability() {
        return errorProbability;
    }

    public boolean[] getElements() {
        return elements;
    }

    public int getElementsSize() {
        return elementsSize;
    }

    public int getNrOfHashFunctions() {
        return nrOfHashFunctions;
    }

    public int getNrofWordsInserted() {
        return nrofWordsInserted;
    }
}
