package bitone.akeneo.product_generator.domain.model.attribute;

import java.util.Date;
import java.math.BigDecimal;

public class Properties {
    private boolean unique;
    private String metricFamily;
    private String defaultMetricUnit;
    private BigDecimal numberMin;
    private BigDecimal numberMax;
    private Date dateMin;
    private Date dateMax;


    public Properties(boolean unique, String metricFamily, String defaultMetricUnit, BigDecimal numberMin, BigDecimal numberMax, Date dateMin, Date dateMax) {
        this.unique = unique;
        this.metricFamily = metricFamily;
        this.defaultMetricUnit = defaultMetricUnit;
        this.numberMin = numberMin;
        this.numberMax = numberMax;
        this.dateMin = dateMin;
        this.dateMax = dateMax;
    }

    public boolean isUnique() {
        return unique;
    }

    public String getMetricFamily() {
        return metricFamily;
    }

    public String getDefaultMetricUnit() {
        return defaultMetricUnit;
    }

    public Date getDateMin() {
        return dateMin;
    }

    public Date getDateMax() {
        return dateMax;
    }

    public BigDecimal getNumberMin() {
        return numberMin;
    }

    public BigDecimal getNumberMax() {
        return numberMax;
    }
}
