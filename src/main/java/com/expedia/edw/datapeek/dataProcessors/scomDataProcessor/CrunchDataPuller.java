package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.expedia.e3.qm.crunch.common.DataTypes.DataList;
import com.expedia.e3.qm.crunch.core.MainEngine;
import com.expedia.e3.qm.crunch.core.Types.ExecuteOptions;

public class CrunchDataPuller implements DataPuller {


    public CrunchDataPuller() {
    }

    @Override
	public ResultSetWrapper pull(String query) {
        LinkedHashMap<String, DataList<? extends Map<String, Object>, String, Object>> result = MainEngine
                .execute(query, new ExecuteOptions());

        Object key = result.keySet().toArray()[0];
        List<String> metaData = result.get(key).getHeaders();
        ResultSetWrapper rsw = new ResultSetWrapper(result.get(key).iterator(), metaData);
        return rsw;
    }

}
