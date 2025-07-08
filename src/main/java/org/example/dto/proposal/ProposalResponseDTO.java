package org.example.dto.proposal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProposalResponseDTO {
    private Long id;
    private Double proposedPrice;
    private ZonedDateTime proposedStartAt;
    private Integer duration;
    private Long orderId;
    private Long expertId;

}