package com.amr.uniVerse.feed.service;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.feed.dto.*;
import com.amr.uniVerse.feed.entity.Comment;
import com.amr.uniVerse.feed.entity.Post;
import com.amr.uniVerse.feed.entity.PostLike;
import com.amr.uniVerse.feed.repository.CommentRepository;
import com.amr.uniVerse.feed.repository.PostLikeRepository;
import com.amr.uniVerse.feed.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    // ---------- POSTS ----------

    @Transactional
    public PostResponse createPost(CreatePostRequest request, User currentUser) {
        Post post = Post.builder()
                .content(request.content())
                .imageUrl(request.imageUrl())
                .author(currentUser)
                .build();

        Post saved = postRepository.save(post);
        return mapToPostResponse(saved, currentUser);
    }

    public FeedPageResponse getAllPosts(int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<PostResponse> posts = postsPage.getContent().stream()
                .map(p -> mapToPostResponse(p, currentUser))
                .toList();

        return new FeedPageResponse(
                posts,
                postsPage.getNumber(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements(),
                postsPage.hasNext()
        );
    }

    public FeedPageResponse getUserPosts(Long userId, int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable);

        List<PostResponse> posts = postsPage.getContent().stream()
                .map(p -> mapToPostResponse(p, currentUser))
                .toList();

        return new FeedPageResponse(
                posts,
                postsPage.getNumber(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements(),
                postsPage.hasNext()
        );
    }

    public PostResponse getPostById(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToPostResponse(post, currentUser);
    }

    @Transactional
    public void deletePost(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isOwner = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You are not allowed to delete this post");
        }

        postRepository.delete(post);
    }

    // ---------- COMMENTS ----------

    @Transactional
    public CommentResponse addComment(Long postId, CreateCommentRequest request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .author(currentUser)
                .content(request.content())
                .build();

        return CommentResponse.fromEntity(commentRepository.save(comment));
    }

    public List<CommentResponse> getComments(Long postId) {
        // Ensure post exists
        postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isOwner = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isPostOwner = comment.getPost().getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isOwner && !isPostOwner && !isAdmin) {
            throw new RuntimeException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }

    // ---------- LIKES ----------

    @Transactional
    public PostResponse toggleLike(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        var existing = postLikeRepository.findByPostIdAndUserId(postId, currentUser.getId());

        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get()); // Unlike
        } else {
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(currentUser)
                    .build();
            postLikeRepository.save(like); // Like
        }

        return mapToPostResponse(post, currentUser);
    }

    // ---------- HELPER ----------

    private PostResponse mapToPostResponse(Post post, User currentUser) {
        long likeCount = postLikeRepository.countByPostId(post.getId());
        long commentCount = commentRepository.countByPostId(post.getId());
        boolean likedByMe = currentUser != null &&
                postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUser.getId());

        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getAuthor().getId(),
                post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName(),
                post.getAuthor().getEmail(),
                likeCount,
                commentCount,
                likedByMe,
                post.getCreatedAt()
        );
    }
}