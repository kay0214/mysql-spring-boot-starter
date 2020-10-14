/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.personal.mysql.dynamic;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sunpeikai
 * @version DataSourceBean, v0.1 2020/10/12 17:47
 * @description
 */
public class DataSourceBean {
    private int size;
    private List<DataSources> dataSources;

    public void addDataSource(boolean master, DataBaseType type, DataSource dataSource){
        if(dataSources == null){
            dataSources = new ArrayList<>();
        }
        dataSources.add(new DataSources(master, type.name() + size, dataSource));
        size ++ ;
    }

    public List<DataSource> getAllDataSource(){
        return dataSources.stream().map(DataSources::getDataSource).collect(Collectors.toList());
    }

    public DataSource getMasterDataSource(){
        return dataSources.stream().filter(DataSources::isMaster).map(DataSources::getDataSource).findAny().orElse(null);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<DataSources> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSources> dataSources) {
        this.dataSources = dataSources;
    }

    public static class DataSources{
        private boolean master;
        private String name;
        private DataSource dataSource;

        public DataSources(boolean master, String name, DataSource dataSource) {
            this.master = master;
            this.name = name;
            this.dataSource = dataSource;
        }

        public boolean isMaster() {
            return master;
        }

        public void setMaster(boolean master) {
            this.master = master;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }
    }

    public enum DataBaseType {
        WRITE,READ
    }
}
