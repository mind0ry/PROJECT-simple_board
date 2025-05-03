// src/main/java/com/simpleboard/controller/PostController.java
package com.simpleboard.controller;

import com.simpleboard.domain.Comment;
import com.simpleboard.domain.Post;
import com.simpleboard.service.CommentService;
import com.simpleboard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    // -------------------- JSON API --------------------
    // 클라이언트가 Accept: application/json 헤더를 보낼 때만 동작

    @GetMapping(headers = "Accept=application/json")
    @ResponseBody
    public List<Post> listApi() {
        return postService.getAllPosts();
    }

    @GetMapping(path = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public Post detailApi(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/new")
    public String writeView(Model model) {
        model.addAttribute("postForm", new PostForm());
        return "posts/new";
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    @ResponseBody
    public Post createApi(@RequestBody PostRequest request, Principal principal) {
        return postService.createPost(request.getTitle(), request.getContent(), principal.getName());
    }

    @PostMapping
    public String write(
            @ModelAttribute("postForm") PostForm form,
            Principal principal
    ) {
        postService.createPost(
                form.getTitle(),
                form.getContent(),
                principal.getName()
        );
        // 생성 후 바로 목록으로 리다이렉트
        return "redirect:/posts";
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, headers = "Accept=application/json")
    @ResponseBody
    public Post updateApi(@PathVariable Long id,
                          @RequestBody PostRequest request,
                          Principal principal) {
        return postService.updatePost(id, request.getTitle(), request.getContent(), principal.getName());
    }

    @DeleteMapping(path = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public void deleteApi(@PathVariable Long id, Principal principal) {
        postService.deletePost(id, principal.getName());
    }

    // -------------------- JSON 댓글 API --------------------
    @PostMapping(
            path = "/{id}/comments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json"
    )
    @ResponseBody
    public CommentResponse addCommentApi(
            @PathVariable Long id,
            @RequestBody CommentRequest request,
            Principal principal
    ) {
        Comment saved = commentService.addComment(id, request.getContent(), principal.getName());
        return new CommentResponse(
                saved.getId(),
                saved.getPost().getId(),
                saved.getUser().getNickname(),
                saved.getContent(),
                saved.getCreatedAt()
        );
    }

    // -------------------- Thymeleaf VIEW --------------------
    // 브라우저 기본 요청(Accept: text/html)일 때 동작

    /** 게시글 목록 뷰 */
    @GetMapping
    public String listView(Model model, Principal principal) {
        String currentUsername = principal != null ? principal.getName() : "guest";
        model.addAttribute("currentUsername", currentUsername);
        model.addAttribute("posts", postService.getAllPosts());
        return "posts/list";
    }

    /** 게시글 상세 뷰 */
    @GetMapping("/{id}")
    public String detailView(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("comments", commentService.getComments(id));
        return "posts/detail";
    }

    /** 댓글 작성 처리 (폼 제출) */
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             Principal principal) {
        commentService.addComment(id, content, principal.getName());
        return "redirect:/posts/" + id;
    }

    // -------------------- DTO --------------------
    public static class PostRequest {
        private String title;
        private String content;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class CommentRequest {
        private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class CommentResponse {
        private Long id;
        private Long postId;
        private String author;      // 닉네임
        private String content;
        private LocalDateTime createdDate;

        public CommentResponse(Long id, Long postId, String author, String content, LocalDateTime createdDate) {
            this.id = id;
            this.postId = postId;
            this.author = author;
            this.content = content;
            this.createdDate = createdDate;
        }
        public Long getId() { return id; }
        public Long getPostId() { return postId; }
        public String getAuthor() { return author; }
        public String getContent() { return content; }
        public LocalDateTime getCreatedDate() { return createdDate; }
    }

    public static class PostForm {
        private String title;
        private String content;
        // getters / setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
