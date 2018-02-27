package net.kiranatos.main;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Begin!");
        Thread server = new Thread (new MyServerSocket(5060));
        server.start();
        
        System.out.println("Server started!");    
        
        try {
            
            server.join();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


/*
3.3 Система обмена сообщениями
Виталий Чибриков, из курса Разработка веб сервиса на Java (часть 2) — 3 Многопоточность

Задача:
Написать серверную часть клиент-серверного приложения на сокетах 
(не веб сокетах, а обычных сокетах).
Тестирующее приложение будет имитировать клиентские приложения.
Сервер должен слушать обращения клиентов на localhost:5050 
и отправлять обратно все пришедшие от них сообщения.
То есть, если клиент присылает "Hello!" ему обратно должно быть отправлено "Hello!".

В процессе тестирования к серверу будут одновременно подключаться 10 клиентов. 
Каждый клиент будет отправлять по сообщению каждую миллисекунду в течение 5 секунд.
Сервер должен отвечать всем клиентам одновременно.
Время на обработку сообщений от всех клиентов не более 10 секунд. 
Если сервер отвечает дольше -- увеличьте число потоков для обработки сообщений.

Вы можете решить задачу либо через создание потока на каждый Socket, 
либо использую неблокирующие сокеты из NIO.

Инструкция подготовки к локальной проверке:
Соберите сервер со всеми зависимостями на библиотеки в server.jar
Для этого запустите Maven projects/<Project name>/Plugins/assembly/assembly:single
либо assembly.sh (assembly.bat)

Скопируйте server.jar на уровень src и запустите
java -jar server.jar

В логах консоли вы должны увидеть сообщения о старте сервера.

Инструкция подготовки к автоматической проверке:
Добавьте в лог сообщение "Server started". По появлению в логе этого 
сообщения тестирующая система пойдет, что к вашему серверу можно обращаться.
Соберите server.jar, содержащий все библиотеки.

Во время проверки тестовая система:
запустит ваш сервер,
подождет пока "Server started",
создаст 10 сокетов в 10 потоках и каждым из них обратиться к серверу на localhost:5050
Каждый клиент будет отправлять одно сообщение в миллисекунду на сервер, 
и ждать пока оно вернется обратно.
После отправки 5000 сообщений клиент отправит на сервер "Bue.", по этому 
сигналу сервер должен разорвать соединение с сокетом.
После того как все клиенты отработают, тестирующая система оценит время, 
которое ушло на проверку. Общее время работы должно быть менее 10 секунд.
*/




/*
----
InputStream sin = socket.getInputStream();
DataInputStream in = new DataInputStream(sin);
message = (String) in.readUTF();
----
Я пытался использовать DataInputStream, но (как я понял) данные не прочитаются пока не закроется соединения
Вместо это я использую 
InputStream inputStream = socket.getInputStream();
BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
и потом читаем в бесконечном цикле
line = in.readLine();
------
Идеальный тест: 1) запускаем telnet; 
2) вводим адрес нашего локального сервера; 
3) отправляем на сервер строку текста; 
5) если telnet выведет её сразу же на экран, то всё работает правильно, если нет, значит сервер работает некорректно.
---
while (true) {
                line = in.readLine();
                if("Bue.".equals(line)) break;
                out.println(line);
                out.flush();
            }

Client ﻿(перенос строки обязателен)

out.println("bla bla bla\\n");
out.flush();
line = in.readLine();

System.out.println(line);
----------------
Это конечно не принципиально, но видимо вместо "Bue" предполагалось "Bye" ?
------------
Прошу помощи. Возможно, что-то сталкивался с таким. Делаю через NIO. Открываю ServerSocketChannel в неблокировочном режиме. В потоке ловлю входящие подключения по сокетам. Поймал - стартовал отдельный поток, который слушает только этот сокет и работает с ним. Запускаю в тестовом режиме - все отлично. По телнету открываю несколько подключений к порту, шлю любые сообщения - мне в ответ прилетает то же самое. Отключение сокета по "Bue." - все замечательно. Тесты не проходят. Причем, постоянно с разными формулировками. 
Вот код для чтения из сокета:
private String channelRead() {
    ByteBuffer buffer = ByteBuffer.allocate(256);
    buffer.clear();
    try {
        mSocketChannel.read(buffer);  // ОШИБКА ВЫЛЕТАЕТ НА ЭТОЙ СТРОКЕ
        if (buffer.hasRemaining()) {
            byte[] bytes = buffer.array();
            return new String(bytes, Charset.forName("UTF-8"));
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}

Разобрался самостоятельно. Не выполнялся флип после чтения из сокета и неправильно интерпретировалась строка. Вот правильный код:
private String channelRead() {
    ByteBuffer buffer = ByteBuffer.allocate(256);
    buffer.clear();
    try {
        mSocketChannel.read(buffer);
        if (buffer.hasRemaining()) {
            buffer.flip();
            byte[] bytes = new byte[buffer.limit()];
            buffer.get(bytes, 0, bytes.length);
            return new String(bytes, Charset.forName("UTF-8"));
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
---------
Эммм... Мне проще, наверное, будет проект в архиве сбросить. Да  тут, собственно, и рассказывать особо нечего. Все предельно просто. Заводите какой-нибудь "процессор", который стартанет поток: в потоке создавайте ServerSocketChannel и делайте ему bind к нужному порту. Я ставил серверу неблокировочный режим. Далее просто в цикле проверяйте SocketChannel - именно такой объект вернет метод сервера accept. Если он не null - значит было подключение в этот момент. Тогда создавайте отдельный "процессор" уже для работы с сокетом - и передавайте сам полученный только что сокет в него. Процессор уже в цикле будет читать из сокета, сравнивать со стоп-словом и - если это не "Bue." - писать то же самое в сокет. Иначе тупо отключаем сокет. Все методы с примерами расписаны в руководстве, ссылку на которое я привел. У меня в это проекте 4 класса всего лишь. Кода до безобразия мало.
-------------
вот код :

public class ChatServer{

    private ServerSocket serverSocket;
    private boolean finished;
    public ChatServer(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10000);
        finished = false;
    }
    public void close(){
        finished = true;
    }
    public void start(){
        System.out.println("Server started");
        while(!finished){
            try {
                //System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                //System.out.println("Server started");
                new ChatSocket(serverSocket.accept(),this).start();
            }catch (IOException io){
                io.printStackTrace();
            }
        }

    }
}



public class ChatSocket extends Thread {
    Socket socket;
    ChatServer chatServer;
    public ChatSocket(Socket socket,ChatServer chatServer){
        this.socket = socket;
        this.chatServer = chatServer;
    }
    @Override
    public void run(){
        try{
            //System.out.println("Just connected to "
                    //+ socket.getRemoteSocketAddress());
            DataInputStream in =
                    new DataInputStream(socket.getInputStream());
            String message = in.readUTF();
            System.out.println(message);
            if(!message.equals("Blue.")) {
                DataOutputStream out =
                        new DataOutputStream(socket.getOutputStream());
                out.writeUTF(message);
                out.close();
            }else chatServer.close();
            in.close();
            socket.close();
        }catch (IOException io){
            io.printStackTrace();
        }
    }
}

public class Main {
    public static void main(String[] args){
        //int port = Integer.parseInt(args[0]);
        try
        {
            ChatServer server = new ChatServer(5050);
            server.start();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}

 я написал клиент и все прекрасно работает но тестирующая система только пишет "10" и ничего больше как будьто что-то ждет
 ----------------
 private BufferedReader lin;
private OutputStreamWriter lout;

try {
    lout.write("");
    while (true){
        line = lin.readLine();
        if (line.equals("Bue")){break;}
        System.out.println(line);
        line = line + "\n";
        lout.write(line);
        lout.flush();
    }
	
	Както у вас всё сложно..

try { 
 in = new BufferedReader(new InputStreamReader(s.getInputStream()));
 out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
 while (true) {
  String str = in.readLine();
  if (str.equals("Bue"))
   break;
  out.println(str);
 }
 s.close();
}


Александр Стрижаков
год назад
ссылка
0


@Алексей_Цуркан Да собсна, во многом похоже на ваше) У вас, конечно, короче. Только в OutputStreamWriter нет такой функции, как println(), поэтому приходится самому цеплять окончание строки "\n", что я и делаю. Кстати, я только потом понял, что для корректного перевода строки в Windows нужно выводить "\r\n".

System.out.println(line) - это я сыпал в лог сервера сообщениями, которые принимаю. Отладку делал.
lout.flush() - это очистка буфера, без этой команды бывают проблемы с выводом.

lout.write(""); - а вот без этой команды почему-то не работало с OutputStreamWriter.

До использования какого-то другого Writer'а я тоже дошел на выходных, когда отоспался. Но за 3 дня, которые провисел мой первый вопрос, мне никто не подсказал, что так можно; поэтому я дописывал так.

А потоки ввода-вывода я создаю в конструкторе. Я не знаю, какой из вариантов лучше в данном случае.

Владимир Ахтырский
год назад
ссылка
1

Странно, что без lout.write(""); не сработало, специально делал посимвольную обработку (управляющие символы также переносил в результирующую строку), тест прошел успешно:

InputStreamReader ir;

OutputStreamWriter ow;

do {
    str = "";
    do {
        ch = (char) ir.read();
        str += String.valueOf(ch);
        if (str.equals("Bue.")) break;
    } while (ch != '\n');

    ow.write(str);
    ow.flush();
} while (!str.equals("Bue."));

*/