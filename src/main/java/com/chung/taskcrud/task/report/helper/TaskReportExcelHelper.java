package com.chung.taskcrud.task.report.helper;

import com.chung.taskcrud.task.entity.Task;
import com.chung.taskcrud.task.entity.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.thymeleaf.util.ObjectUtils.nullSafe;

@Component
@RequiredArgsConstructor
public class TaskReportExcelHelper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] buildExcel(List<Task> tasks) {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            createTasksSheet(wb, tasks);
            createSummarySheet(wb, tasks);

            wb.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build excel report: " + e.getMessage(), e);
        }
    }

    private void createTasksSheet(Workbook wb, List<Task> tasks) {
        Sheet sheet = wb.createSheet("Tasks");
        int rowId = 0;

        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row header = sheet.createRow(rowId++);
        String[] cols = {
                "Task ID", "Title", "Description", "Status", "Priority",
                "Due Date", "Tags", "Created By", "Assignee",
                "Created At", "Updated At"
        };

        for (int i = 0; i < cols.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(headerStyle);
        }

        for (Task t : tasks) {
            Row r = sheet.createRow(rowId++);

            r.createCell(0).setCellValue(nullSafeLong(t.getId()));
            r.createCell(1).setCellValue(nullSafe(t.getTitle()));
            r.createCell(2).setCellValue(nullSafe(t.getDescription()));
            r.createCell(3).setCellValue(t.getStatus() != null ? t.getStatus().name() : "");
            r.createCell(4).setCellValue(t.getPriority() != null ? t.getPriority().name() : "");
            r.createCell(5).setCellValue(t.getDueDate() != null ? t.getDueDate().format(DATE_FMT) : "");
            r.createCell(6).setCellValue(tagsCsv(t));
            r.createCell(7).setCellValue(t.getCreatedBy() != null ? nullSafe(t.getCreatedBy().getEmail()) : "");
            r.createCell(8).setCellValue(t.getAssignee() != null ? nullSafe(t.getAssignee().getEmail()) : "");

            r.createCell(9).setCellValue(formatInstant(t.getCreatedAt()));
            r.createCell(10).setCellValue(formatInstant(t.getUpdatedAt()));
        }

        for (int i = 0; i < cols.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createSummarySheet(Workbook wb, List<Task> tasks) {
        Sheet sheet = wb.createSheet("Summary");

        Map<TaskStatus, Long> byStatus = new EnumMap<>(TaskStatus.class);
        for (TaskStatus s : TaskStatus.values()) byStatus.put(s, 0L);
        for (Task t : tasks) {
            TaskStatus s = t.getStatus() != null ? t.getStatus() : TaskStatus.TODO;
            byStatus.put(s, byStatus.get(s) + 1);
        }

        int r = 0;
        Row h = sheet.createRow(r++);
        h.createCell(0).setCellValue("Metric");
        h.createCell(1).setCellValue("Value");

        r = addMetric(sheet, r, "Total tasks", String.valueOf(tasks.size()));
        for (var e : byStatus.entrySet()) {
            r = addMetric(sheet, r, e.getKey().name(), String.valueOf(e.getValue()));
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private int addMetric(Sheet sheet, int rowIdx, String k, String v) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(k);
        row.createCell(1).setCellValue(v);
        return rowIdx + 1;
    }

    private String tagsCsv(Task t) {
        if (t.getTags() == null || t.getTags().isEmpty()) return "";
        // giả định Tag entity có getName()
        return t.getTags().stream()
                .map(tag -> {
                    try {
                        return (String) tag.getClass().getMethod("getName").invoke(tag);
                    } catch (Exception ex) {
                        return tag.toString();
                    }
                })
                .distinct()
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    private String formatInstant(java.time.Instant ins) {
        if (ins == null) return "";
        return DATETIME_FMT.format(ins.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    private String nullSafe(String s) { return s == null ? "" : s; }
    private double nullSafeLong(Long v) { return v == null ? 0 : v; }
}
