package com.example.blog;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.domain.Post;
import com.example.blog.domain.PostRepository;
import com.example.blog.domain.valueobject.Author;
import com.example.blog.domain.valueobject.PostId;
import com.example.blog.helper.Posts;
import com.example.blog.infrastructure.jpa.entity.PostEntity;
import com.example.blog.infrastructure.jpa.repository.PostEntityRepository;
import com.example.blog.web.dto.PostRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Integration Tests")
class BlogApplicationTests {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PostEntityRepository postEntityRepository;
	@Autowired
	private PostRepository postRepository;
	
	// Data test
	private Post post1;
    private Post post2;

    @BeforeEach
	void setup() {
        UUID authorUuid1 = UUID.randomUUID();
        Author author1 = new Author(authorUuid1.toString());
		post1 = new Post("foo 1", "bar 1", author1);

        Author author2 = new Author(UUID.randomUUID().toString());
		post2 = new Post("foo 2", "bar 2", author2);
		
		// delete all records from database
		postEntityRepository.deleteAll();
		assertEquals(0, postEntityRepository.count());
	}
	
	// **** GET Posts ****
	
	@Test
	@DisplayName("GET /admin/posts should return the first page of posts")
    void getPosts_shouldReturnPageOfPosts() throws Exception {
		// Arrange
		postRepository.save(post1);
		postRepository.save(post2);
		
		// Act & Assert
		mvc.perform(get("/admin/posts")
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content.[0].title").value(post1.getTitle()))
			.andExpect(jsonPath("$.content.[1].title").value(post2.getTitle()))
			.andExpect(jsonPath("$.pageNumber").value(0))
			.andExpect(jsonPath("$.pageSize").value(20))
			.andExpect(jsonPath("$.totalElements").value(2))
			.andExpect(jsonPath("$.totalPages").value(1));
	}
	
	@Test
	@DisplayName("GET /admin/posts with pagination parameters should return the correct page")
	void getPosts_withPaginationParam_shouldReturnPageOfPosts() throws Exception {
		// Arrange
		postRepository.save(post1);
		postRepository.save(post2);
		
		// Act & Assert
		// Get page 2
		mvc.perform(get("/admin/posts?page=1")
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content").isEmpty())
			.andExpect(jsonPath("$.pageNumber").value(1))
			.andExpect(jsonPath("$.pageSize").value(20))
			.andExpect(jsonPath("$.totalElements").value(0))
			.andExpect(jsonPath("$.totalPages").value(1));
		
		// Get page 1 size 1
		mvc.perform(get("/admin/posts?size=1")
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.pageNumber").value(0))
			.andExpect(jsonPath("$.pageSize").value(1))
			.andExpect(jsonPath("$.totalElements").value(1))
			.andExpect(jsonPath("$.totalPages").value(2));
	}
	
	@Test
	@DisplayName("GET /admin/posts/{id} when post exists should return the post details")
	void getPost_existsPost_shouldReturn() throws Exception {
		// Arrange
		PostEntity postEntity1 = PostEntity.fromDomain(post1);
		postEntityRepository.save(postEntity1);
		UUID postUuid1 = postEntity1.getId();
		
		// Act & Assert
		mvc.perform(get("/admin/posts/{id}", postUuid1.toString())
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").value(postUuid1.toString()))
			.andExpect(jsonPath("$.title").value(post1.getTitle()))
			.andExpect(jsonPath("$.slug").value(post1.getSlug()));
	}
    
	@Test
	@DisplayName("GET /admin/posts/{id} when post does not exist should return 404 Not Found")
	void getPost_nonExisting_shouldReturn404() throws Exception {
		// Arrange
		UUID postUuidRandom = UUID.randomUUID();
		
		// Act & Assert
		mvc.perform(get("/admin/posts/{id}", postUuidRandom.toString()))
			.andDo(print())
			.andExpect(status().isNotFound());
	}
	
	// **** POST Post ****
	@Test
	@DisplayName("POST /admin/posts with valid data should create a new post and return 201 Created")
	void createPost_validPost_success() throws Exception {
		// Arrange
		String postReqValidJson = objectMapper.writeValueAsString(Posts.postReqValid());
		
		// Act & Assert
		mvc.perform(post("/admin/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(postReqValidJson))
			.andDo(print())
			.andExpect(status().isCreated());

		assertEquals(1, postEntityRepository.count());
	}
	
	@Test
	@DisplayName("POST /admin/posts with blank title or content should return 400 Bad Request")
	void createPost_withBlankTitleAndContent_shouldReturn400() throws Exception {
		// Arrange
		String postReqBlankTitleAndContentJson = objectMapper.writeValueAsString(Posts.postReqBlankTitleAndContent());
		
		// Act & Assert
		mvc.perform(post("/admin/posts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(postReqBlankTitleAndContentJson))
			.andDo(print())
			.andExpect(status().isBadRequest());

		// Assert
		assertEquals(0, postEntityRepository.count());
	}
	
	// **** PUT Post ****
	@Test
	@Transactional
	@DisplayName("PUT /admin/posts/{id} when post exists with valid data should update the post and return 202 Accepted")
	void editPost_existsPostAndValidPost_success() throws Exception {
		// Arrange
		PostEntity postEntity1 = PostEntity.fromDomain(post1);
		postEntityRepository.save(postEntity1);
		UUID postUuid1 = postEntity1.getId();
		
		// no need authorId attribute for updating
		PostRequestDto postReqUpdate = new PostRequestDto("title updated", "content updated", null, List.of());
		String postReqUpdateJson = objectMapper.writeValueAsString(postReqUpdate);
		
		// Act
		mvc.perform(put("/admin/posts/{id}", postUuid1.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(postReqUpdateJson))
			.andDo(print())
			.andExpect(status().isAccepted());
		
		// Assert
		Post post1Updated = postRepository.findById(PostId.fromUUID(postUuid1));
		assertEquals(postReqUpdate.title(), post1Updated.getTitle());
		assertEquals(postReqUpdate.content(), post1Updated.getContent());
		assertNotEquals(post1.getSlug(), post1Updated.getSlug());
		assertNotNull(post1Updated.getUpdatedAt());
	}
	
	@Test
	@DisplayName("PUT /admin/posts/{id} when post does not exist should return 404 Not Found")
	void editPost_nonExistingPost_shouldReturn404() throws Exception {
		// Arrange
		// no need authorId attribute for updating
		PostRequestDto postReqUpdate = new PostRequestDto("title updated", "content updated", null, List.of());
		String postReqUpdateJson = objectMapper.writeValueAsString(postReqUpdate);
		
		// Act & Assert
		mvc.perform(put("/admin/posts/{id}", UUID.randomUUID().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(postReqUpdateJson))
			.andDo(print())
			.andExpect(status().isNotFound());
	}
	
	@Test
	@Transactional
	@DisplayName("PUT /admin/posts/{id} when post exists with blank title or content should return 400 Bad Request")
	void editPost_existsPostAndBlankTitleOrContent_shouldReturn400() throws Exception {
		// Arrange
		PostEntity postEntity1 = PostEntity.fromDomain(post1);
		postEntityRepository.save(postEntity1);
		UUID postUuid1 = postEntity1.getId();
		
		// no need authorId attribute for updating
		String postReqUpdateJson = objectMapper.writeValueAsString(Posts.postReqBlankTitleAndContent());
		
		// Act & Assert
		mvc.perform(put("/admin/posts/{id}", UUID.randomUUID().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(postReqUpdateJson))
			.andDo(print())
			.andExpect(status().isBadRequest());
		
		// Assert
		Post post1Updated = postRepository.findById(PostId.fromUUID(postUuid1));
		assertEquals(post1.getTitle(), post1Updated.getTitle());
		assertEquals(post1.getContent(), post1Updated.getContent());
		assertEquals(post1.getSlug(), post1Updated.getSlug());
	}
	
	// **** DELETE Post **** 
	@Test
	@DisplayName("DELETE /admin/posts/{id} when post exists should delete the post and return 204 No Content")
	void deletePost_existsPost_shouldDelete() throws Exception {
		// Arrange
		PostEntity postEntity1 = PostEntity.fromDomain(post1);
		postEntityRepository.save(postEntity1);
		UUID postUuid1 = postEntity1.getId();
		
		// Act & Assert
		mvc.perform(delete("/admin/posts/{id}", postUuid1.toString()))
			.andDo(print())
			.andExpect(status().isNoContent());
		
		// Assert
		mvc.perform(get("/admin/posts/{id}", postUuid1.toString())
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("DELETE /admin/posts/{id} when post does not exist should return 404 Not Found")
	void deletePost_nonExistingPost_shouldReturn404() throws Exception {
		// Act & Assert
		mvc.perform(delete("/admin/posts/{id}", UUID.randomUUID().toString()))
			.andDo(print())
			.andExpect(status().isNotFound());
	}
	
}