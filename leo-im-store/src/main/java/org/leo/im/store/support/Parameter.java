package org.leo.im.store.support;

/**
 * 数据库参数类
 * 
 * @author Leo
 * @date 2018/3/19
 */
public final class Parameter {

private String name;
    
    private ParameterDataTypeEnum dataType;
    
    private Object value;
    
    public Parameter(String name, ParameterDataTypeEnum dataType, Object value) {
        this.name = name;
        this.dataType = dataType;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public ParameterDataTypeEnum getDataType() {
        return this.dataType;
    }
    public void setDataType(ParameterDataTypeEnum dataType) {
        this.dataType = dataType;
    }
    
    public Object getValue() {
        return this.value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    
}
