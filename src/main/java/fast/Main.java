package fast;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        FastString s1 = new FastString("Hello, ".getBytes());
        FastString s2 = new FastString("world!".getBytes());
        FastString s3 = s1.concat(s2);

        System.out.println(s3.toString());        // Outputs: "Hello, world!"
        System.out.println(s3.length());          // Outputs: 13
        System.out.println(s3.charAt(0));         // Outputs: 'H'
        System.out.println(s3.subSequence(0, 5)); // Outputs: "Hello"

        System.out.println("----");

        FastStringRopeLike sr1 = new FastStringRopeLike("Hello, ".getBytes());
        FastStringRopeLike sr2 = new FastStringRopeLike("world!".getBytes());
        FastStringRopeLike sr3 = sr1.concat(sr2);

        System.out.println(sr3.toString());        // Outputs: "Hello, world!"
        System.out.println(sr3.length());          // Outputs: 13
        System.out.println(sr3.charAt(0));         // Outputs: 'H'
        System.out.println(sr3.subSequence(0, 5)); // Outputs: "Hello"
    }
}