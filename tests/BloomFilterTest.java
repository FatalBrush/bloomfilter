/**
 * Created by Seb on 14.12.2016.
 */

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BloomFilterTest {
    private static final String TESTFILE = "/words.txt"; // located in source folder
    private static final int NR_OF_LINES = 58110; // our test file has this amout of lines
    private static final double ERROR_PROBABILITY = 0.1; // "p" in the formula, set as wished
    private BloomFilter bf;

    @Before
    public void setup(){
        bf = new BloomFilter(NR_OF_LINES,ERROR_PROBABILITY);
        try {
            Path filePath = Paths.get(getClass().getResource(TESTFILE).toURI());
            Stream<String> streamOfLines = Files.lines(filePath);
            streamOfLines.forEach(word -> {
                bf.insertWord(word);
            });
            Assert.assertEquals(NR_OF_LINES, bf.getNrofWordsInserted());
        } catch (Exception e){
            Assert.fail("Setup failed!");
        }
    }

    @Test
    public void testConstructor(){
        BloomFilter bftmp = new BloomFilter(10,0.2);
        Assert.assertEquals(10,bftmp.getNrOfExpectedElements());
        Assert.assertEquals(0.2, bftmp.getErrorProbability(), 0.1);
        // m  = -(n * ln(p) / (ln(2))^2), result is cast to integer. values calculated with matlab.
        Assert.assertEquals(33,bftmp.getElementsSize());
        // k = - (ln(p)/ln(2)), result is cast to integer. values calculated with matlab.
        Assert.assertEquals(2,bftmp.getNrOfHashFunctions());
    }

    @Test
    public void testErrorProbability(){
        List<String> tmpwords = new ArrayList<>();
        try {
            /*
            Read file again but this time add a string to each word so they do not exist in our bloom filter
             */
            Path filePath = Paths.get(getClass().getResource(TESTFILE).toURI());
            Stream<String> streamOfLines = Files.lines(filePath);
            streamOfLines.forEach(word -> {
                tmpwords.add(word + "1234");
            });
        } catch (Exception e){
            Assert.fail("testErrorProbability failed!");
        }

        int falsePositives = 0;
        for (String str: tmpwords) {
            if(bf.containsWord(str)){
                falsePositives++;
                /*
                In case the bloom-filter says "yes, I've got this item!" then it counts as a false positive
                since our bloom filter should not have that item.
                 */
            }
        }

        double calcErrorProbability = ((double)falsePositives / (double)tmpwords.size()); // it's important to cast each value to double before division, otherwise integer division!
        Assert.assertTrue(calcErrorProbability <= ERROR_PROBABILITY); // if the bloom filter works right, we reach the desired error probability or are below that value
    }
}
