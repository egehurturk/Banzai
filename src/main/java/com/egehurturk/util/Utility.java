package com.egehurturk.util;

import com.egehurturk.exceptions.FileSizeOverflowException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utility {

    public static long MAX_FILE_LENGTH = 20000000000L;

    /**
     * Reads file from given {@link File} object. This method uses "old"
     * {@link java.io} style, where the source is coming from {@link InputStream}
     * and {@link OutputStream}, more specifically {@link FileInputStream}.
     * A buffer ({@code byte[] array} is used to buffer file contents from {@link InputStream}
     * This method closes all streams
     *
     * @param file                              - {@link File} object that is the file to be read
     * @return                                  - {@code byte[] array} buffer
     * @throws IOException                      - IO operation error
     * @throws FileSizeOverflowException        - If file length is very long
     */
    public static byte[] readFile_IO(File file) throws IOException, FileSizeOverflowException {
        if (file.length() > MAX_FILE_LENGTH) {
            throw new com.egehurturk.exceptions.FileSizeOverflowException("File " +
                    file.getName() + "is too big to handle. Server accepts " +
                    MAX_FILE_LENGTH + " file size, the requested file is " +
                    file.length() + " bytes.");
        }

        byte[] _buffer = new byte[(int) file.length()];
        InputStream in = null;

        try {
            in = new FileInputStream(file);
            if ( in.read(_buffer) == -1 ) {
                throw new IOException(
                        "EOF reached while reading file. File is probably empty");
            }
        }
        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch (IOException err) {
                err.printStackTrace();
            }
        }
        return _buffer;
    }

    /**
     * Reads file from given {@link File} object. This method uses "new"
     * {@link java.nio} style, where the source is coming from {@link java.nio.channels.Channel}
     * more specifically {@link FileChannel}.
     * A buffer ({@link ByteBuffer} is used to buffer file contents from {@link FileChannel}
     * This method closes all channels and returns {@link ByteBuffer} as {@code byte[]} array
     *
     * @param file                              - {@link File} object that is the file to be read
     * @return                                  - {@code byte[] array} buffer (from {@link ByteBuffer})
     * @throws IOException                      - IO operation error
     */
    public static byte[] readFile_NIO(File file) throws IOException {
        RandomAccessFile rFile = new RandomAccessFile(file.getName(), "rw");
        FileChannel inChannel = rFile.getChannel();

        ByteBuffer _buffer = ByteBuffer.allocate(1024);

        // read from buffer to channel
        int bytesRead = inChannel.read(_buffer);

        while (bytesRead != -1) {
            // flip buffer to read
            _buffer.flip();

            while (_buffer.hasRemaining()) {
                // read from buffer and store it in byte
                byte b = _buffer.get();
            }
            // clear for overflow
            _buffer.clear();

            // read the buffer again
            bytesRead = inChannel.read(_buffer);
        }
        // close all channels
        inChannel.close();
        rFile.close();
        return _buffer.array();
    }

    /**
     * This method uses a direct approach and uses the "new"
     * style, {@link java.nio}.
     * @param file              - {@link File} to be read
     * @return                  - {@code byte[]} array, buffer
     * @throws IOException      - IO operation error
     */
    public static byte[] readFile_NIO_DIRECT(File file) throws IOException {
        byte[] buffer = Files.readAllBytes(file.toPath());
        return buffer;
    }

    /**
     * A "fast" approach to read from a {@link File} with {@link MappedByteBuffer}
     * However, in my experiments, this resulted in the slowest performance, whereas
     * {@link #readFile_IO(File)} was the fastest
     *
     * @param file               - {@link File} to be read
     * @return                   - {@link MappedByteBuffer} object
     * @throws IOException       - IO operation error
     */
    public static MappedByteBuffer read_NIO_MAP(File file) throws IOException {
        RandomAccessFile rFile = new RandomAccessFile(file.getName(), "rw");
        FileChannel inChannel = rFile.getChannel();

        // create a new mappedbyte buffer
        MappedByteBuffer buffer = inChannel.map(
                FileChannel.MapMode.READ_ONLY, 0, inChannel.size());

        // load the buffer
        buffer.load();
        for (int i = 0; i < buffer.limit(); i++)
        {
            // get from buffer
            byte a = buffer.get();
        }
        buffer.clear();

        // close all the channels
        inChannel.close();
        rFile.close();
        return buffer;
    }

    public static String enumStatusToString(String status) {
        String returnedVal = "";
        switch (status) {
            case "Continue":
                returnedVal =  "_100_CONTINUE";
                break;
            case "OK":
                returnedVal =  "_200_OK";
                break;
            case "Bad Request":
                returnedVal =  "_400_BAD_REQUEST";
                break;
            case "Forbidden":
                returnedVal =  "_403_FORBIDDEN";
                break;
            case "Not Found":
                returnedVal =  "_404_NOT_FOUND";
                break;
            case "Method Not Allowed":
                returnedVal =  "_405_METHOD_NOT_ALLOWED";
                break;
            case "Internal Server Error":
                returnedVal =  "_500_INTERNAL_ERROR";
                break;
            case "Not Implemented":
                returnedVal =  "_501_NOT_IMPLEMENTED";
                break;
        }
        return returnedVal;
    }

    public static boolean isDirectory(String dirPath) {
        return Files.isDirectory(Paths.get(dirPath));
    }

    public static String removeLastChars(String str, int chars) {
        return str.substring(0, str.length() - chars);
    }
}
