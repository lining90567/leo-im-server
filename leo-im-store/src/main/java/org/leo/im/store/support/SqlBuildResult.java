package org.leo.im.store.support;

import java.util.List;

/**
 * SQL构建结果类
 * 
 * @author Leo
 * @date 2018/3/20
 */
public final class SqlBuildResult {
    
    private String countSql;
    
    private String sql;
    
    private List<Parameter> parameters;
    
    public SqlBuildResult(String sql, List<Parameter> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }
    
    public SqlBuildResult(String countSql, String sql, List<Parameter> parameters) {
        this.countSql = countSql;
        this.sql = sql;
        this.parameters = parameters;
    }
    
    public String getCountSql() {
        return this.countSql;
    }
    
    public String getSql() {
        return this.sql;
    }
    
    public List<Parameter> getParameters() {
        return this.parameters;
    }

}
