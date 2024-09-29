package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long mentorId) {
        log.info("Getting mentees for mentor with ID: {}", mentorId);
        if (!mentorshipRepository.existsById(mentorId)) {
            log.error("Mentor with ID {} does not exist", mentorId);
            throw new IllegalArgumentException("Mentor does not exist");
        }
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(mentorId);
        List<UserDto> menteesDtos = userMapper.toUsersDtos(mentees);

        log.info("Found {} mentees for mentor with ID: {}", menteesDtos.size(), mentorId);
        return menteesDtos;
    }

    public List<UserDto> getMentors(long menteeId) {
        log.info("Getting mentors for mentee with ID: {}", menteeId);
        if (!mentorshipRepository.existsById(menteeId)) {
            log.error("Mentee with ID {} does not exist", menteeId);
            throw new IllegalArgumentException("Mentee does not exist");
        }
        List<User> mentors = mentorshipRepository.findMentorsByMenteeId(menteeId);
        List<UserDto> mentorDtos = userMapper.toUsersDtos(mentors);

        log.info("Found {} mentors for mentee with ID: {}", mentorDtos.size(), menteeId);
        return mentorDtos;
    }

    @Transactional
    public void deleteMenteeOfMentor(long mentorId, long menteeId) {
        log.info("Deleting mentee with ID {} for mentor with ID {}", menteeId, mentorId);

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> {
                    log.error("Mentor with ID {} does not exist", mentorId);
                    return new IllegalArgumentException("Mentor not found with id: " + mentorId);
                });

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> {
                    log.error("Mentee with ID {} does not exist", menteeId);
                    return new IllegalArgumentException("Mentee not found with id: " + menteeId);
                });

        if (!mentor.getMentees().contains(mentee)) {
            log.error("Mentee with ID {} is not assigned to mentor with ID {}", menteeId, mentorId);
            throw new IllegalArgumentException("Mentee is not assigned to this mentor.");
        }

        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
        log.info("Successfully deleted mentee with ID {} for mentor with ID {}", menteeId, mentorId);
    }


    @Transactional
    public void deleteMentorOfMentee(long mentorId, long menteeId) {
        log.info("Deleting mentor with ID {} for mentee with ID {}", mentorId, menteeId);

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> {
                    log.error("Mentee with ID {} does not exist", menteeId);
                    return new IllegalArgumentException("Mentee not found with id: " + menteeId);
                });

        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> {
                    log.error("Mentor with ID {} does not exist", mentorId);
                    return new IllegalArgumentException("Mentor not found with id: " + mentorId);
                });

        if (!mentee.getMentors().contains(mentor)) {
            log.error("Mentor with ID {} is not assigned to mentee with ID {}", mentorId, menteeId);
            throw new IllegalArgumentException("Mentor is not assigned to this mentee.");
        }

        mentee.getMentors().remove(mentor);

        userRepository.save(mentee);
        log.info("Successfully deleted mentor with ID {} for mentee with ID {}", mentorId, menteeId);
    }


}
