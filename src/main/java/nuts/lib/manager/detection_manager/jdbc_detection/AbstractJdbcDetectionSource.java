package nuts.lib.manager.detection_manager.jdbc_detection;

import nuts.lib.manager.detection_manager.DetectionSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public abstract class AbstractJdbcDetectionSource implements DetectionSource<Map<String, Object>> {

    private final JdbcTemplate jdbcTemplate;

    public AbstractJdbcDetectionSource(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AbstractJdbcDetectionSource fetchSize(int fetchSize) {
        this.jdbcTemplate.setFetchSize(fetchSize);
        return this;
    }

    @Override
    public List<Map<String, Object>> poll() {
        System.out.println(getDetectionQuery());
        return jdbcTemplate.queryForList(getDetectionQuery());
    }

    /**
     * The return of the getDetectionQuery method should return the SQL Select query string,
     * and the query action determines that it has detected the retrieved data.
     *
     * @since 2024. 06. 19
     */
    protected abstract String getDetectionQuery();

}