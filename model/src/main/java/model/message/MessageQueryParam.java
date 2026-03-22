package model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageQueryParam {
    //接收方id
    private Integer receiverId;
    //发送方id
    private Integer senderId;
    private Integer page;
    private Integer pageSize=20;
}
