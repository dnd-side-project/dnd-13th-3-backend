package org.minu.dnd13th3backend.user.service;

import lombok.RequiredArgsConstructor;
import org.minu.dnd13th3backend.common.exception.BusinessException;
import org.minu.dnd13th3backend.common.exception.ErrorCode;
import org.minu.dnd13th3backend.user.dto.request.ProfileRequest;
import org.minu.dnd13th3backend.user.dto.request.ProfileUpdateRequest;
import org.minu.dnd13th3backend.user.dto.response.ProfileDetailResponse;
import org.minu.dnd13th3backend.user.entity.Profile;
import org.minu.dnd13th3backend.user.entity.User;
import org.minu.dnd13th3backend.user.repository.ProfileRepository;
import org.minu.dnd13th3backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public ProfileDetailResponse getProfile(Long userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        
        return ProfileDetailResponse.from(profile);
    }

    @Transactional
    public Integer createProfile(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (profileRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.PROFILE_ALREADY_EXISTS);
        }

        Integer characterIndex = generateRandomCharacterIndex();
        
        Profile profile = Profile.builder()
                .user(user)
                .nickname(request.getNickname())
                .characterIndex(characterIndex)
                .goalType(request.getGoalType())
                .goalCustom(request.getGoalCustom())
                .screenTimeGoalType(request.getScreenTimeGoalType())
                .screenTimeGoalCustom(request.getScreenTimeGoalCustom())
                .build();

        profileRepository.save(profile);
        return characterIndex;
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        String nickname = request.getNickname() != null ? request.getNickname() : profile.getNickname();
        Integer characterIndex = request.getCharacterIndex() != null ? request.getCharacterIndex() : profile.getCharacterIndex();

        profile.updateProfile(
                nickname,
                request.getGoalType(),
                request.getGoalCustom(),
                request.getScreenTimeGoalType(),
                request.getScreenTimeGoalCustom(),
                characterIndex
        );
    }
    
    private Integer generateRandomCharacterIndex() {
        return random.nextInt(6) + 1;
    }

}