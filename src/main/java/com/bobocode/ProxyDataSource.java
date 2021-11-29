package com.bobocode;

import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxyDataSource extends PGSimpleDataSource {

    private final int DEFAULT_POOL_SIZE = 10;
    private Queue<Connection> connectionPool = new ConcurrentLinkedQueue<>();

    public ProxyDataSource(String url, String userName, String password) {
        super();
        init(url, userName, password);
    }

    @Override
    public Connection getConnection() {
        return connectionPool.poll();
    }

    @SneakyThrows
    private void init(String url, String userName, String password) {
        setURL(url);
        setUser(userName);
        setPassword(password);

        for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
            try (Connection connection = super.getConnection()) {
                ProxyConnection proxyConnection = new ProxyConnection(connection, connectionPool);
                connectionPool.add(proxyConnection);
            }
        }
    }

}
