package org.example.base_project.entity;

import java.math.BigDecimal;

/**
 * 电视剧播放量数据实体
 */
public class TvDramaPlayData {
    public Long id;

    public String dramaName;
    public String dramaType;

    public String playCountText;
    public Long playCount;

    public Long likeCount;
    public Long negativeCount;

    public BigDecimal score;
    public String collectTime;

    public String createTime;
}