package fast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {
    private static final int N = 1000; // Number of strings to concatenate
    private static final byte[][] bytesList = new byte[N][];

    // Initialize byte arrays with "hello" encoded in UTF-8
    static {
        for (int i = 0; i < N; i++) {
            bytesList[i] = "hello".getBytes(StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) {
        // Warm-up phase to stabilize JVM performance
        for (int i = 0; i < 10000; i++) {
            new String(bytesList[0], StandardCharsets.UTF_8);
            new FastString(bytesList[0]);
            new FastStringRopeLike(bytesList[0]);
        }

        // Create lists of objects for each implementation
        List<String> stringList = new ArrayList<>();
        List<FastString> fsList = new ArrayList<>();
        List<FastStringRopeLike> fsrList = new ArrayList<>();

        // Construct objects from byte arrays (isolating construction time)
        for (byte[] b : bytesList) {
            stringList.add(new String(b, StandardCharsets.UTF_8));
            fsList.add(new FastString(b));
            fsrList.add(new FastStringRopeLike(b));
        }

        // Test String concatenation
        long startTime = System.nanoTime();
        String s = "";
        for (String next : stringList) {
            s = s.concat(next);
        }
        long stringConcatTime = System.nanoTime() - startTime;
        long stringToStringTime = 0; // toString() is trivial for String
        String finalString = s;

        // Test FastString concatenation
        startTime = System.nanoTime();
        FastString fs = new FastString(new byte[0]); // Empty FastString
        for (FastString next : fsList) {
            fs = fs.concat(next);
        }
        long fsConcatTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        String fsString = fs.toString();
        long fsToStringTime = System.nanoTime() - startTime;

        // Test FastStringRopeLike concatenation
        startTime = System.nanoTime();
        FastStringRopeLike fsr = new FastStringRopeLike(new byte[0]); // Empty FastStringRopeLike
        for (FastStringRopeLike next : fsrList) {
            fsr = fsr.concat(next);
        }
        long fsrConcatTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        String fsrString = fsr.toString();
        long fsrToStringTime = System.nanoTime() - startTime;

        // Print results
        System.out.println("String concat time: " + stringConcatTime + " ns");
        System.out.println("String toString time: " + stringToStringTime + " ns");
        System.out.println("String total time: " + (stringConcatTime + stringToStringTime) + " ns");
        System.out.println();

        System.out.println("FastString concat time: " + fsConcatTime + " ns");
        System.out.println("FastString toString time: " + fsToStringTime + " ns");
        System.out.println("FastString total time: " + (fsConcatTime + fsToStringTime) + " ns");
        System.out.println();

        System.out.println("FastStringRopeLike concat time: " + fsrConcatTime + " ns");
        System.out.println("FastStringRopeLike toString time: " + fsrToStringTime + " ns");
        System.out.println("FastStringRopeLike total time: " + (fsrConcatTime + fsrToStringTime) + " ns");
    }
}