package org.example.base_project.mapper;

import org.apache.ibatis.annotations.Param;
import org.example.base_project.entity.RentalHouseData;
import org.example.base_project.entity.RentalHouseQuery;

import java.util.List;
import java.util.Map;

public interface RentalHouseMapper {
    List<RentalHouseData> selectPage(RentalHouseQuery query);

    Long selectCount(RentalHouseQuery query);

    RentalHouseData selectById(@Param("id") Long id);

    void insert(RentalHouseData data);

    void update(RentalHouseData data);

    void deleteById(@Param("id") Long id);

    List<String> selectRoomOptions();

    List<String> selectFloorGrpOptions();

    List<String> selectSubwayOptions();

    List<String> selectRegionOptions();

    List<String> selectHeatingOptions();

    Map<String, Object> selectAnalysisSummary(RentalHouseQuery query);

    List<Map<String, Object>> selectRegionStats(RentalHouseQuery query);

    List<Map<String, Object>> selectRentSegmentStats(RentalHouseQuery query);

    List<Map<String, Object>> selectAreaSegmentStats(RentalHouseQuery query);

    List<Map<String, Object>> selectRoomDistribution(RentalHouseQuery query);

    List<Map<String, Object>> selectBedroomStats(RentalHouseQuery query);

    List<Map<String, Object>> selectSubwayStats(RentalHouseQuery query);

    List<Map<String, Object>> selectFloorStats(RentalHouseQuery query);

    List<Map<String, Object>> selectHeatingStats(RentalHouseQuery query);

    List<Map<String, Object>> selectRegionBedroomHeatmap(RentalHouseQuery query);

    List<Map<String, Object>> selectRentAreaScatter(RentalHouseQuery query);

    List<Map<String, Object>> selectFeatureRadarStats(RentalHouseQuery query);
}
