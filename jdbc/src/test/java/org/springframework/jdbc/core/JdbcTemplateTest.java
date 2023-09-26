package org.springframework.jdbc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.test_supporter.DataSourceConfig;
import org.springframework.jdbc.core.test_supporter.DatabasePopulatorUtils;
import org.springframework.jdbc.core.test_supporter.User;
import org.springframework.jdbc.core.test_supporter.UserDao;

class JdbcTemplateTest {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceConfig.getInstance());
    private final UserDao userDao = new UserDao(DataSourceConfig.getInstance());

    @BeforeEach
    void setUp() {
        DatabasePopulatorUtils.execute(DataSourceConfig.getInstance());
    }

    @Test
    @DisplayName("execute Query로 읽는 쿼리를 실행할 수 있다.")
    void executeQuery() {
        final User user = new User("hong-sile", "hong", "hong@teco.com");
        userDao.insert(user);
        final String sql = "select id, account, password, email from users where id = ?";
        final Long id = 1L;

        final User actual = jdbcTemplate.executeQuery(sql, (rs) ->
                new User(
                    rs.getLong(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4)
                )
            , id);

        assertAll(
            () -> assertThat(id)
                .isEqualTo(actual.getId()),
            () -> assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user)
        );
    }

    @Test
    @DisplayName("execute로 쓰는 쿼리를 실행할 수 있다.")
    void execute() {
        final User user = new User("hong-sile", "hong", "hong@teco.com");
        final String sql = "insert into users (account, password, email) values (?, ?, ?)";

        final Long id = jdbcTemplate.executeUpdate(
            sql,
            user.getAccount(), user.getPassword(), user.getEmail()
        );

        final User actual = userDao.findById(id);

        assertAll(
            () -> assertThat(id)
                .isEqualTo(actual.getId()),
            () -> assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user)
        );
    }
}
