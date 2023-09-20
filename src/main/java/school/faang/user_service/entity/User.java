package school.faang.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username", length = 64, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 64, nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 32, unique = true)
    private String phone;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "about_me", length = 4096)
    private String aboutMe;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "city", length = 64)
    private String city;

    @Column(name = "experience")
    private Integer experience;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "subscription",
            joinColumns = @JoinColumn(name = "followee_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private List<User> followers = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "followers")
    private List<User> followees = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "owner")
    private List<Event> ownedEvents = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "mentors")
    private List<User> mentees = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "mentorship",
            joinColumns = @JoinColumn(name = "mentee_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id"))
    private List<User> mentors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiver")
    private List<MentorshipRequest> receivedMentorshipRequests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "requester")
    private List<MentorshipRequest> sentMentorshipRequests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "inviter")
    private List<GoalInvitation> sentGoalInvitations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "invited")
    private List<GoalInvitation> receivedGoalInvitations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "mentor")
    private List<Goal> setGoals = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "users")
    private List<Goal> goals = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "users")
    private List<Skill> skills = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> participatedEvents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "author")
    private List<Recommendation> recommendationsGiven = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiver")
    private List<Recommendation> recommendationsReceived = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Contact> contacts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Rating> ratings = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fileId", column = @Column(name = "profile_pic_file_id")),
            @AttributeOverride(name = "smallFileId", column = @Column(name = "profile_pic_small_file_id"))
    })
    private UserProfilePic userProfilePic;

    @OneToOne(mappedBy = "user")
    private ContactPreference contactPreference;

    @OneToOne(mappedBy = "user")
    private Premium premium;

    @Column(name = "storage_size")
    private BigInteger storageSize;

    @Column(name = "max_storage_size")
    private BigInteger maxStorageSize;
}