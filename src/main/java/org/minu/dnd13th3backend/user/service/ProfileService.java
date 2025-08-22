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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileDetailResponse getProfile(Long userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        
        return ProfileDetailResponse.from(profile);
    }

    @Transactional
    public void createProfile(Long userId, ProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (profileRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.PROFILE_ALREADY_EXISTS);
        }

        Profile profile = Profile.builder()
                .user(user)
                .nickname(request.getNickname())
                .goalType(request.getGoalType())
                .goalCustom(request.getGoalCustom())
                .screenTimeGoalType(request.getScreenTimeGoalType())
                .screenTimeGoalCustom(request.getScreenTimeGoalCustom())
                .build();

        profileRepository.save(profile);
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        profile.updateProfile(
                profile.getNickname(),
                request.getGoalType(),
                request.getGoalCustom(),
                request.getScreenTimeGoalType(),
                request.getScreenTimeGoalCustom()
        );
    }

}