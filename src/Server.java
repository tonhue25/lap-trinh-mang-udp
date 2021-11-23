
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author tonth
 */
public class Server {

    public static InetAddress inet;
    public static DatagramSocket socket;
    public static String ketQua = "";
    public static int port = 8888;
    private static final int PIECES_OF_FILE_SIZE = 1024 * 32;

    public static void main(String[] args) throws UnknownHostException, SocketException, IOException {
        inet = InetAddress.getByName("localhost");
        socket = new DatagramSocket(port, inet);
        System.out.println("Server đang chạy....");
        byte[] dataPacket1;
        byte[] dataPacket2;
        byte[] dataPacket3;
        DatagramPacket packet1;
        DatagramPacket packet2;
        DatagramPacket packet3;
        DatagramPacket output;

        String line;

        OUTER:
        while (true) {
            dataPacket1 = new byte[1024];
            packet1 = new DatagramPacket(dataPacket1, dataPacket1.length);
            socket.receive(packet1);
            line = new String(packet1.getData(), 0, packet1.getLength());
            switch (line) {
                case "bai1": {
                    dataPacket1 = new byte[1024];
                    packet1 = new DatagramPacket(dataPacket1, dataPacket1.length);
                    socket.receive(packet1);
                    String dir1 = new String(packet1.getData(), 0, packet1.getLength());

                    dataPacket3 = new byte[1024];
                    packet3 = new DatagramPacket(dataPacket3, dataPacket3.length);
                    socket.receive(packet3);
                    String dir3 = new String(packet3.getData(), 0, packet3.getLength());
                    
                    dataPacket2 = new byte[1024];
                    packet2 = new DatagramPacket(dataPacket2, dataPacket2.length);
                    socket.receive(packet2);
                    String dir2 = new String(packet2.getData(), 0, packet2.getLength());

                    if (dir2.contains("File not found!")) {
                        ketQua = "File not found!";
                    } else {
                        ketQua = receiveFile(dir3);
                    }
                    output = new DatagramPacket(ketQua.getBytes(), ketQua.getBytes().length, packet1.getAddress(), packet1.getPort());
                    socket.send(output);
                    break;
                }
                case "bai2": {
                    ketQua = "";
                    dataPacket1 = new byte[1024];
                    packet1 = new DatagramPacket(dataPacket1, dataPacket1.length);
                    socket.receive(packet1);
                    String dir1 = new String(packet1.getData(), 0, packet1.getLength());

                    ketQua = mailClient(dir1);
                    output = new DatagramPacket(ketQua.getBytes(), ketQua.getBytes().length, packet1.getAddress(), packet1.getPort());
                    socket.send(output);
                    break;
                }
            }
        }
    }

    public static String receiveFile(String fileName) {
        byte[] receiveData = new byte[PIECES_OF_FILE_SIZE];
        DatagramPacket receivePacket;
        try {
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            InetAddress inetAddress = receivePacket.getAddress();
            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);
            FileInfo fileInfo = (FileInfo) ois.readObject();
            // show file info
            if (fileInfo != null) {
                File fileReceive = new File(fileInfo.getDestinationDirectory()
                        + fileName);
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(fileReceive));
                // write pieces of file
                for (int i = 0; i < (fileInfo.getPiecesOfFile() - 1); i++) {
                    receivePacket = new DatagramPacket(receiveData, receiveData.length,
                            inetAddress, port);
                    socket.receive(receivePacket);
                    bos.write(receiveData, 0, PIECES_OF_FILE_SIZE);
                }
                // write last bytes of file
                receivePacket = new DatagramPacket(receiveData, receiveData.length,
                        inetAddress, port);
                socket.receive(receivePacket);
                bos.write(receiveData, 0, fileInfo.getLastByteLength());
                bos.flush();
                ketQua = "file đã di chuyển thành công!";
                bos.close();
            }
            if (fileInfo == null) {
                ketQua = "File không tồn tại!!";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ketQua;
    }

    public static String mailClient(String str) {
        ketQua = str.replaceAll("\\s", "");
        ketQua = ketQua.toLowerCase();
        ketQua = ketQua + "@ptithcm.edu.vn";
        return ketQua;
    }
}
