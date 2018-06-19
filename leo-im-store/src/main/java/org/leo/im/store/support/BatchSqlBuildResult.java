package org.leo.im.store.support;

import java.util.List;

/**
 * 批量SQL构建结果类
 * 
 * @author Leo
 * @date 2018/5/29
 */
public final class BatchSqlBuildResult {
    
    private String sql;
    
    private List<List<Parameter>> parameters;
    
    public BatchSqlBuildResult(String sql, List<List<Parameter>> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }
    
    public String getSql() {
        return this.sql;
    }
    
    public List<List<Parameter>> getParameters() {
        return this.parameters;
    }

}
