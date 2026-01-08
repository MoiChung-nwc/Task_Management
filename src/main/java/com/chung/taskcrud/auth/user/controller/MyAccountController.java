package com.chung.taskcrud.auth.user.controller;

import com.chung.taskcrud.auth.user.dto.request.ChangeMyEmailRequest;
import com.chung.taskcrud.auth.user.dto.request.ChangeMyPasswordRequest;
import com.chung.taskcrud.auth.user.dto.request.UpdateMyProfileRequest;
import com.chung.taskcrud.auth.user.dto.response.MyProfileResponse;
import com.chung.taskcrud.auth.user.dto.response.SimpleMessageResponse;
import com.chung.taskcrud.auth.user.service.MyAccountService;
import com.chung.taskcrud.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Tag(name = "Users - My Account", description = "APIs cho tài khoản đang đăng nhập (me/profile/password/email)")
@SecurityRequirement(name = "bearerAuth")
public class MyAccountController {

    private final MyAccountService myAccountService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Lấy thông tin tài khoản hiện tại (me)",
            description = "Trả về thông tin profile của user đang đăng nhập. Yêu cầu Bearer token."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            )
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MyProfileResponse>> me(
            @Parameter(hidden = true) Authentication authentication,
            HttpServletRequest http
    ) {
        Long userId = (Long) authentication.getPrincipal();
        MyProfileResponse data = myAccountService.getMe(userId);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Cập nhật profile",
            description = "Cập nhật thông tin profile của user đang đăng nhập. Yêu cầu Bearer token."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            )
    })
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MyProfileResponse>> updateProfile(
            @Parameter(hidden = true) Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload cập nhật profile",
                    content = @Content(
                            schema = @Schema(implementation = UpdateMyProfileRequest.class),
                            examples = @ExampleObject(
                                    name = "Update profile example",
                                    value = """
                                    {
                                      "fullName": "Nguyen Van A",
                                      "phone": "0901234567",
                                      "avatarUrl": "https://example.com/avatar.png"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody UpdateMyProfileRequest request,
            HttpServletRequest http
    ) {
        Long userId = (Long) authentication.getPrincipal();
        MyProfileResponse data = myAccountService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Đổi mật khẩu",
            description = "Đổi mật khẩu của user đang đăng nhập. Yêu cầu Bearer token."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đổi mật khẩu thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail / mật khẩu hiện tại sai (tuỳ implement)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            )
    })
    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> changePassword(
            @Parameter(hidden = true) Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload đổi mật khẩu",
                    content = @Content(
                            schema = @Schema(implementation = ChangeMyPasswordRequest.class),
                            examples = @ExampleObject(
                                    name = "Change password example",
                                    value = """
                                    {
                                      "currentPassword": "OldP@ssw0rd123",
                                      "newPassword": "NewP@ssw0rd456"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody ChangeMyPasswordRequest request,
            HttpServletRequest http
    ) {
        Long userId = (Long) authentication.getPrincipal();
        myAccountService.changePassword(userId, request);

        return ResponseEntity.ok(ApiResponse.success(
                SimpleMessageResponse.builder().message("Password updated successfully").build(),
                http.getRequestURI(),
                traceId()
        ));
    }

    @Operation(
            summary = "Đổi email",
            description = "Đổi email của user đang đăng nhập. Thường yêu cầu verify email mới để login. Yêu cầu Bearer token."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đổi email thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail / email không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email đã tồn tại (nếu hệ thống có)",
                    content = @Content
            )
    })
    @PutMapping("/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> changeEmail(
            @Parameter(hidden = true) Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload đổi email",
                    content = @Content(
                            schema = @Schema(implementation = ChangeMyEmailRequest.class),
                            examples = @ExampleObject(
                                    name = "Change email example",
                                    value = """
                                    {
                                      "newEmail": "new-email@example.com",
                                      "password": "P@ssw0rd123"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody ChangeMyEmailRequest request,
            HttpServletRequest http
    ) {
        Long userId = (Long) authentication.getPrincipal();
        myAccountService.changeEmail(userId, request);

        return ResponseEntity.ok(ApiResponse.success(
                SimpleMessageResponse.builder().message("Email updated. Please verify your new email to login.").build(),
                http.getRequestURI(),
                traceId()
        ));
    }
}
