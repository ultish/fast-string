package fast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FastStringPerformanceTest {
    private static final int ITERATIONS = 1_000_000;
    private static final String SAMPLE_TEXT = "Hello, World! This is a test string with some length.";
    private static final byte[] SAMPLE_BYTES = SAMPLE_TEXT.getBytes(StandardCharsets.UTF_8);

    public static void main(String[] args) {
        // Warm-up to stabilize JVM
        warmUp();

        // Run performance tests
        testConstruction();
        testLength();
        testToString();
        testCharAt();
        testSubstring();
        testConcat();
    }

    private static void warmUp() {
        for (int i = 0; i < 10_000; i++) {
            new String(SAMPLE_BYTES, StandardCharsets.UTF_8);
            new FastString(SAMPLE_BYTES);
            new FastStringRopeLike(SAMPLE_BYTES);
        }
    }

    private static void testToString() {
        String str = new String(SAMPLE_BYTES, StandardCharsets.UTF_8);
        FastString fastStr = new FastString(SAMPLE_BYTES);
        FastStringRopeLike fastStrRopeLike = new FastStringRopeLike(SAMPLE_BYTES);


        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String fsString = str.toString();
        }
        long strToStringTime = (System.nanoTime() - startTime);

        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String fsString = fastStr.toString();
        }
        long fsToStringTime = (System.nanoTime() - startTime);


        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String fsrlString = fastStrRopeLike.toString();
        }
        long fsRlToStringTime = (System.nanoTime() - startTime);

        System.out.printf("ToString - String: %d ns, FastString: %d ns, FastStringRopeLike: %d ns, Ratio (FS/S): %.2f%n, Ratio (FSRL/S): %.2f%n",
                strToStringTime, fsToStringTime, fsRlToStringTime, (double) fsToStringTime / strToStringTime, (double) fsRlToStringTime / strToStringTime);
    }

    private static void testConcat() {
        // Create lists of objects for each implementation
        List<String> stringList = new ArrayList<>();
        List<FastString> fsList = new ArrayList<>();
        List<FastStringRopeLike> fsrList = new ArrayList<>();
        int N = 1000; // Number of strings to concatenate
        byte[][] bytesList = new byte[N][];
        for (int i = 0; i < N; i++) {
            bytesList[i] = ("hello" + i).getBytes(StandardCharsets.UTF_8);
        }

        for (byte[] b : bytesList) {
            stringList.add(new String(b, StandardCharsets.UTF_8));
            fsList.add(new FastString(b));
            fsrList.add(new FastStringRopeLike(b));
        }

        // Test String concatenation
        String s = "";
        long startTime = System.nanoTime();
        for (String next : stringList) {
            s = s.concat(next);
        }
        long stringConcatTime = System.nanoTime() - startTime;

        FastString fs = new FastString(new byte[0]); // Empty FastString
        startTime = System.nanoTime();
        for (FastString next : fsList) {
            fs = fs.concat(next);
        }
        long fsConcatTime = System.nanoTime() - startTime;

        FastStringRopeLike fsr = new FastStringRopeLike(new byte[0]); // Empty FastStringRopeLike
        startTime = System.nanoTime();
        for (FastStringRopeLike next : fsrList) {
            fsr = fsr.concat(next);
        }
        long fsrConcatTime = System.nanoTime() - startTime;

        System.out.printf("Concat - String: %d ns, FastString: %d ns, FastStringRopeLike: %d ns, Ratio (FS/S): %.2f%n, Ratio (FSRL/S): %.2f%n",
                stringConcatTime, fsConcatTime, fsrConcatTime, (double) fsConcatTime / stringConcatTime, (double) fsrConcatTime / stringConcatTime);

    }

    private static void testConstruction() {
        // Test String construction
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            new String(SAMPLE_BYTES, StandardCharsets.UTF_8);
        }
        long stringTime = System.nanoTime() - startTime;

        // Test FastString construction
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            new FastString(SAMPLE_BYTES);
        }
        long fastStringTime = System.nanoTime() - startTime;

        // Test FastStringRopeLike construction
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            new FastStringRopeLike(SAMPLE_BYTES);
        }
        long fastStringRopeTime = System.nanoTime() - startTime;

        System.out.printf("Construction - String: %d ns, FastString: %d ns, FastStringRopeLike: %d ns, Ratio (FS/S): %.2f%n, Ratio (FSRL/S): %.2f%n",
                stringTime, fastStringTime, fastStringRopeTime, (double) fastStringTime / stringTime, (double) fastStringRopeTime / stringTime);
    }

    private static void testLength() {
        String str = new String(SAMPLE_BYTES, StandardCharsets.UTF_8);
        FastString fastStr = new FastString(SAMPLE_BYTES);
        FastStringRopeLike fastStrRopeLike = new FastStringRopeLike(SAMPLE_BYTES);


        // Test String length
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            str.length();
        }
        long stringTime = System.nanoTime() - startTime;

        // Test FastString length
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            fastStr.length();
        }
        long fastStringTime = System.nanoTime() - startTime;

        // Test FastStringRopeLike length
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            fastStrRopeLike.length();
        }
        long fastStringRopeLikeTime = System.nanoTime() - startTime;

        System.out.printf("Length - String: %d ns, FastString: %d ns, FastStringRopeLike: %d ns, Ratio (FS/S): %.2f%n, Ratio (FSRL/S): %.2f%n",
                stringTime, fastStringTime, fastStringRopeLikeTime, (double) fastStringTime / stringTime, (double) fastStringRopeLikeTime / stringTime);
    }

    private static void testCharAt() {
        String str = new String(SAMPLE_BYTES, StandardCharsets.UTF_8);
        FastString fastStr = new FastString(SAMPLE_BYTES);
        FastStringRopeLike fastStrRopeLike = new FastStringRopeLike(SAMPLE_BYTES);

        int midIndex = str.length() / 2;

        // Test String charAt
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            str.charAt(midIndex);
        }
        long stringTime = System.nanoTime() - startTime;

        // Test FastString charAt
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            fastStr.charAt(midIndex);
        }
        long fastStringTime = System.nanoTime() - startTime;

        // Test FastStringRopeLike charAt
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            fastStrRopeLike.charAt(midIndex);
        }
        long fastStringRopeLikeTime = System.nanoTime() - startTime;


        System.out.printf("CharAt (mid) - String: %d ns, FastString: %d ns, FastStringRopeLike: %d ns, Ratio (FS/S): %.2f%n, Ratio (FSRL/S): %.2f%n",
                stringTime, fastStringTime, fastStringRopeLikeTime, (double) fastStringTime / stringTime, (double) fastStringRopeLikeTime / stringTime);
    }

    private static void testSubstring() {
        String str = new String(SAMPLE_BYTES, StandardCharsets.UTF_8);
        FastString fastStr = new FastString(SAMPLE_BYTES);
        FastStringRopeLike fastStrRopeLike = new FastStringRopeLike(SAMPLE_BYTES);

        int start = 7;  // "World"
        int end = 12;

        // Test String substring
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            str.substring(start, end);
        }
        long stringTime = System.nanoTime() - startTime;

        // Test FastString subSequence
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            fastStr.subSequence(start, end);
        }
        long fastStringTime = System.nanoTime() - startTime;

        // Test FastStringRopeLike subSequence
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            fastStrRopeLike.subSequence(start, end);
        }
        long fastStringRopeLikeTime = System.nanoTime() - startTime;

        System.out.printf("Substring - String: %d ns, FastString: %d ns, FastStringRopeLike: %d ns, Ratio (FS/S): %.2f%n, Ratio (FSRL/S): %.2f%n",
                stringTime, fastStringTime, fastStringRopeLikeTime, (double) fastStringTime / stringTime, (double) fastStringRopeLikeTime / stringTime);
    }
}