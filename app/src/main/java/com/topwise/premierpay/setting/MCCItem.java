package com.topwise.premierpay.setting;

public class MCCItem {
    private String code;
    private String description;
    private boolean isRange;
    private Integer minCode;
    private Integer maxCode;
    private String category;

    public MCCItem() {}

    public MCCItem(String code, String description, boolean isRange,
                   Integer minCode, Integer maxCode, String category) {
        this.code = code;
        this.description = description;
        this.isRange = isRange;
        this.minCode = minCode;
        this.maxCode = maxCode;
        this.category = category;
    }

    // Getter和Setter方法
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isRange() { return isRange; }
    public void setRange(boolean range) { isRange = range; }

    public Integer getMinCode() { return minCode; }
    public void setMinCode(Integer minCode) { this.minCode = minCode; }

    public Integer getMaxCode() { return maxCode; }
    public void setMaxCode(Integer maxCode) { this.maxCode = maxCode; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        if (isRange) {
            return code + " - " + description + " (" + category + ")";
        }
        return code + " - " + description + " (" + category + ")";
    }
}
