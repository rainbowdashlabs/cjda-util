package de.chojo.jdautil.database;

import de.chojo.jdautil.database.builder.QueryBuilder;
import de.chojo.jdautil.database.builder.QueryBuilderFactory;
import de.chojo.jdautil.database.builder.stage.ConfigurationStage;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Base class which can be used for classes which call the database.
 * <p>
 * Provides convinience methods for connection retrieval, logging and a querybuilder.
 * <p>
 * You may use a {@link QueryBuilderFactory} for builder creation.
 */
public class QueryObject {
    private static final Logger log = getLogger(QueryObject.class);
    private final DataSource dataSource;

    public QueryObject(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get a query builder for easy sql execution.
     *
     * @param clazz clazz which should be retrieved. Doesnt matter if you want a list and multiple results or not.
     * @param <T>   type of result
     * @return query builder in a query stage
     */
    protected <T> ConfigurationStage<T> queryBuilder(Class<T> clazz) {
        return QueryBuilder.builder(dataSource, clazz);
    }

    public void logDbError(String message, SQLException e) {
        log.error(DBUtil.prettyException(message, e), e);
    }

    public void logDbError(SQLException e) {
        logDbError("An error occured while executing a query", e);
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
