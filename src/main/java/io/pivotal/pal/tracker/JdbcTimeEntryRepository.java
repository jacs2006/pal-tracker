package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;


    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        String sql = "Insert INTO time_entries(project_id, user_id, date, hours) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setLong(1, timeEntry.getProjectId());
                    ps.setLong(2, timeEntry.getUserId());
                    ps.setDate(3, Date.valueOf(timeEntry.getDate()));
                    ps.setInt(4, timeEntry.getHours());
                    return ps;
                }, keyHolder);

        Number key = keyHolder.getKey();

        return find(key.longValue());
    }

    @Override
    public TimeEntry find(long id) {
        List<TimeEntry> customers = jdbcTemplate.query("select * from time_entries where id =?",
                new Object[]{id}, (resultSet, i) -> {
                    return new TimeEntry(
                            resultSet.getLong("id"),
                            resultSet.getLong("project_id"),
                            resultSet.getLong("user_id"),
                            resultSet.getDate("date").toLocalDate(),
                            resultSet.getInt("hours"));
                });

        if (customers.size() == 1) {
            return customers.get(0);
        }
        return null;
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("select * from time_entries",
                (resultSet, i) -> {
                    return new TimeEntry(
                            resultSet.getLong("id"),
                            resultSet.getLong("project_id"),
                            resultSet.getLong("user_id"),
                            resultSet.getDate("date").toLocalDate(),
                            resultSet.getInt("hours"));
                });
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String sql = "update time_entries set project_id = ?, user_id = ?, date=?,hours=?";

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setLong(1, timeEntry.getProjectId());
                    ps.setLong(2, timeEntry.getUserId());
                    ps.setDate(3, Date.valueOf(timeEntry.getDate()));
                    ps.setInt(4, timeEntry.getHours());
                    return ps;
                });

        return find(id);
    }

    @Override
    public void delete(long id) {
        String sql = "delete from time_entries where id = ?";

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setLong(1, id);
                    return ps;
                });
    }
}
