# Task CRUD API (Monolithic) — Spring Boot

Dự án quản lý **Task** theo mô hình **Monolithic** gồm:  
✅ Auth (Register + Verify Email + Login JWT + Refresh/Logout)  
✅ RBAC (Role/Permission trong JWT)  
✅ Task / Subtask / Comment  
✅ Notification (theo task events)  
✅ Task Logs (audit + change history)  
✅ Pagination + Filter + Soft Delete  

---

## 1) Tech Stack

- **Java** 17+
- **Spring Boot** 3+
- **Spring Security (JWT)**
- **Spring Data JPA + Specification**
- **MySQL**
- **Lombok**
- **Jakarta Validation** (`jakarta.validation`)

---

## 2) API Response Format

Tất cả API trả về theo format chuẩn:

```json
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
