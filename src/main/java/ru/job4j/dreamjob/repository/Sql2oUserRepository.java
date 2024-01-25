package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.User;

import java.util.Collection;
import java.util.Optional;
@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO users(email, name, password)
                    VALUES(:email, :name, :password)
                    """;
            var query = connection.createQuery(sql, true)
            .addParameter("email", user.getEmail())
            .addParameter("name", user.getName())
            .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            return Optional.of(user);
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        Optional<User> user = Optional.empty();
        try (var connection = sql2o.open()) {
            var sql = """
                    SELECT * FROM users where email = :email and password = :password
                    """;
            var query = connection.createQuery(sql);
            query.addParameter("email", email);
            query.addParameter("password", password);
            user = query.executeAndFetch(User.class).stream().findFirst();
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users");
            return query.executeAndFetch(User.class);
        }
    }

}
