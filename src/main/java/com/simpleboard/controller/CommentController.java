// src/main/java/com/simpleboard/controller/CommentController.java
package com.simpleboard.controller;

import com.simpleboard.domain.Comment;
import com.simpleboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    /** 댓글 목록(Model에 담아 Thymeleaf로 렌더링) */
    @GetMapping
    public String list(@PathVariable Long postId, Model model) {
        List<Comment> comments = commentService.getComments(postId);
        model.addAttribute("comments", comments);
        return "posts/detail :: commentSection";
        // 또는 posts/detail.html 전체 렌더링 시, PostController와 함께 comments를 Model에 담아 사용하세요.
    }

    /** 댓글 작성 */
    @PostMapping
    public String add(@PathVariable Long postId,
                      @RequestParam("content") String content,
                      Principal principal) {
        commentService.addComment(postId, content, principal.getName());
        return "redirect:/posts/" + postId;
    }

    /** 댓글 삭제 */
    @DeleteMapping("/{commentId}")
    public String delete(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return "redirect:/posts/" + postId;
    }
}
