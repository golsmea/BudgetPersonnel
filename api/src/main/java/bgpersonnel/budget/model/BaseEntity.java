package bgpersonnel.budget.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    @Column(name = "createdBy")
    private String createdBy;
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    @Column(name = "updatedBy")
    private String updatedBy;
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
