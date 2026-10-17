package org.example.springboot.enumClass;

/**
 * 随访状态枚举
 */
public enum FollowUpStatus {
    /**
     * 未开始
     */
    NOT_STARTED(0, "未开始"),
    
    /**
     * 进行中
     */
    IN_PROGRESS(1, "进行中"),
    
    /**
     * 已完成
     */
    COMPLETED(2, "已完成"),
    
    /**
     * 已取消
     */
    CANCELLED(3, "已取消");
    
    private final int value;
    private final String label;
    
    FollowUpStatus(int value, String label) {
        this.value = value;
        this.label = label;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getLabel() {
        return label;
    }
    
    /**
     * 根据值获取枚举
     */
    public static FollowUpStatus getByValue(int value) {
        for (FollowUpStatus status : FollowUpStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return NOT_STARTED;
    }
}