package com.example.sns.service;

import com.example.sns.domain.dto.ArticleDto;
import com.example.sns.domain.dto.ArticleImageDto;
import com.example.sns.domain.dto.CommentDto;
import com.example.sns.domain.dto.ResponseDto;
import com.example.sns.domain.entity.Article;
import com.example.sns.domain.entity.ArticleImages;
import com.example.sns.domain.entity.Comment;
import com.example.sns.domain.entity.User;
import com.example.sns.exception.*;
import com.example.sns.repository.FeedImgRepository;
import com.example.sns.repository.FeedRepository;
import com.example.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.example.sns.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final UserRepository userRepository;
    private final FeedRepository feedRepository;
    private final FeedImgRepository feedImgRepository;

    // 기본이미지 경로
    @Value("${default.image.address}")
    private String defaultImageAddress;

    // 피드 등록
    @Transactional
    public ResponseDto createFeed(List<MultipartFile> files, ArticleDto articleDto) {
        // 사용자 정보 가져오기
        String username = getUserName();
        User findUser =
                userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        // 게시물 등록
        Article saveArticle = feedRepository.save(
                Article.builder()
                        .title(articleDto.getTitle())
                        .content(articleDto.getContent())
                        .isDelete(false)
                        .deletedAt(null)
                        .user(findUser)
                        .build());
        // 이미지가 있을 때
        if(files != null) {
            // draft true로 설정
            saveArticle.setDraft(true);
            // 폴더 생성
            String imgDir = String.format("img/feed/%d/", findUser.getId());
            try {
                Files.createDirectories(Path.of(imgDir));
            } catch (IOException e){
                throw new CommonException(FOLDER_CREATE_ERROR, FOLDER_CREATE_ERROR.getMessage());
            }
            // 이미지 저장
            for (MultipartFile file : files) {

                String originName = file.getOriginalFilename();
                String path = imgDir + originName;

                try {
                    file.transferTo(Path.of(path));
                } catch (IOException e) {
                    throw new CommonException(UPLOAD_ERROR, UPLOAD_ERROR.getMessage());
                }

                ArticleImages articleImages = ArticleImages.builder()
                        .article(saveArticle)
                        .imageUrl(path)
                        .build();
                feedImgRepository.save(articleImages);
            }
        } else {
            saveArticle.setDraft(false);
        }

        return new ResponseDto("피드가 등록되었습니다.");
    }

    // 피드 목록 조회
    public List<ArticleDto> readAllFeed(String username) {

        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        List<ArticleDto> list = new ArrayList<>();

        for (Article article : findUser.getArticles()) {
            // 이미지 파일이 있는 경우와 없는 경우로 나눔
            ArticleDto articleDto;
            if(article.getDraft()) {
                articleDto = ArticleDto.builder()
                        .id(article.getId())
                        .username(article.getUser().getUsername())
                        .title(article.getTitle())
                        .thumbImgUrl(article.getArticleImages().get(0).getImageUrl())
                        .build();
            } else {
                articleDto = ArticleDto.builder()
                        .id(article.getId())
                        .username(article.getUser().getUsername())
                        .title(article.getTitle())
                        .thumbImgUrl(defaultImageAddress)
                        .build();
            }
            list.add(articleDto);
        }
        if(list.isEmpty())
            throw new CommonException(FEED_NOT_FOUND, FEED_NOT_FOUND.getMessage());
        return list;
    }

    // 피드 단독 조회
    public ArticleDto readOne(Long articleId) {
        // 로그인 확인
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));

        Article findFeed = feedRepository.findById(articleId).orElseThrow(() -> new CommonException(FEED_NOT_FOUND, FEED_NOT_FOUND.getMessage()));
        
        // 이미지 url 추가
        List<ArticleImageDto> imgUrls = new ArrayList<>();
        for (ArticleImages articleImage : findFeed.getArticleImages()) {
            imgUrls.add(ArticleImageDto.builder()
                            .id(articleImage.getId())
                            .imgUrl(articleImage.getImageUrl())
                            .build());
        }
        // 댓글 목록 추가
        List<CommentDto> comments = new ArrayList<>();
        for (Comment comment : findFeed.getComments()) {
            comments.add(CommentDto.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .build());
        }
        return ArticleDto.builder()
                .title(findFeed.getTitle())
                .content(findFeed.getContent())
                .imgUrls(imgUrls)
                .comments(comments)
                .likeCount(findFeed.getLikeArticles().size())
                .build();
    }

    // 피드 수정
    @Transactional
    public ResponseDto updateFeed(Long articleId, List<MultipartFile> files, ArticleDto articleDto) {
        // 사용자 인증
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Article findArticle = feedRepository.findById(articleId).orElseThrow(() -> new CommonException(FEED_NOT_FOUND, FEED_NOT_FOUND.getMessage()));

        // 게시글을 수정할 수 있는 사용자 인지 확인
        validationCheck(findUser, findArticle);

        // 피드 수정
        findArticle.updateArticle(articleDto);

        // 이미지 수정
        // 이미지 추가 항목이 있으면
        if(files != null) {
            // 폴더 생성
            String imgDir = String.format("img/feed/%d/", findUser.getId());
            try {
                Files.createDirectories(Path.of(imgDir));
            } catch (IOException e){
                throw new CommonException(FOLDER_CREATE_ERROR, FOLDER_CREATE_ERROR.getMessage());
            }
            // 이미지 저장
            for (MultipartFile file : files) {

                String originName = file.getOriginalFilename();
                String path = imgDir + originName;

                try {
                    file.transferTo(Path.of(path));
                } catch (IOException e) {
                    throw new CommonException(UPLOAD_ERROR, UPLOAD_ERROR.getMessage());
                }

                feedImgRepository.save(ArticleImages.builder()
                        .article(findArticle)
                        .imageUrl(path)
                        .build());
            }
        }

        // 삭제할 이미지가 있으면
        if(articleDto.getImgUrls() != null) {
            for (ArticleImageDto image : articleDto.getImgUrls()) {
                ArticleImages findImg =
                            feedImgRepository.findById(image.getId()).orElseThrow(() -> new CommonException(IMAGE_NOT_FOUND, IMAGE_NOT_FOUND.getMessage()));
                try {
                    // 실제 파일 삭제
                    File file = new File(findImg.getImageUrl());
                    boolean delete = file.delete();

                    // DB에서 삭제
                    feedImgRepository.deleteById(findImg.getId());
                } catch (Exception e) {
                    throw new CommonException(SERVER_ERROR,SERVER_ERROR.getMessage());
                }
            }
        }

        return new ResponseDto("피드가 수정되었습니다");
    }

    // 피드 삭제
    // soft delete 적용
    @Transactional
    public ResponseDto deleteFeed(Long articleId) {
        String username = getUserName();
        User findUser = userRepository.findByUsername(username).orElseThrow(() -> new CommonException(USER_NOT_FOUND, USER_NOT_FOUND.getMessage()));
        Article findArticle = feedRepository.findById(articleId).orElseThrow(() -> new CommonException(FEED_NOT_FOUND, FEED_NOT_FOUND.getMessage()));

        // 게시글을 수정할 수 있는 사용자 인지 확인
        validationCheck(findUser, findArticle);

        // 이미지 삭제
        List<ArticleImages> articleImages = feedImgRepository.findByArticleId(findArticle.getId());
        for (ArticleImages articleImage : articleImages) {
            try {
                // 실제 파일 삭제
                File file = new File(articleImage.getImageUrl());
                boolean delete = file.delete();

                // DB에서 삭제
                feedImgRepository.deleteById(articleImage.getId());
            } catch (Exception e) {
                throw new CommonException(SERVER_ERROR,SERVER_ERROR.getMessage());
            }
        }

        // 피드 삭제
        if(findArticle.getId().equals(articleId)) {
            feedRepository.deleteById(articleId);
        } else {
            throw new CommonException(FEED_NOT_FOUND, FEED_NOT_FOUND.getMessage());
        }

        return new ResponseDto("피드가 삭제되었습니다");
    }

    private static void validationCheck(User findUser, Article findArticle) {
        // 사용자가 피드 소유자인지 확인
        if(!findArticle.getUser().equals(findUser)) {
            throw new CommonException(FEED_USER_NOT_MATCH, FEED_USER_NOT_MATCH.getMessage());
        }
    }

    private static String getUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
