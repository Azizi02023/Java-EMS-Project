
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
package dto;



public class ErrorsDTOs {

    @Data
    @AllArgsConstructor
        private Date timestamp;
        private String message;
        private String details;

}
