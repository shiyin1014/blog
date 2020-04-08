package com.dsy.blog.po;

import lombok.*;

import javax.persistence.Id;

/**
 * Created on 2020/4/8
 * Package com.dsy.blog.po
 *
 * @author dsy
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BlogTag {
    @Id
    private Integer blogTagId;
    private Integer blogId;
    private Integer TagId;
}
