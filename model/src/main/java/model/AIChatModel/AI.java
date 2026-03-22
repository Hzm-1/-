package model.AIChatModel;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AI {
    //AI唯一标识id
    private Integer id;
    //AI业务编码
    private String aiCode;
    //AI名称
    private String aiName;
    //AI类型
    private String aiType;
    //AI配置JSON
    private String aiConfig;
    //状态（1-启用，0-禁用，2-维护中）
    private Integer aiStatus;
    //AI描述
    private String description;
    //创建人
    private String creator;
    //创建时间
    private LocalDateTime createdAt;
    //修改时间
    private LocalDateTime updatedAt;
    //扩展信息
    private String extInfo;
}
