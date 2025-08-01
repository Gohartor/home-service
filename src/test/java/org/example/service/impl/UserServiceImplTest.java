package org.example.service.impl;

import org.example.dto.admin.UserSearchFilterDto;
import org.example.dto.customer.CustomerLoginDto;
import org.example.dto.customer.CustomerRegisterDto;
import org.example.dto.customer.CustomerUpdateProfileDto;
import org.example.dto.expert.*;
import org.example.entity.Service;
import org.example.entity.User;
import org.example.entity.enumerator.ExpertStatus;
import org.example.entity.enumerator.RoleType;
import org.example.exception.DuplicateResourceException;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.service.OrderService;
import org.example.service.ServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository repository;

    @Mock
    UserMapper userMapper;

    @Spy
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    ServiceService serviceService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    MultipartFile mockFile;

    @Mock
    ExpertLoginRequestDto expertLoginRequestDto;

    @Mock
    CustomerLoginDto customerLoginDto;

    @Mock
    ExpertUpdateProfileDto expertUpdateProfileDto;

    @Mock
    OrderService orderService;

    @Mock
    CustomerUpdateProfileDto customerUpdateProfileDto;

    @Mock
    FileStorageService fileStorageService;

    @Mock
    ExpertRegisterDto expertRegisterDto;


    @Test
    void listPendingExperts() {
        User user1 = new User(); user1.setId(1L);
        User user2 = new User(); user2.setId(2L);

        List<User> fakeUsers = Arrays.asList(user1, user2);

        when(repository.findByRoleAndExpertStatus(RoleType.EXPERT, ExpertStatus.NEW))
                .thenReturn(fakeUsers);

        ExpertResponseDto dto1 = mock(ExpertResponseDto.class);
        ExpertResponseDto dto2 = mock(ExpertResponseDto.class);

        when(userMapper.toExpertResponseDto(user1)).thenReturn(dto1);
        when(userMapper.toExpertResponseDto(user2)).thenReturn(dto2);

        List<ExpertResponseDto> result = userService.listPendingExperts();

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
        verify(repository, times(1)).findByRoleAndExpertStatus(RoleType.EXPERT, ExpertStatus.NEW);
        verify(userMapper, times(1)).toExpertResponseDto(user1);
        verify(userMapper, times(1)).toExpertResponseDto(user2);
    }



    @Test
    void approveExpert_shouldSetExpertStatusToApproved_andSaveUser() {

        Long expertId = 123L;
        User expert = new User();
        expert.setId(expertId);
        expert.setRole(RoleType.EXPERT);
        expert.setExpertStatus(ExpertStatus.NEW);

        when(repository.findByIdAndRole(expertId, RoleType.EXPERT))
                .thenReturn(Optional.of(expert));

       userService.approveExpert(expertId);


        assertEquals(ExpertStatus.APPROVED, expert.getExpertStatus());
        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verify(repository).save(expert);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void approveExpert_shouldThrowNotFoundExceptionIfUserNotFound() {
        Long expertId = 404L;
        when(repository.findByIdAndRole(expertId, RoleType.EXPERT)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            userService.approveExpert(expertId);
        });
        assertEquals("Expert not found", ex.getMessage());

        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verifyNoMoreInteractions(repository);
    }



    @Test
    void rejectExpert_shouldSetExpertStatusToRejected_andSaveUser() {

        Long expertId = 199L;
        User expert = new User();
        expert.setId(expertId);
        expert.setRole(RoleType.EXPERT);
        expert.setExpertStatus(ExpertStatus.NEW);

        when(repository.findByIdAndRole(expertId, RoleType.EXPERT))
                .thenReturn(Optional.of(expert));


        userService.rejectExpert(expertId);


        assertEquals(ExpertStatus.REJECTED, expert.getExpertStatus());
        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verify(repository).save(expert);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void rejectExpert_shouldThrowNotFoundExceptionIfUserNotFound() {

        Long expertId = 404L;
        when(repository.findByIdAndRole(expertId, RoleType.EXPERT)).thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            userService.rejectExpert(expertId);
        });
        assertEquals("Expert not found", ex.getMessage());

        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verifyNoMoreInteractions(repository);
    }




    @Test
    void addExpertToService_shouldAddExpertAndServiceAndSaveBoth() {

        Long expertId = 10L;
        Long serviceId = 20L;


        User expert = new User();
        expert.setId(expertId);
        expert.setRole(RoleType.EXPERT);
        expert.setExpertStatus(ExpertStatus.APPROVED);
        expert.setServices(new HashSet<>());


        Service service = new Service();
        service.setId(serviceId);
        service.setExperts(new HashSet<>());

        when(repository.findByIdAndRole(expertId, RoleType.EXPERT))
                .thenReturn(Optional.of(expert));
        when(serviceService.findEntityById(serviceId))
                .thenReturn(Optional.of(service));


        userService.addExpertToService(expertId, serviceId);


        assertTrue(expert.getServices().contains(service), "Expert must contain the service in services set");
        assertTrue(service.getExperts().contains(expert), "Service must contain the expert in experts set");
        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verify(serviceService).findEntityById(serviceId);
        verify(repository).save(expert);
        verify(serviceService).save(service);
        verifyNoMoreInteractions(repository, serviceService);
    }


    @Test
    void addExpertToService_shouldThrowNotFoundException_whenExpertNotFound() {

        Long expertId = 21L, serviceId = 31L;
        when(repository.findByIdAndRole(expertId, RoleType.EXPERT)).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.addExpertToService(expertId, serviceId);
        });
        assertEquals("Expert not found", exception.getMessage());
        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verifyNoMoreInteractions(repository, serviceService);
    }

    @Test
    void addExpertToService_shouldThrowNotFoundException_whenServiceNotFound() {

        Long expertId = 22L, serviceId = 33L;
        User expert = new User();
        expert.setId(expertId);
        expert.setRole(RoleType.EXPERT);
        expert.setServices(new HashSet<>());
        when(repository.findByIdAndRole(expertId, RoleType.EXPERT)).thenReturn(Optional.of(expert));
        when(serviceService.findEntityById(serviceId)).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.addExpertToService(expertId, serviceId);
        });
        assertEquals("Service not found", exception.getMessage());

        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verify(serviceService).findEntityById(serviceId);
        verifyNoMoreInteractions(repository, serviceService);
    }






    @Test
    void removeExpertFromService_shouldRemoveExpertAndServiceAndSaveBoth() {

        Long expertId = 55L, serviceId = 88L;

        User expert = new User();
        expert.setId(expertId);
        expert.setRole(RoleType.EXPERT);
        expert.setServices(new HashSet<>());

        Service service = new Service();
        service.setId(serviceId);
        service.setExperts(new HashSet<>());


        expert.getServices().add(service);
        service.getExperts().add(expert);

        when(repository.findByIdAndRole(expertId, RoleType.EXPERT))
                .thenReturn(Optional.of(expert));
        when(serviceService.findEntityById(serviceId))
                .thenReturn(Optional.of(service));


        userService.removeExpertFromService(expertId, serviceId);


        assertFalse(expert.getServices().contains(service));
        assertFalse(service.getExperts().contains(expert));
        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verify(serviceService).findEntityById(serviceId);
        verify(repository).save(expert);
        verify(serviceService).save(service);
        verifyNoMoreInteractions(repository, serviceService);
    }

    @Test
    void removeExpertFromService_shouldThrowNotFoundException_whenExpertNotFound() {

        Long expertId = 60L, serviceId = 70L;
        when(repository.findByIdAndRole(expertId, RoleType.EXPERT)).thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                userService.removeExpertFromService(expertId, serviceId)
        );
        assertEquals("Expert not found", ex.getMessage());
        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verifyNoMoreInteractions(repository, serviceService);
    }


    @Test
    void removeExpertFromService_shouldThrowNotFoundException_whenServiceNotFound() {

        Long expertId = 61L, serviceId = 71L;
        User expert = new User();
        expert.setId(expertId);
        expert.setRole(RoleType.EXPERT);
        expert.setServices(new HashSet<>());

        when(repository.findByIdAndRole(expertId, RoleType.EXPERT))
                .thenReturn(Optional.of(expert));
        when(serviceService.findEntityById(serviceId)).thenReturn(Optional.empty());


        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                userService.removeExpertFromService(expertId, serviceId)
        );
        assertEquals("Service not found", ex.getMessage());

        verify(repository).findByIdAndRole(expertId, RoleType.EXPERT);
        verify(serviceService).findEntityById(serviceId);
        verifyNoMoreInteractions(repository, serviceService);
    }










    @Test
    void registerCustomer_shouldSaveCustomer_whenInputValid() {

        CustomerRegisterDto dto = mock(CustomerRegisterDto.class);
        when(dto.email()).thenReturn("reza@site.com");
        when(dto.password()).thenReturn("customer123");
        when(repository.existsByEmail("reza@site.com")).thenReturn(false);

        User user = new User();
        when(userMapper.fromCustomerRegisterDto(dto)).thenReturn(user);
        when(passwordEncoder.encode("customer123")).thenReturn("encoded-xyz");


        userService.registerCustomer(dto);


        assertEquals("encoded-xyz", user.getPassword());
        verify(repository).existsByEmail("reza@site.com");
        verify(userMapper).fromCustomerRegisterDto(dto);
        verify(passwordEncoder).encode("customer123");
        verify(repository).save(user);
    }

    @Test
    void registerCustomer_shouldThrowDuplicateResource_whenEmailExists() {

        CustomerRegisterDto dto = mock(CustomerRegisterDto.class);
        when(dto.email()).thenReturn("r@dup.com");
        when(repository.existsByEmail("r@dup.com")).thenReturn(true);


        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> userService.registerCustomer(dto)
        );
        assertEquals("Email already in use!", ex.getMessage());
        verify(repository).existsByEmail("r@dup.com");
        verifyNoMoreInteractions(repository, userMapper, passwordEncoder);
    }






    @Test
    void saveProfileImage_success() throws Exception {
        String email = "test@example.com";
        String originalName = "x.png";
        when(mockFile.getOriginalFilename()).thenReturn(originalName);
        doNothing().when(mockFile).transferTo(any(Path.class));

        String filename = userService.saveProfileImage(mockFile, email);

        assertTrue(filename.startsWith("expert_" + email));
        assertTrue(filename.endsWith(".png"));
        verify(mockFile).transferTo(any(Path.class));
    }

    @Test
    void saveProfileImage_handlesIOException() throws Exception {
        when(mockFile.getOriginalFilename()).thenReturn("a.png");
        doThrow(new IOException("fail")).when(mockFile).transferTo(any(Path.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.saveProfileImage(mockFile, "a@b.com"));
        assertTrue(ex.getMessage().contains("Failed to save"));
    }





    @Test
    void loginExpert_shouldThrowIllegalArg_whenEmailNotFound() {
        when(expertLoginRequestDto.email()).thenReturn("notfound@x.com");
        when(repository.findByEmail("notfound@x.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.loginExpert(expertLoginRequestDto)
        );
        assertEquals("Invalid email or password.", ex.getMessage());
        verify(repository).findByEmail("notfound@x.com");
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void loginExpert_shouldThrowIllegalState_whenExpertStatusIsNotApproved() {
        User expert = new User();
        expert.setExpertStatus(ExpertStatus.PENDING);
        when(expertLoginRequestDto.email()).thenReturn("expert@y.com");
        when(repository.findByEmail("expert@y.com")).thenReturn(Optional.of(expert));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.loginExpert(expertLoginRequestDto)
        );
        assertEquals("Your account is not approved yet.", ex.getMessage());
        verify(repository).findByEmail("expert@y.com");
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void loginExpert_shouldThrowIllegalArg_whenPasswordNotMatch() {
        User expert = new User();
        expert.setExpertStatus(ExpertStatus.APPROVED);
        expert.setPassword("hashed");
        when(expertLoginRequestDto.email()).thenReturn("k@k.com");
        when(expertLoginRequestDto.password()).thenReturn("wrong");
        when(repository.findByEmail("k@k.com")).thenReturn(Optional.of(expert));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.loginExpert(expertLoginRequestDto)
        );
        assertEquals("Invalid email or password.", ex.getMessage());
        verify(repository).findByEmail("k@k.com");
        verify(passwordEncoder).matches("wrong", "hashed");
    }






    @Test
    void loginCustomer_shouldSucceed_whenInputIsValid() {
        User customer = new User();
        customer.setExpertStatus(ExpertStatus.APPROVED);
        customer.setPassword("storedHash");
        when(customerLoginDto.email()).thenReturn("ali@site.com");
        when(customerLoginDto.password()).thenReturn("mypassword");
        when(repository.findByEmail("ali@site.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("mypassword", "storedHash")).thenReturn(true);

        assertDoesNotThrow(() -> userService.loginCustomer(customerLoginDto));
        verify(repository).findByEmail("ali@site.com");
        verify(passwordEncoder).matches("mypassword", "storedHash");
    }

    @Test
    void loginCustomer_shouldThrowIllegalArg_whenEmailNotFound() {
        when(customerLoginDto.email()).thenReturn("notfound@site.com");
        when(repository.findByEmail("notfound@site.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.loginCustomer(customerLoginDto));
        assertEquals("Invalid email or password.", ex.getMessage());
        verify(repository).findByEmail("notfound@site.com");
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void loginCustomer_shouldThrowIllegalState_whenStatusNotApproved() {
        User customer = new User();
        customer.setExpertStatus(ExpertStatus.PENDING);
        when(customerLoginDto.email()).thenReturn("ali2@site.com");
        when(repository.findByEmail("ali2@site.com")).thenReturn(Optional.of(customer));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.loginCustomer(customerLoginDto));
        assertEquals("Your account is not approved yet.", ex.getMessage());
        verify(repository).findByEmail("ali2@site.com");
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void loginCustomer_shouldThrowIllegalArg_whenPasswordNotMatch() {
        User customer = new User();
        customer.setExpertStatus(ExpertStatus.APPROVED);
        customer.setPassword("storedHash");
        when(customerLoginDto.email()).thenReturn("ali3@site.com");
        when(customerLoginDto.password()).thenReturn("wrongpass");
        when(repository.findByEmail("ali3@site.com")).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrongpass", "storedHash")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.loginCustomer(customerLoginDto));
        assertEquals("Invalid email or password.", ex.getMessage());
        verify(repository).findByEmail("ali3@site.com");
        verify(passwordEncoder).matches("wrongpass", "storedHash");
    }





    @Test
    void updateExpertProfile_shouldUpdateExpertAndSavePhoto_whenInputValid() throws Exception {
        Long expertId = 10L;
        User user = new User();
        user.setId(expertId);
        user.setRole(RoleType.EXPERT);
        user.setExpertStatus(ExpertStatus.APPROVED);


        MockMultipartFile photo = new MockMultipartFile("profile", "ali.jpg", "image/jpg", "img".getBytes());

        when(repository.findById(expertId)).thenReturn(Optional.of(user));
        when(orderService.hasActiveOrderForExpert(expertId)).thenReturn(false);
        when(expertUpdateProfileDto.profilePhoto()).thenReturn(photo);


        userService.updateExpertProfile(expertId, expertUpdateProfileDto);

        verify(repository).findById(expertId);
        verify(orderService).hasActiveOrderForExpert(expertId);
        verify(userMapper).updateExpertProfileFromDto(expertUpdateProfileDto, user);
        verify(repository).save(user);

        assertNotNull(user.getProfilePhoto());
        assertEquals(ExpertStatus.PENDING, user.getExpertStatus());
    }





    @Test
    void updateExpertProfile_shouldThrowIllegalArg_whenExpertNotFound() {
        Long expertId = 33L;
        when(repository.findById(expertId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateExpertProfile(expertId, expertUpdateProfileDto));

        assertEquals("Expert not found.", ex.getMessage());
        verify(repository).findById(expertId);
        verifyNoMoreInteractions(orderService, userMapper, repository);
    }

    @Test
    void updateExpertProfile_shouldThrowIllegalArg_whenUserIsNotExpert() {
        Long expertId = 44L;
        User user = new User();
        user.setId(expertId);
        user.setRole(RoleType.CUSTOMER);

        when(repository.findById(expertId)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateExpertProfile(expertId, expertUpdateProfileDto));

        assertEquals("User is not expert.", ex.getMessage());
        verify(repository).findById(expertId);
        verifyNoMoreInteractions(orderService, userMapper, repository);
    }

    @Test
    void updateExpertProfile_shouldThrowIllegalState_whenExpertHasActiveOrder() {
        Long expertId = 55L;
        User user = new User();
        user.setId(expertId);
        user.setRole(RoleType.EXPERT);

        when(repository.findById(expertId)).thenReturn(Optional.of(user));
        when(orderService.hasActiveOrderForExpert(expertId)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.updateExpertProfile(expertId, expertUpdateProfileDto));

        assertEquals("You cannot update your profile while you have active jobs.", ex.getMessage());
        verify(repository).findById(expertId);
        verify(orderService).hasActiveOrderForExpert(expertId);
        verifyNoMoreInteractions(userMapper, repository);
    }

    @Test
    void updateExpertProfile_shouldThrowRuntimeExc_whenPhotoSaveFails() throws Exception {
        Long expertId = 66L;
        User user = new User();
        user.setId(expertId);
        user.setRole(RoleType.EXPERT);

        MultipartFile badPhoto = mock(MultipartFile.class);
        when(repository.findById(expertId)).thenReturn(Optional.of(user));
        when(orderService.hasActiveOrderForExpert(expertId)).thenReturn(false);
        when(expertUpdateProfileDto.profilePhoto()).thenReturn(badPhoto);
        when(badPhoto.isEmpty()).thenReturn(false);

        doThrow(new IOException("fail"))
                .when(badPhoto).transferTo(any(Path.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateExpertProfile(expertId, expertUpdateProfileDto));
        assertEquals("Failed to save profile photo", ex.getMessage());
        assertTrue(ex.getCause() instanceof IOException);
    }







    @Test
    void updateCustomerProfile_shouldUpdateAndSave_whenInputValid() {
        Long customerId = 77L;
        User user = new User();
        user.setId(customerId);
        user.setRole(RoleType.CUSTOMER);

        when(repository.findById(customerId)).thenReturn(Optional.of(user));

        userService.updateCustomerProfile(customerId, customerUpdateProfileDto);


        verify(repository).findById(customerId);
        verify(userMapper).updateCustomerProfileFromDto(customerUpdateProfileDto, user);
        verify(repository).save(user);
    }

    @Test
    void updateCustomerProfile_shouldThrowIllegalArg_whenCustomerNotFound() {
        Long customerId = 88L;
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateCustomerProfile(customerId, customerUpdateProfileDto));

        assertEquals("Customer not found.", ex.getMessage());
        verify(repository).findById(customerId);
        verifyNoMoreInteractions(userMapper, repository);
    }

    @Test
    void updateCustomerProfile_shouldThrowIllegalArg_whenUserIsNotCustomer() {
        Long customerId = 99L;
        User user = new User();
        user.setId(customerId);
        user.setRole(RoleType.EXPERT);

        when(repository.findById(customerId)).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.updateCustomerProfile(customerId, customerUpdateProfileDto));

        assertEquals("User is not expert.", ex.getMessage());
        verify(repository).findById(customerId);
        verifyNoMoreInteractions(userMapper, repository);
    }







    @Test
    void searchUsers_shouldCallRepositoryWithSpecificationAndReturnResult() {

        UserSearchFilterDto filter = new UserSearchFilterDto(
                RoleType.EXPERT,
                "ali",
                "painting",
                4.0,
                5.0,
                2,
                20
        );

        Page<User> expected = new PageImpl<>(List.of(new User(), new User()));
        ArgumentCaptor<Specification<User>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expected);


        Page<User> result = userService.searchUsers(filter);


        verify(repository).findAll(specCaptor.capture(), pageableCaptor.capture());
        assertSame(expected, result);

        Pageable usedPageable = pageableCaptor.getValue();
        assertEquals(2, usedPageable.getPageNumber());
        assertEquals(20, usedPageable.getPageSize());

        assertNotNull(specCaptor.getValue());
    }


    @Test
    void searchUsers_shouldUseDefaultPageable_whenPageAndSizeIsNull() {

        UserSearchFilterDto filter = new UserSearchFilterDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Page<User> expected = Page.empty();
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expected);


        Page<User> result = userService.searchUsers(filter);


        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        assertSame(expected, result);

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }






    @Test
    void registerExpert_shouldSaveExpertAndReturnProfileDto_whenSuccess() throws Exception {

        String photoUrl = "http://mycdn/1.jpg";

        try (MockedStatic<FileStorageService> storageMock = mockStatic(FileStorageService.class)) {
            storageMock.when(() -> FileStorageService.saveProfilePhoto(mockFile)).thenReturn(photoUrl);

            User expert = new User();
            expert.setProfilePhoto(photoUrl);
            expert.setExpertStatus(ExpertStatus.PENDING);

            ExpertProfileDto expertProfileDto = mock(ExpertProfileDto.class);
            when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(userMapper.mapToProfileDto(any(User.class))).thenReturn(expertProfileDto);

            ExpertProfileDto result = userService.registerExpert(expertRegisterDto, mockFile);

            assertSame(expertProfileDto, result);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(repository).save(userCaptor.capture());
            User savedExpert = userCaptor.getValue();
            assertEquals(ExpertStatus.PENDING, savedExpert.getExpertStatus());
            assertEquals(photoUrl, savedExpert.getProfilePhoto());

            verify(userMapper).mapToProfileDto(savedExpert);
        }
    }

    @Test
    void registerExpert_shouldThrowRuntimeException_whenProfilePhotoIoFails() throws Exception {

        IOException ioException = new IOException("disk error");
        try (MockedStatic<FileStorageService> storageMock = mockStatic(FileStorageService.class)) {
            storageMock.when(() -> FileStorageService.saveProfilePhoto(mockFile))
                    .thenThrow(ioException);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userService.registerExpert(expertRegisterDto, mockFile));

            assertEquals("Failed to save profile photo", ex.getMessage());
            assertSame(ioException, ex.getCause());

            verify(repository, never()).save(any());
            verify(userMapper, never()).mapToProfileDto(any());
        }
    }


}