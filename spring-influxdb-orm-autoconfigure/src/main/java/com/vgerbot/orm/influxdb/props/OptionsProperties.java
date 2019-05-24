package com.vgerbot.orm.influxdb.props;

import com.vgerbot.orm.influxdb.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = Constants.INFLUXDB_ORM_OPTIONS_PROPERTIES_PREFIX)
public class OptionsProperties {
    private Boolean enableGzip = false;

    private Boolean enableBatch = false;
    private Integer batchActions = 2000;
    private Integer batchFlushDuration = 100;

    private Long httpConnectTimeout = 10_000L;
    private Long httpReadTimeout = 10_000L;
    private Long httpWriteTimeout = 10_000L;

    public Boolean getEnableGzip() {
        return enableGzip;
    }

    public void setEnableGzip(Boolean enableGzip) {
        this.enableGzip = enableGzip;
    }

    public Boolean getEnableBatch() {
        return enableBatch;
    }

    public void setEnableBatch(Boolean enableBatch) {
        this.enableBatch = enableBatch;
    }

    public Integer getBatchActions() {
        return batchActions;
    }

    public void setBatchActions(Integer batchActions) {
        this.batchActions = batchActions;
    }

    public Integer getBatchFlushDuration() {
        return batchFlushDuration;
    }

    public void setBatchFlushDuration(Integer batchFlushDuration) {
        this.batchFlushDuration = batchFlushDuration;
    }

    public Long getHttpConnectTimeout() {
        return httpConnectTimeout;
    }

    public void setHttpConnectTimeout(Long httpConnectTimeout) {
        this.httpConnectTimeout = httpConnectTimeout;
    }

    public Long getHttpReadTimeout() {
        return httpReadTimeout;
    }

    public void setHttpReadTimeout(Long httpReadTimeout) {
        this.httpReadTimeout = httpReadTimeout;
    }

    public Long getHttpWriteTimeout() {
        return httpWriteTimeout;
    }

    public void setHttpWriteTimeout(Long httpWriteTimeout) {
        this.httpWriteTimeout = httpWriteTimeout;
    }
}
