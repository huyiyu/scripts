package nio.prepare.reactor.master;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new Server(9999).start();
    }
}
