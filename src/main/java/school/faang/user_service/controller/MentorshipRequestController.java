package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Controller
public class MentorshipRequestController {
    MentorshipRequestService mentorshipRequestService;
    MentorshipRequestValidator mentorshipRequestValidator;

    @Autowired
    public MentorshipRequestController(MentorshipRequestService mentorshipRequestService, MentorshipRequestValidator mentorshipRequestValidator) {
        this.mentorshipRequestService = mentorshipRequestService;
        this.mentorshipRequestValidator = mentorshipRequestValidator;
    }

    @PostMapping
    public ResponseEntity<String> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestValidator.validateRequestMentorshipDescription(mentorshipRequestDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description is required");
        }

        try {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Mentorship request created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating mentorship request: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptRequest(@PathVariable long id) {
        mentorshipRequestService.acceptRequest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(RequestFilterDto filter) {
        List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(filter);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
        return ResponseEntity.ok().build();
    }

}
