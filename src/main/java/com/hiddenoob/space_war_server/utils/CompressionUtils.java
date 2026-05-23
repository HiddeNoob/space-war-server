package com.hiddenoob.space_war_server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressionUtils {

    private static final Logger logger =
            LoggerFactory.getLogger(CompressionUtils.class);

    private static final ThreadLocal<Deflater> THREAD_LOCAL_DEFLATER =
            ThreadLocal.withInitial(() -> new Deflater(Deflater.BEST_COMPRESSION, true));

    private static final ThreadLocal<Inflater> THREAD_LOCAL_INFLATER =
            ThreadLocal.withInitial(() -> new Inflater(true));

    public static byte[] compress(byte[] input) {
        if (input == null || input.length == 0) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(0); // Not compressed
            if (input != null) {
                try {
                    outputStream.write(input);
                } catch (IOException e) {
                    logger.error("Error writing to ByteArrayOutputStream for " +
                            "null/empty input: " + e.getMessage(), e);
                }
            }
            return outputStream.toByteArray();
        }

        Deflater deflater = THREAD_LOCAL_DEFLATER.get();
        deflater.reset();
        deflater.setInput(input);
        deflater.finish();

        ByteArrayOutputStream compressedOutputStream =
                new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        try {
            // TODO compressleme bytenı burda koy
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                compressedOutputStream.write(buffer, 0, count);
            }
            compressedOutputStream.close();
        } catch (IOException e) {
            logger.error("Compression failed: " + e.getMessage(), e);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(0); // Not compressed
            try {
                outputStream.write(input);
            } catch (IOException ioException) {
                logger.error("Error writing original data after compression " +
                        "failure: " + ioException.getMessage(), ioException);
            }
            return outputStream.toByteArray();
        }

        byte[] compressedData = compressedOutputStream.toByteArray();

        if (compressedData.length <= 0 || compressedData.length >= input.length) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(0); // Not compressed
            try {
                outputStream.write(input);
            } catch (IOException e) {
                logger.error("Error writing original data when compression " +
                        "did not reduce size: " + e.getMessage(), e);
            }
            return outputStream.toByteArray();
        } else {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(1); // Compressed
            try {
                outputStream.write(compressedData);
            } catch (IOException e) {
                logger.error("Error writing compressed data: " + e.getMessage(), e);
            }
            return outputStream.toByteArray();
        }
    }

    public static byte[] decompress(byte[] input) {
        if (input == null || input.length == 0) {
            return input;
        }

        byte compressionFlag = input[0];
        byte[] data = new byte[input.length - 1];
        System.arraycopy(input, 1, data, 0, input.length - 1);

        if (compressionFlag == 0) { // Not compressed
            return data;
        } else if (compressionFlag == 1) { // Compressed
            Inflater inflater = THREAD_LOCAL_INFLATER.get();
            inflater.reset();
            inflater.setInput(data);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            try {
                while (!inflater.finished()) {
                    int count = inflater.inflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
                outputStream.close();
            } catch (DataFormatException e) {
                logger.error("Decompression failed due to data format error: "
                        + e.getMessage(), e);
                return data; // Return original (compressed) data if
                // decompression fails
            } catch (IOException e) {
                logger.error("Decompression failed due to IO error: " + e.getMessage(), e);
                return data; // Return original (compressed) data if
                // decompression fails
            }

            return outputStream.toByteArray();
        } else {
            logger.warn("Unknown compression flag: " + compressionFlag + ". " +
                    "Returning original input without processing.");
            return input; // Unknown flag, return original input
        }
    }
}