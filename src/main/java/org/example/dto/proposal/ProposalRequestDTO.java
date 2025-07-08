package org.example.dto.proposal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProposalRequestDTO {

    @NotNull
    @Min(value = 0)
    private Double proposedPrice;

    @NotNull
    private ZonedDateTime proposedStartAt;

    @NotNull
    @Min(value = 0)
    private Integer duration;

    @NotNull
    private Long orderId;

    @NotNull
    private Long expertId;

}