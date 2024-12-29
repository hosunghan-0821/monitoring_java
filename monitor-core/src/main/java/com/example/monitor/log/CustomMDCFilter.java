package com.example.monitor.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

public class CustomMDCFilter extends Filter<ILoggingEvent> {
    private String mdcKey;
    private String mdcValue;

    public void setMdcKey(String mdcKey) {
        this.mdcKey = mdcKey;
    }

    public void setMdcValue(String mdcValue) {
        this.mdcValue = mdcValue;
    }

    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        String value = MDC.get(mdcKey);
        if (mdcValue.equals(value)) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}
