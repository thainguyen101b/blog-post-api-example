package com.example.blog.utils.mapper;

import com.example.blog.application.query.PostDTO;
import com.example.blog.domain.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Named;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "id", source = "id.id")
    @Mapping(target = "commenter", source = "commenter.id")
    @Mapping(target = "isApproved", source = "comment", qualifiedByName = "mapIsApproved")
    PostDTO.CommentDTO toDto(Comment comment);

    @Named("mapIsApproved")
    default boolean mapIsApproved(Comment comment) {
        return comment != null && comment.isApproved();
    }

}
