package com.example.blog.utils.mapper;

import com.example.blog.application.command.PostCreateCommand;
import com.example.blog.application.command.PostEditCommand;
import com.example.blog.application.query.PostDTO;
import com.example.blog.domain.Post;
import com.example.blog.web.dto.PostRequestDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;

@Mapper(componentModel = ComponentModel.SPRING,
        uses = {CategoryMapper.class, CommentMapper.class})
public interface PostMapper {

    @Mapping(target = "id", source = "id.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "isPublished", source = "post", qualifiedByName = "mapIsPublished")
    @Mapping(target = "isDeleted", source = "post", qualifiedByName = "mapIsDeleted")
    PostDTO toDto(Post post);
    
    PostCreateCommand toCreateCommand(PostRequestDto dto);
    
    PostEditCommand toEditCommand(PostRequestDto dto);

    @Named("mapIsPublished")
    default boolean mapIsPublished(Post post) {
        return post != null && post.isPublished();
    }

    @Named("mapIsDeleted")
    default boolean mapIsDeleted(Post post) {
        return post != null && post.isDeleted();
    }

}
