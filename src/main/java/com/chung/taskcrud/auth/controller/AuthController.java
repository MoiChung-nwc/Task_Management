package com.chung.taskcrud.auth.controller;

import com.chung.taskcrud.auth.dto.request.LoginRequest;
import com.chung.taskcrud.auth.dto.request.LogoutRequest;
import com.chung.taskcrud.auth.dto.request.RefreshRequest;
import com.chung.taskcrud.auth.dto.request.RegisterRequest;
import com.chung.taskcrud.auth.dto.response.AuthResponse;
import com.chung.taskcrud.auth.dto.response.LoginResponse;
import com.chung.taskcrud.auth.service.AuthService;
import com.chung.taskcrud.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication APIs: register, verify email, login, refresh, logout")
public class AuthController {

    private final AuthService authService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Register",
            description = "Đăng ký tài khoản mới. Hệ thống thường sẽ gửi email xác thực (nếu có cấu hình)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đăng ký thành công (API đang trả 200)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email/username đã tồn tại (nếu hệ thống có)",
                    content = @Content
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload đăng ký",
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Register example",
                                    value = """
                                    {
                                      "email": "user1@example.com",
                                      "username": "user1",
                                      "password": "P@ssw0rd123",
                                      "fullName": "User One"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest http
    ) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Verify email",
            description = "Xác thực email thông qua token (thường lấy từ link gửi qua email)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Xác thực thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Token không hợp lệ / hết hạn",
                    content = @Content
            )
    })
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(
            @Parameter(description = "Verify token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestParam("token") String token,
            HttpServletRequest http
    ) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Verified successfully", http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Login",
            description = "Đăng nhập để nhận access token (và refresh token nếu hệ thống trả về)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đăng nhập thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Sai tài khoản/mật khẩu hoặc chưa verify email (tuỳ hệ thống)",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload đăng nhập",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Login example",
                                    value = """
                                    {
                                      "username": "user1",
                                      "password": "P@ssw0rd123"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest http
    ) {
        LoginResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Refresh token",
            description = "Dùng refresh token để lấy access token mới."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Refresh thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Refresh token không hợp lệ / hết hạn",
                    content = @Content
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload refresh",
                    content = @Content(
                            schema = @Schema(implementation = RefreshRequest.class),
                            examples = @ExampleObject(
                                    name = "Refresh example",
                                    value = """
                                    {
                                      "refreshToken": "d7b6b4b1-3db1-4f8b-8fe0-3b55c2a7b3a1"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody RefreshRequest request,
            HttpServletRequest http
    ) {
        AuthResponse data = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Logout",
            description = "Đăng xuất. Thường sẽ revoke/blacklist refresh token (tuỳ thiết kế)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Logout thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token không hợp lệ / hết hạn (tuỳ hệ thống)",
                    content = @Content
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload logout",
                    content = @Content(
                            schema = @Schema(implementation = LogoutRequest.class),
                            examples = @ExampleObject(
                                    name = "Logout example",
                                    value = """
                                    {
                                      "refreshToken": "d7b6b4b1-3db1-4f8b-8fe0-3b55c2a7b3a1"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody LogoutRequest request,
            HttpServletRequest http
    ) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }
}
