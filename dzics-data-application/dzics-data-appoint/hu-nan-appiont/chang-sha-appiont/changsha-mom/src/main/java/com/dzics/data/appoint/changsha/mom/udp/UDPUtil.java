package com.dzics.data.appoint.changsha.mom.udp;

import com.google.common.primitives.Bytes;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author: van
 * @since: 2022-07-06
 */
public class UDPUtil {

    public static boolean sendUdpServer(String ip, int port, String message) {
        DatagramSocket ds = null;
        try {
            byte[] byteStart = "##".getBytes();
            byte[] messageBytes = message.getBytes();
            int length = messageBytes.length + 5;
            byte[] leBytes = UDPUtil.tolh(length);
            byte[] concat = Bytes.concat(leBytes, messageBytes);

            byte sum = UDPUtil.sum(concat, 0, concat.length - 1);
            byte[] sumByte = new byte[1];
            sumByte[0] = sum;
            byte[] bytesEnd = "!!".getBytes();
            byte[] linefeed = {13};
            byte[] concatSendByte = Bytes.concat(byteStart, leBytes, messageBytes, sumByte, bytesEnd, linefeed);
            DatagramPacket dpSend = new DatagramPacket(concatSendByte, concatSendByte.length, InetAddress.getByName(ip), port);
            ds = new DatagramSocket();
            ds.send(dpSend);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }

    public static byte[] tolh(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static byte sum(byte[] buffer, int sset, int eset) {
        int sumInt = 0;
        for (int i = sset; i <= eset; i++) {
            sumInt += (buffer[i] & 0xFF);
        }
        return (byte) ((sumInt + 52) % 127);
    }
}
