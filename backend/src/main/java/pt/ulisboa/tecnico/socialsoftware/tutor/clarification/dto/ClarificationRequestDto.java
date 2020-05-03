package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.StudentQuestion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClarificationRequestDto {
    private Integer id;
    private Integer key;
    private Integer owner;
    private Integer question;
    private String content;
    private String creationDate;
    private ClarificationRequestAnswerDto answer;
    private ClarificationRequest.RequestStatus status;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClarificationRequestDto() {
    }

    public ClarificationRequestDto(ClarificationRequest clarificationRequest) {
        this.id = clarificationRequest.getId();
        this.content = clarificationRequest.getContent();
        this.question = clarificationRequest.getQuestion().getId();
        this.owner = clarificationRequest.getOwner().getId();
        this.status = clarificationRequest.getStatus();

        if (clarificationRequest.getCreationDate() != null)
            this.creationDate = clarificationRequest.getCreationDate().format(formatter);

        clarificationRequest.getAnswer().ifPresent(ans -> this.answer = new ClarificationRequestAnswerDto(ans));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer id) {
        this.owner = id;
    }

    public Integer getQuestionId() {
        return question;
    }

    public void setQuestionId(Integer id) {
        this.question = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String date) {
        creationDate = date;
    }

    public LocalDateTime getCreationDateDate() {
        if (getCreationDate() == null || getCreationDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getCreationDate(), formatter);
    }

    public void setAnswer(ClarificationRequestAnswerDto answer) {
        this.answer = answer;
    }

    public ClarificationRequestAnswerDto getAnswer() {
        return answer;
    }

    public ClarificationRequest.RequestStatus getStatus() { return this.status; }

    public void setStatus(ClarificationRequest.RequestStatus status) { this.status = status; }
}