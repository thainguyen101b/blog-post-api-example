package com.example.blog.application.query.mapper;

import com.example.blog.application.query.PostDTO;
import com.example.blog.domain.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "id", source = "id.id")
    @Mapping(target = "commenter", source = "commenter.id")
    @Mapping(target = "isApproved", source = "comment", qualifiedByName = "mapIsApproved")
    PostDTO.CommentDTO toDto(Comment comment);

    default boolean mapIsApproved(Comment comment) {
        return comment != null && comment.isApproved();
    }

}
