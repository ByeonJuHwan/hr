package com.sohwakmo.hr.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Entity(name = "MESSAGE")
@SequenceGenerator(name = "MESSAGE_SEQ_GEN", sequenceName = "MESSAGE_SEQ", allocationSize = 1)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MESSAGE_SEQ_GEN")
    private Integer messageNo;
    private String messageType;
    private String title;
    private String content;
    private String fileUrl1;
    private String fileUrl2;
    private String fileUrl3;
    private LocalDateTime sendTime;
    private LocalDateTime readTime;
    private Integer receiveNo;
    private Integer receiveReadCheck;
    private Integer receiveTrash;
    private Integer receiveDelete;
    private Integer senderNo;
    private Integer senderTrash;
    private Integer senderDelete;

}
