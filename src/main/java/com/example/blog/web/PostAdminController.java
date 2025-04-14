package com.example.blog.web;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.application.PostQueryService;
import com.example.blog.application.PostService;
import com.example.blog.application.query.PostDTO;
import com.example.blog.utils.Page;
import com.example.blog.utils.mapper.PostMapper;
import com.example.blog.web.dto.PostRequestDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/admin/posts")
@RequiredArgsConstructor
public class PostAdminController {
	
	private final PostService postService;
	private final PostQueryService postQueryService;
	private final PostMapper postMapper;
	
	@GetMapping
	@Transactional(readOnly = true)
	public ResponseEntity<Page<PostDTO>> getAllPosts(Pageable pageable) {
		Page<PostDTO> posts = postQueryService.findAll(pageable.getPageNumber(), pageable.getPageSize());
		return new ResponseEntity<Page<PostDTO>>(posts, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	@Transactional(readOnly = true)
	public ResponseEntity<PostDTO> getPostById(@PathVariable UUID id) {
		return new ResponseEntity<PostDTO>(postQueryService.findById(id), HttpStatus.OK);
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<Void> createNewPost(@Valid @RequestBody PostRequestDto dto) {
		postService.createPost(postMapper.toCreateCommand(dto));
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> editPost(@PathVariable UUID id, @Valid @RequestBody PostRequestDto dto) {
		postService.editPost(id, postMapper.toEditCommand(dto));
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
		postService.deletePost(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
}
