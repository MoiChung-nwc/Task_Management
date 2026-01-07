Task CRUD API (Monolithic) — Spring Boot

Dự án quản lý Task theo mô hình Monolithic, có Auth (Register + Verify Email + Login JWT), RBAC, Task/Subtask/Comment, Notification, Task Logs (audit + change history), pagination + filter, soft delete.

1. Tech stack

Java 17+

Spring Boot 3+

Spring Security (JWT)

Spring Data JPA + Specification

MySQL

Lombok

Validation (jakarta.validation)

2. API Response format

Tất cả API trả về theo format chuẩn:

{
  "success": true,
  "status": 200,
  "code": "SYS_000",
  "message": "Success",
  "data": {},
  "timestamp": "2026-01-06T08:56:29.884681900Z",
  "path": "/api/...",
  "traceId": "uuid"
}


success=false khi lỗi (4xx/5xx)

code theo ErrorCode

traceId sinh mới mỗi response (debug dễ hơn)

3. Setup project
3.1. ENV / application.properties

Ví dụ:

spring.datasource.url=jdbc:mysql://localhost:3306/task_db
spring.datasource.username=root
spring.datasource.password=123456

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT
application.security.jwt.secret-key=YOUR_SECRET_KEY_>=_32_CHARS
application.security.jwt.expiration=86400000
application.security.jwt.refresh-token.expiration=1209600

app.base-url=http://localhost:8080


Lưu ý: secret-key nên >= 32 ký tự.

4. RBAC Design
4.1. Roles

SYSTEM_ADMIN (bypass): toàn quyền hệ thống

USER: chỉ thao tác task mình tạo hoặc task được gán

4.2. Permissions (sample)

TASK_READ

TASK_CREATE

TASK_UPDATE_OWN_OR_ASSIGNED

TASK_DELETE_OWN_OR_ASSIGNED

(mở rộng thêm nếu cần)

JWT sẽ chứa:

{
  "sub": "5",
  "email": "usera@gmail.com",
  "roles": ["USER"],
  "permissions": ["TASK_CREATE","TASK_READ","TASK_UPDATE_OWN_OR_ASSIGNED","TASK_DELETE_OWN_OR_ASSIGNED"]
}

5. Authentication
5.1. Register + Verify Email
Register

POST /api/auth/register

Body:

{
  "email": "usera@gmail.com",
  "password": "123456a@",
  "fullName": "User A"
}


Response:

{
  "success": true,
  "status": 200,
  "code": "SYS_000",
  "message": "Success",
  "data": null
}

Verify

GET /api/auth/verify?token=...

Response:

{
  "success": true,
  "status": 200,
  "code": "SYS_000",
  "message": "Success",
  "data": "Verified successfully"
}

5.2. Login

POST /api/auth/login

Body:

{
  "email": "usera@gmail.com",
  "password": "123456a@"
}


Response (LoginResponse):

{
  "success": true,
  "status": 200,
  "code": "SYS_000",
  "message": "Success",
  "data": {
    "accessToken": "jwt...",
    "refreshToken": "opaque...",
    "userId": 5,
    "email": "usera@gmail.com",
    "roles": ["USER"],
    "permissions": [
      "TASK_UPDATE_OWN_OR_ASSIGNED",
      "TASK_DELETE_OWN_OR_ASSIGNED",
      "TASK_CREATE",
      "TASK_READ"
    ]
  }
}

5.3. Refresh Token

POST /api/auth/refresh

Body:

{
  "refreshToken": "opaque..."
}


Response:

{
  "success": true,
  "status": 200,
  "code": "SYS_000",
  "message": "Success",
  "data": {
    "accessToken": "jwt...",
    "refreshToken": "newOpaque..."
  }
}

5.4. Logout

POST /api/auth/logout

Body:

{
  "refreshToken": "opaque..."
}

6. Postman Setup
6.1. Collection Variables

Tạo biến:

baseUrl = http://localhost:8080

accessToken = (token trả về từ login)

6.2. Authorization Header

Ở các request cần auth:

Key: Authorization
Value: Bearer {{accessToken}}

7. Task APIs
7.1. Create Task

POST /api/tasks

Body:

{
  "title": "Test Create Task",
  "description": "Test Demo",
  "priority": "HIGH",
  "status": "TODO",
  "dueDate": "2026-01-10",
  "tags": ["backend", "spring"],
  "assigneeId": 4
}


Response:

{
  "success": true,
  "data": {
    "id": 2,
    "title": "Test Create Task",
    "status": "TODO",
    "priority": "HIGH",
    "dueDate": "2026-01-10",
    "createdById": 5,
    "assigneeId": 4,
    "tags": ["backend","spring"]
  }
}

7.2. Update Task

PUT /api/tasks/{id}

Body:

{
  "title": "Task updated",
  "priority": "MEDIUM",
  "tags": ["backend", "log"]
}

7.3. Soft Delete Task

DELETE /api/tasks/{id}

Response:

{
  "success": true,
  "data": null
}


Soft delete nghĩa là set deleted_at != null, dữ liệu vẫn còn trong DB.

7.4. List Tasks (Pagination + Filter)

GET /api/tasks?page=0&size=10&status=TODO&priority=HIGH&dueFrom=2026-01-01&dueTo=2026-01-31&tag=spring&assigneeId=4

Response:

{
  "success": true,
  "data": {
    "items": [ { "id": 2, "title": "..." } ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}

7.5. Task Detail (Subtasks + Comments + Logs)

GET /api/tasks/{id}

Response:

{
  "success": true,
  "data": {
    "task": { "id": 2, "title": "..." },
    "subtasks": [],
    "comments": [],
    "logs": []
  }
}

8. Subtask APIs
8.1. Create Subtask

POST /api/tasks/{taskId}/subtasks

Body:

{
  "title": "Design DB",
  "status": "TODO"
}

8.2. Update Subtask

PUT /api/tasks/{taskId}/subtasks/{subtaskId}

Body:

{
  "title": "Design DB (updated)",
  "status": "DONE"
}

8.3. Delete Subtask (soft)

DELETE /api/tasks/{taskId}/subtasks/{subtaskId}

8.4. Detail Subtask

GET /api/tasks/{taskId}/subtasks/{subtaskId}

9. Comment APIs
9.1. Create Comment

POST /api/tasks/{taskId}/comments

Body:

{
  "content": "Test create comment."
}

9.2. List Comments

GET /api/tasks/{taskId}/comments?page=0&size=10

9.3. Update Comment

PUT /api/tasks/{taskId}/comments/{commentId}

Body:

{
  "content": "Test create comment (update)."
}

9.4. Delete Comment (soft)

DELETE /api/tasks/{taskId}/comments/{commentId}

10. Notification APIs
10.1. List My Notifications

GET /api/notifications?unreadOnly=false&page=0&size=10

Response:

{
  "success": true,
  "data": {
    "items": [
      {
        "id": 10,
        "type": "TASK_UPDATED",
        "title": "Task updated",
        "message": "usera@gmail.com updated task #2: \"Test Create Task\"",
        "entityType": "TASK",
        "entityId": 2,
        "readAt": null,
        "createdAt": "2026-01-06T09:10:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}

10.2. Mark Notification as Read

PUT /api/notifications/{id}/read

11. Task Logs (History + changes)

task_logs: lưu event (create/update/assign/status/delete) + actor + createdAt

task_log_changes: lưu các field thay đổi (oldValue/newValue)

Example log record
{
  "id": 100,
  "eventType": "TASK_UPDATED",
  "actorEmail": "usera@gmail.com",
  "createdAt": "2026-01-07T10:00:00Z",
  "changes": [
    { "fieldName": "title", "oldValue": "Old", "newValue": "New" },
    { "fieldName": "priority", "oldValue": "HIGH", "newValue": "MEDIUM" }
  ]
}
