package com.sohwakmo.hr.service;

import com.sohwakmo.hr.domain.Employee;
import com.sohwakmo.hr.domain.Message;
import com.sohwakmo.hr.dto.MessageSearchDto;
import com.sohwakmo.hr.dto.MessageSendDto;
import com.sohwakmo.hr.repository.EmployeeRepository;
import com.sohwakmo.hr.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 메세지 보내기 기능 수행할 때 db에 저장하고 파일을 저장
     * @param dto
     * @param files
     * @throws IOException
     */
    public void create(MessageSendDto dto, List<MultipartFile> files) throws IOException {
        log.info("create(dto = {}, files = {})", dto, files);

        long employeeNo = dto.getSenderNo();
        log.info("employeeNo = {}", employeeNo);
        Employee employee = employeeRepository.findByEmployeeNo(employeeNo);
        log.info("employee = {}", employee);

        Message message = MessageSendDto.builder()
                .senderNo(dto.getSenderNo()).messageType(dto.getMessageType()).title(dto.getTitle()).receiveNo(dto.getReceiveNo()).content(dto.getContent())
                .employee(employee)
                .build().toEntity();
        log.info("message = {}", message);

        for (MultipartFile multipartFile : files) {
            if(multipartFile.isEmpty()) {
                messageRepository.save(message);
            } else {
                saveFile(multipartFile, message);
                messageRepository.save(message);
            }
        }

    }

    /**
     * 파일을 저장하는 메서드
     * @param file
     * @param message
     * @throws IOException
     */
    public void saveFile(MultipartFile file, Message message) throws IOException {
        log.info("saveFile(file = {}, message = {})", file, message);

        // 파일 저장 경로
        String projectFilePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\message";
        log.info("projectFilePath = {}", projectFilePath);

        // 파일 이름
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString() + "_" + file.getOriginalFilename();
        log.info("fileName = {}", fileName);

        File saveFile = new File(projectFilePath, fileName);

        // 파일을 해당경로에 저장
        file.transferTo(saveFile);

        if (message.getFilePath1() == null) {
            message.setFilePath1(projectFilePath + "\\" + fileName);
            message.setFileName1(file.getOriginalFilename());
            log.info("message = {}", message);
        } else if (message.getFilePath2() == null) {
            message.setFilePath2(projectFilePath + "\\" + fileName);
            message.setFileName2(file.getOriginalFilename());
            log.info("message = {}", message);
        } else if (message.getFilePath3() == null) {
            message.setFilePath3(projectFilePath + "\\" + fileName);
            message.setFileName3(file.getOriginalFilename());
            log.info("message = {}", message);
        }

    }

    /**
     * 받은쪽지함 들어가면 로그인한 번호로 받은쪽지 보여주기
     * @param employeeNo
     */
    public List<Message> read(Integer employeeNo) {
        log.info("read(employeeNo = {})", employeeNo);

        List<Message> messageList = new ArrayList<>();
        messageList = messageRepository.findByReceiveNoOrderByMessageNoDesc(employeeNo);
        log.info("messageList = {}", messageList);

        return messageList;
    }

    /**
     * 받은쪽지함 검색
     * @param employeeNo
     * @param messageType
     * @param contentType
     * @param keyword
     * @return
     */
    public List<MessageSearchDto> searchMessage(Integer employeeNo, String messageType, String contentType, String keyword) {
        log.info("searchMessage(employeeNo = {}, messageType = {}, contentType = {}, keyword = {})", employeeNo, messageType, contentType, keyword);

        List<MessageSearchDto> messageSearchDtoList = new ArrayList<>();

        if (messageType == null) { // 메세지 타입이 null인 경우
            log.info("null");
            switch(contentType) {
                case "all" :
                    messageSearchDtoList = messageRepository.findByReceiveNoAll(employeeNo, keyword);
                    break;
                case "title" :
                    messageSearchDtoList = messageRepository.findByReceiveNoAndTitle(employeeNo, keyword);
                    break;
                case "sender" :
                    messageSearchDtoList = messageRepository.findByReceiveNoAndSenderName(employeeNo, keyword);
                    break;
            }
        } else { // 메세지 타입이 있는 경우
            log.info("not null");
            switch(contentType) {
                case "all" :
                    messageSearchDtoList = messageRepository.findByMessageTypeAndReceiveNoAll(employeeNo, keyword, messageType);
                    break;
                case "title" :
                    messageSearchDtoList = messageRepository.findByMessageTypeAndReceiveNoAndTitle(employeeNo, keyword, messageType);
                    break;
                case "sender" :
                    messageSearchDtoList = messageRepository.findByMessageTypeAndReceiveNoAndName(employeeNo, keyword, messageType);
                    break;
            }
        }

        log.info("messageSearchDtoList = {}", messageSearchDtoList);

        return messageSearchDtoList;
    }


}