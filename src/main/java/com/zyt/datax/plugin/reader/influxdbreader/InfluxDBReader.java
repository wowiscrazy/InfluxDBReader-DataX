package com.zyt.datax.plugin.reader.influxdbreader;

import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.element.StringColumn;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordSender;
import com.alibaba.datax.common.spi.Reader;
import com.alibaba.datax.common.util.Configuration;
import com.zyt.datax.plugin.reader.tsdbreader.TSDBReaderErrorCode;
import com.zyt.datax.plugin.reader.tsdbreader.util.HttpUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyt.datax.plugin.reader.tsdbreader.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName InfluxDBReader
 * @Description: InfluxDBReader
 * @Author ZYT
 * @Date 2020/3/3
 * @Version V1.0
 **/
public class InfluxDBReader extends Reader {
    public static class Job extends Reader.Job {

        @Override
        public List<Configuration> split(int adviceNumber) {
            Configuration readerSliceConfig = super.getPluginJobConf();
            List<Configuration> splittedConfigs = new ArrayList<Configuration>();
            splittedConfigs.add(readerSliceConfig);
            return splittedConfigs;
        }

        @Override
        public void init() {}

        @Override
        public void destroy() {}
    }

    public static class Task extends Reader.Task{
        private static final Logger LOG = LoggerFactory.getLogger(InfluxDBReader.Task.class);

        private String dbType;
        private String address;
        private String username;
        private String password;
        private String database;
        private String querySql;

        @Override
        public void startRead(RecordSender recordSender) {
            if("InfluxDB".equals(dbType)){
                if(StringUtils.isBlank(database)||StringUtils.isBlank(querySql)){
                    throw DataXException.asDataXException(
                            TSDBReaderErrorCode.REQUIRED_VALUE, "InfluxDB的场景下 : database或者sql参数不能为空！", null);
                }
                String tail="/query";
                String enc="utf-8";
                String result= "";
                try {
                    String url=address+tail
                            +"?db=" + URLEncoder.encode(database,enc) ;
                    if(!"".equals(username)){
                        url+="&u=" + URLEncoder.encode(username,enc);
                    }
                    if(!"".equals(password)){
                        url+="&p=" + URLEncoder.encode(password,enc) ;
                    }
                    if(querySql.contains("#lastMinute#")){
                        this.querySql = querySql.replace("#lastMinute#", TimeUtils.getLastMinute());
                    }
                    url+="&q=" + URLEncoder.encode(querySql,enc);
                    result = HttpUtils.get(url);
                } catch (Exception e) {
                    throw DataXException.asDataXException(
                            TSDBReaderErrorCode.ILLEGAL_VALUE, "获取数据点的过程中出错！", e);
                }

                if(StringUtils.isBlank(result)){
                    throw DataXException.asDataXException(
                            TSDBReaderErrorCode.ILLEGAL_VALUE, "没有获取到数据！", null);
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONArray results = (JSONArray) jsonObject.get("results");
                    JSONObject resultsMap = (JSONObject) results.get(0);
                    if(resultsMap.containsKey("series")){
                        JSONArray series= (JSONArray) resultsMap.get("series");
                        JSONObject seriesMap = (JSONObject) series.get(0);
                        if(seriesMap.containsKey("values")){
//                            JSONArray columns = (JSONArray) seriesMap.get("columns");
                            JSONArray values = (JSONArray) seriesMap.get("values");
                            for (Object row:values) {
                                JSONArray rowArray = (JSONArray) row;
                                Record record = recordSender.createRecord();
                                for (Object s:rowArray) {
                                    record.addColumn(new StringColumn(s.toString()));
                                }
                                recordSender.sendToWriter(record);
                            }
                        }

                    }else if(resultsMap.containsKey("error")){
                        throw DataXException.asDataXException(
                                TSDBReaderErrorCode.ILLEGAL_VALUE, "结果集中包含错误！", null);
                    }

                } catch (Exception e) {
                    throw DataXException.asDataXException(
                            TSDBReaderErrorCode.ILLEGAL_VALUE, "发送数据点的过程中出错！", e);
                }
            }
        }

        @Override
        public void init() {
            Configuration readerSliceConfig = super.getPluginJobConf();
            this.dbType = readerSliceConfig.getString("dbType");
            this.address = readerSliceConfig.getString("address");
            this.username=readerSliceConfig.getString("username");
            this.password=readerSliceConfig.getString("password");
            this.database=readerSliceConfig.getString("database");
            this.querySql=readerSliceConfig.getString("querySql");
        }

        @Override
        public void destroy() {}
    }
}
