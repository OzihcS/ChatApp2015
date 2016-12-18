import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
CLASS THAT IS USED FOR ALL DATA SENDING AND RECEIVING OPERATIONS
 */

public class Connection {
    private PrintWriter printer;
    private Scanner scanner;
    private Socket socket;

    public Connection(Socket socket) {
        try {
            this.socket = socket;
            this.printer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            this.scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }

    // Sending commands
    public void send(String line) {
        this.printer.print(line + "\n");
        this.printer.flush();
    }

    // Sending messages
    public void sendMsg(String line) {
        this.printer.print("Message" + "\n");
        this.printer.flush();
        this.printer.print(line + "\n");
        this.printer.flush();
    }

    // Sending protocol greetings
    public void sendNick(String ver, String nick, boolean busy) {
        StringBuilder line = new StringBuilder();
        line.append(ver + " user " + nick);
        if (busy)
            line.append(" busy");
        this.printer.print(line + "\n");
        this.printer.flush();
    }

    // Receiving command,messages
    public String receive() {
        try {
            return this.scanner.nextLine();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public SocketAddress getSocketAddress() {
        return this.socket.getRemoteSocketAddress();
    }

    //Sending file
    public boolean sendFiles() {
        boolean isSent = false;
        JFileChooser fileChooser = new JFileChooser();
        int openedFile = fileChooser.showOpenDialog(null);
        if (openedFile == JFileChooser.APPROVE_OPTION) {
            File sendFile = fileChooser.getSelectedFile();
            this.printer.print("File" + "\n");
            this.printer.flush();
            this.printer.print(sendFile.getName() + "\n");
            this.printer.flush();
            try (ServerSocket ss = new ServerSocket(Constants.FILE_PORT)) {
                Socket fileSocket = ss.accept();
                Scanner scanner1 = new Scanner(fileSocket.getInputStream());
                String checkFile = scanner1.nextLine();
                if (checkFile.equals(sendFile.getName())) {
                    try {
                        DataOutputStream dos = new DataOutputStream(fileSocket.getOutputStream());
                        FileInputStream fis = new FileInputStream(sendFile);
                        byte[] bytes = new byte[64 * 1024];
                        int size;
                        while ((size = fis.read(bytes)) != -1) {
                            dos.write(bytes, 0, size);
                            dos.flush();
                        }
                        dos.close();
                        fis.close();
                        isSent = true;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    fileSocket.close();
                    ss.close();
                } else {
                    fileSocket.close();
                    ss.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isSent;
    }

    //Receiving command,file
    public boolean receiveFile() {
        boolean isReceived = false;
        String fileName = scanner.nextLine();
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String[] SocketAddress = String.valueOf(socket.getRemoteSocketAddress()).split("/");
            try (Socket socket1 = new Socket(SocketAddress[1].split(":")[0], Constants.FILE_PORT)) {
                PrintWriter printWriter = new PrintWriter(socket1.getOutputStream());
                printWriter.print(fileName + "\n");
                printWriter.flush();
                try {
                    DataInputStream dis = new DataInputStream(socket1.getInputStream());
                    FileOutputStream fos = new FileOutputStream(fileChooser.getSelectedFile());
                    byte[] bytes = new byte[64 * 1024];
                    int size;
                    while ((size = dis.read(bytes)) != -1) {
                        fos.write(bytes, 0, size);
                        fos.flush();
                    }
                    dis.close();
                    fos.close();
                    socket1.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return isReceived;
    }
}