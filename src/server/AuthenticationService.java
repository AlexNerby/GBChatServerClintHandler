package server;

import java.util.HashSet;
import java.util.Set;

public class AuthenticationService {
    private Set<Client> clients = new HashSet<>();

    public AuthenticationService() {
        clients.add(new Client("l1", "p1", "u1"));
        clients.add(new Client("l2", "p2", "u2"));
        clients.add(new Client("l3", "p3", "u3"));
    }

    public Client findByLoginAndPassword(String login, String password) {
        for (Client c : clients) {
            if (c.getLogin().equals(login) && c.getPassword().equals(password)) {
                return c;
            }
        }
        return null;
    }
//
    static public class Client {
        private String login;
        private String password;
        private String name;

        public Client(String login, String password, String name) {
            this.login = login;
            this.password = password;
            this.name = name;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getName() {
            return name;
        }
    }
}

