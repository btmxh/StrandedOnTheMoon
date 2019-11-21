/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.utils;

import java.nio.ByteBuffer;

/**
 *
 * @author Welcome
 */
public class ByteArray {

    public static void putInt(byte[] arr, int offset, int i) {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        buf.putInt(i);
        buf.position(0);
        put(arr, offset, buf);
    }
    
    public static void put(byte[] arr, int this_offset, int arr_offset, byte[] arr2, int length) {
        System.arraycopy(arr2, this_offset, arr, arr_offset, length);
    }
    
    public static void put(byte[] arr, int this_offset, ByteBuffer buf) {
        if(buf.hasArray()) {
           byte[] arr2 = buf.array();
           put(arr, this_offset, buf.position(), arr2, buf.remaining());
        } else {
            while(true) {
                arr[this_offset++] = buf.get();
            }
        }
    }

    public static int getInt(byte[] arr, int offset) {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        buf.put(arr, offset, Integer.BYTES);
        buf.flip();
        return buf.getInt();
    }

    public static ByteBuffer toBuffer(byte[] arr) {
        ByteBuffer buf = ByteBuffer.allocate(arr.length);
        buf.put(arr);
        buf.flip();
        return buf;
    }
    
    public static byte[] fromBuffer(ByteBuffer buf) {
        byte[] arr = new byte[buf.remaining()];
        put(arr, 0, buf);
        return arr;
    }

    public static void put(byte[] data, int offset, byte b) {
        data[offset] = b;
    }
}
