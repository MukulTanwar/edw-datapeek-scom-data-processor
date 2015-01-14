package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;



public class ResultSetWrapper {

    private Iterator<Map<String, Object>> rowIter;
    
    private List<String> metaData;    
    
    @Inject  
    public ResultSetWrapper(Iterator<Map<String, Object>> iter, List<String> metaData) {
        this.rowIter = iter;
        this.metaData = metaData;
    }

    public Iterator<Map<String, Object>> getResultSet() {
        return rowIter;
    }

    public List<String> getMetadata() {
        return metaData;
    }
}
