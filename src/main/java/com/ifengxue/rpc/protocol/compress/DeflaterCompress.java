package com.ifengxue.rpc.protocol.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * deflater 压缩/解压缩
 * Created by LiuKeFeng on 2017-04-20.
 */
public class DeflaterCompress implements ICompress {
    @Override
    public byte[] compress(byte[] buffer) {
        Deflater deflater = new Deflater();
        deflater.reset();
        deflater.setInput(buffer);
        deflater.finish();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(buffer.length);
        byte[] buf = new byte[1024];
        try {
            while (!deflater.finished()) {
                int len = deflater.deflate(buf);
                arrayOutputStream.write(buf, 0, len);
            }
            return arrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            deflater.end();
            try {
                arrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public byte[] decompress(byte[] buffer) {
        Inflater inflater = new Inflater();
        inflater.reset();
        inflater.setInput(buffer);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(buffer.length);
        byte[] buf = new byte[1024];
        try {
            while (!inflater.finished()) {
                int len = inflater.inflate(buf);
                arrayOutputStream.write(buf, 0, len);
            }
            return arrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            inflater.end();
            try {
                arrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
