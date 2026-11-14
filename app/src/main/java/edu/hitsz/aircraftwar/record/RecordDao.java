package edu.hitsz.aircraftwar.record;

import java.util.List;

/** 排行榜数据访问接口；公共方法名和参数保持原项目约定。 */
public interface RecordDao {
    List<Record> getAllRecords();
    void doAdd(Record record);
    void doDelete(Record record);
}
