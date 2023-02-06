package com.house.houseserver.core.dto;

import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@ToString
@Getter
@XmlRootElement(name = "body")
public class AptBodyOriginDto {

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<AptTrDto> items;

    @XmlElement(name = "numOfRows")
    private Integer numOfRows;

    @XmlElement(name = "pageNo")
    private Integer pageNo;

    @XmlElement(name = "totalCount")
    private Integer totalCount;
}
