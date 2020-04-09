package com.dsy.blog.modelEntity;

import lombok.*;

/**
 * Created on 2020/4/9
 * Package com.dsy.blog.modelEntity
 *
 * @author dsy
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TagTops {

    private Integer tagId;
    private String name;
    private Integer BlogNumber;
}
