package model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EsTask {
    // 任务id
    private Integer id;
    // 用户id
    private Integer userId;
    //操作类型：INSERT/UPDATE/DELETE……
    private String operationType;
    //待同步数据（JSON格式）
    private String data;
    //任务状态：0-待处理，1-成功，2-失败，3-重试中
    private int status;
    //已重试次数
    private int retryCount;
    //创建时间
    private LocalDateTime createdTime;
    //更新时间
    private LocalDateTime updatedTime;

}
