package nuts.lib.manager.data_access_manager.jdbc;

import nuts.lib.manager.data_access_manager.jdbc.db_module.DatabaseQueryModule;
import nuts.lib.manager.data_access_manager.jdbc.db_module.SupportQueryModule;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * JdbcTemplate class mapper class, adds convenience to writing SQL to a limited extent.
 *
 * @since 2024. 06. 21
 */
public class JdbcAccessManager {
    //TODO 조인, 정렬, 페이징 관련 기능 추가 필요

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseQueryModule queryModule;

    public JdbcAccessManager(DataSource dataSource, SupportQueryModule queryModule) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.queryModule = queryModule.getDatabaseQueryModule();
    }

    public JdbcAccessManager(JdbcTemplate jdbcTemplate, SupportQueryModule queryModule) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryModule = queryModule.getDatabaseQueryModule();
    }

    public List<Map<String, Object>> select(String sql, Object... args) {
        return jdbcTemplate.queryForList(sql, new ArgumentPreparedStatementSetter(args));
    }

    public List<Map<String, Object>> select(String tableName) {
        return jdbcTemplate.queryForList("SELECT * FROM ?", tableName);
    }

    public List<Map<String, Object>> select(String tableName, String whereCondition) {
        return jdbcTemplate.queryForList("SELECT * FROM ? WHERE %s".formatted(whereCondition), tableName);
    }

    public <T> List<T> select(String sql, Class<T> elementType) {
        return jdbcTemplate.queryForList(sql, elementType);
    }


    public int insert(String sql, Object... args) {
        return this.update(sql, args);
    }

    public int insert(String tableName, Map<String, Object> data) {
        StringBuilder sql = createDynamicInsert(tableName, data);

        return jdbcTemplate.update(sql.toString(), data.values().toArray());
    }

    public int[] insertBatch(String... sqls) {
        return this.updateBatch(sqls);
    }

    public int[] insertBatch(String tableName, List<Map<String, Object>> dataList) {

        List<String> stringList = dataList.stream().map(e -> createDynamicInsert2(tableName, e).toString()).toList();
        return insertBatch(stringList.toArray(String[]::new));

    }

    public int update(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    public int update(String tableName, Map<String, Object> data, String whereCondition) {
        StringBuilder sql = createDynamicUpdate(tableName, data, whereCondition);

        return jdbcTemplate.update(sql.toString(), data.values().toArray());
    }

    public int[] updateBatch(String... sqls) {
        return jdbcTemplate.batchUpdate(sqls);
    }

    public int upsert(String tableName, String key, Map<String, Object> data) {
        String sql = queryModule.upsertQuery(tableName, key, data);
        return jdbcTemplate.update(sql);
    }

    public int[] upsertBatch(String tableName, String key, List<Map<String, Object>> dataList) {
        List<String> sqlList = dataList.stream().map(e -> queryModule.upsertQuery(tableName, key, e)).toList();
        System.out.println(sqlList.get(0));
        return jdbcTemplate.batchUpdate(sqlList.toArray(String[]::new));

    }

    private StringBuilder createDynamicInsert(String tableName, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");

        for (String column : data.keySet()) {
            sql.append(column).append(", ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(") VALUES (");

        for (int i = 0; i < data.size(); i++) {
            sql.append("?, ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(")");
        return sql;
    }

    private StringBuilder createDynamicUpdate(String tableName, Map<String, Object> data, String whereCondition) {
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        for (String column : data.keySet()) {
            sql.append(column).append(" = ?, ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE ").append(whereCondition);
        return sql;
    }


    private StringBuilder createDynamicInsert2(String tableName, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");

        for (String column : data.keySet()) {
            sql.append(column).append(", ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(") VALUES (");

        for (Object value : data.values()) {
            sql.append(convertToSqlValue(value)).append(", ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(")");
        return sql;
    }

    private String convertToSqlValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof String || value instanceof Character) {
            return "'" + value.toString().replace("'", "''") + "'";
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "TRUE" : "FALSE";
        } else {
            return value.toString();
        }
    }

}