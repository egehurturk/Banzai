package com.egehurturk.util;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
A utility class that is used in compressing HTTP resources. This class contains
 various methods for compressing objects.
 */
public final class Compressor {

    /** Compress the given body with the GZIP (GNU zip) compression algorithm.
     * @param body byte array to be compressed
     * @return compressed byte array
     */
    public static byte[] compress_GZIP(byte[] body) throws IOException {
        if (body == null || body.length == 0)
            throw new IllegalArgumentException("Body size cannot be 0");

        ByteArrayOutputStream ba_OS = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip_OS = new GZIPOutputStream(ba_OS)) {
            gzip_OS.write(body);
        }
        return ba_OS.toByteArray();
    }

    public static String decompress(final byte[] compressed) throws IOException {
        final StringBuilder outStr = new StringBuilder();
        if ((compressed == null) || (compressed.length == 0)) {
            return "";
        }
        if (isCompressed(compressed)) {
            final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                outStr.append(line);
            }
        } else {
            outStr.append(compressed);
        }
        return outStr.toString();
    }

    public static boolean isCompressed(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

}
