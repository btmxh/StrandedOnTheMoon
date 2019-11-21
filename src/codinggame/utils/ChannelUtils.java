/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Welcome
 */
public class ChannelUtils {
    public static int readInt(ReadableByteChannel cnl) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        int read = cnl.read(buf);
        buf.position(0);
        if(read == -1)  throw new EOFException("End of File");
        return buf.getInt();
    }
    
    public static ByteBuffer read(ReadableByteChannel cnl, int read) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(read);
        int readAmt = cnl.read(buf);
        buf.position(0);
        if(readAmt < read)  throw new EOFException("End of File");
        return buf;
    }
    
    public static void writeInt(WritableByteChannel cnl, int i) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        buf.putInt(i);
        buf.flip();
        cnl.write(buf);
    }

    public static byte[] read_arr(ReadableByteChannel cnl, int stride) throws IOException {
        ByteBuffer buf = read(cnl, stride);
        return ByteArray.fromBuffer(buf);
    }
}
