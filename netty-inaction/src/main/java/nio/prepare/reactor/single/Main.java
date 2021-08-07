package nio.prepare.reactor.single;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new Server(8888).start();
    }
}
