package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;


public interface DataPuller {
    public ResultSetWrapper pull(String query);
}
