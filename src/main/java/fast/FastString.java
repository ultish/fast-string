package fast;

import java.nio.charset.StandardCharsets;

public class FastString implements CharSequence {
    /**
     * The underlying byte array holding the string data.
     */
    private final byte[] data;

    /**
     * Starting offset in the byte array.
     */
    private final int offset;

    /**
     * Length of the byte segment in bytes.
     */
    private final int byteLength;

    /**
     * Number of characters (not bytes) in the string.
     */
    private final int charLength;

    /**
     * Constructs a FastString from a byte array segment.
     *
     * @param data       The byte array (not copied).
     * @param offset     Starting position in the array.
     * @param byteLength Length of the segment in bytes.
     */
    public FastString(byte[] data, int offset, int byteLength) {
        if (data == null || offset < 0 || byteLength < 0 || offset + byteLength > data.length) {
            throw new IllegalArgumentException("Invalid byte array parameters");
        }
        this.data = data;
        this.offset = offset;
        this.byteLength = byteLength;
        this.charLength = computeCharLength(data, offset, byteLength);
    }

    /**
     * Constructs a FastString from an entire byte array.
     *
     * @param data The byte array (not copied).
     */
    public FastString(byte[] data) {
        this(data, 0, data.length);
    }


    /**
     * Computes the number of characters in the byte array segment assuming UTF-8 encoding.
     */
    private static int computeCharLength(byte[] data, int offset, int byteLength) {
        int charCount = 0;
        int end = offset + byteLength;
        for (int i = offset; i < end; ) {
            int c = data[i] & 0xFF; // Treat byte as unsigned
            if (c < 0x80) {         // 1-byte character (ASCII)
                i += 1;
            } else if ((c & 0xE0) == 0xC0) { // 2-byte character
                i += 2;
            } else if ((c & 0xF0) == 0xE0) { // 3-byte character
                i += 3;
            } else {                // 4-byte character
                i += 4;
            }
            charCount++;
        }
        return charCount;
    }

    @Override
    public int length() {
        return charLength;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= charLength) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + charLength);
        }
        int byteIndex = findByteIndexOfChar(index);
        return decodeChar(data, byteIndex);
    }

    /**
     * Finds the byte position of the given character index.
     */
    private int findByteIndexOfChar(int charIndex) {
        int charCount = 0;
        int i = offset;
        while (charCount < charIndex) {
            int c = data[i] & 0xFF;
            if (c < 0x80) {
                i += 1;
            } else if ((c & 0xE0) == 0xC0) {
                i += 2;
            } else if ((c & 0xF0) == 0xE0) {
                i += 3;
            } else {
                i += 4;
            }
            charCount++;
        }
        return i;
    }

    /**
     * Decodes a single UTF-8 character starting at the given byte index.
     */
    private char decodeChar(byte[] data, int byteIndex) {
        int c = data[byteIndex] & 0xFF;
        if (c < 0x80) {
            return (char) c;
        } else if ((c & 0xE0) == 0xC0) { // 2-byte sequence
            int b1 = c & 0x1F;
            int b2 = data[byteIndex + 1] & 0x3F;
            return (char) ((b1 << 6) | b2);
        } else if ((c & 0xF0) == 0xE0) { // 3-byte sequence
            int b1 = c & 0x0F;
            int b2 = data[byteIndex + 1] & 0x3F;
            int b3 = data[byteIndex + 2] & 0x3F;
            return (char) ((b1 << 12) | (b2 << 6) | b3);
        } else { // 4-byte sequence (returns first char of surrogate pair)
            int b1 = c & 0x07;
            int b2 = data[byteIndex + 1] & 0x3F;
            int b3 = data[byteIndex + 2] & 0x3F;
            int b4 = data[byteIndex + 3] & 0x3F;
            int codePoint = (b1 << 18) | (b2 << 12) | (b3 << 6) | b4;
            return (char) ((codePoint - 0x10000) >> 10 | 0xD800);
        }
    }

    public FastString concat(FastString other) {
        // Create a new byte array to hold the combined bytes
        byte[] newData = new byte[this.byteLength + other.byteLength];

        // Copy bytes from the first FastString
        System.arraycopy(this.data, this.offset, newData, 0, this.byteLength);

        // Copy bytes from the second FastString
        System.arraycopy(other.data, other.offset, newData, this.byteLength, other.byteLength);

        // Return a new FastString with the combined bytes
        return new FastString(newData, 0, newData.length);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || end > charLength || start > end) {
            throw new IndexOutOfBoundsException("Start: " + start + ", End: " + end + ", Length: " + charLength);
        }
        int byteStart = findByteIndexOfChar(start);
        int byteEnd = (end == charLength) ? offset + byteLength : findByteIndexOfChar(end);
        int newByteLength = byteEnd - byteStart;
        return new FastString(data, byteStart, newByteLength);
    }

    @Override
    public String toString() {
        return new String(data, offset, byteLength, StandardCharsets.UTF_8);
    }
}
