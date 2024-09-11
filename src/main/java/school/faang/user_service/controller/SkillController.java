package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.SkillAssignmentException;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {
    private final SkillService service;
    private final SkillValidator validator;

    @PostMapping()
    public SkillDto create(@RequestBody SkillDto skillDto) {
        validator.validateSkill(skillDto);
        return service.create(skillDto);
    }

    @GetMapping("/{userId}")
    List<SkillDto> getUserSkills(@PathVariable long userId) {
        return service.getUserSkills(userId);
    }

    @GetMapping("/{userId}/offers")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable long userId) {
        return service.getOfferedSkills(userId);
    }

    @PostMapping("/{skillId}/beneficiary/{userId}")
    public SkillDto acquireSkillFromOffers(@PathVariable long skillId, @PathVariable long userId) {
        return service.acquireSkillFromOffers(skillId, userId);
    }

    @ExceptionHandler(SkillAssignmentException.class)
    public ResponseEntity<String> handleSkillAssignmentException(SkillAssignmentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
