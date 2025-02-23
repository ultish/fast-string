package fast;

import java.nio.charset.StandardCharsets;

public class FastStringRopeLike implements CharSequence {
    private final Node node;

    // Public constructor for a byte array segment
    public FastStringRopeLike(byte[] data, int offset, int byteLength) {
        this.node = new Leaf(data, offset, byteLength);
    }

    // Public constructor for an entire byte array
    public FastStringRopeLike(byte[] data) {
        this(data, 0, data.length);
    }

    // Private constructor for internal node construction
    private FastStringRopeLike(Node node) {
        this.node = node;
    }

    //    ### Fast Concatenation: O(1)
//    Concatenates this `FastStringRopeLike` with another by creating a `Concat` node.
    public FastStringRopeLike concat(FastStringRopeLike other) {
        return new FastStringRopeLike(new Concat(this.node, other.node));
    }

    @Override
    public int length() {
        return node.getCharLength();
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= node.getCharLength()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + node.getCharLength());
        }
        return node.charAt(index);
    }

//    @Override
//    public CharSequence subSequence(int start, int end) {
//        if (start < 0 || end > node.getCharLength() || start > end) {
//            throw new IndexOutOfBoundsException("Start: " + start + ", End: " + end + ", Length: " + node.getCharLength());
//        }
//        int byteStart = node.findByteIndexOfChar(start);
//        int byteEnd = (end == node.getCharLength()) ? node.getByteLength() : node.findByteIndexOfChar(end);
//        byte[] allBytes = new byte[node.getByteLength()];
//        node.copyBytesTo(allBytes, 0);
//        return new FastStringRopeLike(new Leaf(allBytes, byteStart, byteEnd - byteStart));
//    }

//    @Override
//    public String toString() {
//        byte[] allBytes = new byte[node.getByteLength()];
//        node.copyBytesTo(allBytes, 0);
//        return new String(allBytes, StandardCharsets.UTF_8);
//    }

    // Abstract node class for the rope structure
    private static abstract class Node {
        abstract int getByteLength();

        abstract int getCharLength();

        abstract void copyBytesTo(byte[] dest, int destOffset);

        abstract char charAt(int index);

        abstract int findByteIndexOfChar(int charIndex);
    }

    // Leaf node: holds raw byte data
    private static class Leaf extends Node {
        byte[] data;
        int offset;
        int byteLength;
        int charLength;

        Leaf(byte[] data, int offset, int byteLength) {
            this.data = data;
            this.offset = offset;
            this.byteLength = byteLength;
            this.charLength = computeCharLength(data, offset, byteLength);
        }

        @Override
        int getByteLength() {
            return byteLength;
        }

        @Override
        int getCharLength() {
            return charLength;
        }

        @Override
        void copyBytesTo(byte[] dest, int destOffset) {
            System.arraycopy(data, offset, dest, destOffset, byteLength);
        }

        @Override
        char charAt(int index) {
            int byteIndex = findByteIndexOfChar(index);
            return decodeChar(data, offset + byteIndex);
        }

        @Override
        int findByteIndexOfChar(int charIndex) {
            int charCount = 0;
            int i = 0;
            while (charCount < charIndex) {
                int c = data[offset + i] & 0xFF;
                i += (c < 0x80) ? 1 : ((c & 0xE0) == 0xC0) ? 2 : ((c & 0xF0) == 0xE0) ? 3 : 4;
                charCount++;
            }
            return i;
        }
    }

    // Concat node: represents concatenation of two nodes
    private static class Concat extends Node {
        Node left;
        Node right;
        int byteLength;
        int charLength;

        Concat(Node left, Node right) {
            this.left = left;
            this.right = right;
            this.byteLength = left.getByteLength() + right.getByteLength();
            this.charLength = left.getCharLength() + right.getCharLength();
        }

        @Override
        int getByteLength() {
            return byteLength;
        }

        @Override
        int getCharLength() {
            return charLength;
        }

        @Override
        void copyBytesTo(byte[] dest, int destOffset) {
            left.copyBytesTo(dest, destOffset);
            right.copyBytesTo(dest, destOffset + left.getByteLength());
        }

        @Override
        char charAt(int index) {
            int leftCharLength = left.getCharLength();
            return (index < leftCharLength) ? left.charAt(index) : right.charAt(index - leftCharLength);
        }

        @Override
        int findByteIndexOfChar(int charIndex) {
            int leftCharLength = left.getCharLength();
            if (charIndex < leftCharLength) {
                return left.findByteIndexOfChar(charIndex);
            } else {
                int rightCharIndex = charIndex - leftCharLength;
                return left.getByteLength() + right.findByteIndexOfChar(rightCharIndex);
            }
        }
    }

    // Computes character length of a UTF-8 byte segment
    private static int computeCharLength(byte[] data, int offset, int byteLength) {
        int charCount = 0;
        int end = offset + byteLength;
        for (int i = offset; i < end; ) {
            int c = data[i] & 0xFF;
            i += (c < 0x80) ? 1 : ((c & 0xE0) == 0xC0) ? 2 : ((c & 0xF0) == 0xE0) ? 3 : 4;
            charCount++;
        }
        return charCount;
    }

    // Decodes a single UTF-8 character (up to 3 bytes)
    private static char decodeChar(byte[] data, int index) {
        int c = data[index] & 0xFF;
        if (c < 0x80) {
            return (char) c;
        } else if ((c & 0xE0) == 0xC0) {
            return (char) (((c & 0x1F) << 6) | (data[index + 1] & 0x3F));
        } else if ((c & 0xF0) == 0xE0) {
            return (char) (((c & 0x0F) << 12) | ((data[index + 1] & 0x3F) << 6) | (data[index + 2] & 0x3F));
        } else {
            throw new UnsupportedOperationException("4-byte UTF-8 sequences not supported");
        }
    }


    // faster toString
    private byte[] byteCache;
    private String strCache;

    private void ensureByteCache() {
        if (byteCache == null) {
            byteCache = new byte[node.getByteLength()];
            node.copyBytesTo(byteCache, 0);
        }
    }

    @Override
    public String toString() {
        if (strCache == null) {
            ensureByteCache();
            strCache = new String(byteCache, StandardCharsets.UTF_8);
        }
        return strCache;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        ensureByteCache();
        int byteStart = node.findByteIndexOfChar(start);
        int byteEnd = (end == node.getCharLength()) ? node.getByteLength() : node.findByteIndexOfChar(end);
        return new FastStringRopeLike(new Leaf(byteCache, byteStart, byteEnd - byteStart));
    }
}