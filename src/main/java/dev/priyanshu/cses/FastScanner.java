package dev.priyanshu.cses;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FastScanner {
    private final BufferedInputStream in;
    private final byte[] buffer = new byte[1 << 16];
    private int ptr = 0;
    private int len = 0;

    public FastScanner(InputStream inputStream) {
        in = new BufferedInputStream(inputStream);
    }

    private int read() throws IOException {
        if (ptr >= len) {
            len = in.read(buffer);
            ptr = 0;

            if (len <= 0) {
                return -1;
            }
        }

        return buffer[ptr++] & 0xFF;
    }

    public int nextInt() throws IOException {
        int c;

        do {
            c = read();
            if (c == -1) {
                throw new IllegalStateException("Unexpected EOF");
            }
        } while (c <= ' ');

        int sign = 1;
        if (c == '-') {
            sign = -1;
            c = read();
        }

        int value = 0;

        while (c >= '0' && c <= '9') {
            value = value * 10 + (c - '0');
            c = read();
        }

        return value * sign;
    }
}
