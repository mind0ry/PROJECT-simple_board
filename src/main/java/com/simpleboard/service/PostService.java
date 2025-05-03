package com.simpleboard.service;

import com.simpleboard.domain.Post;
import com.simpleboard.domain.User;
import com.simpleboard.repository.PostRepository;
import com.simpleboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 작성: 로그인한 사용자(username)로부터 nickname을 조회하여 author에 저장
     */
    public Post createPost(String title, String content, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        String nickname = user.getNickname();
        Post post = new Post(title, content, nickname);
        return postRepository.save(post);
    }

    /** 전체 게시글 조회 */
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    /** 단일 게시글 조회 */
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 글이 없습니다. id=" + id));
    }

    /** 게시글 수정: 로그인 사용자(username)의 nickname이 author와 일치해야 수정 가능 */
    public Post updatePost(Long id, String title, String content, String username) {
        Post post = getPostById(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        String nickname = user.getNickname();
        if (!post.getAuthor().equals(nickname)) {
            throw new AccessDeniedException("작성자가 아닙니다.");
        }
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    /** 게시글 삭제: 로그인 사용자(username)의 nickname이 author와 일치해야 삭제 가능 */
    public void deletePost(Long id, String username) {
        Post post = getPostById(id);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        String nickname = user.getNickname();
        if (!post.getAuthor().equals(nickname)) {
            throw new AccessDeniedException("작성자가 아닙니다.");
        }
        postRepository.delete(post);
    }
}

