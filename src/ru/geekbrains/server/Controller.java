package ru.geekbrains.server;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract interface Controller <E, K> {
    public abstract List<E> getAll();
    public abstract E getEntityById(K id);
    public abstract E update(E entity);
    public abstract boolean delete(K id);
    public abstract boolean create(E entity);

    public default void closePrepareStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
