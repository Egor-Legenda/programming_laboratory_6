package org.example.server;



import org.example.common.data.City;
import org.example.common.request.CommandsRequest;
import org.example.common.response.CommandsResponse;
import org.example.server.base.ConsoleReaderThread;
import org.example.server.base.Controller;
import org.example.server.data.Coordinates;
import org.example.server.exceptions.IOException;
import org.example.server.data.*;
import javax.swing.text.html.HTMLDocument;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;


public class Server {
    private Selector selector;
    private InetSocketAddress address;
    private Set<SocketChannel> session;
    public static final Logger logger = Logger.getGlobal();

    public Server(String host, int port) {
        this.address = new InetSocketAddress(host, port);
        this.session = new HashSet<SocketChannel>();

    }

    public void start() throws IOException, java.io.IOException, ClassNotFoundException {
        Thread readerThread = new Thread(new ConsoleReaderThread());
        readerThread.start();
        Controller controller =new Controller();
        controller.createCommand();
        this.selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(address);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        logger.info("Сервер запущен, ожидает клиентов");
        while (true) {
            this.selector.select();
            Iterator keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();
                keys.remove();
                if (!key.isValid()) continue;
                if (key.isAcceptable()) accept(key);
                if (key.isReadable()) read(key);
            }
        }

    }

    private void accept(SelectionKey key) throws IOException, java.io.IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_READ);
        this.session.add(channel);
        logger.info("Server: new client " + channel.socket().getRemoteSocketAddress());

    }


    private void read(SelectionKey key) throws IOException, java.io.IOException, ClassNotFoundException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(10000);

        int numRead;
        try {
            numRead = channel.read(byteBuffer);
        }catch (SocketException e){
            this.session.remove(channel);
            logger.info("Вышел клиент: " + channel.socket().getRemoteSocketAddress());
            channel.close();
            key.cancel();
            return;
        }

        byteBuffer.flip();
        ByteArrayInputStream bis = new ByteArrayInputStream(byteBuffer.array(), 0, numRead);

        ObjectInputStream ois = new ObjectInputStream(bis);
        Controller controller = new Controller();
        if (numRead == -1) {
            logger.info("Клиент отключился: " + channel.socket().getRemoteSocketAddress());
            this.session.remove(channel);
            channel.close();
            key.cancel();
            return;
        }
        CommandsRequest city = (CommandsRequest) ois.readObject();
        if (city.getLine()!=null && city.getCity()!=null){
            logger.info("Сервер получил от клиента <"+channel.socket().getRemoteSocketAddress()+"> команду: "+city.getName()+", её аргумент: "+city.getLine()+", а также город: "+city.getCity().toString());


        } else if (city.getLine()!=null) {
            logger.info("Сервер получил от клиента <"+channel.socket().getRemoteSocketAddress()+"> команду: "+city.getName()+", её аргумент: "+city.getLine());

        } else if (city.getCity()!=null) {
            logger.info("Сервер получил от клиента <"+channel.socket().getRemoteSocketAddress()+"> команду: "+city.getName()+", а также город: "+city.getCity().toString());


        }else {
            logger.info("Сервер получил от клиента <"+channel.socket().getRemoteSocketAddress()+"> команду: "+city.getName());

        }
        ois.close();
        bis.close();






        org.example.server.data.City city_new = null;

        try {
            Coordinates coordinates = new Coordinates(city.getCity().getCoordinates().getX(), city.getCity().getCoordinates().getY());
            city_new = new org.example.server.data.City(city.getCity().getId(), city.getCity().getName(), coordinates, LocalDateTime.now(), city.getCity().getArea(), city.getCity().getPopulation(), city.getCity().getMetersAboveSeaLevel(), city.getCity().isCapital(), city.getCity().getPopulationDensity(), Climate.valueOf(city.getCity().getClimate().toString()), new Human(LocalDateTime.now()));
        } catch (NullPointerException e) {

        }

        String result = controller.run(city.getName(), city_new, city.line);
        CommandsResponse commandsResponse = new CommandsResponse(city.getName(), null, result);

        try {




            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            ObjectOutputStream oos1 = new ObjectOutputStream(baos1);
            oos1.writeObject(commandsResponse);
            oos1.flush();
            byte[] responseBytes1 = baos1.toByteArray();
            int responseSize = responseBytes1.length;

            // Отправка размера данных
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
            sizeBuffer.putInt(responseSize);
            sizeBuffer.flip();
            channel.write(sizeBuffer);
            oos1.close();
            baos1.close();


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(commandsResponse);
            oos.flush();
            byte[] responseBytes = baos.toByteArray();
            ByteBuffer responseBuffer = ByteBuffer.wrap(responseBytes);
            channel.write(responseBuffer);
        } catch (IOException e) {
            logger.info("Ошибка отправки сообщения клиенту: "+e.getMessage());

        }
        logger.info("Сервер отправил клиенту <"+channel.socket().getRemoteSocketAddress()+"> сообщение: "+commandsResponse.getResult());

    }
    public static ByteBuffer toBuffer(CommandsResponse object) throws  java.io.IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.flush();
        byte[] data = baos.toByteArray();
        int length = data.length + 4;
        ByteBuffer writeBuffer = ByteBuffer.allocate(length);
        writeBuffer.putInt(data.length);
        writeBuffer.put(data);
        return writeBuffer;
    }
}



