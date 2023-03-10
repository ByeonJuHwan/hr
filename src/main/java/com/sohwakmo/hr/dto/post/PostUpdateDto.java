package com.sohwakmo.hr.dto.post;

import com.sohwakmo.hr.domain.Post;
import lombok.Data;

@Data
public class PostUpdateDto {

    private Integer postNo;
    private String title;
    private String content;
    private Integer viewCnt;

    public Post toEntity() {
        return Post.builder()
                .postNo(postNo).title(title).content(content).viewCnt(viewCnt)
                .build();
    }

}
