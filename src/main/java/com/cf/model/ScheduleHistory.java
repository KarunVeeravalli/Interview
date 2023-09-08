package com.cf.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class ScheduleHistory {
@Id
@GeneratedValue
private Integer scheduleHistoryId;
private String interviewDetails;
}
