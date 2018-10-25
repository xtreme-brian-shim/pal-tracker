package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private final JdbcTemplate template;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                    "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );

            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

            return statement;
        }, keyHolder);
        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        String sql = "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?";
        return template.query(sql, new Object[]{id}, resultExtractor);
    }

    private final RowMapper<TimeEntry> rowMapper = (rs, rowNumber) -> new TimeEntry(rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours"));

    private final ResultSetExtractor<TimeEntry> resultExtractor = (rs) ->
        rs.next() ? rowMapper.mapRow(rs, 1) : null;

    @Override
    public List<TimeEntry> list() {
        return template.query("SELECT * FROM time_entries", rowMapper);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {

        String sql = "UPDATE time_entries "+
                "SET project_id = ?, user_id = ?, date = ?, hours = ? "+
                "WHERE id = ?";

        template.update(sql,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);
        return find(id);
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE from time_entries "+
                "WHERE id = "+id;
        template.execute(sql);
    }
}
