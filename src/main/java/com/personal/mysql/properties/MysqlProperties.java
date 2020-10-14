/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.properties;

import com.personal.mysql.elect.AbstractDataSourceElector;
import com.personal.mysql.elect.DefaultDataSourceElector;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * @author sunpeikai
 * @version MysqlProperties, v0.1 2020/10/12 16:49
 * @description
 */
@ConfigurationProperties(prefix = "mysql")
public class MysqlProperties {
    private boolean enable;
    private Class<? extends DataSource> type;
    private Class<? extends AbstractDataSourceElector> elector = DefaultDataSourceElector.class;
    private String aopExpression;
    private String mapperLocations;
    private List<String> queryMethodPrefixes = Arrays.asList("select", "query", "get", "find", "list", "count", "search", "get", "check", "export");
    private List<DataSourceProperties> write;
    private List<DataSourceProperties> read;

    public static class DataSourceProperties{
        private boolean master;
        private String url;
        private String username;
        private String password;
        private String driverClassName;

        public boolean isMaster() {
            return master;
        }

        public void setMaster(boolean master) {
            this.master = master;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Class<? extends DataSource> getType() {
        return type;
    }

    public void setType(Class<? extends DataSource> type) {
        this.type = type;
    }

    public Class<? extends AbstractDataSourceElector> getElector() {
        return elector;
    }

    public void setElector(Class<? extends AbstractDataSourceElector> elector) {
        this.elector = elector;
    }

    public String getAopExpression() {
        return aopExpression;
    }

    public void setAopExpression(String aopExpression) {
        this.aopExpression = aopExpression;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public List<String> getQueryMethodPrefixes() {
        return queryMethodPrefixes;
    }

    public void setQueryMethodPrefixes(List<String> queryMethodPrefixes) {
        this.queryMethodPrefixes = queryMethodPrefixes;
    }

    public List<DataSourceProperties> getWrite() {
        return write;
    }

    public void setWrite(List<DataSourceProperties> write) {
        this.write = write;
    }

    public List<DataSourceProperties> getRead() {
        return read;
    }

    public void setRead(List<DataSourceProperties> read) {
        this.read = read;
    }
}
