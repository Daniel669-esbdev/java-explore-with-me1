package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.CommentService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Slf4j
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Admin DELETE /admin/comments/{}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}