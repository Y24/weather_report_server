package cn.org.y24;

import cn.org.y24.entity.AccountEntity;
import cn.org.y24.entity.QueryHistoryEntity;
import cn.org.y24.enums.AccountDBActionType;
import cn.org.y24.enums.DataDBActionType;
import cn.org.y24.enums.RequestMethod;
import cn.org.y24.exceptions.UnknownOptionException;
import cn.org.y24.interfaces.BaseDBManager;
import cn.org.y24.actions.AccountDBAction;
import cn.org.y24.actions.DataDBAction;
import cn.org.y24.manager.AccountDBManager;
import cn.org.y24.manager.DataDBManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

public class HandlerThread implements Runnable {
    private final BaseDBManager<AccountDBAction> accountDBManager;
    private final BaseDBManager<DataDBAction> dataDBManager;
    private final Socket socket;

    public HandlerThread(Connection rootConnection, Socket socket) {
        this.socket = socket;
        accountDBManager = new AccountDBManager(rootConnection);
        dataDBManager = new DataDBManager(rootConnection);
    }

    Map<String, String> getOptions(Scanner scanner, List<String> names) throws UnknownOptionException {
        Map<String, String> map = new HashMap<>();
        String line;
        List<String> copy = new ArrayList<>(List.copyOf(names));

        do {
            if (copy.isEmpty())
                return map;
            line = scanner.nextLine();
            if (line == null)
                break;
            String[] s = line.split(": ", 2);
            if (copy.contains(s[0])) {
                map.put(s[0], s[1]);
                copy.remove(s[0]);
            }
        }
        while (true);
        throw new UnknownOptionException();
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            final RequestMethod requestMethod = RequestMethod.valueOf(in.nextLine().split(" ")[1].substring(1));
            switch (requestMethod) {
                case login, logout, register, dispose -> {
                    final Map<String, String> options;
                    try {
                        options = getOptions(in, List.of("name", "password"));
                        // standard response head.
                        out.println("HTTP/1.1 200 OK \r\n");
                        out.println("OK");
                        if (accountDBManager.execute(new AccountDBAction(
                                new AccountEntity(options.get("name"), options.get("password"))
                                , AccountDBActionType.valueOf(requestMethod.toString())))) {
                            out.println("succeed");
                        } else out.println("fail");
                    } catch (UnknownOptionException e) {
                        e.printStackTrace();
                        out.println("HTTP/1.1 200 OK \r\n");
                        out.println("No Access token found.");
                    }
                    out.flush();
                    out.close();
                }
                case push -> {
                    final Map<String, String> options;
                    out.println("HTTP/1.1 200 OK \r\n");
                    try {
                        options = getOptions(in, List.of("name", "password", "count"));
                        // check access token.
                        if (!accountDBManager.execute(new AccountDBAction(
                                new AccountEntity(options.get("name"), options.get("password"))
                                , AccountDBActionType.login))) {
                            out.println("NOK");
                        } else {
                            final int count = Integer.parseInt(options.get("count"));
                            List<String> content = new ArrayList<>(count);
                            for (int i = 0; i < count; i++) {
                                content.add("content" + i);
                            }
                            final List<QueryHistoryEntity> data = getOptions(in, content).values().stream().map(QueryHistoryEntity::new).collect(Collectors.toList());

                            dataDBManager.execute(new DataDBAction(new AccountEntity(options.get("name"), options.get("password")),
                                    DataDBActionType.push, data
                            ));
                            out.println("OK");
                        }
                    } catch (UnknownOptionException e) {
                        e.printStackTrace();
                        out.println("NOK");
                    }
                    out.flush();
                    out.close();
                }
                case fetch -> {
                    final Map<String, String> options;
                    out.println("HTTP/1.1 200 OK \r\n");
                    try {
                        options = getOptions(in, List.of("name", "password"));
                        // check access token.
                        if (!accountDBManager.execute(new AccountDBAction(
                                new AccountEntity(options.get("name"), options.get("password"))
                                , AccountDBActionType.login))) {
                            out.println("NOK");
                        } else {
                            final DataDBAction dbAction = new DataDBAction(new AccountEntity(options.get("name"), options.get("password")), DataDBActionType.fetch);
                            dataDBManager.execute(dbAction);
                            out.println("OK");
                            dbAction.getData().forEach(queryHistoryEntity ->
                                    out.println(queryHistoryEntity.toString()));
                        }
                    } catch (UnknownOptionException e) {
                        e.printStackTrace();
                        out.println("NOK");
                    }
                    out.flush();
                    out.close();
                }
                default -> throw new IllegalStateException("Unexpected value: " + requestMethod);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
