package org.example.base_project.service;

import jakarta.annotation.Resource;
import org.example.base_project.entity.PageResult;
import org.example.base_project.entity.TvDramaPlayData;
import org.example.base_project.entity.TvDramaPlayQuery;
import org.example.base_project.exception.CustomException;
import org.example.base_project.mapper.TvDramaPlayMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TvDramaPlayService {

    @Resource
    private TvDramaPlayMapper tvDramaPlayMapper;

    public PageResult<TvDramaPlayData> page(TvDramaPlayQuery query) {
        normalizePage(query);

        List<TvDramaPlayData> records = tvDramaPlayMapper.selectPage(query);
        Long total = tvDramaPlayMapper.selectCount(query);

        return new PageResult<>(records, query.getCurrent(), query.getSize(), total);
    }

    public TvDramaPlayData detail(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        TvDramaPlayData data = tvDramaPlayMapper.selectById(id);

        if (data == null) {
            throw new CustomException("404", "电视剧播放量数据不存在");
        }

        return data;
    }

    public void create(TvDramaPlayData data) {
        if (data == null) {
            throw new CustomException("400", "新增数据不能为空");
        }

        tvDramaPlayMapper.insert(data);
    }

    public void update(Long id, TvDramaPlayData data) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        TvDramaPlayData dbData = tvDramaPlayMapper.selectById(id);

        if (dbData == null) {
            throw new CustomException("404", "要修改的数据不存在");
        }

        data.id = id;
        tvDramaPlayMapper.update(data);
    }

    public void delete(Long id) {
        if (id == null) {
            throw new CustomException("400", "数据ID不能为空");
        }

        TvDramaPlayData dbData = tvDramaPlayMapper.selectById(id);

        if (dbData == null) {
            throw new CustomException("404", "要删除的数据不存在");
        }

        tvDramaPlayMapper.deleteById(id);
    }

    public Map<String, Object> options() {
        Map<String, Object> result = new HashMap<>();
        result.put("dramaTypes", tvDramaPlayMapper.selectDramaTypeOptions());
        return result;
    }

    /**
     * 电视剧播放量数据挖掘：从题材类型、播放热度、口碑评分、互动倾向和采集时间趋势等维度分析数据规律。
     */
    public Map<String, Object> analysis(TvDramaPlayQuery query) {
        if (query == null) {
            query = new TvDramaPlayQuery();
        }

        Map<String, Object> summary = safeMap(tvDramaPlayMapper.selectAnalysisSummary(query));
        List<Map<String, Object>> typeDistribution = safeList(tvDramaPlayMapper.selectTypeDistribution(query));
        List<Map<String, Object>> scoreSegmentStats = safeList(tvDramaPlayMapper.selectScoreSegmentStats(query));
        List<Map<String, Object>> playSegmentStats = safeList(tvDramaPlayMapper.selectPlaySegmentStats(query));
        List<Map<String, Object>> monthTrend = safeList(tvDramaPlayMapper.selectMonthTrend(query));
        List<Map<String, Object>> typeRadarStats = safeList(tvDramaPlayMapper.selectTypeRadarStats(query));
        List<Map<String, Object>> topDramaRank = safeList(tvDramaPlayMapper.selectTopDramaRank(query));
        List<Map<String, Object>> interactionByScoreSegment = safeList(tvDramaPlayMapper.selectInteractionByScoreSegment(query));

        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        result.put("typeDistribution", typeDistribution);
        result.put("scoreSegmentStats", scoreSegmentStats);
        result.put("playSegmentStats", playSegmentStats);
        result.put("monthTrend", monthTrend);
        result.put("typeRadarStats", typeRadarStats);
        result.put("topDramaRank", topDramaRank);
        result.put("scorePlayScatter", safeList(tvDramaPlayMapper.selectScorePlayScatter(query)));
        result.put("typeScoreHeatmap", safeList(tvDramaPlayMapper.selectTypeScoreHeatmap(query)));
        result.put("interactionByScoreSegment", interactionByScoreSegment);
        result.put("insights", buildInsights(summary, typeDistribution, scoreSegmentStats, playSegmentStats,
                monthTrend, typeRadarStats, topDramaRank, interactionByScoreSegment));

        return result;
    }

    private List<String> buildInsights(Map<String, Object> summary,
                                       List<Map<String, Object>> typeDistribution,
                                       List<Map<String, Object>> scoreSegmentStats,
                                       List<Map<String, Object>> playSegmentStats,
                                       List<Map<String, Object>> monthTrend,
                                       List<Map<String, Object>> typeRadarStats,
                                       List<Map<String, Object>> topDramaRank,
                                       List<Map<String, Object>> interactionByScoreSegment) {
        List<String> insights = new ArrayList<>();

        long totalCount = toLong(summary.get("totalCount"));
        if (totalCount <= 0) {
            insights.add("当前筛选条件下暂无可分析的电视剧播放量数据，可以放宽剧名、类型、播放量、评分或采集时间条件后重新查看。 ");
            return insights;
        }

        insights.add("当前共纳入 " + totalCount + " 条电视剧播放量记录，累计播放量约 "
                + formatCount(summary.get("totalPlayCount")) + "，平均播放量约 "
                + formatCount(summary.get("avgPlayCount")) + "，平均评分为 "
                + formatNumber(summary.get("avgScore")) + " 分。播放量和评分可以共同反映剧集的市场热度与内容口碑。 ");

        insights.add("互动反馈方面，整体点赞量约 " + formatCount(summary.get("totalLikeCount"))
                + "，差评量约 " + formatCount(summary.get("totalNegativeCount"))
                + "，好评倾向为 " + formatNumber(summary.get("positiveRate"))
                + "% 。若高播放同时保持高好评倾向，说明该类内容兼具流量吸引力和用户满意度。 ");

        Map<String, Object> hotType = maxBy(typeDistribution, "totalPlayCount");
        if (hotType != null) {
            insights.add("题材类型中，“" + getText(hotType, "labelName") + "”贡献的总播放量最高，约 "
                    + formatCount(hotType.get("totalPlayCount")) + "，平均评分为 "
                    + formatNumber(hotType.get("avgScore")) + " 分。该类型可作为平台内容供给、推荐运营和宣发资源投入的重点方向。 ");
        }

        Map<String, Object> highScoreType = maxBy(typeDistribution, "avgScore");
        if (highScoreType != null) {
            insights.add("从口碑质量看，“" + getText(highScoreType, "labelName") + "”平均评分最高，达到 "
                    + formatNumber(highScoreType.get("avgScore")) + " 分，平均播放量约 "
                    + formatCount(highScoreType.get("avgPlayCount")) + "。若该类型播放量尚未领先，说明它可能属于高口碑但待进一步传播的潜力内容。 ");
        }

        Map<String, Object> mainScoreSegment = maxBy(scoreSegmentStats, "totalCount");
        if (mainScoreSegment != null) {
            insights.add("评分结构中，“" + getText(mainScoreSegment, "segmentName") + "”数量最多，共 "
                    + toLong(mainScoreSegment.get("totalCount")) + " 条，占比 "
                    + formatNumber(mainScoreSegment.get("ratio")) + "% 。评分集中区间能够反映当前数据集中电视剧整体口碑水平。 ");
        }

        Map<String, Object> mainPlaySegment = maxBy(playSegmentStats, "totalCount");
        if (mainPlaySegment != null) {
            insights.add("播放量分层显示，“" + getText(mainPlaySegment, "segmentName") + "”是主要分布区间，共 "
                    + toLong(mainPlaySegment.get("totalCount")) + " 条。若高播放区间占比偏低，说明数据整体呈现明显长尾特征，少数头部剧集贡献大量播放量。 ");
        }

        Map<String, Object> bestMonth = maxBy(monthTrend, "totalPlayCount");
        if (bestMonth != null) {
            insights.add("时间趋势中，“" + getText(bestMonth, "monthName") + "”累计播放量最高，约 "
                    + formatCount(bestMonth.get("totalPlayCount")) + "，当期平均评分为 "
                    + formatNumber(bestMonth.get("avgScore")) + "。这有助于判断采集周期内热点内容集中爆发的时间窗口。 ");
        }

        Map<String, Object> radarType = maxBy(typeRadarStats, "positiveRate");
        if (radarType != null) {
            insights.add("雷达画像中，“" + getText(radarType, "labelName") + "”的好评倾向较高，为 "
                    + formatNumber(radarType.get("positiveRate")) + "% ，争议度为 "
                    + formatNumber(radarType.get("controversyRate")) + "% 。这类题材更适合用于刻画高满意度用户画像。 ");
        }

        Map<String, Object> topDrama = maxBy(topDramaRank, "playCount");
        if (topDrama != null) {
            insights.add("头部剧集中，《" + getText(topDrama, "dramaName") + "》播放量最高，约 "
                    + formatCount(topDrama.get("playCount")) + "，评分为 "
                    + formatNumber(topDrama.get("score")) + " 分。该剧可作为爆款样本，进一步分析其题材、评分和互动表现。 ");
        }

        Map<String, Object> highPositiveScore = maxBy(interactionByScoreSegment, "positiveRate");
        if (highPositiveScore != null) {
            insights.add("评分与互动关系显示，“" + getText(highPositiveScore, "segmentName") + "”的好评倾向最高，达到 "
                    + formatNumber(highPositiveScore.get("positiveRate")) + "% 。若高评分区间同时具有更高好评倾向，说明用户互动反馈与评分口碑存在较强一致性。 ");
        }

        return insights;
    }

    private void normalizePage(TvDramaPlayQuery query) {
        if (query == null) {
            throw new CustomException("400", "分页参数不能为空");
        }

        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1);
        }

        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
    }

    private Map<String, Object> safeMap(Map<String, Object> value) {
        return value == null ? new HashMap<>() : value;
    }

    private List<Map<String, Object>> safeList(List<Map<String, Object>> value) {
        return value == null ? new ArrayList<>() : value;
    }

    private Map<String, Object> maxBy(List<Map<String, Object>> list, String key) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<String, Object> target = null;
        double maxValue = -Double.MAX_VALUE;
        for (Map<String, Object> item : list) {
            double value = toDouble(item.get(key));
            if (target == null || value > maxValue) {
                target = item;
                maxValue = value;
            }
        }
        return target;
    }

    private String getText(Map<String, Object> map, String key) {
        Object value = map == null ? null : map.get(key);
        return value == null ? "未知" : String.valueOf(value);
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return new BigDecimal(String.valueOf(value)).longValue();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0D;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception ignored) {
            return 0D;
        }
    }

    private String formatNumber(Object value) {
        BigDecimal decimal;
        try {
            decimal = new BigDecimal(String.valueOf(value == null ? 0 : value));
        } catch (Exception ignored) {
            decimal = BigDecimal.ZERO;
        }
        return decimal.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private String formatCount(Object value) {
        double num = toDouble(value);
        if (num >= 100000000) {
            return new BigDecimal(num / 100000000D).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "亿";
        }
        if (num >= 10000) {
            return new BigDecimal(num / 10000D).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "万";
        }
        return new BigDecimal(num).setScale(0, RoundingMode.HALF_UP).toPlainString();
    }
}
