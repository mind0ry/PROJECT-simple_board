package com.simpleboard.service;

import com.simpleboard.domain.Comment;
import com.simpleboard.domain.Post;
import com.simpleboard.domain.User;
import com.simpleboard.exception.UnauthorizedAccessException;
import com.simpleboard.repository.CommentRepository;
import com.simpleboard.repository.PostRepository;
import com.simpleboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /** 게시글 ID로 댓글 목록 조회 */
    @Transactional(readOnly = true)
    public List<Comment> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));
        return commentRepository.findByPost(post);
    }

    /** 댓글 추가 */
    @Transactional
    public Comment addComment(Long postId, String content, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + postId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다. username=" + username));

        Comment comment = new Comment(post, user, content);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    /** 댓글 삭제 (작성자만) */
    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다. id=" + commentId));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("본인만 삭제 가능합니다.");
        }
        commentRepository.delete(comment);
    }
}
