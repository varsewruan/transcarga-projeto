package br.com.transcarga.persistencia;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public final class JpaUtil {

    private static final EntityManagerFactory EMF =
        Persistence.createEntityManagerFactory("TransCargaPU", buildOverrides());

    private JpaUtil() {}

    public static EntityManagerFactory getEmf() {
        return EMF;
    }

    private static Map<String, String> buildOverrides() {
        Map<String, String> props = new HashMap<>();
        String host = require("DB_HOST");
        String port = require("DB_PORT");
        String name = require("DB_NAME");
        String user = require("DB_USER");
        String pass = require("DB_PASSWORD");
        props.put("jakarta.persistence.jdbc.url",
            "jdbc:mariadb://" + host + ":" + port + "/" + name + "?serverTimezone=UTC");
        props.put("jakarta.persistence.jdbc.user", user);
        props.put("jakarta.persistence.jdbc.password", pass);
        return props;
    }

    private static String require(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(
                "Variável de ambiente " + key + " não definida. Ver SETUP.md.");
        }
        return v;
    }
}
