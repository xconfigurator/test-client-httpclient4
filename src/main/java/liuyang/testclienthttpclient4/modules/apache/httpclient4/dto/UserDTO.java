package liuyang.testclienthttpclient4.modules.apache.httpclient4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author liuyang(wx)
 * @since 2021/11/26
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
//@ToString
@Data
//@Component
public class UserDTO {
    @NotNull
    private String id;
    private String username;
    private String info;
    private Double d;
    private BigDecimal bd;
}
