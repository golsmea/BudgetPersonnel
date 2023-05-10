package bgpersonnel.budget.exeception;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ErrorMessageResponse {
    private String message = "Error";
    private LocalDateTime dateTimeError = LocalDateTime.now();
    private String description;

    public ErrorMessageResponse(String description) {
        this.description = description;
    }

}
